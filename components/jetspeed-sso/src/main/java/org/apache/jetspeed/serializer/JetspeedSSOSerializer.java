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
package org.apache.jetspeed.serializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.serializer.objects.JSSnapshot;
import org.apache.jetspeed.serializer.objects.JSSSOSite;
import org.apache.jetspeed.serializer.objects.JSSSOSiteRemoteUser;
import org.apache.jetspeed.serializer.objects.JSSSOSites;
import org.apache.jetspeed.sso.SSOException;
import org.apache.jetspeed.sso.SSOManager;
import org.apache.jetspeed.sso.SSOSite;
import org.apache.jetspeed.sso.SSOUser;
import org.apache.jetspeed.sso.impl.SSOUtils;
import org.slf4j.Logger;

/**
 * JetspeedSSOSerializer - Profiler component serializer
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class JetspeedSSOSerializer extends AbstractJetspeedComponentSerializer
{
    private SSOManager ssoManager;
    private GroupManager groupManager;
    private UserManager userManager;
    
    public JetspeedSSOSerializer(SSOManager ssoManager, GroupManager groupManager, UserManager userManager)
    {
        this.ssoManager = ssoManager;
        this.groupManager = groupManager;
        this.userManager = userManager;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.serializer.AbstractJetspeedComponentSerializer#deleteData(java.util.Map, org.slf4j.Logger)
     */
    protected void deleteData(Map<String,Object> settings, Logger log) throws SerializerException
    {
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_SSO))
        {
            log.info("deleting SSO sites, principals, and security domains");
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.serializer.AbstractJetspeedComponentSerializer#processExport(org.apache.jetspeed.serializer.objects.JSSnapshot, java.util.Map, org.slf4j.Logger)
     */
    protected void processExport(JSSnapshot data, Map<String,Object> settings, Logger log) throws SerializerException
    {
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_SSO))
        {
            log.info("collecting SSO sites, principals, and security domains");
            exportSSOSites(data, settings, log);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.serializer.AbstractJetspeedComponentSerializer#processImport(org.apache.jetspeed.serializer.objects.JSSnapshot, java.util.Map, org.slf4j.Logger)
     */
    protected void processImport(JSSnapshot data, Map<String,Object> settings, Logger log) throws SerializerException
    {
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_SSO))
        {
            log.info("creating SSO sites, principals, and security domains");
            recreateSSOSites(data, settings, log);
        }
    }
    
    /**
     * delete SSO site data
     * 
     * @param settings export settings
     * @param log export logger
     * @throws SerializerException
     */
    private void deleteSSOSites(Map<String,Object> settings, Logger log) throws SerializerException
    {
        Iterator<SSOSite> list = null;
        try
        {
            list = (new ArrayList<SSOSite>(ssoManager.getSites(""))).iterator();
        }
        catch (Exception e)
        {
            throw new SerializerException(SerializerException.GET_EXISTING_OBJECTS.create(new String[]{"SSOSites", e.getMessage()}));
        }
        while (list.hasNext())
        {
            try
            {
                SSOSite s = list.next();
                ssoManager.removeSite(s);
            }
            catch (Exception e)
            {
                throw new SerializerException(e);
            }
        }
    }

    /**
     * Create the SSO Site Wrapper
     * 
     * @param s sso site
     * @return sso site wrapper
     * @throws SSOException
     */
    private JSSSOSite createSSOSite(SSOSite s) throws SSOException
    {
        JSSSOSite site = new JSSSOSite();
        site.setName(s.getName());
        site.setSiteURL(s.getURL());
        site.setAllowUserSet(s.isAllowUserSet());
        site.setCertificateRequired(s.isCertificateRequired());
        site.setChallengeResponseAuthentication(s.isChallengeResponseAuthentication());
        site.setRealm(s.getRealm());
        site.setFormAuthentication(s.isFormAuthentication());
        site.setFormUserField(s.getFormUserField());
        site.setFormPwdField(s.getFormPwdField());

        Iterator<SSOUser> ruIter = ssoManager.getUsersForSite(s).iterator();
        while (ruIter.hasNext())
        {
            SSOUser ru = ruIter.next();
            PasswordCredential ruCredential = ssoManager.getCredentials(ru);
            if (ruCredential != null)
            {
                String ruName = ru.getName();
                String rupType = null;
                String rupName = null;
                Collection<JetspeedPrincipal> ruPrincipals = ssoManager.getPortalPrincipals(ru);
                if ((ruPrincipals != null) && !ruPrincipals.isEmpty())
                {
                    JetspeedPrincipal rup = ruPrincipals.iterator().next();
                    rupName = rup.getName();
                    if (rup.getType().getName().equals("user"))
                    {
                        rupType = "user";
                    }
                    else if (rup.getType().getName().equals("group"))
                    {
                        rupType = "group";
                    }
                }
                if ((rupName != null) && (rupType != null))
                {
                    String ruPassword = SSOUtils.scramble(ruCredential.getPassword());
                    JSSSOSiteRemoteUser siteRemoteUser = new JSSSOSiteRemoteUser();
                    siteRemoteUser.setPrincipalName(rupName);
                    siteRemoteUser.setPrincipalType(rupType);
                    siteRemoteUser.setUserCredential(ruName, ruPassword.toCharArray());
                    site.addRemoteUser(siteRemoteUser);
                }
            }
        }

        return site;
    }

    /**
     * extract SSO sites and save in snapshot file
     * 
     * @param data sso data snapshot
     * @param settings export settings
     * @param log export logger
     * @throws SerializerException
     */
    private void exportSSOSites(JSSnapshot data, Map<String,Object> settings, Logger log) throws SerializerException
    {
        Map<String,JSSSOSite> ssoSitesMap = new HashMap<String,JSSSOSite>();
        Iterator<SSOSite> list = null;
        try
        {
            list = ssoManager.getSites("").iterator();
        }
        catch (Exception e)
        {
            throw new SerializerException(SerializerException.GET_EXISTING_OBJECTS.create(new String[]{"SSOSites", e.getMessage()}));
        }
        while (list.hasNext())
        {
            try
            {
                SSOSite s = list.next();                
                if (!(ssoSitesMap.containsKey(s.getURL())))
                {
                    JSSSOSite site = createSSOSite(s);
                    ssoSitesMap.put(site.getSiteURL(), site);
                    data.getSSOSites().add(site);
                }
            }
            catch (Exception e)
            {
                throw new SerializerException(SerializerException.CREATE_SERIALIZED_OBJECT_FAILED.create(new String[]{"SSOSites", e.getMessage()}));
            }
        }
    }
    
    /**
     * Construct SSO site from (JS) SSOSite.
     * 
     * @param site SSO site
     * @param s existing SSO site
     * @return created SSO site
     * @throws SerializerException
     * @throws SSOException
     * @throws SecurityException
     */
    private SSOSite recreateSSOSite(JSSSOSite site, SSOSite s) throws SerializerException, SSOException, SecurityException
    {
        if (s != null)
        {
            ssoManager.removeSite(s);
        }
        
        s = ssoManager.newSite(site.getName(), site.getSiteURL());
        s = ssoManager.addSite(s);
        s.setAllowUserSet(site.isAllowUserSet());
        s.setCertificateRequired(site.isCertificateRequired());
        s.setChallengeResponseAuthentication(site.isChallengeResponseAuthentication());
        s.setRealm(site.getRealm());
        s.setFormAuthentication(site.isFormAuthentication());
        s.setFormUserField(site.getFormUserField());
        s.setFormPwdField(site.getFormPwdField());

        if (site.getRemoteUsers() != null)
        {
            Iterator<JSSSOSiteRemoteUser> ruIter = site.getRemoteUsers().iterator();
            while (ruIter.hasNext())
            {
                JSSSOSiteRemoteUser rUser = ruIter.next();
                if (rUser.getPassword() != null)
                {
                    String pName = rUser.getPrincipalName();
                    String pType = rUser.getPrincipalType();
                    JetspeedPrincipal principal = null;
                    if (pType.equals("user"))
                    {
                        principal = userManager.getUser(pName);
                    }
                    else if (pType.equals("group"))
                    {
                        principal = groupManager.getGroup(pName);
                    }
                    if (principal != null)
                    {
                        String rName = rUser.getName();
                        String rPassword = new String(rUser.getPassword());
                        ssoManager.addUser(s, principal, rName, SSOUtils.unscramble(rPassword));
                    }
                    else
                    {
                        throw new IllegalArgumentException("Cannot lookup or create SSO remote user for principal "+pType+":"+pName);
                    }
                }
            }
        }
        
        return s;
    }
    
    /**
     * Create imported SSO sites.
     * 
     * @param data sso data snapshot
     * @param settings import settings
     * @param log import logger
     * @throws SerializerException
     */
    private void recreateSSOSites(JSSnapshot data, Map<String,Object> settings, Logger log) throws SerializerException
    {
        log.debug("recreateSSOSites - processing");

        JSSSOSites sites = data.getSSOSites();
        if ((sites != null) && (sites.size() > 0))
        {
            Iterator<JSSSOSite> sitesIter = sites.iterator();
            while (sitesIter.hasNext())
            {
                JSSSOSite site = sitesIter.next();
                try
                {
                    SSOSite s = ssoManager.getSiteByUrl(site.getSiteURL());
                    if ((s == null) || isSettingSet(settings, JetspeedSerializer.KEY_OVERWRITE_EXISTING))
                    {
                        s = recreateSSOSite(site, s);
                        ssoManager.updateSite(s);
                    }
                }
                catch (Exception e)
                {
                    throw new SerializerException(SerializerException.CREATE_OBJECT_FAILED.create("SSOSite",e.getLocalizedMessage()));
                }
            }
        }
        else
        {
            log.debug("NO SSO SITES?????");
        }
        
        log.debug("recreateSSOSites - done");        
    }
}
