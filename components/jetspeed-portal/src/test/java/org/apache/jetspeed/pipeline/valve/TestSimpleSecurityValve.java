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

package org.apache.jetspeed.pipeline.valve;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import junit.framework.TestCase;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.jetspeed.container.state.MockRequestContext;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.impl.SimpleSecurityValveImpl;
import org.apache.jetspeed.request.RequestContext;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Test for simple SecurityValve implementation.
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public class TestSimpleSecurityValve extends TestCase {

    /**
     * Test SimpleSecurityValveImpl implementation.
     *
     * @throws Exception on unexpected exception
     */
    public void testRequestFilterValve() throws Exception {

        // test default configuration
        SecurityValve valve = new SimpleSecurityValveImpl("test-valve");
        valve.initialize();
        RequestContext requestContext = createRequestContext("/test", "127.0.0.1", null, null);
        StubValveContext valveContext = new StubValveContext();
        valve.invoke(requestContext, valveContext);
        assertNull(((MockHttpServletResponse) requestContext.getResponse()).getHeader(SimpleSecurityValveImpl.HTTP_WWW_AUTHENTICATE_HEADER));
        assertFalse(((MockHttpServletResponse) requestContext.getResponse()).wasErrorSent());
        assertTrue(valveContext.nextInvoked);

        // test HTTP Basic Authentication configuration
        valve = new SimpleSecurityValveImpl("test-valve");
        ((SimpleSecurityValveImpl)valve).setIncludes(Arrays.asList(new String[]{"/include/**"}));
        ((SimpleSecurityValveImpl)valve).setAuthenticationRealm("test-realm");
        ((SimpleSecurityValveImpl)valve).setAuthenticationUser("test-user");
        ((SimpleSecurityValveImpl)valve).setAuthenticationPasswordHash(DigestUtils.md5Hex("test-password"));
        valve.initialize();
        requestContext = createRequestContext("/include/test", "127.0.0.1", "test-user", "test-password");
        valveContext = new StubValveContext();
        valve.invoke(requestContext, valveContext);
        assertNull(((MockHttpServletResponse) requestContext.getResponse()).getHeader(SimpleSecurityValveImpl.HTTP_WWW_AUTHENTICATE_HEADER));
        assertFalse(((MockHttpServletResponse) requestContext.getResponse()).wasErrorSent());
        assertTrue(valveContext.nextInvoked);
        requestContext = createRequestContext("/include/test", "127.0.0.1", null, null);
        valveContext = new StubValveContext();
        valve.invoke(requestContext, valveContext);
        assertEquals("Basic realm=\"test-realm\"", ((MockHttpServletResponse) requestContext.getResponse()).getHeader(SimpleSecurityValveImpl.HTTP_WWW_AUTHENTICATE_HEADER));
        assertTrue(((MockHttpServletResponse) requestContext.getResponse()).wasErrorSent());
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, ((MockHttpServletResponse) requestContext.getResponse()).getErrorCode());
        assertFalse(valveContext.nextInvoked);
        requestContext = createRequestContext("/include/test", "127.0.0.1", "not-test-user", "not-test-password");
        valveContext = new StubValveContext();
        valve.invoke(requestContext, valveContext);
        assertEquals("Basic realm=\"test-realm\"", ((MockHttpServletResponse) requestContext.getResponse()).getHeader(SimpleSecurityValveImpl.HTTP_WWW_AUTHENTICATE_HEADER));
        assertTrue(((MockHttpServletResponse) requestContext.getResponse()).wasErrorSent());
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, ((MockHttpServletResponse) requestContext.getResponse()).getErrorCode());
        assertFalse(valveContext.nextInvoked);
        requestContext = createRequestContext("/exclude/test", "127.0.0.1", null, null);
        valveContext = new StubValveContext();
        valve.invoke(requestContext, valveContext);
        assertNull(((MockHttpServletResponse) requestContext.getResponse()).getHeader(SimpleSecurityValveImpl.HTTP_WWW_AUTHENTICATE_HEADER));
        assertFalse(((MockHttpServletResponse) requestContext.getResponse()).wasErrorSent());
        assertTrue(valveContext.nextInvoked);

        // test valid IP Address configuration
        valve = new SimpleSecurityValveImpl("test-valve");
        ((SimpleSecurityValveImpl)valve).setIncludes(Arrays.asList(new String[]{"/include/**"}));
        ((SimpleSecurityValveImpl)valve).setExcludes(Arrays.asList(new String[]{"/include/exclude/**"}));
        ((SimpleSecurityValveImpl)valve).setValidIPAddresses(Arrays.asList(new String[]{"127.0.0.1", "10.0.0.0/8"}));
        valve.initialize();
        requestContext = createRequestContext("/include/test", "127.0.0.1", null, null);
        valveContext = new StubValveContext();
        valve.invoke(requestContext, valveContext);
        assertNull(((MockHttpServletResponse) requestContext.getResponse()).getHeader(SimpleSecurityValveImpl.HTTP_WWW_AUTHENTICATE_HEADER));
        assertFalse(((MockHttpServletResponse) requestContext.getResponse()).wasErrorSent());
        assertTrue(valveContext.nextInvoked);
        requestContext = createRequestContext("/include/test", "10.0.0.23", null, null);
        valveContext = new StubValveContext();
        valve.invoke(requestContext, valveContext);
        assertNull(((MockHttpServletResponse) requestContext.getResponse()).getHeader(SimpleSecurityValveImpl.HTTP_WWW_AUTHENTICATE_HEADER));
        assertFalse(((MockHttpServletResponse) requestContext.getResponse()).wasErrorSent());
        assertTrue(valveContext.nextInvoked);
        requestContext = createRequestContext("/include/test", "173.194.79.105", null, null);
        valveContext = new StubValveContext();
        valve.invoke(requestContext, valveContext);
        assertNull(((MockHttpServletResponse) requestContext.getResponse()).getHeader(SimpleSecurityValveImpl.HTTP_WWW_AUTHENTICATE_HEADER));
        assertTrue(((MockHttpServletResponse) requestContext.getResponse()).wasErrorSent());
        assertEquals(HttpServletResponse.SC_FORBIDDEN, ((MockHttpServletResponse) requestContext.getResponse()).getErrorCode());
        assertFalse(valveContext.nextInvoked);
        requestContext = createRequestContext("/include/exclude/test", "173.194.79.105", null, null);
        valveContext = new StubValveContext();
        valve.invoke(requestContext, valveContext);
        assertNull(((MockHttpServletResponse) requestContext.getResponse()).getHeader(SimpleSecurityValveImpl.HTTP_WWW_AUTHENTICATE_HEADER));
        assertFalse(((MockHttpServletResponse) requestContext.getResponse()).wasErrorSent());
        assertTrue(valveContext.nextInvoked);
    }



    /**
     * Create mock RequestContext with the specified HttpServletRequest pathInfo,
     * remoteAddr, and HTTP Basic Authorization header.
     *
     * @param pathInfo request pathInfo
     * @param remoteAddr request remoteAddr
     * @param user request HTTP Basic Authentication user or null
     * @param password request HTTP Basic Authentication password or null
     * @return mock RequestContext
     */
    private RequestContext createRequestContext(String pathInfo, String remoteAddr, String user, String password) {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setPathInfo(pathInfo);
        servletRequest.setRemoteAddr(remoteAddr);
        if ((user != null) && (password != null)) {
            String authorization = user+":"+password;
            try {
                authorization = "Basic " + Base64.encodeBase64String(authorization.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException uee) {
            }
            servletRequest.setHeader(SimpleSecurityValveImpl.HTTP_AUTHORIZATION_HEADER, authorization);
        }
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        RequestContext requestContext = new MockRequestContext();
        requestContext.setRequest(servletRequest);
        requestContext.setResponse(servletResponse);
        return requestContext;
    }

    /**
     * Stub ValveContext implementation that tracks ValveContext.invokeNext() invocations.
     */
    private class StubValveContext implements ValveContext {

        private boolean nextInvoked;

        @Override
        public void invokeNext(RequestContext request) throws PipelineException {
            nextInvoked = true;
        }
    }

}
