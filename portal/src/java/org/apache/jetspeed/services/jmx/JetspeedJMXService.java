/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.services.jmx;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.modelmbean.ModelMBean;
import javax.naming.Context;

import mx4j.adaptor.rmi.jrmp.JRMPAdaptorMBean;
import mx4j.tools.naming.NamingService;
import mx4j.util.StandardMBeanProxy;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.modeler.ManagedBean;
import org.apache.commons.modeler.Registry;
import org.apache.jetspeed.cps.BaseCommonService;
import org.apache.jetspeed.cps.CPSInitializationException;

/**
 * 
 * JetspeedJMXService
 * 
 * Provides access to Jetpeed's internals via JMX MBeans.  Users may also configure their
 * own MBeans and register them through this service.  Knowledge of JMX and MBeans is
 * required to use this service effectively however this information is outside the scope of this 
 * documentation.
 * <br />
 * Both the <code>init()</code> and <code>shutdown()</code> methods are synchronized
 * to prevent multiple threads from trying to access either of these simultaneously.  The chance of this
 * in a production scenario is almost nill however, when ran through unit tests were multiple tests
 * fire one after another,  can cause attempts to register an MBean multiple times causing
 * JMX and naming exceptions.
 * <br />
 * This service uses the MX4J implementation of JMX.
 * 
 *  <h4>Service Parameters</h4>
 *  <table>
 *   <tr><th>Property</th><th>Description</th><th>Type</th><th>Default</th></tr>
 *   <tr>
 *     <td>services.JMXService.classname</td>
 *     <td>Class name of the JMX service</td>
 *     <td>String</td>
 *     <td>"org.apache.jetspeed.services.jmx.JetspeedJMXService"</td>
 *  </tr>
 *  <tr>
 *     <td>services.JMXService.mbeans</td>
 *     <td>Comma delimited list of MBean names to initialize</td>
 *     <td>comma delimited String</td>
 *     <td>none</td>
 *  </tr>
 *   <tr>
 *      <td>services.JMXService.mbeans.[MBean Name]</td>
 *     <td>
 *        Where [MBean] is one of the values in <code>services.JMXService.mbeans</code>
 *        This should be the implementation class that will be instantiated for the indicated MBean.
 *        Each MBean defined in <code>services.JMXService.mbeans</code> must have one
 *        and only one corresponding  <code>services.JMXService.mbeans.[MBean Name]</code>
 *        entry.
 *     </td>
 *     <td>String</td>
 *     <td>none</td>
 *  </tr>
 *  <tr>
 *      <td>services.JMXService.enable.remote.jmx</td>
 *     <td>
 *        Whether or not remote access via RMI is procied to the registered MBeans.
 *         This must be enabled is you have portlet applications that wish to access 
 *          Jetspeed MBeans.
 *      <strong>NOTE: These system properties are effected when using
 *       the remote MBeans feature:</strong>
 *       <br />
 *        javax.naming.Context.INITIAL_CONTEXT_FACTORY = "com.sun.jndi.rmi.registry.RegistryContextFactory"
 *       <br />
 *        javax.naming.Context.PROVIDER_URL = "rmi://localhost:"+services.JMXService.naming.service.port
 *     </td>
 *     <td>boolean</td>
 *     <td>true</td>
 *  </tr>
 * <tr>
 *      <td>services.JMXService.naming.service.port</td>
 *     <td>
 *         The TCP port that the naming (JNDI) mbean listens on for requests.  This will
 *         be how your portlet applications will lookup MBeans.
 *     </td>
 *     <td>int</td>
 *     <td>1099</td>
 *  </tr>
 *   <tr>
 *      <td>services.JMXService.mbean.descriptor</td>
 *     <td>
 *       MBean descriptor file used by the JMService to create MBeans.
 *     </td>
 *     <td>String</td>
 *     <td>/WEB-INF/conf/jetspeed-mbeans-descriptors.xml</td>
 *  </tr>
 *  <tr>
 *      <td>services.JMXService.mbean.descriptor</td>
 *     <td>
 *       MBean descriptor file used by the JMService to create MBeans.
 *     </td>
 *     <td>String</td>
 *     <td>/WEB-INF/conf/jetspeed-mbeans-descriptors.xml</td>
 *  </tr>
 *  <tr>
 *      <td>services.JMXService.load.from.classpath</td>
 *     <td>
 *       Whether or not to load the mbean decriptor from as a classpath
 *       resource.  For this to work the the mbean descriptor must be within the
 *       classpath.
 *     </td>
 *     <td>boolean</td>
 *     <td>false</td>
 *  </tr>
 *  <tr>
 *      <td>services.JMXService.jrmp.jndi.name</td>
 *     <td>
 *       JNDI resource name the Jave Remote Method Protocol
 *       adapter that will be used by portlet applications to retreive
 *       registered MBeans
 *     </td>
 *     <td>string</td>
 *     <td>"jmrp"</td>
 *  </tr>
 * </table>
 * 
 * @author <a href="mailto:jford@apache.org">Jeremy Ford</a> 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class JetspeedJMXService extends BaseCommonService
{
    public static final String SERVICE_NAME = "JMXService";
    private MBeanServer server;
    private Registry registry;
    private HashMap simpleNameMap;
    private ObjectName rmiRegistry;
    private JRMPAdaptorMBean jrmpBean;
    private static final Log log = LogFactory.getLog(JetspeedJMXService.class);
    private static boolean isLocked;

    private static final synchronized void lockForOperation(String operation)
    {
        System.out.println("JMX locked by operation " + operation);
        isLocked = true;
    }

    private static synchronized final void unlockForOperation(String operation)
    {
        System.out.println("JMX released by operation " + operation);
        isLocked = false;
    }

    public void init() throws CPSInitializationException
    {
        if (isInitialized())
        {
            return;
        }

        try
        {
            // holding pattern
            while (isLocked);

            lockForOperation("init");

            log.info("Initializing JetspeedJMX...");
            simpleNameMap = new HashMap();

            Configuration serviceConf = getConfiguration();

            String descriptor = serviceConf.getString("mbean.descriptor", "/WEB-INF/conf/jetspeed-mbeans-descriptors.xml");
            boolean loadFromClassPath = serviceConf.getBoolean("load.from.classpath", false);

            log.info("Loading mbean descriptor " + descriptor);

            log.info("Descriptor: " + descriptor);
            InputStream stream = null;
            if (loadFromClassPath)
            {
                stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(descriptor);
            }
            else
            {
                File mbeanFile = new File(getRealPath(descriptor));
                if (!mbeanFile.exists())
                {
                    throw new CPSInitializationException(
                        "MBean descriptor " + mbeanFile.getCanonicalPath() + " could not be found.");
                }
                stream = new BufferedInputStream(new FileInputStream(mbeanFile));
            }

            try
            {
                log.info("Loading registry...");
                Registry.loadRegistry(stream);
                stream.close();

                log.info("Finished loading registry");
            }
            catch (Exception e)
            {
                throw new CPSInitializationException("Failed to load registry: " + descriptor);
            }

            server = Registry.getServer();
            registry = Registry.getRegistry();

            String[] mbeans = serviceConf.getStringArray("mbeans");

            log.info("MBeans: " + mbeans.length);

            for (int i = 0; i < mbeans.length; i++)
            {
                String mbean = mbeans[i];

                log.info("MBean: " + mbean);

                ManagedBean managed = registry.findManagedBean(mbean);
                if (managed == null)
                {
                    System.out.println("Failed to find managed bean: " + mbean);
                    log.warn("Could not load managed bean " + mbean);
                    continue;
                }

                String domain = managed.getDomain();
                if (domain == null)
                {
                    domain = server.getDefaultDomain();
                }

                log.info("Domain: " + domain);

                Object theBean;

                String className = serviceConf.getString("mbeans." + mbean);
                log.info("ClassName: " + className);
                theBean = Class.forName(className).newInstance();

                log.info("TheBean: " + theBean);
                ModelMBean modelBean = managed.createMBean(theBean);

                log.info("ModelBean: " + modelBean);
                ObjectName oname = createObjectName(domain, managed);

                log.info("ObjectName: " + oname);

                server.registerMBean(modelBean, oname);

                log.info("Registered bean " + oname);

                simpleNameMap.put(mbean, oname);

                log.info("Registered simple name " + mbean + " to qualified object name " + oname);

            }

            boolean enableRemoteJMX = serviceConf.getBoolean("enable.remote.jmx", true);
            if (enableRemoteJMX)
            {
                initRemoteAccess(serviceConf);
            }

            setInit(true);
        }
        catch (Exception e)
        {
            log.fatal("Unable to start JMX service", e);
            // roll back all registered beans
            removeAllMBeans();
            if (e instanceof CPSInitializationException)
            {
                throw (CPSInitializationException) e;
            }
            else
            {
                throw new CPSInitializationException("Unable to start JMX service", e);
            }

        }
        finally
        {
            unlockForOperation("init");
        }
    }

    protected void initRemoteAccess(Configuration conf) throws CPSInitializationException
    {
        // Create a MBeanServer
        try
        {
            // MBeanServer server = MBeanServerFactory.createMBeanServer();

            // Create and start the naming service
            ObjectName naming = new ObjectName("Naming:type=rmiregistry");
            int namingPort = conf.getInt("naming.service.port", 1099);
            //            server.createMBean(
            //                "mx4j.tools.naming.NamingService",
            //                naming,
            //                null,
            //                new Object[] { new Integer(namingPort)},
            //                new String[] { "int" });
            NamingService ns = new NamingService();
            server.registerMBean(ns, naming);
            simpleNameMap.put("NamingService", naming);
            rmiRegistry = naming;

            // Create the JRMP adaptor
            ObjectName adaptor = new ObjectName("Adaptor:protocol=JRMP");
            server.createMBean("mx4j.adaptor.rmi.jrmp.JRMPAdaptor", adaptor, null);
            simpleNameMap.put("Adaptor", adaptor);

            try
            {
                server.invoke(naming, "start", null, null);
            }
            catch (Exception e1)
            {
                log.warn(
                    "Jetspeed JMX failed to start mx4j RMI Name Server.  This could be because an RMI name server has already been registered. "
                        + e1.getMessage(),
                    e1);
                System.out.println(
                    "Jetspeed JMX failed to start mx4j RMI Name Server.  This could be because an RMI name server has already been registered. "
                        + e1.getMessage());

            }

            jrmpBean = (JRMPAdaptorMBean) StandardMBeanProxy.create(JRMPAdaptorMBean.class, server, adaptor);

            // Set the JNDI name with which will be registered
            String jndiName = conf.getString("jrmp.jndi.name", "jrmp");
            jrmpBean.setJNDIName(jndiName);

            // Optionally, you can specify the JNDI properties, 
            // instead of having in the classpath a jndi.properties file
            jrmpBean.putJNDIProperty(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
            jrmpBean.putJNDIProperty(Context.PROVIDER_URL, "rmi://localhost:" + namingPort);

            System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
            System.setProperty(Context.PROVIDER_URL, "rmi://localhost:" + namingPort);

            // Registers the JRMP adaptor in JNDI and starts it
            jrmpBean.start();

            log.info("JetspeedJMX remote services sucessfuly started!");
        }
        catch (Exception e)
        {
            String msg = "Unable to start Jetspeed JMX remote services.";
            log.error(msg, e);
            throw new CPSInitializationException(msg, e);
        }

    }

    public String[] getManagedBeans()
    {
        return registry.findManagedBeans();
    }

    public ManagedBean getManagedBean(String name)
    {
        return registry.findManagedBean(name);
    }

    /**
     * Indicates whether or the remote MBean access has been enabled.
     * <br/>
     * This can be configured in Jetpseed properties setting the value
     * of the <code>services.JMXService.enable.remote.jmx</code> to
     * either <code>true</code> or <code>false</code>.
     * 
     * @return <code>true</code> is the remote JMX MBean access is enabled
     * <code>flase</code> if it is not.
     */
    public boolean isRemoteEnabled()
    {
        return getConfiguration().getBoolean("enable.remote.jmx", true);
    }

    public void invoke(String name, String operation, Object[] parameters, String[] signature)
    {
        ManagedBean mb = getManagedBean(name);

        try
        {
            ObjectName objName = createObjectName(name, mb);
            server.invoke(objName, operation, parameters, signature);
        }
        catch (Exception e)
        {
            log.error("Unable to invoke MBean", e);
            //  e.printStackTrace();
        }

    }

    public Object getAttribute(ObjectName objectName, String attribute)
    {
        try
        {
            return server.getAttribute(objectName, attribute);
        }
        catch (Exception e)
        {
            log.error("Unable to retreive MBean attribute", e);
            //  e.printStackTrace();
            return null;
        }

    }

    public MBeanServer getMBeanServer()
    {
        return server;
    }

    private ObjectName createObjectName(String domain, ManagedBean bean) throws MalformedObjectNameException
    {

        return new ObjectName(domain + ":mbean=" + bean.getName());

    }

    /**
     * 
     * @param simpleName 
     * @return
     */
    public ObjectName resolveObjectName(String simpleName)
    {
        return (ObjectName) simpleNameMap.get(simpleName);
    }

    /**
     * @see org.apache.fulcrum.Service#shutdown()
     */
    public void shutdown()
    {
        try
        {
            while (isLocked);

            lockForOperation("shutdown");

            if (rmiRegistry != null)
            {
                if (jrmpBean != null)
                {
                    jrmpBean.stop();
                }

                System.out.println("stopping RMI registry");

                server.invoke(rmiRegistry, "stop", null, null);
                Thread.sleep(500);
            }

        }
        catch (Exception e)
        {
            System.out.println("Unable to stop rmi registry and/or the jrmp adapter mbean" + e.getMessage());
            log.warn("Unable to stop rmi registry and/or the jrmp adapter mbean", e);
        }

        removeAllMBeans();

        unlockForOperation("shutdown");
        setInit(false);
    }

    private void removeAllMBeans()
    {
        Iterator itr = simpleNameMap.values().iterator();
        while (itr.hasNext())
        {
            ObjectName oName = (ObjectName) itr.next();
            try
            {
                log.info("Unregistering MBean: " + oName);
                server.unregisterMBean(oName);
                log.info("MBean: " + oName + " was successfuly unregistered.");
            }
            catch (Exception e)
            {
                // Not that important if we fail to un-register
                log.warn("Unable to unregister MBean " + oName, e);
            }

        }
    }

}
