/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package org.apache.jetspeed.components.persistence.store.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer;
import org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent;
import org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * <p>
 * DefaultPersistenceStoreContainer
 * </p>
 * 
 * 
 * @
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $ $
 *
 */
public class DefaultPersistenceStoreContainer
    extends DefaultPicoContainer
    implements PersistenceStoreContainer, PersistenceStoreEventListener
{
    /** Holds the current threads PersistenceStore */
    private ThreadLocal TL_store;

    private Map storeLastUsed;
    
    private int storeTTL;
    
    private int checkInterval;

    private static final Log log = LogFactory.getLog(DefaultPersistenceStoreContainer.class);

    /** 
     * <p>
     * getStore
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer#getStore(java.lang.String)
     * @param storeId
     * @return
     */
    public PersistenceStore getStore(String storeId)
    {
        PersistenceStore store;
        try
        {
            store = (PersistenceStore) getComponentInstance(storeId);
            store.addEventListener(this);
            return store;
        }
        catch (Throwable e)
        {            
            e.printStackTrace();
            log.error(e.toString(), e);
            throw new IllegalStateException(e.toString());
        }

    }

    /** 
     * <p>
     * getPersistenceStoreForThread
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer#getPersistenceStoreForThread()
     * @return
     */
    public PersistenceStore getStoreForThread(String storeId)
    {
        // Retreive a map of stores for this thread
        Map stores = (Map) TL_store.get();
        if (stores == null)
        {
            stores = new HashMap();
            TL_store.set(stores);
            PersistenceStore store = getStore(storeId);
            if (store != null)
            {
                stores.put(storeId, store);
            }

            return store;
        }
        else
        {
            PersistenceStore store = (PersistenceStore) stores.get(storeId);
            if (store != null)
            {
                return store;
            }

            store = getStore(storeId);
            if (store != null)
            {
                stores.put(storeId, store);
                return store;
            }
        }

        // no store defined for storeId
        return null;

    }

    /** 
     * <p>
     * afterClose
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener#afterClose(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void afterClose(PersistenceStoreEvent event)
    {
        // If the store being closed is this Threads store, nuke it.
        if (event.getPersistenceStore().equals(TL_store.get()))
        {
            TL_store.set(null);
            // Remove the closed store from the check list of active Stores
            storeLastUsed.remove(event.getPersistenceStore());
        }

    }

    /** 
     * <p>
     * afterDeletePersistent
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener#afterDeletePersistent(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void afterDeletePersistent(PersistenceStoreEvent event)
    {
        // TODO Auto-generated method stub

    }

    /** 
     * <p>
     * afterLookup
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener#afterLookup(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void afterLookup(PersistenceStoreEvent event)
    {
        // TODO Auto-generated method stub

    }

    /** 
     * <p>
     * afterMakePersistent
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener#afterMakePersistent(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void afterMakePersistent(PersistenceStoreEvent event)
    {
        // TODO Auto-generated method stub

    }

    /** 
     * <p>
     * beforeClose
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener#beforeClose(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void beforeClose(PersistenceStoreEvent event)
    {
        // TODO Auto-generated method stub

    }

    /** 
     * <p>
     * beforeDeletePersistent
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener#beforeDeletePersistent(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void beforeDeletePersistent(PersistenceStoreEvent event)
    {
        storeLastUsed.put(event.getPersistenceStore(), new Date());

    }

    /** 
     * <p>
     * beforeLookup
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener#beforeLookup(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void beforeLookup(PersistenceStoreEvent event)
    {
        storeLastUsed.put(event.getPersistenceStore(), new Date());

    }

    /** 
     * <p>
     * beforeMakePersistent
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener#beforeMakePersistent(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void beforeMakePersistent(PersistenceStoreEvent event)
    {
        storeLastUsed.put(event.getPersistenceStore(), new Date());

    }

    /** 
     * <p>
     * afterBegin
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.TransactionEventListener#afterBegin(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void afterBegin(PersistenceStoreEvent event)
    {
        // TODO Auto-generated method stub

    }

    /** 
     * <p>
     * afterCommit
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.TransactionEventListener#afterCommit(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void afterCommit(PersistenceStoreEvent event)
    {
        // TODO Auto-generated method stub

    }

    /** 
     * <p>
     * afterRollback
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.TransactionEventListener#afterRollback(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void afterRollback(PersistenceStoreEvent event)
    {
        // TODO Auto-generated method stub

    }

    /** 
     * <p>
     * beforeBegin
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.TransactionEventListener#beforeBegin(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void beforeBegin(PersistenceStoreEvent event)
    {
        storeLastUsed.put(event.getPersistenceStore(), new Date());

    }

    /** 
     * <p>
     * beforeCommit
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.TransactionEventListener#beforeCommit(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void beforeCommit(PersistenceStoreEvent event)
    {
        storeLastUsed.put(event.getPersistenceStore(), new Date());

    }

    /** 
     * <p>
     * beforeRollback
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.TransactionEventListener#beforeRollback(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void beforeRollback(PersistenceStoreEvent event)
    {
        storeLastUsed.put(event.getPersistenceStore(), new Date());
    }

    protected class InactivityMonitor extends Thread
    {
        int ttl;
        int checkInterval;
        boolean started = true;

        protected InactivityMonitor(int ttl, int checkInterval)
        {
            this.ttl = ttl;
            this.checkInterval = checkInterval;
            setName("PersistenceStore inactivity monitor [TTL:" + ttl + "] [interval:" + checkInterval + "]");
        }

        /**
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            while (started)
            {
                
                try
                {
                    Iterator keys = storeLastUsed.keySet().iterator();
                    while (keys.hasNext())
                    {
                        PersistenceStore store = (PersistenceStore) keys.next();
                        Date last = (Date) storeLastUsed.get(store);
                        Date now = new Date();
                        if ((now.getTime() - last.getTime()) > ttl)
                        {
                            log.debug("PersistenceStore " + store + " has exceeded its TTL, attemting to close.");
                            // broker should now be considered available
                            try
                            {
                                store.close();
                                storeLastUsed.remove(store);
                                log.debug("PersistenceStore successfully closed.");
                            }
                            catch (Throwable e1)
                            {
                                log.error("Unable to close PersistenceStore " + store, e1);
                            }
                        }
                    }
                    sleep(checkInterval);
                }
                catch (Exception e)
                {
                }
            }
        }

        public void safeStop()
        {
            started = false;
        }

    }

    /**
     * @see org.picocontainer.Startable#start()
     */
    public void start()
    {
        super.start();
        
        storeLastUsed = new HashMap();
        //		default to 15 seconds of inactivity, after which the broker is recalimed to the pool
        
        log.info("PersistenceStore Time To Live set to " + storeTTL);
                log.info("PersistenceStore will be checked for inactivity every " + (checkInterval / 1000) + " seconds.");

        InactivityMonitor monitor = new InactivityMonitor(storeTTL, checkInterval);
        monitor.setDaemon(true);
        monitor.setPriority(Thread.MIN_PRIORITY);
       // monitor.setContextClassLoader(Thread.currentThread().getContextClassLoader());
        monitor.start();
    }

	/**
	 * 
	 */
	public DefaultPersistenceStoreContainer(int storeTTL, int checkInterval) {
		super();
		TL_store = new ThreadLocal();
		this.storeTTL  = storeTTL;
		this.checkInterval = checkInterval;
		
	}
	/**
	 * @param arg0
	 */
	public DefaultPersistenceStoreContainer(ComponentAdapterFactory arg0, int storeTTL, int checkInterval) {
		super(arg0);
		TL_store = new ThreadLocal();
		this.storeTTL  = storeTTL;
		this.checkInterval = checkInterval;
	}
	/**
	 * @param arg0
	 * @param arg1
	 */
	public DefaultPersistenceStoreContainer(ComponentAdapterFactory arg0,
			PicoContainer arg1, int storeTTL, int checkInterval) {
		super(arg0, arg1);
		TL_store = new ThreadLocal();
		this.storeTTL  = storeTTL;
		this.checkInterval = checkInterval;
	}
	/**
	 * @param arg0
	 */
	public DefaultPersistenceStoreContainer(PicoContainer arg0, int storeTTL, int checkInterval) {
		super(arg0);
		TL_store = new ThreadLocal();
		this.storeTTL  = storeTTL;
		this.checkInterval = checkInterval;
	}
}
