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
import org.apache.jetspeed.container.state.MockRequestContext;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.impl.RequestFilterValveImpl;
import org.apache.jetspeed.request.RequestContext;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * Test for RequestFilterValve implementation.
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public class TestRequestFilterValve extends TestCase {

    /**
     * Test RequestFilterValveImpl implementation.
     *
     * @throws Exception on unexpected exception
     */
    public void testRequestFilterValve() throws Exception {

        // test default filtering
        RequestFilterValve valve = new RequestFilterValveImpl("test-valve");
        RequestContext requestContext = createRequestContext("/include");
        StubValveContext valveContext = new StubValveContext();
        valve.invoke(requestContext, valveContext);
        assertFalse(((MockHttpServletResponse) requestContext.getResponse()).wasErrorSent());
        assertTrue(valveContext.nextInvoked);

        // test include/exclude filtering
        valve = new RequestFilterValveImpl("test-valve");
        ((RequestFilterValveImpl)valve).setIncludes(Arrays.asList(new String[]{"/include/**"}));
        ((RequestFilterValveImpl)valve).setExcludes(Arrays.asList(new String[]{"/include/exclude/**"}));
        requestContext = createRequestContext("/include/include");
        valveContext = new StubValveContext();
        valve.invoke(requestContext, valveContext);
        assertFalse(((MockHttpServletResponse) requestContext.getResponse()).wasErrorSent());
        assertTrue(valveContext.nextInvoked);
        requestContext = createRequestContext("/exclude");
        valveContext = new StubValveContext();
        valve.invoke(requestContext, valveContext);
        assertTrue(((MockHttpServletResponse) requestContext.getResponse()).wasErrorSent());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, ((MockHttpServletResponse) requestContext.getResponse()).getErrorCode());
        assertFalse(valveContext.nextInvoked);
        requestContext = createRequestContext("/include/exclude/exclude");
        valveContext = new StubValveContext();
        valve.invoke(requestContext, valveContext);
        assertTrue(((MockHttpServletResponse) requestContext.getResponse()).wasErrorSent());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, ((MockHttpServletResponse) requestContext.getResponse()).getErrorCode());
        assertFalse(valveContext.nextInvoked);

        // test exclude filtering
        valve = new RequestFilterValveImpl("test-valve");
        ((RequestFilterValveImpl)valve).setIncludes(null);
        ((RequestFilterValveImpl)valve).setExcludes(Arrays.asList(new String[]{"/exclude/**"}));
        requestContext = createRequestContext("/include");
        valveContext = new StubValveContext();
        valve.invoke(requestContext, valveContext);
        assertFalse(((MockHttpServletResponse) requestContext.getResponse()).wasErrorSent());
        assertTrue(valveContext.nextInvoked);
        requestContext = createRequestContext("/exclude");
        valveContext = new StubValveContext();
        valve.invoke(requestContext, valveContext);
        assertTrue(((MockHttpServletResponse) requestContext.getResponse()).wasErrorSent());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, ((MockHttpServletResponse) requestContext.getResponse()).getErrorCode());
        assertFalse(valveContext.nextInvoked);
    }

    /**
     * Create mock RequestContext with the specified HttpServletRequest pathInfo.
     *
     * @param pathInfo request pathInfo
     * @return mock RequestContext
     */
    private RequestContext createRequestContext(String pathInfo) {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setPathInfo(pathInfo);
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
