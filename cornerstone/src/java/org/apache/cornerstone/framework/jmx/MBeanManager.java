/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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

package org.apache.cornerstone.framework.jmx;

/**
 * This is the Main class in the framework's implemntation of JMX.
 * This class is called by the framework at initialisation time.
 * @see the com.cisco.salesit.framework.portal.controller.EsalesPortalInitServlet
 * This MbeanManager is responsible for starting up the JMX server.
 * This MBeanManager is responsible for Starting the HttpAdaptor, that
 * is used to view the monitored Modelled MBean objects.
 * Also facilitates the receiption of the all objects published for monitoring
 * via the pubsub mechanism.  Once an object is recived automatic
 * registration of the received objects is coplaced with the main JMX server.
 * 
 */

import java.lang.reflect.Method;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import org.apache.cornerstone.framework.api.pubsub.ISubscriber;
import org.apache.cornerstone.framework.constant.Constant;
import org.apache.cornerstone.framework.core.BaseObject;
import org.apache.cornerstone.framework.pubsub.BasePubSubManager;
import org.apache.log4j.Logger;
import com.sun.jdmk.comm.HtmlAdaptorServer;

public class MBeanManager extends BaseObject implements ISubscriber
{
    public static final String MBEAN_MGR_NAME = "MBeanManager";
    
    public static final String HTML_ADAPTOR_PORT = "jmx.httpAdapter.port";
    public static String HTML_ADAPTOR_OBJECT_NAME = MBEAN_MGR_NAME + ":name=htmladaptor";
    public static String JMX_HTTP_ADAPTER_ENABLED = "jmx.httpAdapter.enabled";
 
    public static MBeanManager getSingleton()
    {
        return _Singleton;    
    }
    
    /**
     * Constrcutor.
     * Starts-up an JMX Server
     * Starts-up a HttpAdaptor server.
     *
     */                                    
        
    public MBeanManager()
    {
        super();
        _mbeanServer = MBeanServerFactory.createMBeanServer(MBEAN_MGR_NAME);

        String jmxHttpAdapterEnabledString = "true";    // TODO
        if (jmxHttpAdapterEnabledString != null && jmxHttpAdapterEnabledString.trim().equalsIgnoreCase("true"))
        {
            HtmlAdaptorServer htmlAdaptorServer = new HtmlAdaptorServer();
            try
            {
                ObjectName adapterName = new ObjectName(HTML_ADAPTOR_OBJECT_NAME);

                String httpAdaptorPortStr = getConfigProperty(HTML_ADAPTOR_PORT);
                Integer httpAdaptorPortInteger = new Integer(httpAdaptorPortStr);
                htmlAdaptorServer.setPort(httpAdaptorPortInteger.intValue());
                _mbeanServer.registerMBean(htmlAdaptorServer, adapterName);
                  
                htmlAdaptorServer.start();
                
                BasePubSubManager.getSingleton().subscribe(Constant.JMX_MANAGED, this);
             }
             catch(Exception e)
             {
                 _Logger.error("failed to initialize MBeanManager", e);
             }
        }
    }
    
    /**
     * Receives the monitored object and registeres it with the
     * JMX Server.
     * @param monitorObj the object to be monitred as an mbean.
     * 
     */

    public void receive(Object monitoredObject)
    {
        BaseModelMBean baseModelMBean = new BaseModelMBean(monitoredObject);
        String modelMBeanName = MBeanManager.MBEAN_MGR_NAME + ":" + "name=" + callGetName(monitoredObject);
        ObjectName monitoredObjectName = null;
        try
        {
            monitoredObjectName = new ObjectName(modelMBeanName);
        }
        catch (Exception e)
        {
            _Logger.error("failed to create object name '" + modelMBeanName + "'", e);
        }
        
        try
        {
            _mbeanServer.registerMBean(    baseModelMBean.getRequiredModelMBean(),    monitoredObjectName);
        }
        catch(Exception e)
        {
            _Logger.error("failed to register MBean '" + modelMBeanName + "'", e);
        }
    }

    protected String callGetName(Object monitoredObject)
    {
        try
        {
            Method getNameMethod = monitoredObject.getClass().getMethod("getName", null);
           	return (String) getNameMethod.invoke(monitoredObject, null);
        }
        catch (Exception e)
        {
           	return monitoredObject.toString();
        }
    }

    private static Logger _Logger = Logger.getLogger(MBeanManager.class);
    private static MBeanManager _Singleton = new MBeanManager();                                                                    
    protected MBeanServer _mbeanServer = null;
}