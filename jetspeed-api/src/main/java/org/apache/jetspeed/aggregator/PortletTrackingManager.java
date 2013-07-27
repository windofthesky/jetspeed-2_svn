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

import org.apache.jetspeed.container.PortletWindow;

import java.util.List;


/**
 * The Portlet Tracking Manager will track portlets rendering statistics, taking portlets out of
 * service when their rendering time exceeds a configurable threshold. This threshold has two parameters:
 * <tt>defaultPortletTimeout</tt> is a value in milliseconds representing the maximum render time to be considered
 * as a timeout for a given portlet window. <tt>outOfServiceLimit</tt> represents the number of times the portlet window
 * reaches the timeout threshold before it is taken out of service.
 * <p>
 * This service is used primarily by the rendering engine to determine whether to take a portlet window out of
 * service to avoid long 'timeouts' when rendering a portlet. The service also provides functions to put the
 * portlet windows back into service.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public interface PortletTrackingManager
{
    /**
     * Returns the default timeout threshold for rendering a portlet in milliseconds. This threshold is used to
     * determine if the portlet has timed out, and is used in combination with <code>getOutOfServiceLimit</code>
     *
     * @return the default rendering timeout
     */
    long getDefaultPortletTimeout();

    /**
     * The number of times a portlet window can timeout before it is taken out of service
     *
     * @return the number of times a portlet window can time out before it is considered out of service
     */
    int getOutOfServiceLimit();

    /**
     * Ask if a given window is out of service or not
     * @param window this window will be checked
     * @return <tt>true</tt> when the portlet window is determined to be out of service
     */
    boolean isOutOfService(PortletWindow window);

    /**
     * Given a render time, ask if the given portlet window has exceeded the timeout threshold
     *
     * @param renderTime the time it took to render a portlet
     * @param window this window will be checked
     * @return <tt>true</tt> when the render time exceeded the timeout threshold
     */
    boolean exceededTimeout(long renderTime, PortletWindow window);

    /**
     * Increment the render timeout count for a given portlet window
     * @param window the portlet window to be incremented
     */
    void incrementRenderTimeoutCount(PortletWindow window);

    /**
     * Set a specific caching expiration timeout for a given portlet window
     *
     * @param window this window will have a new expiration timeout value
     * @param expiration the value of the timeout expiration
     */
    void setExpiration(PortletWindow window, long expiration);

    /**
     * Reset this portlet window as successfully rendered, clearing all expiration counts
     *
     * @param window this window will have its expiration status reset
     */
    void success(PortletWindow window);

    /**
     * Take a given portlet window out of service. This portlet window will no longer be rendered.
     *
     * @param window this window will be taken out of service
     */
    void takeOutOfService(PortletWindow window);

    /**
     * Put a given portlet window that is out of service back into service and re-enable rendering of the window
     *
     * @param window this window will be put back into service
     */
    void putIntoService(PortletWindow window);

    /**
     *  Given a list of full portlet names in format of <tt>portletApp::portletName</tt>, put all windows back
     *  into service for each of the portlet names given. Note that a portlet can have many windows associated with it.
     *  Each of the windows associated with a given portlet will be put back into service.
     *
     * @param fullPortletNames a list of strings of full portlet names in format <tt>portletApp::portletName</tt>
     */
    void putIntoService(List<String> fullPortletNames);

    /**
     * Retrieve the list of out of service portlet windows for a given portlet, or an empty <code>PortletTrackingInfo</code>
     * if none are found
     *
     * @param fullPortletName a full portlet names in format <tt>portletApp::portletName</tt>
     * @return a single <code>PortletTrackingInfo</code> which includes a list of window ids
     */
    PortletTrackingInfo getOutOfServiceList(String fullPortletName);

    /**
     * Retrieve the list of out of service portlet windows for the entire system
     *
     * @return a list of portlet windows represented by <code>PortletTrackingInfo</code>
     */
    List<PortletTrackingInfo> getOutOfServiceList();

    /**
     * Returns <tt>true</tt> if this service is enabled, otherwise <tt>false</tt>. The service is enabled via the
     * <tt>jetspeed.properties</tt> property named <tt>portal.core.aggregator.portlet.timeout</tt>. The value
     * represents the timeout threshold in milliseconds. A value of zero disables portlet tracking.
     * @return <tt>true</tt> if this service is enabled, otherwise <tt>false</tt>
     */
    boolean isEnabled();
}