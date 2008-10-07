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
package org.apache.jetspeed.container.invoker;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.StackObjectPool;

/**
 * AbstractPortletInvokerFactory handles the actual creation fo invokers.
 * It manages the generic object pooling and objection creation and release patterns
 * for all invoker implementations. 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public abstract class AbstractPortletInvokerFactory
    extends BasePoolableObjectFactory
{
    /** The pool of local portlet invokers */ 
    protected ObjectPool pool;
    
    protected String invokerClass = null;    
    protected int invokerPoolSize;
    
    public AbstractPortletInvokerFactory()
    {
    }
    
    /**
     * @param invokerClass
     * @param invokerPoolSize
     */
    public void init(String invokerClass, int invokerPoolSize)
    {
        this.invokerClass = invokerClass;
        this.invokerPoolSize = invokerPoolSize;
        pool = new StackObjectPool(this, invokerPoolSize);        
    }
    
    /**
     * @return
     * @throws Exception
     */
    public JetspeedPortletInvoker getPortletInvoker()
        throws Exception
    {
        return (JetspeedPortletInvoker)pool.borrowObject();
    }

    public void releaseObject(Object object) throws Exception 
    {
        JetspeedPortletInvoker invoker = (JetspeedPortletInvoker)object;
        pool.returnObject(invoker);
    }

    /* (non-Javadoc)
     * @see org.apache.commons.pool.PoolableObjectFactory#makeObject()
     */
    public Object makeObject() throws Exception
    {        
        return Class.forName(invokerClass).newInstance();
    }
    
}
