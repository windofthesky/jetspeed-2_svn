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

package org.apache.jetspeed.pipeline.valve.impl;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.SecurityValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * This valve implementation filters incoming requests based on simple HTTP Basic
 * Authentication and/or Remote IP Address.
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public class SimpleSecurityValveImpl extends AbstractFilterValveImpl implements SecurityValve {

    private static final Logger log = LoggerFactory.getLogger(SimpleSecurityValveImpl.class);

    public static final String HTTP_AUTHORIZATION_HEADER = "Authorization";
    public static final String HTTP_WWW_AUTHENTICATE_HEADER = "WWW-Authenticate";

    private static final String DEFAULT_AUTHENTICATION_REALM = "Jetspeed Portal";

    /** Valve name. */
    private String name;

    /** HTTP Basic Authentication realm. */
    private String authenticationRealm;

    /** HTTP Basic Authentication username. */
    private String authenticationUser;

    /** HTTP Basic Authentication password MD5 HEX hash. */
    private String authenticationPasswordHash;

    /** Valid IP addresses and/or subnets. */
    private List<String> validIPAddresses;

    /** Parsed valid IP addresses. */
    private List<ValidIPAddress> parsedValidIPAddresses = new ArrayList<ValidIPAddress>();

    /**
     * Named valve constructor.
     *
     * @param name name of valve
     */
    public SimpleSecurityValveImpl(String name) {
        this.name = name;
    }

    @Override
    public void invoke(RequestContext request, ValveContext context) throws PipelineException {

        // get request path relative to pipeline/servlet path
        String requestPath = request.getRequest().getPathInfo();

        // test request path includes and excludes
        if (includesRequestPath(requestPath) && !excludesRequestPath(requestPath)) {

            // check HTTP Basic Authentication
            if (authenticationRealm != null) {
                boolean authorized = false;
                String authorizationHeader = request.getRequest().getHeader(HTTP_AUTHORIZATION_HEADER);
                if ((authorizationHeader != null) && (authorizationHeader.startsWith("Basic "))) {
                    authorizationHeader = authorizationHeader.substring(6);
                    try {
                        authorizationHeader = new String(Base64.decodeBase64(authorizationHeader), "UTF-8");
                    } catch (Exception e) {
                        authorizationHeader = null;
                    }
                    if ((authorizationHeader != null) && !authorizationHeader.isEmpty()) {
                        String[] authorizationCredentials = authorizationHeader.split(":");
                        if ((authorizationCredentials.length == 2) && authenticationUser.equals(authorizationCredentials[0])) {
                            String authorizationCredentialsPasswordHash = DigestUtils.md5Hex(authorizationCredentials[1]);
                            authorized = authorizationCredentialsPasswordHash.equalsIgnoreCase(authenticationPasswordHash);
                        }
                    }
                }
                if (!authorized) {
                    if (log.isDebugEnabled()) {
                        log.debug("Request filtered by " + request.getPipeline().getName() + "." + name + " authorization: " + authorizationHeader);
                    }
                    try {
                        request.getResponse().setHeader(HTTP_WWW_AUTHENTICATE_HEADER, "Basic realm=\"" + authenticationRealm + "\"");
                        request.getResponse().sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    } catch (IOException ioe) {
                        if (log.isDebugEnabled()) {
                            log.error("Unexpected exception sending error for filtered request, (" + request.getRequest().getPathInfo() + "): " + ioe, ioe);
                        }
                    }
                    return;
                }
            }

            // check valid remote IP address
            if (!parsedValidIPAddresses.isEmpty()) {
                String remoteIPAddress = request.getRequest().getRemoteAddr();
                boolean valid = false;
                try {
                    int remoteIP = getIP(remoteIPAddress);
                    for (ValidIPAddress validIPAddress : parsedValidIPAddresses) {
                        if (validIPAddress.matchIP(remoteIP)) {
                            valid = true;
                            break;
                        }
                    }
                } catch (Exception e) {
                }
                if (!valid) {
                    if (log.isDebugEnabled()) {
                        log.debug("Request filtered by " + request.getPipeline().getName() + "." + name + " IP address: " + remoteIPAddress);
                    }
                    try {
                        request.getResponse().sendError(HttpServletResponse.SC_FORBIDDEN);
                    } catch (IOException ioe) {
                        if (log.isDebugEnabled()) {
                            log.error("Unexpected exception sending error for filtered request, (" + request.getRequest().getPathInfo() + "): " + ioe, ioe);
                        }
                    }
                    return;
                }
            }
        }

        // continue valve execution on pipeline
        context.invokeNext(request);
    }

    @Override
    public void initialize() throws PipelineException {

        // validate HTTP Basic Authentication configuration
        if ((authenticationUser != null) && !authenticationUser.isEmpty() && (authenticationPasswordHash != null) && !authenticationPasswordHash.isEmpty()) {
            if ((authenticationRealm == null) || authenticationRealm.isEmpty()) {
                authenticationRealm = DEFAULT_AUTHENTICATION_REALM;
            }
        } else {
            authenticationRealm = null;
            authenticationUser = null;
            authenticationPasswordHash = null;
        }

        // setup valid IP addresses configuration
        if ((validIPAddresses != null) && !validIPAddresses.isEmpty()) {
            for (String validIPAddress : validIPAddresses) {
                try {
                    parsedValidIPAddresses.add(new ValidIPAddress(validIPAddress));
                } catch (Exception e) {
                    log.error("SimpleSecurityValve: unable to parse valid IP address '"+validIPAddress+"': " + e, e);
                }
            }
        }
    }

    /**
     * Get valve name.
     *
     * @return valve name
     */
    public String getName() {
        return name;
    }

    /**
     * Get HTTP Basic Authentication realm.
     *
     * @return HTTP Basic Authentication realm
     */
    public String getAuthenticationRealm() {
        return authenticationRealm;
    }

    /**
     * Set HTTP Basic Authentication realm.
     *
     * @param authenticationRealm HTTP Basic Authentication realm
     */
    public void setAuthenticationRealm(String authenticationRealm) {
        this.authenticationRealm = authenticationRealm;
    }

    /**
     * Get HTTP Basic Authentication username.
     *
     * @return HTTP Basic Authentication username
     */
    public String getAuthenticationUser() {
        return authenticationUser;
    }

    /**
     * Set HTTP Basic Authentication username.
     *
     * @param authenticationUser HTTP Basic Authentication username
     */
    public void setAuthenticationUser(String authenticationUser) {
        this.authenticationUser = authenticationUser;
    }

    /**
     * Get HTTP Basic Authentication password MD5 HEX hash.
     *
     * @return HTTP Basic Authentication password MD5 HEX hash
     */
    public String getAuthenticationPasswordHash() {
        return authenticationPasswordHash;
    }

    /**
     * Set HTTP Basic Authentication password MD5 HEX hash.
     *
     * @param authenticationPasswordHash HTTP Basic Authentication password MD5 HEX hash
     */
    public void setAuthenticationPasswordHash(String authenticationPasswordHash) {
        this.authenticationPasswordHash = authenticationPasswordHash;
    }

    /**
     * Get valid IP addresses and/or subnets.
     *
     * @return list of valid IP addresses and/or subnets
     */
    public List<String> getValidIPAddresses() {
        return validIPAddresses;
    }

    /**
     * Set valid IP addresses and/or subnets.
     *
     * @param validIPAddresses list of valid IP addresses and/or subnets
     */
    public void setValidIPAddresses(List<String> validIPAddresses) {
        this.validIPAddresses = validIPAddresses;
    }

    /**
     * Class used to capture IPV4 addresses and subnets for matching tests.
     */
    private static class ValidIPAddress {
        private int mask;
        private int ip;

        private ValidIPAddress(String validIPAddress) throws UnknownHostException {
            int bitsIndex = validIPAddress.indexOf("/");
            if (bitsIndex != -1) {
                int bits = Integer.parseInt(validIPAddress.substring(bitsIndex+1));
                this.mask = -1 << (32 - bits);
                validIPAddress = validIPAddress.substring(0, bitsIndex);
            } else {
                this.mask = -1;
            }
            this.ip = getIP(validIPAddress) & this.mask;
        }

        private boolean matchIP(int testIP) {
            return ((testIP & mask) == ip);
        }
    }

    /**
     * Convert string IPV4 address to int representation which is easier to mask.
     *
     * @param ipAddress String IPV4 address
     * @return int address
     * @throws UnknownHostException
     */
    private static int getIP(String ipAddress) throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName(ipAddress);
        if (inetAddress instanceof Inet4Address) {
            byte [] bytes = ((Inet4Address)inetAddress).getAddress();
            return ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | ((bytes[3] & 0xFF) << 0);
        } else {
            throw new UnknownHostException(ipAddress);
        }
    }
}
