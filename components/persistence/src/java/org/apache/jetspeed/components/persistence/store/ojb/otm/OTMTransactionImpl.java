/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.components.persistence.store.ojb.otm;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.Transaction;
import org.apache.jetspeed.components.persistence.store.TransactionEventListener;
import org.apache.jetspeed.components.persistence.store.impl.TransactionEventInvoker;
import org.apache.ojb.otm.OTMConnection;
import org.apache.ojb.otm.OTMKit;

/**
 * <p>
 * OTMTransactionImpl
 * </p>
 * 
 *  @
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $ $
 *  
 */
public class OTMTransactionImpl implements Transaction
{
    private OTMConnection conn;
    private OTMKit kit;
    private List eventListeners;
    private TransactionEventInvoker invoker;
    private OTMStoreImpl store;
    private static final Log log = LogFactory.getLog(OTMTransactionImpl.class);
    public OTMTransactionImpl(OTMConnection conn, OTMKit kit, PersistenceStore store)
    {
        if (conn == null)
        {
            throw new IllegalArgumentException("The OTM Connection cannot be null.");
        }
        
        if (kit == null)
        {
            throw new IllegalArgumentException("The OTM kit cannot be null.");
        }
        
        if (store == null)
        {
            throw new IllegalArgumentException("The PersistenceStore cannot be null.");
        }
        
        eventListeners = new ArrayList();
        this.store = (OTMStoreImpl)store;
        this.kit = kit;
        this.conn = conn;
        invoker = new TransactionEventInvoker(eventListeners, store);
    }

    /**
     * <p>
     * begin
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.Transaction#begin()
     *  
     */
    public void begin()
    {
        org.apache.ojb.otm.core.Transaction OTMTx = kit.getTransaction(conn);
        if (!OTMTx.isInProgress())
        {
            invoker.beforeBegin();
            OTMTx.begin();
            invoker.afterBegin();
        }
    }

    /**
     * <p>
     * commit
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.Transaction#commit()
     *  
     */
    public void commit()
    {
        org.apache.ojb.otm.core.Transaction OTMTx = kit.getTransaction(conn);
        invoker.beforeCommit();
        
        OTMTx.commit();
        store.setTransaction(null);
        OTMTx = null;
        invoker.afterCommit();
    }

    /**
     * <p>
     * rollback
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.Transaction#rollback()
     *  
     */
    public void rollback()
    {
        org.apache.ojb.otm.core.Transaction OTMTx = kit.getTransaction(conn);
        if (OTMTx != null && OTMTx.isInProgress())
        {
            invoker.beforeRollback();
            OTMTx.rollback();
            invoker.afterRollback();
        }
        else
        {
            log.warn("OTM Transaction was NOT rolled back because no transaction was in progress");
        }
    }

    /**
     * <p>
     * isOpen
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.Transaction#isOpen()
     * @return
     */
    public boolean isOpen()
    {
        org.apache.ojb.otm.core.Transaction OTMTx = kit.getTransaction(conn);
        return OTMTx.isInProgress();
    }

    /**
     * <p>
     * getWrappedTransaction
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.Transaction#getWrappedTransaction()
     * @return
     */
    public Object getWrappedTransaction()
    {
        org.apache.ojb.otm.core.Transaction OTMTx = kit.getTransaction(conn);
        return OTMTx;
    }

    /**
     * <p>
     * addEventListener
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.Transaction#addEventListener(org.apache.jetspeed.components.persistence.store.TransactionEventListener)
     * @param listener
     */
    public void addEventListener(TransactionEventListener listener)
    {
        eventListeners.add(listener);
    }

    /**
     * <p>
     * checkpoint
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.Transaction#checkpoint()
     *  
     */
    public void checkpoint()
    {
        org.apache.ojb.otm.core.Transaction OTMTx = kit.getTransaction(conn);
        OTMTx.checkpoint();
    }
}
