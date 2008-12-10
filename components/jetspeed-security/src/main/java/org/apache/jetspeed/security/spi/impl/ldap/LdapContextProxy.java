/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security.spi.impl.ldap;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.springframework.ldap.core.support.LdapContextSource;

/**
 * @author <a href="mailto:vkumar@apache.org">Vivek Kumar</a>
 * @version $Id:
 */
public class LdapContextProxy implements InvocationHandler
{
    private Properties env;
    private LdapContext ctx;
    private LdapContextSource springContext;
    private String initialContextFactory;
    private String userFilter;
    private String memberShipSearchScope;
    private String userSearchBase;

    public LdapContextProxy(LdapContextSource context, String factory, String userFilter, String memberShipSearchScope,String userSearchBase)
    {
        springContext = context;
        env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, factory);
        env.put(Context.PROVIDER_URL, springContext.getUrls()[0]);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_CREDENTIALS, springContext.getAuthenticationSource().getCredentials());
        env.put(Context.SECURITY_PRINCIPAL, springContext.getAuthenticationSource().getPrincipal());
        this.initialContextFactory = factory;
        this.userFilter = userFilter;
        this.memberShipSearchScope = memberShipSearchScope;
        this.userSearchBase = userSearchBase;
    }

    public LdapContext getCtx() throws NamingException
    {
        if (ctx == null)
        {
            ctx = new InitialLdapContext(env, null);
        }
        return ctx;
    }

    private void closeCtx()
    {
        if (ctx != null)
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
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    public synchronized Object invoke(Object proxy, Method m, Object[] args) throws Throwable
    {
        Object result = null;
        boolean close = "close".equals(m.getName()) && args.length == 0;
        if (close && ctx == null)
        {
            // don't need to do anything
            ;
        }
        else
        {
            LdapContext ctx = getCtx();
            try
            {
                result = m.invoke(ctx, args);
                if (close)
                {
                    closeCtx();
                }
            }
            catch (Throwable t)
            {
                closeCtx();
                if (t instanceof InvocationTargetException)
                {
                    t = ((InvocationTargetException) t).getTargetException();
                }
                if (t instanceof ServiceUnavailableException || t instanceof CommunicationException)
                {
                    try
                    {
                        ctx = getCtx();
                        result = m.invoke(ctx, args);
                    }
                    catch (Throwable t2)
                    {
                        closeCtx();
                        if (t2 instanceof InvocationTargetException)
                        {
                            t2 = ((InvocationTargetException) t2).getTargetException();
                        }
                        throw t2;
                    }
                }
                throw t;
            }
        }
        return result;
    }

    public String getInitialContextFactory()
    {
        return initialContextFactory;
    }

    public String getUserFilter()
    {
        return userFilter;
    }
    
    public String getUserSearchBase()    
    {
        return this.userSearchBase;
    }

    public String getRootContext()
    {
        return springContext.getBaseLdapPathAsString();
        
    }
    public LdapContextSource getContextSource()
    {
        return this.springContext;
    }
    public String getMemberShipSearchScope()
    {
        return memberShipSearchScope;
    }
}
