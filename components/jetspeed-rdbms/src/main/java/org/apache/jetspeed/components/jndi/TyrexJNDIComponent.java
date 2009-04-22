/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
/**
 * Created on Feb 4, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.components.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tyrex.naming.MemoryContext;
import tyrex.tm.RuntimeContext;

/**
 * <p>
 * TyrexJNDIComponent
 * </p>
 * <p>
 * 
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class TyrexJNDIComponent implements JNDIComponent
{

    private static final Logger log = LoggerFactory.getLogger(TyrexJNDIComponent.class);

    private MemoryContext rootJNDIContext;

    /**
     * @see org.apache.fulcrum.Service#init()
     */
    public TyrexJNDIComponent() throws NamingException
    {
        Context ctx = null;

        // Construct a non-shared memory context
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "tyrex.naming.MemoryContextFactory");
        rootJNDIContext = new MemoryContext(null);
        rootJNDIContext.createSubcontext("jdbc");
        ctx = rootJNDIContext.createSubcontext("comp");
        ctx = ctx.createSubcontext("env");
        ctx = ctx.createSubcontext("jdbc");

        //	Associate the memory context with a new
        //	runtime context and associate the runtime context
        //	with the current thread
        bindToCurrentThread();
        log.info("JNDI successfully initiallized");

    }
	

    /**
     * @see org.apache.jetspeed.cps.jndi.JNDIService#getRootContext()
     */
    public Context getRootContext()
    {
        return rootJNDIContext;
    }

    /**
     * @see org.apache.jetspeed.cps.jndi.JNDIService#bindToCurrentThread()
     */
    public void bindToCurrentThread() throws NamingException
    {
        RuntimeContext runCtx = RuntimeContext.newRuntimeContext(rootJNDIContext, null);
        RuntimeContext.setRuntimeContext(runCtx);
    }
    
    /**
     *  
     * <p>
     * bindObject
     * </p>
     * 
     * @see org.apache.jetspeed.cps.jndi.JNDIComponent#bindObject(java.lang.String, java.lang.Object)
     * @param bindToName
     * @param obj
     * @throws NamingException
     */
	public void bindObject(String bindToName, Object obj) throws NamingException
	{
	    log.debug("Binding "+obj+" to name "+bindToName);
		Context ctx = getRootContext();
		ctx.bind(bindToName, obj);		
	}

    /** 
     * <p>
     * unbindFromCurrentThread
     * </p>
     * 
     * @see org.apache.jetspeed.components.jndi.JNDIComponent#unbindFromCurrentThread()
     * @throws NamingException
     */
    public void unbindFromCurrentThread() throws NamingException
    {
		RuntimeContext.unsetRuntimeContext();
		RuntimeContext.cleanup(Thread.currentThread());		
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.jndi.JNDIComponent#unbindObject(java.lang.String)
     */
    public void unbindObject( String name ) throws NamingException
    {
        log.debug("Unbinding name "+name);
		Context ctx = getRootContext();
		ctx.unbind(name);		

    }
}
