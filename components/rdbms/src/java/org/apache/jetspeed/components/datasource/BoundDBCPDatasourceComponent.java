/*
 * Created on Feb 27, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components.datasource;

import javax.naming.NamingException;

import org.apache.jetspeed.components.jndi.JNDIComponent;


/**
 * @author Sweaver
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class BoundDBCPDatasourceComponent extends DBCPDatasourceComponent
{
    private JNDIComponent jndi;
    private String bindName;

    
    /* (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable
    {
        stop();
        super.finalize();
    }
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
        }
        catch (NamingException e)
        {
             throw new IllegalStateException("Naming exception "+e.toString());
        }
        super.stop();
    }
}
