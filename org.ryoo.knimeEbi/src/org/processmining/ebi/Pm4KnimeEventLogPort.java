package org.processmining.ebi;

import org.eclipse.core.runtime.Platform;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.PortTypeRegistry;
import org.osgi.framework.Bundle;

public final class Pm4KnimeEventLogPort { // <- I think I can delete this because this is reflection for the XLogPortObject, which is already exposed

    static final String PORT_OBJECT_CLASS_NAME = "org.pm4knime.portobject.XLogPortObject";
    static final String PORT_OBJECT_SPEC_CLASS_NAME = "org.pm4knime.portobject.XLogPortObjectSpec";

    private static final String[] BUNDLE_SYMBOLIC_NAMES = {
        "org.pm4knime.java",
        "org.pm4knime.plugin"
    };

    private Pm4KnimeEventLogPort() {
    }

    public static PortType portType() {
        final Class<?> portObjectClass = loadClass(PORT_OBJECT_CLASS_NAME);
        if (!PortObject.class.isAssignableFrom(portObjectClass)) {
            throw new IllegalStateException(PORT_OBJECT_CLASS_NAME + " is not a KNIME port object.");
        }

        @SuppressWarnings("unchecked")
        final Class<? extends PortObject> typedPortObjectClass =
                (Class<? extends PortObject>)portObjectClass;
        return PortTypeRegistry.getInstance().getPortType(typedPortObjectClass);
    }

    public static boolean isEventLogSpec(final Object spec) {
        return spec != null && PORT_OBJECT_SPEC_CLASS_NAME.equals(spec.getClass().getName());
    }

    private static Class<?> loadClass(final String className) {
        final Bundle bundle = findBundle();
        try {
            return bundle.loadClass(className);
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("PM4KNIME class is unavailable: " + className, ex);
        }
    }

    private static Bundle findBundle() {
        for (final String symbolicName : BUNDLE_SYMBOLIC_NAMES) {
            final Bundle bundle = Platform.getBundle(symbolicName);
            if (bundle != null) {
                return bundle;
            }
        }

        throw new IllegalStateException("PM4KNIME is not available in the KNIME launch configuration.");
    }
}
