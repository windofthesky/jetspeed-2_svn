/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.jetspeed.services.perisistence;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.BaseService;
import org.apache.fulcrum.InitializationException;
import org.apache.jetspeed.services.perisistence.impl.FulcrumServicePathResloverImpl;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * 
 * JetspeedPersistenceService
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class JetspeedPersistenceService extends BaseService implements PersistenceService
{
    private Map plugins;
    private PersistencePlugin defaultPlugin = null;

    private static final Log log = LogFactory.getLog(JetspeedPersistenceService.class);

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistenceService#getDefaultPlugin()
     */
    public PersistencePlugin getDefaultPlugin()
    {
        return this.defaultPlugin;
    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistenceService#getPlugin(java.lang.String)
     */
    public PersistencePlugin getPlugin(String name)
    {
        return (PersistencePlugin) plugins.get(name);
    }

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
        try
        {
            Configuration conf = getConfiguration();

            defaultPlugin = null;

            String fileName = conf.getString("plugin.file");
            SAXBuilder builder = new SAXBuilder();
            String filePath = getRealPath(fileName);
            log.info("Persistence plugins path: " + filePath);
            File file = new File(filePath);
            Document document = builder.build(file);
            Element pluginElements = document.getRootElement();
            Iterator pluginItr = pluginElements.getChildren("plugin").iterator();
            while (pluginItr.hasNext())
            {
                PluginConfiguration pluginConf = getConfiguration((Element) pluginItr.next());
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

    public PersistencePlugin createPlugin(PluginConfiguration pluginConf) throws PluginInitializationException
    {
        try
        {
            String className = pluginConf.getClassName();
            pluginConf.setPathResolver(new FulcrumServicePathResloverImpl(this));
            if (className == null)
            {
                throw new InitializationException("No class defined for plugin " + pluginConf.getName());
            }
            PersistencePlugin plugin = (PersistencePlugin) Class.forName(className).newInstance();
            plugin.init(pluginConf, this);
            plugins.put(pluginConf.getName(), plugin);
            // Check if this is the default plugin

            if (pluginConf.isDefault() && defaultPlugin != null)
            {
                String message = "You may only define one plugin as the default.  Default plugin NOT changed.";
                log.warn(message);
            }
            else if (pluginConf.isDefault() && defaultPlugin == null)
            {
                defaultPlugin = plugin;
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

    protected static PluginConfiguration getConfiguration(Element xmlConf) throws InitializationException
    {
        PluginConfiguration pluginConf = new BasicPluginConfiguration();

        String name = xmlConf.getAttributeValue("name");
        if (name == null)
        {
            String message = "Plugins must define a \"name\" attribute.";
            log.error(message);
            throw new InitializationException(message);
        }
        pluginConf.setName(name);
        Element classNameE = xmlConf.getChild("classname");

        String isDefault = xmlConf.getAttributeValue("default");
        pluginConf.setDefault(new Boolean(isDefault).booleanValue());

        pluginConf.setClassName(classNameE.getTextTrim());
        Iterator propertyIterator = xmlConf.getChildren("property").iterator();
        while (propertyIterator.hasNext())
        {
            Element prop = (Element) propertyIterator.next();
            pluginConf.setProperty(prop.getAttributeValue("name"), prop.getTextTrim());
        }

        return pluginConf;
    }

    /**
     * @see org.apache.fulcrum.Service#shutdown()
     */
    public void shutdown()
    {

        super.shutdown();

    }

}
