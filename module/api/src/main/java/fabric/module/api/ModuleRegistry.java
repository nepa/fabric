/**
 * 
 */
package fabric.module.api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Registry for Fabric's modules.
 * 
 * @author Marco Wegner
 */
public final class ModuleRegistry implements Iterable<FabricModule> {

    /**
     * This instance holds the registered Fabric modules mapped to their
     * respective names.
     */
    private final Map<String, FabricModule> modules;

    {
        this.modules = new HashMap<String, FabricModule>( );
    }

    /**
     * Constructs a new module registry.
     */
    public ModuleRegistry( ) {
        //
    }

    /**
     * Registers a single module in this registry.
     * 
     * @param m The module to be registered.
     * @throws Exception If the registry already contains a module with the same
     *         name as the module to be added now.
     */
    public void register(FabricModule m) throws Exception {
        final String moduleName = m.getName( );
        if (this.modules.containsKey(moduleName)) {
            throw new Exception(String.format("registry already contains module with name '%s'",
                    moduleName));
        }
        this.modules.put(moduleName, m);
    }

    /**
     * Returns the module which has the specified name.
     * 
     * @param name The module name to be queried.
     * @return The module.
     * @throws Exception If there is no module registered with the specified
     *         name.
     */
    public FabricModule get(String name) throws Exception {
        if (!this.modules.containsKey(name)) {
            throw new Exception(String.format("no module regsitered with name '%s'", name));
        }
        return this.modules.get(name);
    }

    @Override
    public String toString( ) {
        final StringBuilder sb = new StringBuilder( );
        sb.append("Registered modules:\n-------------------\n");
        for (final Entry<String, FabricModule> e : this.modules.entrySet( )) {
            final FabricModule m = e.getValue( );
            sb.append(String.format("Module[%s]: %s\n", m.getName( ), m.getDescription( )));
        }
        return sb.toString( );
    }

    @Override
    public Iterator<FabricModule> iterator( ) {
        return this.modules.values( ).iterator( );
    }
}