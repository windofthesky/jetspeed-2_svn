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
package org.apache.jetspeed.components.datasource;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.picocontainer.Startable;

/**
 * <p>
 * DBCPDatasourceComponent
 * </p>
 * 
 * 
 * @
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $ $
 *
 */
public class DBCPDatasourceComponent implements DatasourceComponent, Startable
{

    private static final Log log = LogFactory.getLog(DBCPDatasourceComponent.class);

    protected PoolingDataSource dataSource;
    
    private String user;
    
    private String password;
    
    private String driverName;
    
    private String connectURI;    
    
    private int maxActive;
    
    private int maxWait;
    
    private byte whenExhausted;
    
    private boolean autoCommit;

    private PoolableConnectionFactory dsConnectionFactory;
    /**
     * 
     * Creates a simple commons DBCP connection pool using the following
     * parameters.
     * <p>
     * If you need to bind the datasource of this component to 
     * JNDI please @see org.apache.jetspeed.components.jndi.JNDIComponent
     * </p>
     * 
     * @param user User name that will be used to connect to the DB
     * @param password Password that will be used to connect to the DB
     * @param driverName Fully qualified driver to be used by the connection pool
     * @param connectURI Fully qualified URI to the DB.
     * @param maxActive Maximum active connection
     * @param maxWait if <code>whenExhausted</code> is set to GenericObjectPool.WHEN_EXHAUSTED_BLOCK
     * the length of time to block while waiting for a connection to become 
     * available.
     * @param whenExhausted GenericObjectPool.WHEN_EXHAUSTED_BLOCK, GenericObjectPool.WHEN_EXHAUSTED_GROW or
     * GenericObjectPool.WHEN_EXHAUSTED_FAIL. @see org.apache.commons.pooling.GenericObjectPool
     * for more information on these settings
     * @param autoCommit Whether or not this datasource will autocommit
     * @throws ClassNotFoundException If the <code>driverName</code> could not be
     * located within any classloaders.
     */
    public DBCPDatasourceComponent(
        String user,
        String password,
        String driverName,
        String connectURI,
        int maxActive,
        int maxWait,
        byte whenExhausted,
        boolean autoCommit)
        
    {

        log.info("Setting up data source pooling for " + driverName);

        log.info("Max active connnections set to: " + maxActive);

        log.info("Pool is set to \"" + whenExhausted + "\" when all connections are exhausted.");
		
		this.user = user;
		this.password = password;
		this.driverName = driverName;
		this.connectURI = connectURI;
		this.maxActive = maxActive;
		this.maxWait = maxWait;
		this.autoCommit = autoCommit;
    }

    /**
     * 
     * <p>
     * getDatasource
     * </p>
     * 
     * <p>
     *   returns the datasource created by this component
     * </p>
     * @return
     *
     */
    public DataSource getDatasource()
    {
        return dataSource;
    }

    /** 
     * <p>
     * start
     * </p>
     * 
     * @see org.picocontainer.Startable#start()
     * 
     */
    public void start()
    {

        try
        {
            log.info("Attempting to start DBCPCDatasourceComponent.");
            Class.forName(driverName);
            
            ObjectPool connectionPool = new GenericObjectPool(null, maxActive, whenExhausted, maxWait);
            
            ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectURI, user, password);
            
            dsConnectionFactory = new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);
            
            dataSource = new PoolingDataSource(connectionPool);
            
            log.info("DBCPCDatasourceComponent successfuly started!");
        }
        catch (Throwable e)
        {
            
            String msg = "Unable to start DBCPCDatasourceComponent: "+e.toString();
            log.error(msg, e);
            throw new IllegalStateException(msg);
        }
    }

    /** 
     * <p>
     * stop
     * </p>
     * 
     * @see org.picocontainer.Startable#stop()
     * 
     */
    public void stop()
    {
        try
        {
            dsConnectionFactory.getPool().close();
        }
        catch (Exception e)
        {
            IllegalStateException ise =
                new IllegalStateException("Unable to sfaely shutdown the DBCPConnection pool: " + e.toString());
            ise.initCause(e);
        }

    }

}
