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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

    private static final Log log = LogFactory.getLog(TyrexJNDIComponent.class);

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

}
