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
package org.apache.jetspeed.container.factory.impl;

import java.util.List;
import java.util.Properties;

import org.apache.jetspeed.components.omfactory.OMFactoryComponentImpl;
import org.apache.jetspeed.container.factory.PlutoFactory;
import org.apache.pluto.factory.Factory;
import org.picocontainer.PicoException;
import org.picocontainer.Startable;

/**
 * <p>
 * Manages the life-time of portal-to-container shared factories as defined by Pluto's factory interfaces.
 * A factory must derive from <a href='http://jakarta.apache.org/pluto/apidocs/org/apache/pluto/factory/Factory.html'>org.apache.pluto.factory.Factory</a> and implement the
 * <CODE>init()</CODE> and <CODE>destroy()</CODE> methods to meet Pluto's factory contract.
 * Factories create the shared classes between the portal and Pluto container. 
 * Implementations are created by portal provided factories. Most of the shared
 * classes are implementations of the Java Portlet API interfaces. 
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 * @see <a href="org.apache.jetspeed.container.factory.PlutoFactory">PlutoFactory</a>
 */
public class PlutoFactoryContainer
    extends OMFactoryComponentImpl
    implements Startable, PlutoFactory
{
    public PlutoFactoryContainer(Properties props)
    {
        super(props);
    }
    
    /* (non-Javadoc)
     * @see org.picocontainer.PicoContainer#getComponentInstance(java.lang.Object)
     */
    public Object getComponentInstance(Object arg0) throws PicoException
    {
        // TODO Auto-generated method stub
        return super.getComponentInstance(arg0);
    }
    /* (non-Javadoc)
     * @see org.picocontainer.PicoContainer#getComponentInstances()
     */
    public List getComponentInstances() throws PicoException
    {
        // TODO Auto-generated method stub
        return super.getComponentInstances();
    }

    public Factory getFactory (Class managedInterface)
    {
        return ((Factory) getComponentInstance (managedInterface));
    }
    
}
