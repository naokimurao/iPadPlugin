package com.bt.xmpp.plugin;

import java.io.File;

import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.util.Log;
import org.xmpp.component.ComponentException;
import org.xmpp.component.ComponentManager;
import org.xmpp.component.ComponentManagerFactory;

public class IPadAnywherePlugin  implements Plugin {
	
	private static final String NAME 		= "ipadanywhere";
	private static final String DESCRIPTION = "iPad Anywhere Plugin";
	
	private PluginManager manager;
	private File pluginDirectory;
	private ComponentManager componentManager;
	private IPadAnywhereComponent ipadAnywhereComponent;
	
	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		this.manager = manager;
		this.pluginDirectory = pluginDirectory;
		componentManager = ComponentManagerFactory.getComponentManager();
		ipadAnywhereComponent = new IPadAnywhereComponent();

		try {
			Log.info("Registering IPadAnywhere plugin as a component");
			componentManager.addComponent(NAME, ipadAnywhereComponent);
		} catch (ComponentException ce) {
			Log.error(ce.getMessage());
		} catch (Exception e) {
			Log.error(e.getMessage());
		}
	}
	

	@Override
	public void destroyPlugin() {
		Log.info("unloading " + NAME + " plugin resources");
		try {
			Log.info("Unregistering IPadAnywhere plugin as a component");
			componentManager.removeComponent(NAME);
        } catch(Exception e) {
        	//componentManager.getLog().error(e);
        }
        ipadAnywhereComponent = null;
	}

	public String getName() {
		 return NAME;
	}

	public String getDescription() {
		return DESCRIPTION;
	}

	public ComponentManager getComponentManager() {
     return componentManager;
 }

	public IPadAnywhereComponent getIPadAnywhereComponent() {
		return ipadAnywhereComponent;
	}

}
