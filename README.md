# knime-ebi

**Ebi extension for KNIME Analytics Platform** — brings the Ebi stochastic process mining toolkit into KNIME as 54 native nodes, wired into the PM4KNIME port-object ecosystem.

Ebi is a process mining toolkit built around *stochastic* process models — models that carry probabilities, not just control flow. This extension makes its commands usable as drag-and-drop KNIME nodes, so stochastic discovery, conformance checking and hypothesis testing can be composed into KNIME workflows alongside everything else the platform offers.

---

## How it works

```
KNIME workflow
      │
      │  PM4KNIME port objects (XLog, PetriNet, ProcessTree)
      ▼
Ebi*NodeFactory                  ← generated from Ebi's own command metadata
      │  serialises inputs to XES / PNML / PTML strings
      ▼
org.processmining.ebi.CallEbi    ← JNI bridge
      │  call_ebi(command, outputFormat, inputs[])
      ▼
lib/ebi.dll                      ← native Ebi library (Windows x86-64)
      │  returns the result as a string in the requested format
      ▼
parsed back into a KNIME table or a PM4KNIME port object
```

Three ideas carry the whole design:

**1. Everything crosses the boundary as text.** Event logs go to Ebi as XES, models as PNML or PTML, scalars as plain strings. `CallEbi.call_ebi(commandName, outputFormat, inputs)` is the single chokepoint. This keeps the JNI surface to exactly one native method and avoids marshalling Java object graphs across the boundary.

**2. The nodes are generated, not hand-written.** Ebi can describe its own command surface (`Ebi itself java`). The scaffolder in `org.ryoo.knimeEbi.scaffolder` parses that output — command name, descriptions, parameter types, return type — and emits a `…NodeFactory` (plus a `…NodeSettings` where the command takes non-port parameters), then registers the factory in `plugin.xml`. Supporting new Ebi commands is a matter of rebuilding the DLL and re-running the generator, not writing Java.

**3. The JNI package name is load-bearing.** `ebi.dll` exports `Java_org_processmining_ebi_CallEbi_call_1ebi_1internal`, so `CallEbi` **must** live in `org.processmining.ebi` under that exact method name. Moving or renaming it breaks the native binding at class-initialisation time.

### Interop details worth knowing

- **Log serialisation via reflection.** `XESUtil` reaches OpenXES (`XLog`, `XesXmlSerializer`) through the PM4KNIME bundle's own classloader instead of importing it directly, which spares this bundle from re-exporting OpenXES packages across the OSGi boundary.
- **PTML repair.** Ebi's process-tree output is looser than what PM4KNIME's reader accepts. `PtmlCompatibilityUtil` re-parses it with a hardened StAX pipeline, assigns canonical UUIDs to the tree, its nodes and its edges, rewrites all cross-references, and rejects genuinely broken graphs rather than guessing at them.
- **Error propagation.** Ebi signals failure by returning a string starting with `Ebi: error:`. Every generated node checks for that prefix and raises it as a KNIME node failure, so the real Ebi diagnostic reaches the user instead of a downstream parse error.

---

## Node catalogue

All 54 nodes are registered at the root of the KNIME node repository.

**Discovery (15)** — directly-follows graph · Inductive Miner · Inductive Miner (infrequent) · flower DFA · flower process tree · prefix-tree DFA · prefix-tree process tree · trace model · uniform / occurrence / random stochastic labelled Petri nets · uniform / occurrence / random stochastic process trees · alignments between stochastic labelled Petri nets

**Conformance (12)** — Earth mover's · unit Earth mover's · entropic relevance · Hellinger · Jensen-Shannon · chi-squared · Markovian, plus sampled variants of the distance-based measures for large logs

**Analysis (8)** — completeness · variety · activities · boundedness · empty traces · any traces · infinitely many traces · timestamps ordered

**Hypothesis testing (3)** — bootstrap test · log categorical-attribute test · log-model permutation test

**Association (2)** — single trace attribute · all trace attributes

**Filtering and sampling (4)** — filter empty traces · filter by event activity · filter by trace length · sample folds

**Conversion and utility (10)** — convert log · convert labelled Petri net · convert stochastic labelled Petri net · convert stochastic deterministic / nondeterministic finite automaton · reduce labelled Petri net · reduce process tree · probability log · Ebi information · visualise text

> **On model ports:** process trees are accepted as *inputs*, but every model-producing node currently emits a `PetriNetPortObject` (PNML). The PTML output path exists in the scaffolder and in `PtmlCompatibilityUtil`, but the current generation maps model output to PNML.

---

## Requirements

| | |
|---|---|
| KNIME Analytics Platform | nightly 5.12 (see `org.knime.sdk.setup/KNIME-AP.target`) |
| Java | 21 (`JavaSE-21`) |
| PM4KNIME | `org.pm4knime.feature` from the KNIME trusted community-contributions update site |
| Platform | **Windows x86-64 only** — `lib/ebi.dll` is a native PE32+ binary |

The Windows-only constraint comes from the bundled native library. Building Ebi as a `.so` or `.dylib` and extending `CallEbi`'s loader would lift it.

---

## How to run

1. Go to the releases.
2. Download and execute the newest release `.jar` file.

## Development setup

1. Install the KNIME SDK (Eclipse with the KNIME target-platform tooling).
2. Import both projects into the workspace:
   - `org.ryoo.knimeEbi` — the extension itself
   - `org.knime.sdk.setup` — target platform and launch configuration
3. Open `org.knime.sdk.setup/KNIME-AP.target` and choose **Set as Active Target Platform**. It resolves KNIME AP nightly 5.12 plus PM4KNIME from the community-contributions site. The first resolution downloads a lot and takes a while.
4. Launch `org.knime.sdk.setup/KNIME Analytics Platform.launch` to start a KNIME instance with the extension installed.

`API-Baseline.target` is provided for API-baseline comparison during development.

### Regenerating the nodes

`org.ryoo.knimeEbi.entryPoint.Main` re-runs the scaffolder against the current `ebi.dll`:

```bash
java -cp bin org.ryoo.knimeEbi.entryPoint.Main
```

Run it with the working directory set to `org.ryoo.knimeEbi/`. It writes `src/org/ryoo/knimeEbi/node/*.java` and appends new factories to `plugin.xml`. The working directory matters: paths are resolved relative to it, and outside OSGi `CallEbi` falls back to loading `lib/ebi.dll` from there.

Commands are skipped when they carry no `@Plugin` declaration, or when they take no inputs (Ebi's "itself" commands). Hand-edits to generated files are overwritten on the next run — change the generator instead.

---

## Repository layout

```
org.ryoo.knimeEbi/
├── src/org/processmining/ebi/CallEbi.java      JNI bridge (package name fixed by the DLL)
├── src/org/ryoo/knimeEbi/
│   ├── node/                                   54 generated node factories + 16 settings classes
│   ├── defaultNode/EbiDefaultNodeFactory.java  shared base: metadata → node, port-type resolution
│   ├── scaffolder/                             metadata parser, source generators, plugin.xml registrar
│   ├── util/                                   XES serialisation, PTML repair, table specs
│   └── entryPoint/Main.java                    generator entry point
├── lib/ebi.dll                                 native Ebi library (~30 MB, Windows x86-64)
├── doc/                                        generated Javadoc
├── META-INF/MANIFEST.MF                        OSGi bundle definition
└── plugin.xml                                  node repository registrations

org.knime.sdk.setup/                            target platform + launch configuration
```

---

## Known limitations

- Windows-only, because of the bundled native library.
- Nodes register at the node-repository root (`category-path="/"`) rather than under a dedicated Ebi category.
- Nodes use a placeholder icon and report `sinceVersion(0, 0, 0)`.
- Model output is normalised to PNML; PTML output is implemented but not currently wired up by the generator.

---

## License

Copyright © 2026 Reinhold X.A. Yoo. All rights reserved. See [LICENSE](LICENSE).

Ebi and PM4KNIME are the work of their respective authors and carry their own licenses.
