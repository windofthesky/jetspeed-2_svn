/**
 * Created on Jan 21, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.persistence.impl;

import org.apache.fulcrum.InitializationException;

import org.apache.jetspeed.persistence.PersistencePlugin;
import org.apache.jetspeed.persistence.TransactionStateException;
import org.apache.ojb.broker.Identity;
import org.apache.ojb.broker.PBKey;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerFactory;
import org.apache.ojb.otm.OTMConnection;
import org.apache.ojb.otm.core.Transaction;
import org.apache.ojb.otm.core.TransactionException;
import org.apache.ojb.otm.kit.SimpleKit;
import org.apache.ojb.otm.lock.LockingException;
import org.apache.ojb.otm.states.State;

/**
 * <p>
 * OTMOJBPersistencePlugin
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class OTMOJBPersistencePlugin extends AbstractOJBPersistencePlugin implements PersistencePlugin
{

    protected SimpleKit kit = SimpleKit.getInstance();

    protected PBKey pbKey;
    protected final ThreadLocal TLconn = new ThreadLocal();

    /**
     * @see org.apache.jetspeed.persistence.PersistencePlugin#beginTransaction()
     */
    public void beginTransaction() throws TransactionStateException
    {
        kit.getTransaction(getOTMConnection()).begin();
    }

    /**
     * @see org.apache.jetspeed.persistence.PersistencePlugin#commitTransaction()
     */
    public void commitTransaction() throws TransactionStateException
    {
        try
        {
            if (!kit.getTransaction(getOTMConnection()).isInProgress())
            {
                throw new TransactionStateException("Transaction is not progress, cannot commit.");
            }
            kit.getTransaction(getOTMConnection()).commit();
        }
        finally
        {
        	close();
        }
      

    }

    /**
     * @see org.apache.jetspeed.persistence.PersistencePlugin#markDirty(java.lang.Object)
     */
    public Object markDirty(Object obj) throws TransactionStateException
    {
        OTMConnection conn = getOTMConnection();
        Identity id = conn.getIdentity(obj);
        try
        {
            State state = conn.getEditingContext().lookupState(id);
            state.markDirty();
        }
        catch (LockingException e)
        {
            throw new TransactionStateException(
                "Unable to lock object " + obj.getClass().toString() + " to invalidate." + e.toString(),
                e);
        }
        return obj;		
    }

    /**
     * @see org.apache.jetspeed.persistence.PersistencePlugin#prepareForDelete(java.lang.Object)
     */
    public void prepareForDelete(Object obj) throws TransactionStateException
    {
		OTMConnection conn = getOTMConnection();
        if (!kit.getTransaction(conn).isInProgress())
        {
            throw new TransactionStateException("Transaction is not progress, cannot delete.");
        }
        try
        {
			conn.deletePersistent(obj);
        }
        catch (LockingException e)
        {
            throw new TransactionStateException(
                "Unable to lock object " + obj.getClass().toString() + " to delete." + e.toString(),
                e);
        }

    }

    /**
     * @see org.apache.jetspeed.persistence.PersistencePlugin#prepareForUpdate(java.lang.Object)
     */
    public void prepareForUpdate(Object obj) throws TransactionStateException
    {
        OTMConnection conn = getOTMConnection();
        if (!kit.getTransaction(conn).isInProgress())
        {
            throw new TransactionStateException("Transaction is not progress, cannot update.");
        }
        try
        {
            conn.lockForWrite(obj);

        }
        catch (LockingException e)
        {
            throw new TransactionStateException(
                "Unable to lock object " + obj.getClass().toString() + " to delete." + e.toString(),
                e);
        }

    }

    /**
     * @see org.apache.jetspeed.persistence.PersistencePlugin#rollbackTransaction()
     */
    public void rollbackTransaction() throws TransactionStateException
    {
       try
        {
             if (!kit.getTransaction(getOTMConnection()).isInProgress())
                {
                    throw new TransactionStateException("Transaction is not progress, cannot rollback.");
                }
                kit.getTransaction(getOTMConnection()).rollback();
        }
       finally
       {
       		close();
       }
     

    }

    /**
     * @see org.apache.jetspeed.persistence.impl.AbstractOJBPersistencePlugin#postInit()
     */
    protected void postInit() throws InitializationException
    {
        if (overrideDefaultJcd != null)
        {
            pbKey = new PBKey(overrideDefaultJcd);
        }
        else
        {
            pbKey = PersistenceBrokerFactory.getDefaultKey();
        }

    }

    protected OTMConnection getOTMConnection()
    {
        OTMConnection conn = (OTMConnection) TLconn.get();
        if (conn == null || conn.isClosed())
        {
            conn = kit.acquireConnection(pbKey);
            TLconn.set(conn);
        }
        return conn;		
    }

    /**
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable
    {
        close();

        super.finalize();
    }
	
	/**
	 * Makes sure the OTM Connection has been closed properly
	 *
	 */
    protected void close()
    {
        OTMConnection conn = (OTMConnection) TLconn.get();
        if (conn != null)
        {
            conn.close();
            conn = null;
            TLconn.set(null);
        }
    }

    /**
     * @see org.apache.jetspeed.persistence.PersistencePlugin#makePersistent(java.lang.Object)
     */
    public void makePersistent(Object obj) throws TransactionStateException
    {
        try
        {
            getOTMConnection().makePersistent(obj);
        }
        catch (LockingException e)
        {
           throw new TransactionStateException("Unable to lock "+obj.getClass().getName());
        }

    }

}
