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
package org.apache.jetspeed.sso.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.sso.SSOClient;
import org.apache.jetspeed.sso.SSOException;
import org.apache.jetspeed.sso.SSOSite;


/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class SSOClientImpl implements SSOClient
{

    /* Logging */
    private static final Logger log = LoggerFactory.getLogger(SSOClientImpl.class);
    
    private SSOSite site;
    private PasswordCredential credential;
    private HttpClient httpClient;
    
    public SSOClientImpl(SSOSite site, PasswordCredential credential)
    {
        super();
        this.credential = credential;
        this.site = site;
    }

    public String get(String destUrl, boolean refreshProxy)  throws SSOException {
    	StringWriter sw = new StringWriter();
    	write(destUrl,refreshProxy,sw);
    	return sw.toString();
    }
    
    public void write(String destUrl, boolean refreshProxy, Writer out)  throws SSOException 
    {
        
        GetMethod get = null;
        
        if (refreshProxy == true || httpClient == null)
        {
            get = new GetMethod(site.getURL());
            
            login(get);
            
            /*
             * If the destination URL and the SSO url match
             * use the authentication process but return immediately
             * the result page.
             */
            if( destUrl.compareTo(site.getURL()) == 0)
            {
                try{
                    IOUtils.copy(get.getResponseBodyAsStream(), out);
                } catch (IOException ioe){
                    log.error("Error while reading response from remote site at "+destUrl);
                }                
                get.releaseConnection();
                return;
            } else {
                get.releaseConnection();
            }
        }
        
        // All the SSO authentication done go to the destination url
        get = new GetMethod(destUrl);
        try {
            // execute the GET
            httpClient.executeMethod( get );
            
        } catch (Exception e) {
            log.error("Exception while fetching SSO content. Error: " +e);                            
        }
        
        
        try
        {
        	IOUtils.copy(get.getResponseBodyAsStream(), out);
        }
        catch(IOException ioe)
        {
            throw new SSOException ("SSO Component Error. Failed to get content for URL " + destUrl, ioe);
        }
        catch (Exception e)
        {
            throw new SSOException ("SSO Component Error. Failed to get content for URL " + destUrl, e);
            
        }            
        
        get.releaseConnection();
        
        return;
    }

    public boolean login() throws SSOException {
        GetMethod get = new GetMethod(site.getURL().toString());
        
        int status = login(get);
        
        get.releaseConnection();
        
        return status >= 200 && status < 300;
    }
    
    private int login(GetMethod get) throws SSOException {
        URL url = null;
        
        try{
            url = new URL(site.getURL());
        } catch (MalformedURLException muex){
            log.error(muex.getMessage(), muex);
            throw new SSOException("SSO: Marlformed url: "+site.getURL());
        }
        httpClient = new HttpClient();
        httpClient.getState().setCookiePolicy(CookiePolicy.COMPATIBILITY);
        
        httpClient.getState().setCredentials(
                site.getRealm(),
                url.getHost(),
                new UsernamePasswordCredentials(credential.getUserName(), SSOUtils.unscramble(credential.getPassword()))
            );
     // Build URL if it's Form authentication
        StringBuffer siteURL = new StringBuffer(site.getURL());
        
        // Check if it's form based or ChallengeResponse
        if (site.isFormAuthentication())
        {
            siteURL.append("?").append(site.getFormUserField()).append("=").append(credential.getUserName()).append("&").append(site.getFormPwdField()).append("=").append(SSOUtils.unscramble(credential.getPassword()));
        }
        
        get.setDoAuthentication( true );
        // execute the GET
        int status = -1;
        
        try{
            status = httpClient.executeMethod( get );   
        } catch (IOException ioe){
            log.error("SSO: Error executing get method for url "+site.getURL());
            throw new SSOException(ioe);
        }
        return status;      
    }
}
