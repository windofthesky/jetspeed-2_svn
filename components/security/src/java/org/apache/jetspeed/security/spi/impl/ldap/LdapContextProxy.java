/*
 * Copyright 2000-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the  "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" 
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package org.apache.jetspeed.security.spi.impl.ldap;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.commons.lang.StringUtils;

/**
 * Proxy providing recoverable LdapContext connections.
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class LdapContextProxy implements InvocationHandler
{
    private Properties env;
    private LdapContext ctx;
    
    public static LdapContext createProxy(LdapBindingConfig config)
    {
        LdapContext proxy = config.getContext();
        
        if ( proxy == null || !(Proxy.getInvocationHandler(proxy) instanceof LdapContextProxy))
        {
            proxy = (LdapContext)Proxy.newProxyInstance(LdapContext.class.getClassLoader(),new Class[]{LdapContext.class}, new LdapContextProxy(config));
            config.setContext(proxy);
        }
        return proxy;
    }
    
    private LdapContextProxy(LdapBindingConfig ldapBindingConfig)
    {
        env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, ldapBindingConfig.getInitialContextFactory());
        env.put(Context.PROVIDER_URL, ldapBindingConfig.getLdapScheme() + "://" + ldapBindingConfig.getLdapServerName() + ":"
                + ldapBindingConfig.getLdapServerPort() + "/" + ldapBindingConfig.getRootContext());
        env.put(Context.SECURITY_PRINCIPAL, ldapBindingConfig.getRootDn());
        env.put(Context.SECURITY_CREDENTIALS, ldapBindingConfig.getRootPassword());
        env.put(Context.SECURITY_AUTHENTICATION, ldapBindingConfig.getLdapSecurityLevel());
        if ( !StringUtils.isEmpty(ldapBindingConfig.getLdapSecurityProtocol()) )
        {
            env.put(Context.SECURITY_PROTOCOL, ldapBindingConfig.getLdapSecurityProtocol());
        }
        if ( !StringUtils.isEmpty(ldapBindingConfig.getLdapSocketFactory()) )
        {
            env.put("java.naming.ldap.factory.socket", ldapBindingConfig.getLdapSocketFactory());
        }
    }
    
    private LdapContext getCtx() throws NamingException
    {
        if ( ctx == null )
        {
            ctx = new InitialLdapContext(env, null);
        }
        return ctx;
    }
    
    private void closeCtx()
    {
        if ( ctx != null )
        {
            try
            {
                ctx.close();
            }
            catch (Exception e)
            {
            }
            ctx = null;
        }
    }
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
     *      java.lang.reflect.Method, java.lang.Object[])
     */
    public synchronized Object invoke(Object proxy, Method m, Object[] args) throws Throwable
    {
        Object result = null;
        boolean close = "close".equals(m.getName()) && args.length == 0;
        if ( close && ctx == null )
        {
            // don't need to do anything
            ;
        }
        else
        {
            LdapContext ctx = getCtx();
            
            try
            {
                result = m.invoke(ctx,args);
                if ( close )
                {
                    closeCtx();
                }
            }
            catch (Throwable t)
            {
                closeCtx();
                
                if ( t instanceof InvocationTargetException)
                {
                    t = ((InvocationTargetException)t).getTargetException();
                }
                if (t instanceof ServiceUnavailableException || t instanceof CommunicationException)
                {
                    try
                    {
                        ctx = getCtx();
                        result = m.invoke(ctx,args);
                    }
                    catch (Throwable t2)
                    {
                        closeCtx();
                        if ( t2 instanceof InvocationTargetException)
                        {
                            t2 = ((InvocationTargetException)t2).getTargetException();
                        }
                        
                        throw t2;
                    }
                }
                throw t;
            }
        }
        return result;
    }
}
