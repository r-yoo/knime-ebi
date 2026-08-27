package org.ryoo.knimeEbi.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/** Converts Ebi PTML into the stricter PTML representation read by PM4KNIME. */
public final class PtmlCompatibilityUtil {

    private static final String EDGE_ELEMENT = "parentsNode";

    private PtmlCompatibilityUtil() {
    }

    /**
     * Converts an Ebi PTML document to PM4KNIME-compatible PTML.
     *
     * <p>The conversion validates or repairs the assumed Ebi process-tree structure,
     * assigns canonical UUIDs to the tree, nodes, and edges, and rewrites all
     * references consistently. An unsupported broken graph is rejected rather
     * than guessed.</p>
     *
     * @param ebiPtml PTML returned by Ebi
     * @return PM4KNIME-compatible PTML encoded as a Java string
     * @throws IllegalArgumentException if the input is not supported PTML
     */
    public static String toPm4KnimePtml(final String ebiPtml) {
        if (ebiPtml == null || ebiPtml.isBlank()) {
            throw new IllegalArgumentException("Ebi PTML must not be null or blank.");
        }
        if (ebiPtml.stripLeading().startsWith("Ebi: error:")) {
            throw new IllegalArgumentException("Ebi returned an error instead of PTML: " + ebiPtml.trim());
        }

        try {
            final PtmlDocument document = parseSecurely(ebiPtml);
            final LinkedHashMap<String, PtmlElement> nodes = collectNodes(document);
            List<Edge> edges = collectEdges(document);

            final String rootId = requireAttribute(document.processTree(), "root");
            if (!nodes.containsKey(rootId)) {
                throw new IllegalArgumentException("PTML root references unknown node '" + rootId + "'.");
            }

            if (hasEbiProcessTreeStructure(rootId, nodes)) {
                edges = rebuildEbiProcessTree(document, rootId, nodes);
            }
            validateTree(rootId, nodes, edges);
            rewriteWithCanonicalUuids(document.processTree(), rootId, nodes, edges);
            return serialize(document);
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Could not convert Ebi PTML for PM4KNIME.", ex);
        }
    }

    private static PtmlDocument parseSecurely(final String xml) throws Exception {
        final XMLInputFactory factory = XMLInputFactory.newFactory();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false);
        if (factory.isPropertySupported(XMLConstants.ACCESS_EXTERNAL_DTD)) {
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        }

        final XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
        try {
            PtmlElement ptml = null;
            PtmlElement processTree = null;
            final List<PtmlElement> children = new ArrayList<>();
            int depth = 0;

            while (reader.hasNext()) {
                final int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    depth++;
                    final PtmlElement element = readElement(reader);
                    if (depth == 1) {
                        if (!"ptml".equals(element.name())) {
                            throw new IllegalArgumentException("Expected a <ptml> document root.");
                        }
                        ptml = element;
                    } else if (depth == 2) {
                        if (!"processTree".equals(element.name()) || processTree != null) {
                            throw new IllegalArgumentException("PTML must contain exactly one <processTree>.");
                        }
                        processTree = element;
                    } else if (depth == 3) {
                        children.add(element);
                    } else {
                        throw new IllegalArgumentException("Nested PTML child elements are not supported.");
                    }
                } else if (event == XMLStreamConstants.END_ELEMENT) {
                    depth--;
                } else if ((event == XMLStreamConstants.CHARACTERS || event == XMLStreamConstants.CDATA)
                        && !reader.isWhiteSpace()) {
                    throw new IllegalArgumentException("Text content is not supported inside PTML elements.");
                } else if (event == XMLStreamConstants.DTD || event == XMLStreamConstants.ENTITY_REFERENCE) {
                    throw new IllegalArgumentException("DTD and entity references are not allowed in PTML.");
                }
            }

            if (ptml == null || processTree == null) {
                throw new IllegalArgumentException("PTML does not contain a <processTree>.");
            }
            return new PtmlDocument(ptml, processTree, children);
        } finally {
            reader.close();
        }
    }

    private static PtmlElement readElement(final XMLStreamReader reader) {
        if (reader.getNamespaceURI() != null && !reader.getNamespaceURI().isBlank()) {
            throw new IllegalArgumentException("Namespaced PTML elements are not supported.");
        }

        final LinkedHashMap<String, String> attributes = new LinkedHashMap<>();
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            if (reader.getAttributeNamespace(i) != null && !reader.getAttributeNamespace(i).isBlank()) {
                throw new IllegalArgumentException("Namespaced PTML attributes are not supported.");
            }
            attributes.put(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
        }
        return new PtmlElement(reader.getLocalName(), attributes);
    }

    private static LinkedHashMap<String, PtmlElement> collectNodes(final PtmlDocument document) {
        final LinkedHashMap<String, PtmlElement> nodes = new LinkedHashMap<>();
        for (PtmlElement child : document.children()) {
            if (EDGE_ELEMENT.equals(child.name())) {
                continue;
            }
            final String id = requireAttribute(child, "id");
            if (nodes.putIfAbsent(id, child) != null) {
                throw new IllegalArgumentException("Duplicate PTML node id '" + id + "'.");
            }
        }
        if (nodes.isEmpty()) {
            throw new IllegalArgumentException("PTML process tree contains no nodes.");
        }
        return nodes;
    }

    private static List<Edge> collectEdges(final PtmlDocument document) {
        final List<Edge> edges = new ArrayList<>();
        for (PtmlElement child : document.children()) {
            if (EDGE_ELEMENT.equals(child.name())) {
                edges.add(new Edge(
                    child,
                    requireAttribute(child, "sourceId"),
                    requireAttribute(child, "targetId")
                ));
            }
        }
        return edges;
    }

    private static boolean hasEbiProcessTreeStructure(final String rootId,
            final Map<String, PtmlElement> nodes) {
        return "xorLoop".equals(nodes.get(rootId).name())
            && nodes.containsKey(rootId + "redo")
            && nodes.containsKey(rootId + "exit");
    }

    private static List<Edge> rebuildEbiProcessTree(final PtmlDocument document, final String rootId,
            final LinkedHashMap<String, PtmlElement> nodes) {
        final PtmlElement root = nodes.get(rootId);
        if (!"xorLoop".equals(root.name())) {
            throw unsupportedGraph();
        }

        final PtmlElement redo = findUnique(nodes, "xor", rootId + "redo");
        final PtmlElement exit = findUnique(nodes, "automaticTask", rootId + "exit");
        final List<PtmlElement> otherAutomaticTasks = nodes.values().stream()
            .filter(node -> "automaticTask".equals(node.name()))
            .filter(node -> node != exit)
            .toList();
        if (redo == null || exit == null || otherAutomaticTasks.size() != 1) {
            throw unsupportedGraph();
        }

        final PtmlElement body = otherAutomaticTasks.get(0);
        final List<PtmlElement> activities = nodes.values().stream()
            .filter(node -> node != root && node != redo && node != exit && node != body)
            .toList();
        if (activities.isEmpty() || activities.stream().anyMatch(node -> !"manualTask".equals(node.name()))) {
            throw unsupportedGraph();
        }

        document.children().removeIf(child -> EDGE_ELEMENT.equals(child.name()));
        final List<Edge> repaired = new ArrayList<>();
        repaired.add(appendEdge(document, rootId, body.attribute("id")));
        repaired.add(appendEdge(document, rootId, redo.attribute("id")));
        repaired.add(appendEdge(document, rootId, exit.attribute("id")));
        for (PtmlElement activity : activities) {
            repaired.add(appendEdge(document, redo.attribute("id"), activity.attribute("id")));
        }
        return repaired;
    }

    private static IllegalArgumentException unsupportedGraph() {
        return new IllegalArgumentException(
            "Ebi PTML does not match the assumed Ebi process-tree structure.");
    }

    private static PtmlElement findUnique(final Map<String, PtmlElement> nodes, final String elementName,
            final String preferredId) {
        final PtmlElement preferred = nodes.get(preferredId);
        if (preferred != null && elementName.equals(preferred.name())) {
            return preferred;
        }
        final List<PtmlElement> matches = nodes.values().stream()
            .filter(node -> elementName.equals(node.name()))
            .toList();
        return matches.size() == 1 ? matches.get(0) : null;
    }

    private static Edge appendEdge(final PtmlDocument document, final String sourceId, final String targetId) {
        final LinkedHashMap<String, String> attributes = new LinkedHashMap<>();
        attributes.put("sourceId", sourceId);
        attributes.put("targetId", targetId);
        final PtmlElement element = new PtmlElement(EDGE_ELEMENT, attributes);
        document.children().add(element);
        return new Edge(element, sourceId, targetId);
    }

    private static void validateTree(final String rootId, final Map<String, PtmlElement> nodes,
            final List<Edge> edges) {
        final Map<String, Integer> incoming = new HashMap<>();
        final Map<String, List<String>> children = new HashMap<>();
        for (String nodeId : nodes.keySet()) {
            incoming.put(nodeId, 0);
            children.put(nodeId, new ArrayList<>());
        }

        for (Edge edge : edges) {
            if (!nodes.containsKey(edge.sourceId()) || !nodes.containsKey(edge.targetId())) {
                throw new IllegalArgumentException(
                    "Dangling PTML edge '" + edge.sourceId() + "' -> '" + edge.targetId() + "'.");
            }
            incoming.compute(edge.targetId(), (ignored, count) -> count + 1);
            children.get(edge.sourceId()).add(edge.targetId());
        }

        if (incoming.get(rootId) != 0) {
            throw new IllegalArgumentException("PTML root must not have a parent.");
        }
        for (Map.Entry<String, Integer> entry : incoming.entrySet()) {
            if (!entry.getKey().equals(rootId) && entry.getValue() != 1) {
                throw new IllegalArgumentException(
                    "PTML node '" + entry.getKey() + "' must have exactly one parent.");
            }
        }

        final Set<String> visited = new HashSet<>();
        final ArrayDeque<String> pending = new ArrayDeque<>();
        pending.push(rootId);
        while (!pending.isEmpty()) {
            final String current = pending.pop();
            if (!visited.add(current)) {
                throw new IllegalArgumentException("PTML graph contains a cycle or repeated child.");
            }
            final List<String> nodeChildren = children.get(current);
            final PtmlElement node = nodes.get(current);
            if ("xorLoop".equals(node.name()) && nodeChildren.size() != 3) {
                throw new IllegalArgumentException(
                    "PTML xorLoop '" + current + "' must have body, redo, and exit children.");
            }
            for (int i = nodeChildren.size() - 1; i >= 0; i--) {
                pending.push(nodeChildren.get(i));
            }
        }
        if (visited.size() != nodes.size()) {
            throw new IllegalArgumentException("PTML graph is disconnected.");
        }
    }

    private static void rewriteWithCanonicalUuids(final PtmlElement processTree, final String rootId,
            final LinkedHashMap<String, PtmlElement> nodes, final List<Edge> edges) {
        final Map<String, String> nodeIds = new HashMap<>();
        for (String oldId : nodes.keySet()) {
            nodeIds.put(oldId, stableUuid("node|" + oldId));
        }

        processTree.setAttribute("id", stableUuid("tree|" + rootId + "|" + String.join("|", nodes.keySet())));
        processTree.setAttribute("root", nodeIds.get(rootId));
        for (Map.Entry<String, PtmlElement> node : nodes.entrySet()) {
            node.getValue().setAttribute("id", nodeIds.get(node.getKey()));
        }
        for (int i = 0; i < edges.size(); i++) {
            final Edge edge = edges.get(i);
            edge.element().setAttribute("id", stableUuid(
                "edge|" + i + "|" + edge.sourceId() + "|" + edge.targetId()));
            edge.element().setAttribute("sourceId", nodeIds.get(edge.sourceId()));
            edge.element().setAttribute("targetId", nodeIds.get(edge.targetId()));
        }
    }

    private static String stableUuid(final String value) {
        return UUID.nameUUIDFromBytes(value.getBytes(StandardCharsets.UTF_8)).toString();
    }

    private static String serialize(final PtmlDocument document) throws Exception {
        final StringWriter output = new StringWriter();
        final XMLStreamWriter writer = XMLOutputFactory.newFactory().createXMLStreamWriter(output);
        try {
            writer.writeStartDocument(StandardCharsets.UTF_8.name(), "1.0");
            writer.writeCharacters("\n");
            writeStartElement(writer, document.ptml());
            writer.writeCharacters("\n");
            writeStartElement(writer, document.processTree());
            writer.writeCharacters("\n");
            for (PtmlElement child : document.children()) {
                writer.writeEmptyElement(child.name());
                writeAttributes(writer, child.attributes());
                writer.writeCharacters("\n");
            }
            writer.writeEndElement();
            writer.writeCharacters("\n");
            writer.writeEndElement();
            writer.writeCharacters("\n");
            writer.writeEndDocument();
            writer.flush();
            return output.toString();
        } finally {
            writer.close();
        }
    }

    private static void writeStartElement(final XMLStreamWriter writer, final PtmlElement element) throws Exception {
        writer.writeStartElement(element.name());
        writeAttributes(writer, element.attributes());
    }

    private static void writeAttributes(final XMLStreamWriter writer,
            final LinkedHashMap<String, String> attributes) throws Exception {
        for (Map.Entry<String, String> attribute : attributes.entrySet()) {
            writer.writeAttribute(attribute.getKey(), attribute.getValue());
        }
    }

    private static String requireAttribute(final PtmlElement element, final String attribute) {
        final String value = element.attribute(attribute);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                element.name() + " is missing required attribute '" + attribute + "'.");
        }
        return value;
    }

    private record PtmlDocument(PtmlElement ptml, PtmlElement processTree, List<PtmlElement> children) {
    }

    private record PtmlElement(String name, LinkedHashMap<String, String> attributes) {

        private String attribute(final String name) {
            return attributes.get(name);
        }

        private void setAttribute(final String name, final String value) {
            attributes.put(name, value);
        }
    }

    private record Edge(PtmlElement element, String sourceId, String targetId) {
    }
}
