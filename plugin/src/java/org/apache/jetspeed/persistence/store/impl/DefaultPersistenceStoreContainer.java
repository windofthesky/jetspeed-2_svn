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
package org.apache.jetspeed.persistence.store.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.jetspeed.persistence.store.PersistenceStore;
import org.apache.jetspeed.persistence.store.PersistenceStoreContainer;
import org.apache.jetspeed.persistence.store.PersistenceStoreEvent;
import org.apache.jetspeed.persistence.store.PersistenceStoreEventListener;
import org.apache.jetspeed.persistence.store.PersistenceStoreInitializationException;
import org.apache.jetspeed.persistence.store.PersistenceStoreTypeInitializer;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.defaults.ComponentParameter;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.ConstructorComponentAdapter;
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
public class DefaultPersistenceStoreContainer extends DefaultPicoContainer implements PersistenceStoreContainer, PersistenceStoreEventListener
{
    private Configuration conf;

    /** Holds the current threads PersistenceStore */
    private ThreadLocal TL_store;
    
    private Map storeLastUsed; 

    public DefaultPersistenceStoreContainer(Configuration conf) throws PersistenceStoreInitializationException
    {
        super();
        // get a list of all the stores we want to define
        String[] storeNames = conf.getStringArray("store.name");
        this.conf = conf;

        TL_store = new ThreadLocal();
        
        storeLastUsed = new HashMap();

        // Add the container as a component for callback purposes
        //registerComponentInstance("container", this);

        for (int i = 0; i < storeNames.length; i++)
        {
            String name = storeNames[i];

            // Get the config subset for this store
            Configuration storeConf = conf.subset(name);

            // Register the config to the container
            String storeConfigKey = name + ".config";
            registerComponentInstance(storeConfigKey, storeConf);

            // Get the type initializer for this store's type
            String typeInitializerClassName = storeConf.getString("type.initializer");
            String typeInitializerKey = name + ".type.initializer";

            if (typeInitializerClassName != null)
            {
                PersistenceStoreTypeInitializer typeIniter;
                Class clazz;
                try
                {
                    clazz = Class.forName(typeInitializerClassName);
                    // typeIniter = (PersistenceStoreTypeInitializer) clazz.newInstance();
                    Parameter[] params = new Parameter[] { new ConstantParameter(this), };
                    registerComponentImplementation(typeInitializerKey, clazz, params);

                    // Now wrap it so it is startable
                    // We should be able to use a component Multicaster but
                    // I cant find it in the Pico api anymore :(
                    Parameter[] wrapperParams =
                        new Parameter[] {
                           // new ConstantParameter(TypeInitializerStartableWrapper.class),
                            new ComponentParameter(typeInitializerKey),
                            };
                    registerComponentImplementation(
                        typeInitializerKey + ".wrapper",
                        TypeInitializerStartableWrapper.class,
                        wrapperParams);
                }
                catch (Exception e)
                {
                    throw new PersistenceStoreInitializationException(
                        "Unable to instantiate PersistenceStoreTypeInitilaizer.  " + e.toString(),
                        e);
                }
            }

            // Now register the store type to the container
            String storeClassName = storeConf.getString("classname");
            if (storeClassName == null)
            {
                throw new PersistenceStoreInitializationException("No \"classname\" was defined for PersistenceStore: " + name);
            }

            try
            {
                Class storeClass = Class.forName(storeClassName);
                Parameter[] params = new Parameter[] { new ComponentParameter(storeConfigKey)};
                String storeKey = "store." + name;
                ComponentAdapter adapter = new ConstructorComponentAdapter(storeKey, storeClass, params);
                registerComponent(adapter);
            }
            catch (Exception e)
            {
                throw new PersistenceStoreInitializationException(
                    "Unable to register PersistenceStore to the container: " + e.toString(),
                    e);
            }

        }

    }

    /** 
     * <p>
     * getStore
     * </p>
     * 
     * @see org.apache.jetspeed.persistence.store.PersistenceStoreContainer#getStore(java.lang.String)
     * @param storeId
     * @return
     */
    public PersistenceStore getStore(String storeId)
    {
		PersistenceStore store = (PersistenceStore) getComponentInstance("store." + storeId);
		store.addEventListener(this);
        return store;
    }

    /** 
     * <p>
     * getConfiguration
     * </p>
     * 
     * @see org.apache.jetspeed.persistence.store.PersistenceStoreContainer#getConfiguration()
     * @return
     */
    public Configuration getConfiguration()
    {
        return conf;
    }

    /** 
     * <p>
     * getPersistenceStoreForThread
     * </p>
     * 
     * @see org.apache.jetspeed.persistence.store.PersistenceStoreContainer#getPersistenceStoreForThread()
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
     * @see org.apache.jetspeed.persistence.store.PersistenceStoreEventListener#afterClose(org.apache.jetspeed.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void afterClose(PersistenceStoreEvent event)
    {
    	// If the store being closed is this Threads store, nuke it.
    	if(event.getPersistenceStore().equals(TL_store.get()))
    	{
			TL_store.set(null);
    	}
        

    }

    /** 
     * <p>
     * afterDeletePersistent
     * </p>
     * 
     * @see org.apache.jetspeed.persistence.store.PersistenceStoreEventListener#afterDeletePersistent(org.apache.jetspeed.persistence.store.PersistenceStoreEvent)
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
     * @see org.apache.jetspeed.persistence.store.PersistenceStoreEventListener#afterLookup(org.apache.jetspeed.persistence.store.PersistenceStoreEvent)
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
     * @see org.apache.jetspeed.persistence.store.PersistenceStoreEventListener#afterMakePersistent(org.apache.jetspeed.persistence.store.PersistenceStoreEvent)
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
     * @see org.apache.jetspeed.persistence.store.PersistenceStoreEventListener#beforeClose(org.apache.jetspeed.persistence.store.PersistenceStoreEvent)
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
     * @see org.apache.jetspeed.persistence.store.PersistenceStoreEventListener#beforeDeletePersistent(org.apache.jetspeed.persistence.store.PersistenceStoreEvent)
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
     * @see org.apache.jetspeed.persistence.store.PersistenceStoreEventListener#beforeLookup(org.apache.jetspeed.persistence.store.PersistenceStoreEvent)
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
     * @see org.apache.jetspeed.persistence.store.PersistenceStoreEventListener#beforeMakePersistent(org.apache.jetspeed.persistence.store.PersistenceStoreEvent)
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
     * @see org.apache.jetspeed.persistence.store.TransactionEventListener#afterBegin(org.apache.jetspeed.persistence.store.PersistenceStoreEvent)
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
     * @see org.apache.jetspeed.persistence.store.TransactionEventListener#afterCommit(org.apache.jetspeed.persistence.store.PersistenceStoreEvent)
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
     * @see org.apache.jetspeed.persistence.store.TransactionEventListener#afterRollback(org.apache.jetspeed.persistence.store.PersistenceStoreEvent)
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
     * @see org.apache.jetspeed.persistence.store.TransactionEventListener#beforeBegin(org.apache.jetspeed.persistence.store.PersistenceStoreEvent)
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
     * @see org.apache.jetspeed.persistence.store.TransactionEventListener#beforeCommit(org.apache.jetspeed.persistence.store.PersistenceStoreEvent)
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
     * @see org.apache.jetspeed.persistence.store.TransactionEventListener#beforeRollback(org.apache.jetspeed.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void beforeRollback(PersistenceStoreEvent event)
    {
		storeLastUsed.put(event.getPersistenceStore(), new Date());

    }

}
