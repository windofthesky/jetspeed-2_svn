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
package org.apache.jetspeed.sso.spi.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.components.dao.InitablePersistenceBrokerDaoSupport;
import org.apache.jetspeed.security.SecurityDomain;
import org.apache.jetspeed.security.impl.SecurityDomainImpl;
import org.apache.jetspeed.sso.SSOException;
import org.apache.jetspeed.sso.SSOSite;
import org.apache.jetspeed.sso.SSOUser;
import org.apache.jetspeed.sso.impl.SSOSiteImpl;
import org.apache.jetspeed.sso.spi.SSOSiteManagerSPI;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.springframework.orm.ObjectRetrievalFailureException;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class JetspeedPersistentSSOSiteManager extends
InitablePersistenceBrokerDaoSupport implements SSOSiteManagerSPI
{
    /* Logging */
    private static final Logger log = LoggerFactory.getLogger(JetspeedPersistentSSOSiteManager.class);
    private Hashtable<String,SSOSite> mapSiteNameIndex = new Hashtable<String,SSOSite>();
    private Hashtable<String,SSOSite> mapSiteUrlIndex = new Hashtable<String,SSOSite>();
    private Hashtable<Long,SSOSite> mapSiteDomainIndex = new Hashtable<Long,SSOSite>();
    
    public JetspeedPersistentSSOSiteManager(String repositoryPath) throws ClassNotFoundException
    {
       super(repositoryPath);
    }
    
    public SSOSite add(SSOSite ssoSite) throws SSOException
    {
        try
        {
            getPersistenceBrokerTemplate().store(ssoSite);
            cacheSite(ssoSite);
            return ssoSite;
        }
        catch (Exception e)
        {
            String msg = "Unable to add SSO Site: " + ssoSite.getName();
            log.error(msg, e);
            throw new SSOException(msg, e);
        }                
    }
    
    public boolean exists(String siteUrl) {
		return getByUrl(siteUrl) != null;
	}

    public SSOSite getById(int id){
        try{
            return (SSOSite) getPersistenceBrokerTemplate().getObjectById(SSOSiteImpl.class, id);    
        } catch (ObjectRetrievalFailureException ore){
            return null;
        }
    
    }
    
	public SSOSite getByName(String siteName) {
		
	    Criteria filter = new Criteria();
        filter.addEqualTo("name", siteName);
        Query query = QueryFactory.newQuery(SSOSiteImpl.class, filter);
        SSOSite site = (SSOSite) getPersistenceBrokerTemplate().getObjectByQuery(query);
        if (site != null){
            cacheSite(site);
        }
        return site;   
	}

	public SSOSite getByUrl(String siteUrl)
    {
        Criteria filter = new Criteria();
        filter.addEqualTo("url", siteUrl);
        Query query = QueryFactory.newQuery(SSOSiteImpl.class, filter);
        SSOSite site = (SSOSite) getPersistenceBrokerTemplate().getObjectByQuery(query);
        if (site != null){
            cacheSite(site);
        }
        return site;       
    }

    public SSOSite getSite(SSOUser ssoUser)
    {        
        Collection<SSOSite> sitesForUser = getSites(Arrays.asList(new SSOUser[]{ ssoUser} ));
        if (sitesForUser.size() == 1){
            return sitesForUser.iterator().next();
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public Collection<SSOSite> getSites(String filter)
    {
        
        Criteria finalCriteria = new Criteria();

        
        if (StringUtils.isNotEmpty(filter)){
            
            filter = filter.replaceAll("%", "\\%");
            filter = "%"+filter+"%";
            Criteria urlCriteria = new Criteria();
            urlCriteria.addLike("url", filter);
            finalCriteria.addOrCriteria(urlCriteria);

            Criteria nameCriteria = new Criteria();
            nameCriteria.addLike("name", filter);
            finalCriteria.addOrCriteria(nameCriteria);
        }
        
        Query query = QueryFactory.newQuery(SSOSiteImpl.class, finalCriteria);
        
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }
    
    @SuppressWarnings("unchecked")
    public Collection<SSOSite> getSites(Collection<SSOUser> users)
    {
        if (users.size() > 0){
            Criteria queryCriteria = new Criteria();
            
            for (SSOUser u : users){
                Criteria domainCriteria = new Criteria();
                domainCriteria.addEqualTo("securityDomainId",u.getDomainId());
                queryCriteria.addOrCriteria(domainCriteria);
            }

            QueryByCriteria query = QueryFactory.newQuery(SSOSiteImpl.class, queryCriteria);
            query.addOrderByAscending("name");
            return getPersistenceBrokerTemplate().getCollectionByQuery(query);
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    public void remove(SSOSite site)
    throws SSOException
    {
        try
        {
            getPersistenceBrokerTemplate().delete(site);
            removeSiteFromCache(site);
        }
        catch (Exception e)
        {
            String msg = "Unable to remove SSO Site: " + site.getName();
            log.error(msg, e);
            throw new SSOException(msg, e);
        }        
    }

    public void update(SSOSite site)
    throws SSOException
    {
        try
        {
            getPersistenceBrokerTemplate().store(site);
            cacheSite(site);
        }
        catch (Exception e)
        {
            String msg = "Unable to remove SSO Site: " + site.getName();
            log.error(msg, e);
            throw new SSOException(msg, e);
        }        
    }
    
    protected void cacheSite(SSOSite site){
        if (getCachedSiteByDomainId(site.getSecurityDomainId()) != null){
            removeSiteFromCache(site);    
        }
        mapSiteUrlIndex.put(site.getURL(), site);
        mapSiteDomainIndex.put(site.getSecurityDomainId(), site);
        mapSiteNameIndex.put(site.getName(), site);
    }
    
    protected SSOSite getCachedSiteByUrl(String url){
        return mapSiteUrlIndex.get(url);
    }
    
    protected SSOSite getCachedSiteByDomainId(Long domainId){
        return mapSiteUrlIndex.get(domainId);
    }
    
    protected SSOSite getCachedSiteByName(String name){
        return mapSiteNameIndex.get(name);
    }

    protected void removeSiteFromCache(SSOSite site){
        mapSiteUrlIndex.remove(site.getURL());
        mapSiteDomainIndex.remove(site.getSecurityDomainId());
    }
}
