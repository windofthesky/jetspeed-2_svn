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
package org.apache.jetspeed.openid;

import com.google.step2.discovery.DefaultHostMetaFetcher;
import com.google.step2.discovery.Discovery2;
import com.google.step2.discovery.HostMetaFetcher;
import com.google.step2.discovery.IdpIdentifier;
import com.google.step2.discovery.LegacyXrdsResolver;
import com.google.step2.discovery.ParallelHostMetaFetcher;
import com.google.step2.discovery.SecureDiscoveryInformation;
import com.google.step2.discovery.SecureUrlIdentifier;
import com.google.step2.discovery.XrdDiscoveryResolver;
import com.google.step2.http.DefaultHttpFetcher;
import com.google.step2.xmlsimplesign.CachedCertPathValidator;
import com.google.step2.xmlsimplesign.CertValidator;
import com.google.step2.xmlsimplesign.DefaultCertValidator;
import com.google.step2.xmlsimplesign.DefaultTrustRootsProvider;
import com.google.step2.xmlsimplesign.TrustRootsProvider;
import com.google.step2.xmlsimplesign.Verifier;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.administration.PortalAdministration;
import org.apache.jetspeed.administration.PortalAuthenticationConfiguration;
import org.apache.jetspeed.audit.AuditActivity;
import org.apache.jetspeed.cache.UserContentCacheManager;
import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.openid.step2.GoogleHostMetaFetcher;
import org.apache.jetspeed.security.SecurityAttribute;
import org.apache.jetspeed.security.SecurityAttributes;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.discovery.UrlIdentifier;
import org.openid4java.discovery.html.HtmlResolver;
import org.openid4java.discovery.xri.XriDotNetProxyResolver;
import org.openid4java.discovery.xri.XriResolver;
import org.openid4java.discovery.yadis.YadisResolver;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;
import org.openid4java.message.sreg.SRegMessage;
import org.openid4java.message.sreg.SRegRequest;
import org.openid4java.message.sreg.SRegResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Serves OpenID Relaying Party metadata and accepts
 * authorization from OpenID provider.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class OpenIDRelayingPartyServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(OpenIDRelayingPartyServlet.class);
    
    private static final long OPEN_ID_DISCOVERY_TIMEOUT_SECONDS = 10L;

    private static final String OPEN_ID_DISCOVERY_INIT_PARAM_NAME_PREFIX = "discovery.";
    private static final String OPEN_ID_CONSUMER_INIT_PARAM_NAME_PREFIX = "consumer.";
    private static final String OPEN_ID_CONSUMER_INIT_PARAM_NAME_VALUE = "openid4java";
    private static final String STEP2_CONSUMER_INIT_PARAM_NAME_VALUE = "step2";
    
    private static final String OPEN_ID_PROVIDER_ATTR_NAME = "org.apache.jetspeed.openid.provider";
    private static final String OPEN_ID_DISCOVERY_INFO_ATTR_NAME = "org.apache.jetspeed.openid.discoveryinfo";

    private static final String USER_ATTRIBUTE_EMAIL = "user.business-info.online.email";
    private static final String USER_ATTRIBUTE_NAME = "user.name";
    private static final String USER_ATTRIBUTE_GIVEN_NAME = "user.name.given";
    private static final String USER_ATTRIBUTE_FAMILY_NAME = "user.name.family";
    private static final String USER_ATTRIBUTE_NICKNAME = "user.name.nickName";
    
    private static final String OPEN_ID_LOGIN_LOCALE_ATTR_NAME = "org.apache.jetspeed.openid.locale";
    private static final String OPEN_ID_LOGIN_SERVER_NAME_ATTR_NAME = "org.apache.jetspeed.openid.server.name";
    
    private ConsumerManager openIDConsumerManager;
    private ConsumerManager openIDStep2ConsumerManager;
    private UserManager portalUserManager;
    private PortalAdministration portalAdministration;
    private AuditActivity portalAudit;
    private PortalAuthenticationConfiguration portalAuthenticationConfiguration;
    private UserContentCacheManager portalUserContentCacheManager;
    private OpenIDRegistrationConfiguration initRegistrationConfiguration;
    
    /* (non-Javadoc)
     * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);

        // setup OpenID
        try
        {
            // standard OpenID implementation
            openIDConsumerManager = new ConsumerManager();
            // assemble Step2 OpenID implementation; TODO: utilize a more
            // robust HTTPFetcher implementation since the DefaultHttpFetcher
            // may not be entirely thread safe due to connection manager
            // configuration in HttpComponents, (this is why there are 4
            // discrete instances of DefaultHttpFetcher used below).
            HostMetaFetcher step2GoogleHostMetaFetcher = new GoogleHostMetaFetcher(new DefaultHttpFetcher());
            HostMetaFetcher step2DefaultHostMetaFetcher = new DefaultHostMetaFetcher(new DefaultHttpFetcher());
            ThreadFactory parallelThreadFactory = new ThreadFactory()
            {
                public Thread newThread(Runnable r)
                {
                    Thread newThread = Executors.defaultThreadFactory().newThread(r);
                    newThread.setName(getClass().getSimpleName()+"-"+newThread.getName());
                    newThread.setDaemon(true);
                    return newThread;
                }
            };
            HostMetaFetcher step2HostMetaFetcher = new ParallelHostMetaFetcher(Executors.newFixedThreadPool(10, parallelThreadFactory), OPEN_ID_DISCOVERY_TIMEOUT_SECONDS, step2GoogleHostMetaFetcher, step2DefaultHostMetaFetcher);
            TrustRootsProvider step2XrdsTrustProvider = new DefaultTrustRootsProvider();
            CachedCertPathValidator step2XrdsCertPathValidator = new CachedCertPathValidator(step2XrdsTrustProvider);
            Verifier step2XrdsVerifier = new Verifier(step2XrdsCertPathValidator, new DefaultHttpFetcher());
            CertValidator step2XrdsCertValidator = new DefaultCertValidator();
            XrdDiscoveryResolver step2XrdResolver = new LegacyXrdsResolver(new DefaultHttpFetcher(), step2XrdsVerifier, step2XrdsCertValidator);
            HtmlResolver step2HtmlResolver = new HtmlResolver();
            YadisResolver step2YadisResolver = new YadisResolver();
            XriResolver step2XriResolver = new XriDotNetProxyResolver();
            Discovery2 step2Discovery = new Discovery2(step2HostMetaFetcher, step2XrdResolver, step2HtmlResolver, step2YadisResolver, step2XriResolver);
            openIDStep2ConsumerManager = new ConsumerManager();
            openIDStep2ConsumerManager.setDiscovery(step2Discovery);
        }
        catch (OpenIDException oide)
        {
            throw new ServletException("Unexpected OpenID exception: "+oide, oide);
        }
        
        // configure portal components
        ComponentManager cm = Jetspeed.getComponentManager();
        portalUserManager = cm.lookupComponent("org.apache.jetspeed.security.UserManager");
        portalAdministration = cm.lookupComponent("PortalAdministration");
        portalAudit = cm.lookupComponent("org.apache.jetspeed.audit.AuditActivity");
        portalAuthenticationConfiguration = cm.lookupComponent("org.apache.jetspeed.administration.PortalAuthenticationConfiguration");
        portalUserContentCacheManager = cm.lookupComponent("userContentCacheManager");

        // registration configuration parameters
        if (Boolean.parseBoolean(config.getInitParameter(OpenIDConstants.ENABLE_REGISTRATION_CONFIG_INIT_PARAM_NAME)))
        {
            initRegistrationConfiguration = new OpenIDRegistrationConfiguration();
            initRegistrationConfiguration.setEnableRegistration(config.getInitParameter(OpenIDConstants.ENABLE_REGISTRATION_INIT_PARAM_NAME));
            initRegistrationConfiguration.setUserTemplateDirectory(config.getInitParameter(OpenIDConstants.REGISTRATION_USER_TEMPLATE_INIT_PARAM_NAME));
            initRegistrationConfiguration.setSubsiteRootFolder(config.getInitParameter(OpenIDConstants.REGISTRATION_SUBSITE_ROOT_INIT_PARAM_NAME));
            initRegistrationConfiguration.setRoles(config.getInitParameter(OpenIDConstants.REGISTRATION_ROLES_INIT_PARAM_NAME));
            initRegistrationConfiguration.setGroups(config.getInitParameter(OpenIDConstants.REGISTRATION_GROUPS_INIT_PARAM_NAME));
            initRegistrationConfiguration.setProfilerRules(config.getInitParameter(OpenIDConstants.REGISTRATION_PROFILER_RULE_NAMES_INIT_PARAM_NAME), config.getInitParameter(OpenIDConstants.REGISTRATION_PROFILER_RULE_VALUES_INIT_PARAM_NAME));
        }
    }
    
    /* (non-Javadoc)
     * @see javax.servlet.GenericServlet#destroy()
     */
    public void destroy()
    {
        initRegistrationConfiguration = null;
        portalUserContentCacheManager = null;
        portalAuthenticationConfiguration = null;
        portalAdministration = null;
        portalUserManager = null;
        openIDStep2ConsumerManager = null;
        openIDConsumerManager = null;
        super.destroy();
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        // compute absolute OpenID realm URL
        String openIDRealmURL = openIDRealmURL(request);
        
        // OpenID relaying party metadata request at servlet path
        String requestPath = request.getRequestURI();
        String servletPath = request.getContextPath()+request.getServletPath();
        if (requestPath.equals(servletPath))
        {
            // return Relying Party service metadata if request matches OpenID realm
            response.setHeader("Cache-Control", "no-cache,no-store,private");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            response.setContentType("text/xml");
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.println("<Service xmlns=\"xri://$xrd*($v*2.0)\">");
            out.println("  <Type>http://specs.openid.net/auth/2.0/return_to</Type>");
            out.println("  <URI>"+openIDRealmURL+"</URI>");
            out.println("</Service>");
            out.flush();
            out.close();
            
            // log OpenID request
            if (log.isDebugEnabled())
            {
                log.debug("OpenID realm request received, returned realm: "+openIDRealmURL);
            }
        }
        else 
        {
            // clear error state
            HttpSession httpSession = request.getSession();
            httpSession.removeAttribute(OpenIDConstants.OPEN_ID_ERROR);
            httpSession.setAttribute(OPEN_ID_LOGIN_LOCALE_ATTR_NAME, request.getLocale());
            httpSession.setAttribute(OPEN_ID_LOGIN_SERVER_NAME_ATTR_NAME, request.getServerName());
           
            // parse request from request path
            String servletPathPrefix = servletPath+"/";
            if (requestPath.startsWith(servletPathPrefix))
            {
                requestPath = requestPath.substring(servletPathPrefix.length());
            }
            
            // process OpenID requests
            if (requestPath.equals(OpenIDConstants.OPEN_ID_LOGIN_REQUEST))
            {
                // request parameters
                String discovery = request.getParameter(OpenIDConstants.OPEN_ID_DISCOVERY);
                if ((discovery != null) && (discovery.length() == 0))
                {
                    discovery = null;
                }
                String provider = request.getParameter(OpenIDConstants.OPEN_ID_PROVIDER);
                if ((provider != null) && (provider.length() == 0))
                {
                    provider = null;
                }
                String returnPath = request.getParameter(OpenIDConstants.OPEN_ID_RETURN);
                if ((returnPath == null) || (returnPath.length() == 0))
                {
                    returnPath = request.getContextPath()+"/";
                }

                boolean discoveredProvider = false;
                try
                {
                    // default user supplied discovery string and provider
                    String userSuppliedDiscoveryString = discovery;
                    if ((provider == null) && (userSuppliedDiscoveryString != null))
                    {
                        if (!userSuppliedDiscoveryString.startsWith("http://") && !userSuppliedDiscoveryString.startsWith("https://"))
                        {
                            if (!userSuppliedDiscoveryString.startsWith("xri://") && !userSuppliedDiscoveryString.startsWith("=") && !userSuppliedDiscoveryString.startsWith("@"))
                            {
                                // extract provider from email address
                                int emailDomainSeparatorIndex = userSuppliedDiscoveryString.indexOf('@');
                                if (emailDomainSeparatorIndex != -1)
                                {
                                    // extract provider host name from email address
                                    provider = userSuppliedDiscoveryString.substring(emailDomainSeparatorIndex+1);
                                }
                                else if (Character.isLetterOrDigit(userSuppliedDiscoveryString.charAt(0)))
                                {
                                    provider = userSuppliedDiscoveryString;
                                }
                            }
                        }
                        else
                        {
                            // extract provider from URL host name
                            int domainIndex = userSuppliedDiscoveryString.indexOf("://")+3;
                            int endDomainIndex = userSuppliedDiscoveryString.indexOf('/', domainIndex);
                            if (endDomainIndex == -1)
                            {
                                endDomainIndex = userSuppliedDiscoveryString.length();
                            }
                            provider = userSuppliedDiscoveryString.substring(domainIndex, endDomainIndex);
                            domainIndex = provider.lastIndexOf('.', provider.length());
                            if (domainIndex > 0)
                            {
                                domainIndex = provider.lastIndexOf('.', domainIndex-1);
                            }
                            if (domainIndex != -1)
                            {
                                provider = provider.substring(domainIndex);
                            }
                        }
                    }
                    if (provider != null)
                    {
                        String providerSuppliedDiscoveryString = getInitParameter(OPEN_ID_DISCOVERY_INIT_PARAM_NAME_PREFIX+provider);
                        if (providerSuppliedDiscoveryString != null)
                        {
                            // lookup override discovery string from configuration
                            userSuppliedDiscoveryString = providerSuppliedDiscoveryString;
                        }
                        else if (userSuppliedDiscoveryString == null)
                        {
                            // use provider for discover string if not specified
                            userSuppliedDiscoveryString = provider;
                        }
                    }

                    // select consumer implementation based on provider
                    String providerConsumer = OPEN_ID_CONSUMER_INIT_PARAM_NAME_VALUE;
                    ConsumerManager providerOpenIDConsumerManager = openIDConsumerManager;
                    if (provider != null)
                    {
                        String consumer = getInitParameter(OPEN_ID_CONSUMER_INIT_PARAM_NAME_PREFIX+provider);
                        if ((consumer != null) && consumer.equals(STEP2_CONSUMER_INIT_PARAM_NAME_VALUE))
                        {
                            providerConsumer = STEP2_CONSUMER_INIT_PARAM_NAME_VALUE;
                            providerOpenIDConsumerManager = openIDStep2ConsumerManager;
                        }
                    }
                    
                    // OpenID discovery
                    DiscoveryInformation discovered = null;
                    try
                    {
                        if (userSuppliedDiscoveryString != null)
                        {
                            List discoveries = null;
                            if (providerConsumer.equals(STEP2_CONSUMER_INIT_PARAM_NAME_VALUE))
                            {
                                // verify discovery string is likely a host name
                                if ((userSuppliedDiscoveryString.indexOf("://") == -1) && (userSuppliedDiscoveryString.indexOf('@') == -1) && (userSuppliedDiscoveryString.indexOf('=') == -1))
                                {
                                    // Step2 OpenId discovery
                                    IdpIdentifier providerIdentifier = new IdpIdentifier(userSuppliedDiscoveryString);
                                    discoveries = providerOpenIDConsumerManager.getDiscovery().discover(providerIdentifier);                                    
                                }
                            }
                            else
                            {
                                // standard OpenId discovery
                                discoveries = providerOpenIDConsumerManager.discover(userSuppliedDiscoveryString);
                            }
                            if ((discoveries != null) && !discoveries.isEmpty())
                            {
                                discovered = providerOpenIDConsumerManager.associate(discoveries);
                            }
                        }
                    }
                    catch (OpenIDException oide)
                    {
                        throw new RuntimeException("Unexpected OpenID discovery exception: "+oide, oide);
                    }
                    if (discovered == null)
                    {
                        throw new RuntimeException("No OpenID provider discovered for: "+userSuppliedDiscoveryString);                    
                    }
                    discoveredProvider = true;

                    // log OpenID provider
                    if (log.isDebugEnabled())
                    {
                        log.debug("Discovered OpenID provider endpoint: "+discovered.getOPEndpoint()+", ["+discovered.getClass().getSimpleName()+"]");
                    }

                    // save login state
                    httpSession.setAttribute(OPEN_ID_LOGIN_LOCALE_ATTR_NAME, request.getLocale());
                    httpSession.setAttribute(OPEN_ID_LOGIN_SERVER_NAME_ATTR_NAME, request.getServerName());

                    // save OpenID provider in session
                    httpSession.setAttribute(OPEN_ID_PROVIDER_ATTR_NAME, provider);
                    httpSession.setAttribute(OPEN_ID_DISCOVERY_INFO_ATTR_NAME, discovered);

                    // create OpenID authentication request and redirect
                    String authReturnToURL = openIDRealmURL+"/"+OpenIDConstants.OPEN_ID_AUTHENTICATED_REQUEST+"?"+OpenIDConstants.OPEN_ID_RETURN+"="+returnPath;
                    String authRedirectURL = null;
                    try
                    {
                        // authentication request
                        AuthRequest authRequest = providerOpenIDConsumerManager.authenticate(discovered, authReturnToURL, openIDRealmURL);
                        // request attribute exchange data
                        FetchRequest axRequest = FetchRequest.createFetchRequest();
                        axRequest.addAttribute("email", "http://axschema.org/contact/email", true);
                        axRequest.addAttribute("fullname", "http://axschema.org/namePerson", true);
                        axRequest.addAttribute("lastname", "http://axschema.org/namePerson/last", true);
                        axRequest.addAttribute("firstname", "http://axschema.org/namePerson/first", true);
                        axRequest.addAttribute("nickname", "http://axschema.org/namePerson/friendly", true);
                        authRequest.addExtension(axRequest);
                        // request simple registration data
                        SRegRequest sregRequest = SRegRequest.createFetchRequest();
                        sregRequest.addAttribute("email", true);
                        sregRequest.addAttribute("fullname", true);
                        sregRequest.addAttribute("nickname", true);
                        authRequest.addExtension(sregRequest);
                        // authentication redirect
                        authRedirectURL = authRequest.getDestinationUrl(true);
                    }
                    catch (OpenIDException oide)
                    {
                        throw new RuntimeException("Unexpected OpenID authentication request exception: "+oide, oide);
                    }
                    response.sendRedirect(authRedirectURL);

                    // log authentication redirect
                    if (log.isDebugEnabled())
                    {
                        log.debug("OpenID authentication redirect: "+authRedirectURL);
                    }
                }
                catch (Exception e)
                {
                    // log error and redirect back to portal with error
                    // set as session attribute
                    log.error("OpenID login error: "+e, e);
                    httpSession.setAttribute(OpenIDConstants.OPEN_ID_ERROR, (!discoveredProvider ? OpenIDConstants.OPEN_ID_ERROR_NO_PROVIDER : OpenIDConstants.OPEN_ID_ERROR_CANNOT_AUTH));
                    response.sendRedirect(returnPath);
                }
            }
            else if (requestPath.equals(OpenIDConstants.OPEN_ID_AUTHENTICATED_REQUEST))
            {
                // request parameters
                String returnPath = request.getParameter(OpenIDConstants.OPEN_ID_RETURN);
                if ((returnPath == null) || (returnPath.length() == 0))
                {
                    returnPath = request.getContextPath()+"/";
                }
                
                // session parameters
                Locale loginLocale = (Locale)httpSession.getAttribute(OPEN_ID_LOGIN_LOCALE_ATTR_NAME);
                if (loginLocale != null)
                {
                    httpSession.removeAttribute(OPEN_ID_LOGIN_LOCALE_ATTR_NAME);
                }
                String loginServerName = (String)httpSession.getAttribute(OPEN_ID_LOGIN_SERVER_NAME_ATTR_NAME);
                if (loginServerName != null)
                {
                    httpSession.removeAttribute(OPEN_ID_LOGIN_SERVER_NAME_ATTR_NAME);
                }
                OpenIDRegistrationConfiguration portalRegistrationConfiguration = (OpenIDRegistrationConfiguration)httpSession.getAttribute(OpenIDConstants.OPEN_ID_REGISTRATION_CONFIGURATION);
                if (portalRegistrationConfiguration != null)
                {
                    httpSession.removeAttribute(OpenIDConstants.OPEN_ID_REGISTRATION_CONFIGURATION);
                }

                boolean authenticatedByProvider = false;
                boolean portalUserExists = false;
                try
                {
                    // request parameters
                    ParameterList authParams = new ParameterList(request.getParameterMap());

                    // retrieve OpenID provider from session
                    String provider = (String)httpSession.getAttribute(OPEN_ID_PROVIDER_ATTR_NAME);
                    DiscoveryInformation discovered = (DiscoveryInformation)httpSession.getAttribute(OPEN_ID_DISCOVERY_INFO_ATTR_NAME);

                    // reconstruct the authenticated request URL
                    StringBuffer authRequestURLBuffer = request.getRequestURL();
                    String authRequestQueryString = request.getQueryString();
                    if ((authRequestQueryString != null) && (authRequestQueryString.length() > 0))
                    {
                        authRequestURLBuffer.append('?').append(authRequestQueryString);
                    }
                    String authRequestURL = authRequestURLBuffer.toString();

                    // select consumer implementation based on provider
                    String providerConsumer = OPEN_ID_CONSUMER_INIT_PARAM_NAME_VALUE;
                    if (provider != null)
                    {
                        String consumer = getInitParameter(OPEN_ID_CONSUMER_INIT_PARAM_NAME_PREFIX+provider);
                        if ((consumer != null) && consumer.equals(STEP2_CONSUMER_INIT_PARAM_NAME_VALUE))
                        {
                            providerConsumer = STEP2_CONSUMER_INIT_PARAM_NAME_VALUE;
                        }
                    }
                    
                    // verify the authenticated request
                    VerificationResults verificationResults = null;
                    if (providerConsumer.equals(STEP2_CONSUMER_INIT_PARAM_NAME_VALUE))
                    {
                        // Step2 OpenId verification
                        verificationResults = openIDStep2Verification(authRequestURL, authParams, discovered);
                    }
                    else
                    {
                        // standard OpenId verification
                        verificationResults = openIDVerification(authRequestURL, authParams, discovered);
                    }
                    VerificationResult verification = verificationResults.verification;
                    Identifier verifiedIdentifier = verificationResults.verifiedIdentifier;

                    // extract identifier from verified authenticated request
                    if (verifiedIdentifier == null)
                    {
                        throw new RuntimeException("Verified identifier unavailable for authenticated OpenID login");                    
                    }
                    authenticatedByProvider = true;
                    
                    String email = null;
                    String firstName = null;
                    String lastName = null;
                    String nickname = null;                
                    String fullName = null;
                    // extract requested attribute exchange data
                    AuthSuccess authResponse = (AuthSuccess)verification.getAuthResponse();
                    if (authResponse.hasExtension(AxMessage.OPENID_NS_AX))
                    {
                        try
                        {
                            FetchResponse axResponse = (FetchResponse)authResponse.getExtension(AxMessage.OPENID_NS_AX);
                            email = axResponse.getAttributeValue("email");
                            fullName = axResponse.getAttributeValue("fullname");
                            firstName = axResponse.getAttributeValue("firstname");
                            lastName = axResponse.getAttributeValue("lastname");
                            nickname = axResponse.getAttributeValue("nickname");
                        }
                        catch (OpenIDException oide)
                        {
                            throw new RuntimeException("Unexpected OpenID authenticated attribute exchange fetch exception: "+oide, oide);
                        }
                    }
                    // extract requested simple registration data
                    if (authResponse.hasExtension(SRegMessage.OPENID_NS_SREG))
                    {
                        try
                        {
                            SRegResponse sregResponse = (SRegResponse)authResponse.getExtension(SRegMessage.OPENID_NS_SREG);
                            email = sregResponse.getAttributeValue("email");
                            fullName = sregResponse.getAttributeValue("fullname");
                            nickname = sregResponse.getAttributeValue("nickname");
                        }
                        catch (OpenIDException oide)
                        {
                            throw new RuntimeException("Unexpected OpenID authenticated simple registration fetch exception: "+oide, oide);
                        }
                    }

                    // log authenticated request
                    if (log.isDebugEnabled())
                    {
                        log.debug("Authenticated OpenID verified identifier: "+verifiedIdentifier.getIdentifier()+", email="+email+", fullname="+fullName+", firstname="+firstName+", lastname="+lastName+", nickname="+nickname);
                    }

                    // validate and default attributes
                    if (email == null)
                    {
                        throw new RuntimeException("OpenID email attribute required for portal login");
                    }
                    if (fullName != null)
                    {
                        String [] fullNames = fullName.split("\\s");
                        if ((firstName == null) && (fullNames.length > 1))
                        {
                            firstName = fullNames[0];
                        }
                        if (lastName == null)
                        {
                            lastName = ((fullNames.length > 1) ? fullNames[fullNames.length-1] : fullName);
                        }
                    }
                    if ((nickname == null) && (firstName != null))
                    {
                        nickname = firstName;
                    }
                    if (nickname == null)
                    {
                        int emailDomainIndex = email.indexOf('@');
                        if (emailDomainIndex != -1)
                        {
                            nickname = email.substring(0, emailDomainIndex);
                        }
                    }
                    // construct portal user attributes
                    Map<String,String> userAttributes = new HashMap<String,String>();
                    userAttributes.put(USER_ATTRIBUTE_EMAIL, email);
                    userAttributes.put(USER_ATTRIBUTE_NAME, email);
                    if (firstName != null)
                    {
                        userAttributes.put(USER_ATTRIBUTE_GIVEN_NAME, firstName);
                    }
                    if (lastName != null)
                    {
                        userAttributes.put(USER_ATTRIBUTE_FAMILY_NAME, lastName);                                
                    }
                    if (nickname != null)
                    {
                        userAttributes.put(USER_ATTRIBUTE_NICKNAME, nickname);                                
                    }

                    // login to portal using email, creating portal
                    // user if necessary
                    User portalUser = null;
                    try
                    {
                        portalUser = portalUserManager.getUser(email);
                    }
                    catch (Exception e)
                    {                        
                    }

                    // create portal user if not found
                    if (portalUser == null)
                    {
                        try
                        {
                            // select portal registration configuration and
                            // register portal user
                            String logConfiguration = "none";
                            if (portalRegistrationConfiguration != null)
                            {
                                portalRegistrationConfiguration.merge(initRegistrationConfiguration);
                                logConfiguration = "session, (from login)";
                            }
                            else if (initRegistrationConfiguration != null)
                            {
                                portalRegistrationConfiguration = initRegistrationConfiguration;
                                logConfiguration = "init params";
                            }
                            if ((portalRegistrationConfiguration == null) || portalRegistrationConfiguration.isEnableRegistration())
                            {
                                if (portalRegistrationConfiguration != null)
                                {
                                    portalAdministration.registerUser(email, null,
                                                                      portalRegistrationConfiguration.getRoles(),
                                                                      portalRegistrationConfiguration.getGroups(),
                                                                      userAttributes,
                                                                      portalRegistrationConfiguration.getProfilerRules(),
                                                                      portalRegistrationConfiguration.getUserTemplateDirectory(),
                                                                      portalRegistrationConfiguration.getSubsiteRootFolder(),
                                                                      loginLocale, loginServerName);
                                }
                                else
                                {
                                    portalAdministration.registerUser(email, null, null, null, userAttributes, null, null, null,
                                                                      loginLocale, loginServerName);
                                }
                                portalUser = portalUserManager.getUser(email);

                                // log registered user
                                if (log.isDebugEnabled())
                                {
                                    if (portalUser != null)
                                    {
                                        log.debug("OpenID login registered portal user: "+portalUser.getName()+", configuration: "+logConfiguration+", locale: "+loginLocale+", server: "+loginServerName);
                                    }
                                    else
                                    {
                                        log.debug("OpenID login unregistered portal user, (registration failed): "+email+", configuration: "+logConfiguration+", locale: "+loginLocale+", server: "+loginServerName);
                                    }
                                }
                            }
                            else
                            {
                                // log unregistered user
                                if (log.isDebugEnabled())
                                {
                                    log.debug("OpenID login unregistered portal user, (registration disabled): "+email+", configuration: "+logConfiguration);
                                }                                
                            }
                        }
                        catch (Exception e)
                        {                        
                            throw new RuntimeException("Unable to register portal user: "+email);
                        }
                    }

                    // login registered portal user
                    if (portalUser == null)
                    {
                        throw new RuntimeException("Missing registered portal user: "+email);
                    }
                    portalUserExists = true;

                    // update portal user attributes
                    boolean portalUserAttributesUpdated = false;
                    SecurityAttributes portalUserAttributes = portalUser.getSecurityAttributes();
                    for (Map.Entry<String,String> attribute : userAttributes.entrySet())
                    {
                        String name = attribute.getKey();
                        String value = attribute.getValue();
                        SecurityAttribute userAttribute = portalUserAttributes.getAttribute(name, true);
                        if (!value.equals(userAttribute.getStringValue()))
                        {
                            userAttribute.setStringValue(value);
                            portalUserAttributesUpdated = true;
                        }
                    }
                    if (portalUserAttributesUpdated)
                    {
                        try
                        {
                            portalUserManager.updateUser(portalUser);
                        }
                        catch (Exception e)
                        {
                            throw new RuntimeException("Unable to update attributes for user: "+portalUser.getName());
                        }
                    }
                    
                    Subject subject = null;
                    try
                    {
                        // create subject for portal user
                        subject = portalUserManager.getSubject(portalUser);
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException("Unable to login portal user: "+portalUser.getName());
                    }
                    // create/reset portal session
                    if (portalAuthenticationConfiguration.isCreateNewSessionOnLogin())
                    {
                        httpSession.invalidate();
                        httpSession = request.getSession(true);
                    }
                    else
                    {
                        portalUserContentCacheManager.evictUserContentCache(portalUser.getName(), httpSession.getId());
                    }                         
                    // configure portal session with logged in session
                    httpSession.setAttribute(PortalReservedParameters.SESSION_OPEN_ID_PROVIDER, provider);
                    httpSession.setAttribute(PortalReservedParameters.SESSION_KEY_SUBJECT, subject);

                    // log/audit portal user login
                    portalAudit.logUserActivity(portalUser.getName(), request.getRemoteAddr(), AuditActivity.AUTHENTICATION_SUCCESS, "OpenIDRelayingPartyServlet");

                    // redirect back to portal
                    response.sendRedirect(returnPath);
                }
                catch (Exception e)
                {
                    // log error and redirect back to portal
                    log.error("OpenID login error: "+e, e);
                    httpSession.setAttribute(OpenIDConstants.OPEN_ID_ERROR, (!authenticatedByProvider ? OpenIDConstants.OPEN_ID_ERROR_NOT_AUTH : (!portalUserExists ? OpenIDConstants.OPEN_ID_ERROR_NO_PORTAL_USER : OpenIDConstants.OPEN_ID_ERROR_CANNOT_LOGIN)));
                    response.sendRedirect(returnPath);
                }
            }
            else if (requestPath.equals(OpenIDConstants.OPEN_ID_LOGOUT_REQUEST))
            {
                // request parameters
                String returnPath = request.getParameter(OpenIDConstants.OPEN_ID_RETURN);                
                if ((returnPath == null) || (returnPath.length() == 0))
                {
                    returnPath = request.getContextPath()+"/";
                }

                // clear portal session
                httpSession.invalidate();
                
                // redirect back to portal
                response.sendRedirect(returnPath);
            }
            else
            {
                throw new ServletException("Unexpected OpenID request: "+requestPath);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public final void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        doGet(request, response);
    }
    
    /**
     * Compute OpenID realm URL from request.
     * 
     * @param request servlet request
     * @return OpenID realm URL
     */
    private String openIDRealmURL(HttpServletRequest request)
    {
        StringBuilder openIDRealmURLBuilder = new StringBuilder(request.isSecure() ? "https://" : "http://");
        openIDRealmURLBuilder.append(request.getServerName());
        if ((request.isSecure() && (request.getServerPort() != 443)) || (!request.isSecure() && (request.getServerPort() != 80)))
        {
            openIDRealmURLBuilder.append(':').append(request.getServerPort());
        }
        openIDRealmURLBuilder.append(request.getContextPath()+request.getServletPath());
        return openIDRealmURLBuilder.toString();        
    }
    
    /**
     * OpenID authenticated request verification results.
     */
    private class VerificationResults
    {
        public VerificationResult verification = null;
        public Identifier verifiedIdentifier = null;
    }
    
    /**
     * Standard OpenId authenticated request verification.
     * 
     * @param authRequestURL authenticated request URL
     * @param authParams authenticated request parameters
     * @param discovered discovery information
     * @return verification result
     */
    private VerificationResults openIDVerification(String authRequestURL, ParameterList authParams, DiscoveryInformation discovered)
    {
        try
        {
            if (log.isDebugEnabled())
            {
                log.debug("Verify standard OpenID authentication request using: "+discovered.getOPEndpoint());
            }

            VerificationResults results = new VerificationResults();
            // verify using previously discovered discovery information
            results.verification = openIDConsumerManager.verify(authRequestURL, authParams, discovered);

            if (log.isDebugEnabled() && (results.verification != null))
            {
                log.debug("Verified standard OpenID authentication request: "+authRequestURL);
            }            
            
            // return verified identifier
            results.verifiedIdentifier = results.verification.getVerifiedId();

            if (log.isDebugEnabled() && (results.verifiedIdentifier != null))
            {
                log.debug("Verified standard OpenID authentication request identity: "+results.verifiedIdentifier);
            }
            
            return results;
        }
        catch (OpenIDException oide)
        {
            throw new RuntimeException("Unexpected standard OpenId authenticated request verification exception: "+oide, oide);
        }
    }

    /**
     * Step2 OpenId authenticated request verification.
     * 
     * @param authRequestURL authenticated request URL
     * @param authParams authenticated request parameters
     * @param discovered discovery information
     * @return verification result
     */
    private VerificationResults openIDStep2Verification(String authRequestURL, ParameterList authParams, DiscoveryInformation discovered)
    {
        try
        {
            VerificationResults results = new VerificationResults();
            // verify OpenId authentication request
            String openIdMode = authParams.getParameterValue("openid.mode");
            if ((openIdMode != null) && openIdMode.equals("id_res"))
            {
                AuthSuccess authResponse = AuthSuccess.createAuthSuccess(authParams);
                if ((authResponse != null) && authResponse.isVersion2() && (authResponse.getIdentity() != null) && (authResponse.getClaimed() != null))
                {
                    // get OpenId identifier
                    String providerId = authResponse.getIdentity();
                    Identifier responseClaimedId = openIDStep2ConsumerManager.getDiscovery().parseIdentifier(authResponse.getClaimed(), true);
                    String responseEndpoint = authResponse.getOpEndpoint();
                    
                    if (log.isDebugEnabled())
                    {
                        log.debug("Step2 discovery for identity: "+responseClaimedId);
                    }

                    // get Step2 secure discovery information
                    SecureDiscoveryInformation secureDiscovered = null;
                    
                    // validate previously discovered secure discovery information
                    if (discovered instanceof SecureDiscoveryInformation)
                    {
                        // check for matching version, identifiers, and endpoints
                        if (discovered.isVersion2() && discovered.hasClaimedIdentifier() && discovered.getClaimedIdentifier().equals(responseClaimedId) && discovered.getOPEndpoint().equals(responseEndpoint))
                        {
                            String discoveredProviderId = (discovered.hasDelegateIdentifier() ? discovered.getDelegateIdentifier() : discovered.getClaimedIdentifier().getIdentifier());
                            if (discoveredProviderId.equals(providerId))
                            {
                                secureDiscovered = (SecureDiscoveryInformation)discovered;

                                if (log.isDebugEnabled())
                                {
                                    log.debug("Matched previously discovered Step2 secure discovery information for "+responseClaimedId+" identity: "+secureDiscovered.getOPEndpoint());
                                }
                            }
                        }
                    }

                    // discover secure discovery information if necessary
                    if (secureDiscovered == null)
                    {
                        // perform discovery on claimed identifier
                        List<SecureDiscoveryInformation> discoveredInfos = openIDStep2ConsumerManager.getDiscovery().discover(responseClaimedId);
                        // match secure discovered information: prefer previously associated matches
                        for (SecureDiscoveryInformation discoveredInfo : discoveredInfos)
                        {
                            // match secure discovered information
                            String version = discoveredInfo.getVersion();
                            if ((version != null) && version.equals(DiscoveryInformation.OPENID2_OP) && discoveredInfo.isVersion2() && discoveredInfo.getOPEndpoint().equals(responseEndpoint))
                            {
                                String discoveredProviderId = (discoveredInfo.hasDelegateIdentifier() ? discoveredInfo.getDelegateIdentifier() : discoveredInfo.getClaimedIdentifier().getIdentifier());
                                if (discoveredProviderId.equals(providerId))
                                {
                                    // match previously associated or first discovered
                                    if (openIDStep2ConsumerManager.getPrivateAssociationStore().load(discoveredInfo.getOPEndpoint().toString(), authResponse.getHandle()) != null)
                                    {
                                        secureDiscovered = discoveredInfo;
                                        break;
                                    }
                                    else if (secureDiscovered == null)
                                    {
                                        secureDiscovered = discoveredInfo;                                    
                                    }
                                }                            
                            }
                        }

                        if (log.isDebugEnabled() && (secureDiscovered != null))
                        {
                            log.debug("Discovered Step2 secure discovery information for "+responseClaimedId+" identity: "+secureDiscovered.getOPEndpoint());
                        }
                    }

                    if (log.isDebugEnabled() && (secureDiscovered != null))
                    {
                        log.debug("Verify Step2 OpenID authentication request using: "+secureDiscovered.getOPEndpoint());
                    }

                    // verify using secure discovery information
                    results.verification = openIDStep2ConsumerManager.verify(authRequestURL, authParams, secureDiscovered);

                    if (log.isDebugEnabled() && (results.verification != null))
                    {
                        log.debug("Verified Step2 OpenID authentication request: "+authRequestURL);
                    }
                    
                    // verify secure verified identifier
                    if ((results.verification.getAuthResponse() instanceof AuthSuccess) && (results.verification.getVerifiedId() != null))
                    {
                        // verify secure verification
                        boolean secureVerification = ((secureDiscovered != null) && (secureDiscovered.getClaimedIdentifier() != null) && secureDiscovered.isSecure());
                        if (secureVerification)
                        {
                            try
                            {
                                UrlIdentifier verifiedClaimedId = new UrlIdentifier(results.verification.getVerifiedId().getIdentifier(), true);
                                secureVerification = secureDiscovered.getClaimedIdentifier().getIdentifier().equals(verifiedClaimedId.getIdentifier());
                            }
                            catch (OpenIDException oide)
                            {
                                secureVerification = false;
                            }
                        }
                        
                        // return verified identifier
                        Identifier verifiedId = results.verification.getVerifiedId();
                        results.verifiedIdentifier = (secureVerification ? new SecureUrlIdentifier(verifiedId) : verifiedId);

                        if (log.isDebugEnabled())
                        {
                            log.debug("Verified Step2 OpenID authentication request identity: "+results.verifiedIdentifier);
                        }
                    }
                    else
                    {
                        throw new RuntimeException("Step2 OpenId authenticated request verification failed");
                    }
                }
            }
            return results;
        }
        catch (OpenIDException oide)
        {
            throw new RuntimeException("Unexpected Step2 OpenId authenticated request verification exception: "+oide, oide);
        }
    }
}
