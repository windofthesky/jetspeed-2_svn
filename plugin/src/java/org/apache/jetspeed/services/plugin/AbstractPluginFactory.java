/**
 * Created on Jul 8, 2003
 *
 * 
 * @author
 */
package org.apache.jetspeed.services.plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.BaseService;
import org.apache.fulcrum.InitializationException;
import org.apache.jetspeed.services.persistence.PathResolver;
import org.apache.jetspeed.services.persistence.impl.FulcrumServicePathResloverImpl;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * AbstractPluginService
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public abstract class AbstractPluginFactory extends BaseService
{
    protected Map defaultPlugins;
    protected Map plugins;

    private static final Log log = LogFactory.getLog(AbstractPluginFactory.class);

    protected abstract String getType(Plugin plugin) throws UnsupportedPluginException;

    /**
     * @see org.apache.fulcrum.Service#init()
     */
    public void init() throws InitializationException
    {
        // Are we alreay initialized?
        if (isInitialized())
        {
            return;
        }

        plugins = new HashMap();
        defaultPlugins = new HashMap();
        try
        {
            Configuration conf = getConfiguration();

            String fileName = conf.getString("plugin.file");
            SAXBuilder builder = new SAXBuilder();
            String filePath = getRealPath(fileName);
            log.info("plugins path: " + filePath);
            File file = new File(filePath);
            Document document = null;
            // Load from file system
            if (file.exists())
            {
                document = builder.build(file);
            }
            else
            {
                // or load from classloader as a resource.
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                String pluginFile = cl.getResource(fileName).getFile();
                document = builder.build(pluginFile);
            }

            Element pluginElements = document.getRootElement();
            Iterator pluginItr = pluginElements.getChildren("plugin").iterator();
            while (pluginItr.hasNext())
            {
                ConfigurationReader confReader = new JDOMConfigurationReaderImpl();
                PluginConfiguration pluginConf = confReader.buildConfiguration((Element) pluginItr.next());
                pluginConf.setFactory(this);
                createPlugin(pluginConf);
            }

            // everythings ok, we are now initialized.
            setInit(true);
            log.info("PersistenceService successfuly initialized.");
        }
        catch (Exception e)
        {
            String message = "Unable to initialize PersistenceService.";
            log.fatal(message, e);
            if (!(e instanceof InitializationException))
            {
                throw new InitializationException(message, e);
            }
            else
            {
                throw (InitializationException) e;
            }
        }
    }

    public Plugin createPlugin(PluginConfiguration pluginConf) throws PluginInitializationException
    {
        try
        {
            String className = pluginConf.getClassName();
            // pluginConf.setPathResolver(new JetspeedPathResloverImpl());
            String resolverClassName = getConfiguration().getString("path.resolver.class");
            

            PathResolver resolver = null;
            if (resolverClassName != null)
            {
                Class resolverClass = Class.forName(resolverClassName);
                resolver = (PathResolver) resolverClass.newInstance();
            }
            else
            {
                resolver = new FulcrumServicePathResloverImpl(this);
            }
			
			log.info("Using path resolver class"+resolver.getClass().getName());
            pluginConf.setPathResolver(resolver);
            
            if (className == null)
            {
                throw new InitializationException("No class defined for plugin " + pluginConf.getName());
            }
            Plugin plugin = (Plugin) Class.forName(className).newInstance();
            plugin.init(pluginConf);
            plugins.put(getType(plugin) + "." + pluginConf.getName(), plugin);
            // Check if this is the default plugin

            if (pluginConf.isDefault() && defaultPlugins.get(getType(plugin)) != null)
            {
                String message = "You may only define one plugin as the default.  Default plugin NOT changed.";
                log.warn(message);
            }
            else if (pluginConf.isDefault() && defaultPlugins.get(getType(plugin)) == null)
            {
                defaultPlugins.put(getType(plugin), plugin);
                log.info("Default persistence plugin has been set to \"" + pluginConf.getName() + "\"");
            }

            log.info("Persistence plugin \"" + pluginConf.getName() + " \"initialized using class " + pluginConf.getClassName());

            return plugin;
        }
        catch (Exception e)
        {
            String message = "Unable to create Plugin.";
            log.fatal(message, e);
            if (!(e instanceof PluginInitializationException))
            {
                throw new PluginInitializationException(message, e);
            }
            else
            {
                throw (PluginInitializationException) e;
            }
        }
    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistenceService#getPlugin(java.lang.String)
     */
    public Plugin getPlugin(String type, String name)
    {
        return (Plugin) plugins.get(type + "." + name);
    }

    public Plugin getDefaultPlugin(String type)
    {
        Plugin plugin = (Plugin) defaultPlugins.get(type);
        if (plugin == null)
        {
            log.warn("No default plugin has been defined for type " + type);
        }
        return plugin;
    }

    /**
     * @see org.apache.fulcrum.Service#shutdown()
     */
    public void shutdown()
    {
        super.shutdown();
    }

}
