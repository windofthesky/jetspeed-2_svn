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
package org.apache.jetspeed.cps.components;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.naming.InitialContext;

import org.apache.jetspeed.cps.components.hsql.HSQLServerComponent;
import org.apache.jetspeed.cps.components.jndi.JNDIComponent;
import org.apache.jetspeed.cps.components.jndi.TyrexJNDIComponent;
import org.hsqldb.jdbcDriver;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.DefaultPicoContainer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * <p>
 * TestJNDIComponent
 * </p>
 * 
 * 
 * @
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $ $
 *
 */
public class TestHSQL extends TestCase
{
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestHSQL.class);
    }
    /**
      * Defines the testcase name for JUnit.
      *
      * @param name the testcase's name.
      */
    public TestHSQL(String name)
    {
        super(name);
    }

    public void testHSQL_1() throws Exception
    {
        MutablePicoContainer container = startTestServer();
        // sleep make sure that the server starts
        container.start();
//     /   Thread.sleep(2000);
        Class.forName(jdbcDriver.class.getName());
        Connection conn = DriverManager.getConnection("jdbc:hsqldb:hsql://127.0.0.1","sa", "");
        
        assertNotNull(conn);
        
        assertFalse(conn.isClosed());
        
        conn.close();
            
        container.stop();    
    }
    
    /** 
     * 
     * <p>
     * startTestServer
     * </p>
     * Conviniently creates and return a 
     * container that will run the HSQLServerComponent.
     * 
     * You should manually call stop() to properly shut down the server.
     * 
     * @return
     *
     */
    public static MutablePicoContainer startTestServer()
    {
        MutablePicoContainer container = new DefaultPicoContainer();
        
        Parameter[] params = new Parameter[] {
        	new ConstantParameter(new Integer(9001)),
        	new ConstantParameter("sa"),
        	new ConstantParameter(""),
        	new ConstantParameter("../portal/test/db/hsql/Registry"),
        	new ConstantParameter(new Boolean(false)),
        	new ConstantParameter(new Boolean(true)),	
        };
        container.registerComponentImplementation("HSQLServer", HSQLServerComponent.class, params);
        
        return container;
    }

}
