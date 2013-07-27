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
