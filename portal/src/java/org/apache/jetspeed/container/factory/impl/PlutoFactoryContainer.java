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
