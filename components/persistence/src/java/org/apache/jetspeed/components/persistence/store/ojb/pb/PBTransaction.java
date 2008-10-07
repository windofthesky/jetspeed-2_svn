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
package org.apache.jetspeed.components.persistence.store.ojb.pb;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.jetspeed.components.persistence.store.Transaction;
import org.apache.jetspeed.components.persistence.store.TransactionEventListener;
import org.apache.jetspeed.components.persistence.store.impl.TransactionEventInvoker;
public class PBTransaction implements Transaction
{

    private List eventListeners;
    private TransactionEventInvoker invoker;
    
    private PBStore store;

    public PBTransaction(PBStore store)
    {
        
        this.store = store;
        eventListeners = new ArrayList();
        invoker = new TransactionEventInvoker(eventListeners, store);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.components.persistence.store.Transaction#begin()
     */
    public void begin()
    {        
        if (!store.getBroker().isInTransaction())
        {
            invoker.beforeBegin();
            store.getBroker().beginTransaction();
            invoker.afterBegin();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.components.persistence.store.Transaction#commit()
     */
    public void commit()
    {        
        invoker.beforeCommit();
        Iterator itr = store.toBeStored.iterator();
        while(itr.hasNext())
        {
            store.store(itr.next());
        }
        store.getBroker().commitTransaction();
        invoker.afterCommit();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.components.persistence.store.Transaction#rollback()
     */
    public void rollback()
    {        
        invoker.beforeRollback();
        store.getBroker().abortTransaction();
        invoker.afterRollback();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.components.persistence.store.Transaction#checkpoint()
     */
    public void checkpoint()
    {
        store.checkBroker();
        Iterator itr = store.toBeStored.iterator();
        while(itr.hasNext())
        {
            store.store(itr.next());
        }
       
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.components.persistence.store.Transaction#isOpen()
     */
    public boolean isOpen()
    {        
        return store.getBroker().isInTransaction();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.components.persistence.store.Transaction#getWrappedTransaction()
     */
    public Object getWrappedTransaction()
    {
        return store.getBroker();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.components.persistence.store.Transaction#addEventListener(org.apache.jetspeed.components.persistence.store.TransactionEventListener)
     */
    public void addEventListener(TransactionEventListener listener)
    {
        eventListeners.add(listener);
    }
}
