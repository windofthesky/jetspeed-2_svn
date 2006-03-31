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
    /** parameters that need to be propagated in the action URL (since HTTP request parameters will not be available) */
    public static final String ACTION_PARAMETER_URL    = "_AP_URL";
    public static final String ACTION_PARAMETER_METHOD = "_AP_METHOD";

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
     * @param otherAttributes
     * @return the modified url which is a portlet action
     * 
     * Rewrites all urls HREFS with a portlet action
     */
    public String rewriteUrl(String url, String tag, String attribute, MutableAttributes otherAttributes)
    {
         String modifiedURL = url;
        
        // Any relative URL needs to be converted to a full URL
        if (url.startsWith("/") || (!url.startsWith("http:") && !url.startsWith("https:"))) 
        {
            try
            {
                if (this.getBaseUrl() != null)
                {
                    URL full = new URL(new URL(getBaseUrl()), url);
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
         
        // translate "submit" URL's as actions
        //  <A href="..."/>
        //  <FORM submit="..."/>
        if (( tag.equalsIgnoreCase("A") && attribute.equalsIgnoreCase("href")) ||
            ( tag.equalsIgnoreCase("FORM") && attribute.equalsIgnoreCase("action")))
                
        {
                // Regular URL just add a portlet action
                if (this.actionURL != null)
                {
                    // create Action URL
                    actionURL.setParameter(ACTION_PARAMETER_URL, modifiedURL);
                    if (tag.equalsIgnoreCase("FORM"))
                    {
                        String httpMethod = otherAttributes.getValue("method");
                        if (httpMethod != null)
                            actionURL.setParameter(ACTION_PARAMETER_METHOD, httpMethod);
                    }
                    modifiedURL = actionURL.toString();
                }
        }
        
        // if ( !url.equalsIgnoreCase( modifiedURL ))
        //      System.out.println("In tag: "+tag+", for attribute: "+attribute+", converted url: "+url+", to: "+modifiedURL+", base URL was: "+getBaseUrl());

        return modifiedURL;
    }
}
