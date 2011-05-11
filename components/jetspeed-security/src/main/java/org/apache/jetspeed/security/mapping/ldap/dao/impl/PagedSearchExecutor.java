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
package org.apache.jetspeed.security.mapping.ldap.dao.impl;

import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import org.apache.jetspeed.security.mapping.SearchResultCallbackHandler;
import org.springframework.ldap.control.PagedResultsCookie;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.NameClassPairCallbackHandler;
import org.springframework.ldap.core.SearchExecutor;
import org.springframework.ldap.support.LdapUtils;

/**
 * @author <a href="mailto:ate@douma.nu>Ate Douma</a>
 * @version $Id$
 */
public class PagedSearchExecutor implements SearchExecutor, NameClassPairCallbackHandler
{
    private static final NamingEnumeration<SearchResult> noResultsEnumeration = new NamingEnumeration<SearchResult>()
    {
        public void close() throws NamingException {}
        public boolean hasMore() throws NamingException { return false; }
        public SearchResult next() throws NamingException { return null; }
        public boolean hasMoreElements() { return false; }
        public SearchResult nextElement() { return null; }
    };
    
    private String base;
    private Name baseName;
    private String filter;
    private SearchControls controls;
    private SearchResultCallbackHandler handler;
    private int pageSize;
    
    protected PagedSearchExecutor(String filter, SearchControls controls, SearchResultCallbackHandler handler, int pageSize)
    {
        this.filter = filter;
        this.controls = controls;
        this.handler = handler;
        this.pageSize = pageSize;
    }
    
    public PagedSearchExecutor(String base, String filter, SearchControls controls, SearchResultCallbackHandler handler)
    {
        this(base, filter, controls, handler, -1);
    }

    public PagedSearchExecutor(Name baseName, String filter, SearchControls controls, SearchResultCallbackHandler handler)
    {
        this(baseName, filter, controls, handler, -1);
    }
    
    public PagedSearchExecutor(String base, String filter, SearchControls controls, SearchResultCallbackHandler handler, int pageSize)
    {
        this(filter, controls, handler, pageSize);
        this.base = base;
    }

    public PagedSearchExecutor(Name baseName, String filter, SearchControls controls, SearchResultCallbackHandler handler, int pageSize)
    {
        this(filter, controls, handler, pageSize);
        this.baseName = baseName;
    }
    
    public NamingEnumeration<SearchResult> executeSearch(DirContext ctx) throws NamingException
    {
        NamingEnumeration<SearchResult> results = null;
        PagedResultsDirContextProcessor processor = null;
        boolean noExceptions = false;
        try
        {
            PagedResultsCookie cookie = null;
            int index = 0;
            boolean doNext = true;
            if (handler.getMaxCount() > 0 && controls.getCountLimit() <= 0 || handler.getMaxCount()+1 < controls.getCountLimit())
            {
                controls.setCountLimit(handler.getMaxCount()+1);
            }
            int pageSize = handler.getSearchPageSize() < 0 ? this.pageSize : handler.getSearchPageSize();
            if (pageSize > 0)
            {
                do
                {
                    processor = new PagedResultsDirContextProcessor(pageSize, cookie);
                    processor.preProcess(ctx);
                    results = base != null ? ctx.search(base, filter, controls) : ctx.search(baseName, filter, controls);
                    int pageIndex = 0;
                    while (doNext && results.hasMore())
                    {
                        doNext = handler.handleSearchResult(results.next(), pageSize, pageIndex++, index++);
                    }                
                    processor.postProcess(ctx);
                    cookie = processor.getCookie();
                }
                while (doNext && cookie != null && cookie.getCookie() != null && cookie.getCookie().length != 0);
            }
            else
            {
                results = base != null ? ctx.search(base, filter, controls) : ctx.search(baseName, filter, controls);
                int pageIndex = 0;
                while (doNext && results.hasMore())
                {
                    doNext = handler.handleSearchResult(results.next(), pageSize, pageIndex++, index++);
                }                
            }
            noExceptions = true;
        }
        finally
        {
            if (results != null)
            {
                try 
                {
                    results.close();
                }
                catch (Exception e)
                {
                    // Never mind
                }
            }
            if (processor != null)
            {
                try
                {
                    // Make sure the Paging RequestControls are cleared again!
                    LdapContext ldapContext = (LdapContext)ctx;
                    ldapContext.setRequestControls(null);
                    processor.postProcess(ctx);
                }
                catch (NamingException e)
                {
                    if (noExceptions)
                    {   
                        throw LdapUtils.convertLdapException(e);
                    }
                    else
                    {
                        // ignore as we already have one
                    }
                }
            }
        }
        return noResultsEnumeration;
    }

    public void handleNameClassPair(NameClassPair nameClassPair)
    {
        // ignored
    }
}
