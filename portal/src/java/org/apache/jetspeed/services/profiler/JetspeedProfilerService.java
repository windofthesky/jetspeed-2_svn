/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.services.profiler;

//java.util
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.BaseService;
import org.apache.fulcrum.InitializationException;
import org.apache.jetspeed.om.profile.BasePSMLDocument;
import org.apache.jetspeed.om.profile.BaseProfile;
import org.apache.jetspeed.om.profile.BaseProfileLocator;
import org.apache.jetspeed.om.profile.PSMLDocument;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.ProfileException;
import org.apache.jetspeed.om.profile.ProfileLocator;
import org.apache.jetspeed.om.profile.QueryLocator;
import org.apache.jetspeed.om.profile.psml.PsmlPortlets;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.services.psml.PsmlManager;


/**
 * <p>This is an implementation of the <code>Profiler</code> interface.
 *
 * This implementation maps requests to profiles (PSML resources) based on
 * request parameters, requesting deviced capabilities, and the device's
 * language. </p>
 * <p>This service expects these properties to be set for correct operation:
 * <dl>
 *    <dt>root</dt><dd>The webapp rel. path to the root profiling directory</dd>
 *    <dt>resource.default</dt><dd>The default resource filename</dd>
 *    <dt>resource.ext</dt><dd>The default resource filename extension</dd>
 *    <dt>security</dt><dd>Use security flag</dd>
 *    <dt>fallback.language</dt><dd>Use language configuration flag</dd>
 *    <dt>fallback.country</dt><dd>Use country configuration flag</dd>
 *    <dt>fallback.to.root</dt><dd>Continue falling back past media type flag</dd>
 *
 * </dl>
 * </p>
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @author <a href="mailto:sgala@hisitech.com">Santiago Gala</a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id$
 */

public class JetspeedProfilerService  extends BaseService 
    implements ProfilerService
{

    // configuration keys
    private final static String CONFIG_RESOURCE_DEFAULT = "resource.default";
    private final static String CONFIG_RESOURCE_EXT     = "resource.ext";
    private final static String CONFIG_SECURITY         = "security";
    private final static String CONFIG_ROLE_FALLBACK    = "rolefallback";
    private final static String CONFIG_NEWUSER_TEMPLATE  = "newuser.template";
    private final static String CONFIG_NEWUSER_MEDIA     = "newuser.media_types";
    private final static String CONFIG_FALLBACK_LANGUAGE = "fallback.language";
    private final static String CONFIG_FALLBACK_COUNTRY = "fallback.country";
    private final static String CONFIG_FALLBACK_TO_ROOT = "fallback.to.root";
    private final static String CONFIG_ROLE_MERGE = "rolemerge";
    private final static String CONFIG_ROLE_MERGE_CONTROL = "rolemerge.control";
    private final static String CONFIG_ROLE_MERGE_CONTROLLER = "rolemerge.controller";

    // default configuration values
    private final static String DEFAULT_CONFIG_RESOURCE_DEFAULT = "default";
    private final static String DEFAULT_CONFIG_RESOURCE_EXT = ".psml";
    private final static boolean DEFAULT_CONFIG_SECURITY = false;
    private final static boolean DEFAULT_CONFIG_ROLE_FALLBACK = true;
    private final static String DEFAULT_CONFIG_NEWUSER_TEMPLATE = null;
    private final static String [] DEFAULT_CONFIG_NEWUSER_MEDIA =
    { "html", "wml" };
    private final static String DEFAULT_CONFIG_ROLE_MERGE_CONTROL = "TabControl";
    private final static String DEFAULT_CONFIG_ROLE_MERGE_CONTROLLER = "TabController";

    private final static String PATH_EXTENSION_DELIMITER = ".";
    // messages
    private final static String MSG_MISSING_PARAMETER =
        "JetspeedProfilerService initialization failed. Missing parameter:";

    private final static Log log = LogFactory.getLog(JetspeedProfilerService.class);

    // configuration parameters
    String root;                   // the root psml resource directory
    String resourceDefault;        // the default name for a resource
    String resourceExt;            // the default extension for a resource
    String rolemergeControl;       // the default control used with merged role profiles
    String rolemergeController;    // the default controller used with merged role profiles

    String newUserTemplate = DEFAULT_CONFIG_NEWUSER_TEMPLATE;

    boolean useSecurity = false;   // use security features
    boolean useRoleFallback = true;
    boolean useFallbackLanguage = true;
    boolean useFallbackCountry = true;
    boolean useFallbackToRoot = false;
    boolean useRoleMerge = false;

    String mediaTypes[] = null;


    // --------------------------------------------------------------------------

    /**
     * This is the early initialization method called by the
     * Turbine <code>Service</code> framework
     * @param conf The <code>ServletConfig</code>
     * @exception throws a <code>InitializationException</code> if the service
     * fails to initialize
     */
    public void init() throws InitializationException
    {
        if (isInitialized()) 
        {
            return;        
        }

        try
        {
            initConfiguration();
        }
        catch (Exception e)
        {
            log.error("Profiler: Failed to load Service: " + e);
            e.printStackTrace();
        }

        // initialization done
        setInit(true);

     }

    /**
     * This is the shutdown method called by the
     * Turbine <code>Service</code> framework
     */
    public void shutdown()
    {
    }

    /**
     *  get the Profile object using a profile locator
     *
     * @param locator The locator containing criteria describing the profile.
     * @return a new Profile object
     */
    public Profile getProfile(ProfileLocator locator)
        throws ProfileException
    {
        PSMLDocument doc =  fallback(locator);
        Profile profile = createProfile(locator);
        profile.setDocument(doc);
        return profile;
    }

    /**
     *  Get the Profile object using the request parameters.
     *
     * @param context The request context
     * @return a new Profile object
     */
    public Profile getProfile(RequestContext context)
        throws ProfileException
    {
        // for testing...
        // DST TODO: DEPENDENCY: get Capabilities/MediaType info into the request with a new VALVE 

        QueryLocator locator = new QueryLocator(QueryLocator.QUERY_USER); // TODO: replace with factory
        locator.setUser( "anon" ); // TODO: get this from the context
        locator.setMediaType( "html" ); // TODO: get this from the context.getMediaType
        Iterator it = Profiler.query( locator );
        Profile profile = (Profile)it.next();
        return profile;
    }

    /*
     * A basic profiler fallback algorithm that starts from the most specific parameters,
     * going to the least specific parameters. The PsmlManager implementation is passed
     * a list of ProfileLocators ordered from most specific  to least specific.
     *
     * @param locator The profile locator criteria used to locate a profile.
     * @return The found psml document, or null if not found.
     */
    protected PSMLDocument fallbackList( ProfileLocator original)
    {
        try
        {
            List locators = new LinkedList();
            ProfileLocator locator = (ProfileLocator)original.clone();

            locators.add( locator.clone() );

            // remove country
            if (null != original.getCountry())
            {
                locator.setCountry(null);
                locators.add( locator.clone() );
            }

            // remove language
            if (null != original.getLanguage())
            {
                locator.setLanguage(null);
                locators.add( locator.clone() );
            }

            // fallback mediaType
            if (null != original.getMediaType())
            {
                locator.setMediaType(null);
                locators.add( locator.clone() );
            }

            if (null != original.getGroup())
            {
                locator.setGroup(null);
                locators.add( locator.clone() );
            }
            else if (null != original.getRole())
            {
                locator.setRole(null);
                locators.add( locator.clone() );
            }
            else if (null != original.getUser())
            {
                locator.setUser(null);
                locators.add( locator.clone() );
            }
            PSMLDocument doc = PsmlManager.getDocument( locators );
            return doc;

        }
        catch (CloneNotSupportedException e)
        {
            log.error("Profiler: Could not clone profile locator object", e);
        }
        return null;
    }

    /*
     * A basic profiler fallback algorithm that starts from the most specific parameters,
     * going to the least specific parameters. The PsmlManager implementation is passed
     * a list of ProfileLocators ordered from most specific  to least specific.
     *
     * This is alternate fallback algorithm.
     *
     * @param locator The profile locator criteria used to locate a profile.
     *
     * @return PSMLDocument The located document or null.
     */
    protected PSMLDocument fallback(ProfileLocator locator)
    {
        if (log.isDebugEnabled())
        {
            log.debug( "Profiler: fallback called with: " + locator );
        }

        PSMLDocument doc = PsmlManager.getDocument( locator );
        if (null != doc)
            return doc;

        // remove country
        if (null != locator.getCountry())
        {
            locator.setCountry(null);
            doc = PsmlManager.getDocument( locator );
            if (null != doc)
                return doc;
        }

        // remove language
        if (null != locator.getLanguage())
        {
            locator.setLanguage(null);
            doc = PsmlManager.getDocument( locator );
            if (null != doc)
                return doc;
        }

        // fallback mediaType
        if (useFallbackToRoot)
        {
            if (null != locator.getMediaType())
            {
                locator.setMediaType(null);
                doc = PsmlManager.getDocument( locator );
                if (null != doc)
                    return doc;
            }
        }

        if (!useRoleFallback)
        {
            if (null != locator.getGroup())
            {
                locator.setGroup(null);
                doc = PsmlManager.getDocument( locator );
                if (null != doc)
                    return doc;
            }
            else if (null != locator.getRole())
            {
                locator.setRole(null);
                doc = PsmlManager.getDocument( locator );
                if (null != doc)
                    return doc;
            }
            else if (null != locator.getUser())
            {
                locator.setUser(null);
                doc = PsmlManager.getDocument( locator );
                if (null != doc)
                    return doc;
            }
        }
        return doc;

    }

    /**
     * Loads the configuration parameters for this service from the
     * JetspeedResources.properties file.
     *
     * @exception throws a <code>InitializationException</code> if the service
     * fails to initialize
     */
    private void initConfiguration() throws InitializationException
    {
        resourceDefault = getConfiguration().getString( CONFIG_RESOURCE_DEFAULT, DEFAULT_CONFIG_RESOURCE_DEFAULT );

        resourceExt = getConfiguration().getString( CONFIG_RESOURCE_EXT, DEFAULT_CONFIG_RESOURCE_EXT );
        if (-1 == resourceExt.indexOf(PATH_EXTENSION_DELIMITER))
            resourceExt = PATH_EXTENSION_DELIMITER + resourceExt;

        useSecurity = getConfiguration().getBoolean( CONFIG_SECURITY, DEFAULT_CONFIG_SECURITY );

        useRoleFallback = getConfiguration().getBoolean( CONFIG_ROLE_FALLBACK, DEFAULT_CONFIG_ROLE_FALLBACK );

        newUserTemplate = getConfiguration().getString( CONFIG_NEWUSER_TEMPLATE, DEFAULT_CONFIG_NEWUSER_TEMPLATE );

        useFallbackToRoot = getConfiguration().getBoolean( CONFIG_FALLBACK_TO_ROOT, useFallbackToRoot );

        useFallbackLanguage = getConfiguration().getBoolean( CONFIG_FALLBACK_LANGUAGE, useFallbackLanguage );

        useRoleMerge = getConfiguration().getBoolean( CONFIG_ROLE_MERGE, useRoleMerge );

        rolemergeControl = getConfiguration().getString( CONFIG_ROLE_MERGE_CONTROL, DEFAULT_CONFIG_ROLE_MERGE_CONTROL );

        rolemergeController = getConfiguration().getString( CONFIG_ROLE_MERGE_CONTROLLER, DEFAULT_CONFIG_ROLE_MERGE_CONTROLLER );

        if (useFallbackLanguage == false)
        {
            useFallbackCountry = false;
        }
        else
        {
            useFallbackCountry = getConfiguration().getBoolean( CONFIG_FALLBACK_COUNTRY, useFallbackCountry );
        }

        try
        {
            mediaTypes = getConfiguration().getStringArray(CONFIG_NEWUSER_MEDIA);
        }
        catch (Exception e)
        {}

        if (null == mediaTypes || mediaTypes.length == 0)
        {
            mediaTypes = DEFAULT_CONFIG_NEWUSER_MEDIA;
        }
    }

   /**
    * Builds a dynamic URI based on the current profiler group/role/page
    *
    * @param data The rundata object for the current request.
    * @param locator The description of the profile.
    * @return A new dynamic URI representing all profile parameters from the locator.
    *
    public DynamicURI makeDynamicURI( RunData data, ProfileLocator locator )
        throws ProfileException
    {
        DynamicURI uri = new DynamicURI( data );

       // check mediatype to add to the uri
        String mtype = locator.getMediaType();
        if (null != mtype)
        {
            uri.addPathInfo(Profiler.PARAM_MEDIA_TYPE, mtype);
        }

       // check language to add to the uri
        String language = locator.getLanguage();
        if (null != language)
        {
            uri.addPathInfo(Profiler.PARAM_LANGUAGE, language);
        }

       // check language to add to the uri
        String country = locator.getCountry();
        if (null != country)
        {
            uri.addPathInfo(Profiler.PARAM_COUNTRY, country);
        }

        // check User, Group or Role to add to the uri
        JetspeedUser user = locator.getUser();
        if (null != user)
        {
            if (user.getUserName() != null)
                uri.addPathInfo(Profiler.PARAM_USER, user.getUserName());
        }
        else
        {
            Group group = locator.getGroup();
            if (null != group)
            {
                uri.addPathInfo(Profiler.PARAM_GROUP, group.getName());
            }
            else
            {
                Role role = locator.getRole();
                if (null != role)
                {
                    uri.addPathInfo(Profiler.PARAM_ROLE, role.getName());
                }
            }
        }

        // check Page to add to the uri
        String page = locator.getName();
        if (null != page)
        {
            uri.addPathInfo(Profiler.PARAM_PAGE, page);
        }

        return uri;
    }
    */

    /**
     * Creates a new Profile object that can be successfully managed by
     * the current Profiler implementation
     *
     * @return A new Profile object
     */
    public Profile createProfile()
    {
        return new BaseProfile();
    }

    /**
     * Creates a new Profile object for a specific locator.
     *
     * @param locator The description of the profile.
     * @return A new Profile object
     */
    public Profile createProfile(ProfileLocator locator)
    {
        return new BaseProfile(locator);
    }

    /**
     * Creates a new ProfileLocator object that can be successfully managed by
     * the current Profiler implementation
     *
     * @return A new ProfileLocator object
     */
    public ProfileLocator createLocator()
    {
        return new BaseProfileLocator();
    }

    /**
     * Create a new profile given a profile locator
     *
     * @param locator The description of the new profile to be created.
     * @param portlets The PSML tree
     */
    public Profile createProfile(ProfileLocator locator, Portlets portlets)
            throws ProfileException
    {
        if (portlets == null)
        {
            portlets = new PsmlPortlets();
        }
        Profile profile = createProfile(locator);
        PSMLDocument doc = new BasePSMLDocument(null, portlets);
        profile.setDocument(doc);
        doc = PsmlManager.createDocument(profile);
        profile.setDocument(doc);
        return profile;
    }

   /**
     *  Removes a profile.
     *
     * @param locator The profile locator criteria.
     */
    public void removeProfile( ProfileLocator locator )
    {
        PsmlManager.removeDocument(locator);
    }

    /** Query for a collection of profiles given a profile locator criteria.
     *
     * @param locator The profile locator criteria.
     * @return The list of profiles matching the locator criteria.
     */
    public Iterator query( QueryLocator locator )
    {
        return PsmlManager.query( locator );
    }

    /**
     * @see org.apache.jetspeed.services.profiler.ProfilerService#useRoleProfileMerging
     */
    public boolean useRoleProfileMerging()
    {
        return this.useRoleFallback && this.useRoleMerge;
    }
}