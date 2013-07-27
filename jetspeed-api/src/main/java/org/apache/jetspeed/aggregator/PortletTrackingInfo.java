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
package org.apache.jetspeed.aggregator;

import java.util.List;

/**
 * Portlet Tracking information used in PortletTrackingManager to represent minimal data passed between the tracking
 * manager and clients of the API
 *
 */
public class PortletTrackingInfo {

    private final List<String> windows;
    private final String fullPortletName;

    public PortletTrackingInfo(String fullPortletName, List<String> windows) {
        this.windows = windows;
        this.fullPortletName = fullPortletName;
    }

    public String getFullPortletName() {
        return fullPortletName;
    }

    public List<String> getWindows() {
        return windows;
    }
}
