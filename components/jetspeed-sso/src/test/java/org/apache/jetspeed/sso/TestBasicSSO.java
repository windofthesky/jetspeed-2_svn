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
package org.apache.jetspeed.sso;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.auth.HttpAuthenticator;
import org.apache.commons.httpclient.methods.GetMethod;


/**
 * TestBasicSSO
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class TestBasicSSO extends TestCase
{
    public static Test suite()
    {
        return new TestSuite(TestBasicSSO.class);
    }

    public void testBasicSSO() throws Exception
    {
        System.out.println("Testing SSO");                                      
        // connect("http://localhost:8080/demo/sso-basic", "tomcat", "tomcat");
    }
    
    public void connect(String server, String username, String password) throws Exception
    {
        HttpClient client = new HttpClient();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        StringBuffer authenticationHeader = new StringBuffer("BASIC");
        authenticationHeader.append(" realm=\"");
        authenticationHeader.append("jetspeed");
        authenticationHeader.append("\"");
        Header[] headers = { new Header("WWW-Authenticate", authenticationHeader.toString())};
        AuthScheme scheme = null;

        client.getState().setCredentials(null, null, credentials);
        GetMethod get = new GetMethod(server);
        // post = new MultipartPostMethod(server);
        get.setDoAuthentication(true);

        try
        {
            scheme = HttpAuthenticator.selectAuthScheme(headers);
            HttpConnection conn = client.getHttpConnectionManager().getConnection(get.getHostConfiguration());
            boolean authenticated = HttpAuthenticator.authenticate(scheme, get, conn, client.getState());
            if (!authenticated)
            {
                throw new Exception("Failed to create authentication headers to HTTP Connection");
            }
            client.executeMethod(get);
            System.out.println("response = [" + get.getResponseBodyAsString() + "]");

            Cookie[] cookies = client.getState().getCookies();
            Cookie mycookie = null;
            // Display the cookies
            System.out.println("Present cookies: ");
            for (int i = 0; i < cookies.length; i++) 
            {
                System.out.println(" - " + cookies[i].toExternalForm());
                mycookie = cookies[i];
            }
           // get.releaseConnection();
           // client.endSession();
            
            HttpState initialState = new HttpState();            
            initialState.addCookie(mycookie);
            // client.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
            client = new HttpClient();
            client.setState(initialState);
            get = new GetMethod(server);
            
            client.executeMethod(get);
            System.out.println("response = [" + get.getResponseBodyAsString() + "]");

            cookies = client.getState().getCookies();
            // Display the cookies
            System.out.println("Present cookies: ");
            for (int i = 0; i < cookies.length; i++) 
            {
                System.out.println(" - " + cookies[i].toExternalForm());
            }
            get.releaseConnection();
        }
        catch (Throwable t)
        {
            throw new Exception("Unexpected exception in creating HTTP authentication headers", t);
        }
    }
    
}
