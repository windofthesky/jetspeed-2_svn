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

import javax.sql.DataSource;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.jetspeed.cps.components.datasource.DBCPDatasourceComponent;
import org.apache.jetspeed.cps.components.datasource.DatasourceComponent;
import org.hsqldb.jdbcDriver;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.defaults.ConstantParameter;

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
public class TestDBCPDatasource extends TestCase
{
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestDBCPDatasource.class);
    }
    /**
      * Defines the testcase name for JUnit.
      *
      * @param name the testcase's name.
      */
    public TestDBCPDatasource(String name)
    {
        super(name);
    }

    public void testDBCP_1() throws Exception
    {
    	// Need to have DB to test on ;)
        MutablePicoContainer container = TestHSQL.startTestServer();

        Parameter[] params =
            new Parameter[] {                
                new ConstantParameter("sa"),
                new ConstantParameter(""),
                new ConstantParameter(jdbcDriver.class.getName()),
                new ConstantParameter("jdbc:hsqldb:hsql://127.0.0.1"),
                new ConstantParameter(new Integer(5)),
				new ConstantParameter(new Integer(5000)),
				new ConstantParameter(new Byte(GenericObjectPool.WHEN_EXHAUSTED_GROW)),
				new ConstantParameter(new Boolean(true))
                };
		
		container.registerComponentImplementation(DatasourceComponent.class, DBCPDatasourceComponent.class, params);
        container.start();

		DatasourceComponent dsc = (DatasourceComponent) container.getComponentInstance(DatasourceComponent.class);
		assertNotNull(dsc);
		
		DataSource ds = dsc.getDatasource();
		
		assertNotNull(ds);
		
		Connection conn = ds.getConnection();
		
		assertNotNull(conn);
		
		assertFalse(conn.isClosed());
		
		conn.close();

        container.stop();

    }

}
