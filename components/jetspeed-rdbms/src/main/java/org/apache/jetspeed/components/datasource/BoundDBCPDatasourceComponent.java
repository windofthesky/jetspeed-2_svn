/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.components.datasource;

import javax.naming.NamingException;

import org.apache.jetspeed.components.jndi.JNDIComponent;


/**
 * Bound DBCP Data Source
 *
 * @author <a href="mailto:sweaver@apache.org">Scott Weaver</a>
 * @version $Id$
 */
public class BoundDBCPDatasourceComponent extends DBCPDatasourceComponent
{
    private JNDIComponent jndi;
    private String bindName;

    /**
     * 
     * @param user
     * @param password
     * @param driverName
     * @param connectURI
     * @param maxActive
     * @param maxWait
     * @param whenExhausted
     * @param autoCommit
     * @param bindName JNDI name to bind this <code>javax.sql.DataSource</code>
     * created by this class to.
     * @param jndi JNDIComponent we will use to bind.
     */
    public BoundDBCPDatasourceComponent(String user, String password, String driverName, String connectURI,
            int maxActive, int maxWait, byte whenExhausted, boolean autoCommit, String bindName, JNDIComponent jndi)
    {
        super(user, password, driverName, connectURI, maxActive, maxWait, whenExhausted, autoCommit);
        if(jndi == null)
        {
            throw new IllegalArgumentException("jndi argument cannot be null for BoundDBCPDatasourceComponent");
        }
        
        if(bindName == null)
        {
            throw new IllegalArgumentException("bindName argument cannot be null for BoundDBCPDatasourceComponent");
        }
        
        this.jndi = jndi;
        this.bindName = bindName;

    }
    /**
     * Same as {@link DBCPDatasourceComponent#start()}
     * but also binds these <code>javax.sql.DataSource</code>
     * created to the <code>bindName</code>.
     * 
     * @see org.picocontainer.Startable#start()
     */
    public void start()
    {        
        super.start();
        try
        {
            jndi.bindObject("comp/env/jdbc/"+bindName, getDatasource());
            jndi.bindObject("jdbc/"+bindName, getDatasource());
        }
        catch (NamingException e)
        {
            IllegalStateException ise = new IllegalStateException("Naming exception "+e.toString());
            ise.initCause(e);
            throw ise;
        }
    }

    /* (non-Javadoc)
     * @see org.picocontainer.Startable#stop()
     */
    public void stop()
    {        
        try
        {
            jndi.unbindObject("comp/env/jdbc/"+bindName);
            jndi.unbindObject("jdbc/" + bindName);
        }
        catch (NamingException e)
        {
             throw new IllegalStateException("Naming exception "+e.toString());
        }
        super.stop();
    }
}
