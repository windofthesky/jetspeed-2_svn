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
package org.apache.jetspeed.rewriter;

import java.net.URL;

import javax.portlet.PortletURL;

/**
 * WebContentRewriter
 * 
 * @author <a href="mailto:rogerrutr@apache.org">Roger Ruttimann </a>
 * @version $Id$
 */
public class WebContentRewriter extends RulesetRewriterImpl implements Rewriter
{

    /** WebContentURL */
    public static final String ACTION_PARAMETER_URL = "WCURL";

    /*
     * Portlet URL will be used to replace all URL's
     */
    private PortletURL actionURL = null;

    /**
     * Setters/getters for members
     */
    public void setActionURL(PortletURL action)
    {
        this.actionURL = action;
    }

    public PortletURL getActionURL()
    {
        return this.actionURL;
    }

    /**
     * rewriteURL
     * 
     * @param url
     * @param tag
     * @param attribute
     * @return the modified url which is a portlet action
     * 
     * Rewrites all urls HREFS with a portlet action
     */
    public String rewriteUrl(String url, String tag, String attribute)
    {
        /*
         * TODO: no default. Use default Jetspeed JIRA to make sure that the
         * method was called
         */
        String modifiedURL = "http://nagoya.apache.org/jira/secure/BrowseProject.jspa?id=10492";
        //	Check if it's a relative or full URL
        if (url.startsWith("/") || url.indexOf("http") == -1)
        {
            try
            {
                if (this.getBaseUrl() != null)
                {
                    URL full = new URL(new URL(this.getBaseUrl()), url);
                    modifiedURL = full.toString();
                }
                else
                {
                    modifiedURL = url; // leave as is
                }
            }
            catch (Exception e)
            {
                modifiedURL = url;
            }
        }
        else
        {
            // Apply action URL's not to images
            if (url.indexOf(".gif") == -1 && url.indexOf(".jpg") == -1)
            {
                if (this.actionURL != null)
                {
                    // create Action URL
                    actionURL.setParameter(ACTION_PARAMETER_URL, url);

                    modifiedURL = actionURL.toString();
                }
                else
                {
                    modifiedURL = url;
                }
            }
            else
            {
                modifiedURL = url;
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