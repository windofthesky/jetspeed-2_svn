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
package org.apache.jetspeed.request;

import javax.servlet.http.HttpServletRequest;

/**
 * PortalRequestFactory allows specialized instantiation of a PortalRequest to be
 * used for JetspeedRequestContext.request.
 * <p>
 * JetspeedRequestContext also implements this interface and simply returns the
 * provided request as no wrapping is needed for Tomcat.
 * </p>
 * <p>
 * To actually use a PortalRequest as wrapper (as needed for instance on WebSphere), 
 * inject the PortalRequestFactoryImpl in JetspeedRequestContext.
 * </p>
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public interface PortalRequestFactory
{
    HttpServletRequest createPortalRequest(HttpServletRequest request);
}
