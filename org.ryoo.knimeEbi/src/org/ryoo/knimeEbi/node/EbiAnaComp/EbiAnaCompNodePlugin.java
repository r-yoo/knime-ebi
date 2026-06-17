package org.ryoo.knimeEbi.node.EbiAnaComp;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * Eclipse bundle activator for the EbiAnaComp node plugin.
 */
public class EbiAnaCompNodePlugin extends Plugin {

    private static EbiAnaCompNodePlugin plugin;

    public EbiAnaCompNodePlugin() {
        super();
        plugin = this;
    }

    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        super.stop(context);
        plugin = null;
    }

    public static EbiAnaCompNodePlugin getDefault() {
        return plugin;
    }
}
