/**
 * Created on Feb 4, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.cps.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.InitializationException;
import org.apache.jetspeed.cps.BaseCommonService;

import tyrex.naming.MemoryContext;
import tyrex.tm.RuntimeContext;

/**
 * <p>
 * TyrexJNDIService
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class TyrexJNDIService extends BaseCommonService implements JNDIService
{

    private static final Log log = LogFactory.getLog(TyrexJNDIService.class);

	private MemoryContext rootJNDIContext;

    /**
     * @see org.apache.fulcrum.Service#init()
     */
    public void init() throws InitializationException
    {
        Context ctx = null;
       

        try
        {
            //		Construct a non-shared memory context
            Hashtable env = new Hashtable();
            env.put( Context.INITIAL_CONTEXT_FACTORY, "tyrex.naming.MemoryContextFactory" );
            rootJNDIContext = new MemoryContext(null);
            ctx = rootJNDIContext.createSubcontext("comp");
            ctx = ctx.createSubcontext("env");
            ctx = ctx.createSubcontext("jdbc");
            
            //		Associate the memory context with a new
            //		runtime context and associate the runtime context
            //		with the current thread
			bindToCurrentThread();
            setInit(true);
            log.info("JNDI successfully initiallized");
        }
        catch (Exception e)
        {
            String msg = "Unable to initialize JNDI: "+e.toString();
            log.error(msg, e);
            throw new InitializationException(msg, e);
        }

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

}
