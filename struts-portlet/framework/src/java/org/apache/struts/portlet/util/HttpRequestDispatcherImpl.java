/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts.portlet.util;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * HttpRequestDispatcher
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class HttpRequestDispatcherImpl implements RequestDispatcher {

    private static final Log logger = LogFactory.getLog(HttpRequestDispatcherImpl.class);

    private RequestDispatcher dispatcher;

    private String path;

    public HttpRequestDispatcherImpl(RequestDispatcher dispatcher, String path) {
        this.dispatcher = dispatcher;
        this.path = path;
    }

    protected void invoke(ServletRequest request, ServletResponse response, boolean include) throws ServletException, IOException {
        int startQueryString;
        if (path != null && // let dispatcher handle invalid null value
                ((startQueryString = path.indexOf('?')) != -1) && (path.length() > startQueryString + 1)) {
            request = new DispatchedHttpServletRequestWrapper((HttpServletRequest) request, path.substring(startQueryString + 1));
        }
        if (include)
            dispatcher.include(request, response);
        else
            dispatcher.forward(request, response);
    }

    protected String getPath() {
        return path;
    }

    public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        invoke(request, response, false);
    }

    public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        invoke(request, response, true);
    }

    public String toString() {
        return dispatcher.toString();
    }
}
