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

package org.apache.cornerstone.framework.sysman;

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
            _Logger.info("failed to call getName()", e);
            return null;
        }
    }

    private static Logger _Logger = Logger.getLogger(MBeanManager.class);
    private static MBeanManager _Singleton = new MBeanManager();                                                                    
    protected MBeanServer _mbeanServer = null;
}