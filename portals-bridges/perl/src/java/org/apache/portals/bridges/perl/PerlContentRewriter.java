/* Copyright 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.portals.bridges.perl;


import javax.portlet.PortletURL;

import org.apache.jetspeed.rewriter.Rewriter;
import org.apache.jetspeed.rewriter.RulesetRewriterImpl;

/**
 * PerlContentRewriter
 * 
 * @author <a href="mailto:rogerrutr@apache.org">Roger Ruttimann </a>
 * @version $Id$
 */
public class PerlContentRewriter extends RulesetRewriterImpl implements
        Rewriter {

    /** WebContentURL */
    public static final String ACTION_PARAMETER_URL = "WCURL";

    /* Portlet URL will be used to replace all URL's */
    private PortletURL actionURL = null;

    /* Parameter name attached to action */
    private String actionParameterName = null;

    /*
     * LocalhostIP Some perl script refer to localhost which doesn't work for
     * remote connections. The rewriter will replace any localhost references
     * with the IP address
     */
    private String localHostIP = null;

    /**
     * Setters/getters for members
     */
    public void setActionURL(PortletURL action) {
        this.actionURL = action;
    }

    public PortletURL getActionURL() {
        return this.actionURL;
    }

    /**
     * @return Returns the localHostIP.
     */
    public String getLocalHostIP() {
        return localHostIP;
    }

    /**
     * @param localHostIP
     *                    The localHostIP to set.
     */
    public void setLocalHostIP(String localHostIP) {
        this.localHostIP = localHostIP;
    }

    /**
     * @return Returns the actionParameterName.
     */
    public String getActionParameterName() {
        return actionParameterName;
    }

    /**
     * @param actionParameterName
     *                    The actionParameterName to set.
     */
    public void setActionParameterName(String actionParameterName) {
        this.actionParameterName = actionParameterName;
    }

    /**
     * rewriteURL
     * 
     * @param url
     * @param tag
     * @param attribute
     * @return the modified url which is a portlet action
     * 
     * Rewrites all URL's in the perl script with portlet actions. Tags include
     * A (AREA) and FORM and replaces any localhost with the real IP address if
     * provided
     */
    public String rewriteUrl(String url, String tag, String attribute) {
        String modifiedURL = url;
        // TODO: Remove debug
        System.out.println("Perl HTML output TAG = " + tag + " Attribute = " + attribute);

        // For now only add PortletActions to URL's which are anchors (tag=a) or
        // FORMS and HREF's (attribute= HREF) -- ignore all others links
        if ((		tag.compareToIgnoreCase("A") == 0
                ||  tag.compareToIgnoreCase("FORM") == 0)
                && attribute.compareToIgnoreCase("HREF") == 0) {
            // Regular URL just add a portlet action
            if (this.actionURL != null) {
                // create Action URL
                actionURL.setParameter(actionParameterName, modifiedURL);
                modifiedURL = actionURL.toString();
            }
        }

        return modifiedURL;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.rewriter.Rewriter#shouldRemoveTag(java.lang.String)
     */
    /*
     * public boolean shouldRemoveTag(String tag) { if
     * (tag.equalsIgnoreCase("html")) { return true; } return false; }
     */

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.rewriter.Rewriter#shouldStripTag(java.lang.String)
     */
    /*
     * public boolean shouldStripTag(String tag) { if
     * (tag.equalsIgnoreCase("head")) { return true; } return false; }
     */

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.rewriter.Rewriter#shouldRemoveComments()
     */
    /*
     * public boolean shouldRemoveComments() { return true; }
     */

}