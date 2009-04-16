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
package org.apache.jetspeed.aggregator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.container.PortletWindow;

/**
 * Worker thread processes jobs and notify its WorkerMonitor when completed.
 * When no work is available, the worker simply sets itself in a waiting mode
 * pending reactivation by the WorkerMonitor
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public interface RenderingJob extends Runnable
{
    void execute();
    
    PortletRenderer getRenderer();
    
    PortletWindow getWindow(); 

    PortletContent getPortletContent();

    void setTimeout(long portletTimeout);

    long getTimeout();

    boolean isTimeout();
    
    PortletDefinition getPortletDefinition();

    HttpServletRequest getRequest();

    HttpServletResponse getResponse();

    ContentFragment getFragment();

    RequestContext getRequestContext();

    int getExpirationCache();

    void setWorkerAttribute(String name, Object value);
    
    Object getWorkerAttribute(String name);
    
    void removeWorkerAttribute(String name);
}

