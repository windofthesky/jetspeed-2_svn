/**
 * Created on Feb 4, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.cps.jndi;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.fulcrum.InitializationException;
import org.apache.jetspeed.cps.BaseCommonService;
import org.apache.jetspeed.cps.components.jndi.*;

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
    protected JNDIComponent jndiComponent;

    /**
     * @see org.apache.fulcrum.Service#init()
     */
    public void init() throws InitializationException
    {
        if (!isInitialized())
        {

            try
            {
                jndiComponent = new TyrexJNDIComponent();
                jndiComponent.bindToCurrentThread();
                setInit(true);
            }
            catch (NamingException e)
            {
                throw new InitializationException("jndi naming exception " + e.toString(), e);
            }
        }
    }

    /**
     * @see org.apache.jetspeed.cps.jndi.JNDIService#getRootContext()
     */
    public Context getRootContext()
    {
        return jndiComponent.getRootContext();
    }

    /**
     * @see org.apache.jetspeed.cps.jndi.JNDIService#bindToCurrentThread()
     */
    public void bindToCurrentThread() throws NamingException
    {
        jndiComponent.bindToCurrentThread();
    }

    /** 
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
        jndiComponent.bindObject(bindToName, obj);

    }

    /** 
     * <p>
     * unbindFromCurrentThread
     * </p>
     * 
     * @see org.apache.jetspeed.cps.components.jndi.JNDIComponent#unbindFromCurrentThread()
     * @throws NamingException
     */
    public void unbindFromCurrentThread() throws NamingException
    {
        jndiComponent.unbindFromCurrentThread();

    }

}
