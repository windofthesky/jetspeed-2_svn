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
package org.apache.jetspeed.persistence.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.InitializationException;
import org.apache.jetspeed.persistence.*;
import org.apache.jetspeed.persistence.LookupCriteria;
import org.apache.jetspeed.persistence.ODMGPersistencePlugin;
import org.apache.jetspeed.persistence.PersistenceService;
import org.apache.jetspeed.services.plugin.PluginConfiguration;


import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.odmg.OJB;
import org.apache.ojb.odmg.TransactionImpl;
import org.odmg.Database;
import org.odmg.Implementation;
import org.odmg.ODMGException;
import org.odmg.OQLQuery;
import org.odmg.Transaction;

/**
 * This is a implementation of  <code>PersistencePlugin</code> 
 * that is backed by <a href="http://db.apache.org/ojb">ObjectRelationalBridge (OJB)</a>
 * that uses ODMG to support its transactions.
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 */
public class OJBODMGPersistencePlugin extends AbstractOJBPersistencePlugin implements ODMGPersistencePlugin
{    

	private static final Log log = LogFactory.getLog(OJBODMGPersistencePlugin.class);

	private String overrideDefaultJcd;
	private PluginConfiguration configuration;
	private String persistenceApi;
	private Implementation odmg;
	private Database db;
	private PersistenceService ps;
	
    private ThreadLocal TLtx;
    /**
     * @see com.rippe.essential.agora.services.entity.AgoraEntityService#beginTransaction()
     */
    public void beginTransaction() throws TransactionStateException
    {
    	super.beginTransaction();
        if (TLtx == null || TLtx.get() == null)
        {
            if (TLtx == null)
            {
                TLtx = new ThreadLocal();
            }
            Transaction tx = null;

            try
            {
                tx = odmg.newTransaction();
                tx.begin();
                TLtx.set(tx);
            }
            catch (Exception e)
            {
                if (tx != null)
                {
                    tx.abort();

                }

                log.fatal("beginTransaction() failed.", e);
                throw new TransactionStateException("beginTransaction() failed.", e);
            }
        }
    }
    /**
     * @see com.rippe.essential.agora.services.entity.AgoraEntityService#commitTransaction(java.lang.Object)
     */
    public void commitTransaction() throws TransactionStateException
    {
        Transaction tx = (Transaction) TLtx.get();
        try
        {

            if (tx == null)
            {
                throw new TransactionStateException("Transaction is null for this thread.");
            }

            if (!tx.isOpen())
            {
                throw new TransactionStateException(tx + " is not in open.");
            }

            tx.commit();
			super.commitTransaction();
        }
        catch (Exception e)
        {
            log.error("Unable to commit transaction " + e.toString(), e);
            tx.abort();
            throw new TransactionStateException("Unable to commit transaction " + e.toString(), e);
        }
        finally
        {
            if (tx != null)
            {
                TLtx.set(null);
            }            
        }

    }
    public void prepareForDelete(Object obj) throws TransactionStateException
    {
        Transaction tx = null;
        if (TLtx == null || TLtx.get() == null)
        {
            throw new TransactionStateException("You can not mark objects for delete before a Transaction has been started.");
        }
        else
        {
            try
            {
                tx = (Transaction) TLtx.get();
                tx.lock(obj, Transaction.WRITE);
                db.deletePersistent(obj);
            }
            catch (Exception e)
            {
                throw new TransactionStateException("Unable to set object for deletion " + e.toString(), e);
            }
        }

    }
    /**
     * @see com.rippe.essential.agora.services.entity.AgoraEntityService#update(java.lang.Object)
     */
    public void prepareForUpdate(Object obj) throws TransactionStateException
    {
        Transaction tx = null;
        if (TLtx == null || TLtx.get() == null)
        {
            throw new TransactionStateException("You can not mark objects for update before a Transaction has been started.");
        }
        else
        {
            try
            {
                tx = (Transaction) TLtx.get();
                tx.lock(obj, Transaction.WRITE);
            }
            catch (Exception e)
            {
                throw new TransactionStateException("Unable to set object for update " + e.toString(), e);
            }
        }

    }
    /**
     * @see com.rippe.essential.agora.services.entity.AgoraEntityService#rollbackTranaction(java.lang.Object)
     */
    public void rollbackTransaction() throws TransactionStateException
    {
        Transaction tx = (Transaction) TLtx.get();
        try
        {
            if (tx != null && tx.isOpen())
            {
                tx.abort();
                TLtx.set(null);
            }
            else
            {
                log.warn("Unable to rollback null or not-in-progess connection");
            }

        }
        catch (Exception e)
        {
            if (e instanceof TransactionStateException)
            {
                throw (TransactionStateException) e;
            }
            throw new TransactionStateException("Unable to rollback transaction " + tx + " " + e.toString(), e);
        }
        finally
        {
        	super.rollbackTransaction();
        }
    }

    protected void initODMG() throws InitializationException
    {
        PersistenceBroker broker = null;
        try
        {
            odmg = OJB.getInstance();
            db = odmg.newDatabase();
            broker = getBroker();

            String repoFileName = broker.serviceConnectionManager().getConnectionDescriptor().getJcdAlias();
            db.open(repoFileName, Database.OPEN_READ_WRITE);
            log.info("ODMG initialized.");
        }
        catch (ODMGException e)
        {
            String message = "Failed to initialize ODMG api";
            log.error(message, e);
            throw new InitializationException(message, e);
        }
  

    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PersistencePlugin#newLookupCriteria()
     */
    public LookupCriteria newLookupCriteria()
    {
        return new OjbLookupCriteria();
    }

    /**
     * @see org.apache.jetspeed.services.perisistence.impl.AbstractOJBPersistencePlugin#postInit()
     */
    protected void postInit() throws InitializationException
    {
        initODMG();
    }

    /**
     * @see org.apache.jetspeed.services.persistence.ODMGPersistencePlugin#newODMGTransaction()
     */
    public Transaction newODMGTransaction()
    {
        return odmg.newTransaction();
    }

    /**
     * @see org.apache.jetspeed.services.persistence.ODMGPersistencePlugin#newOQLQuery()
     */
    public OQLQuery newOQLQuery()
    {
        return odmg.newOQLQuery();
    }

    /**
     * @see org.apache.jetspeed.persistence.PersistencePlugin#makeObjectConsistent(java.lang.Object)
     */
    public Object markDirty(Object obj) throws TransactionStateException
    {
        TransactionImpl ojbTx = (TransactionImpl) TLtx.get();
        if(ojbTx != null && ojbTx.isOpen() )
        {
        	ojbTx.markDirty(obj);
        	return obj;
        }
        else
        {
        	throw new  TransactionStateException("No transaction in progress");
        }
        
    }

    /**
     * @see org.apache.jetspeed.persistence.PersistencePlugin#makePersistent(java.lang.Object)
     */
    public void makePersistent(Object obj) throws TransactionStateException
    {
		Transaction tx = null;
		if (TLtx == null || TLtx.get() == null)
		{
			throw new TransactionStateException("You can not make objects persistent before a Transaction has been started.");
		}
		else
		{
			try
			{
				tx = (Transaction) TLtx.get();
				// db.makePersistent(obj);
				tx.lock(obj, Transaction.WRITE);
			}
			catch (Exception e)
			{
				throw new TransactionStateException("Unable to make object persistent " + e.toString(), e);
			}
		}
        

    }

}
