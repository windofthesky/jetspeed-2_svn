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
package org.apache.jetspeed.rewriter;

import java.net.URL;


/**
 * BasicRewriter
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class BasicRewriter extends AbstractRewriter implements Rewriter
{
    /*    
     * This callback is called by the ParserAdaptor implementation to write
     * back all rewritten URLs to point to the proxy server.
     * Given the targetURL, rewrites the link as a link back to the proxy server.
     *
     * @return the rewritten URL to the proxy server.
     *
     */
    public String rewriteUrl(
        String url,
        String tag,
        String attribute)
    {
        String fullPath = "";
        try
        {
            String baseUrl = super.getBaseUrl();
            if (baseUrl != null)
            {
                URL full = new URL(new URL(baseUrl), url);
                fullPath = full.toString();
            }
            else
            {
                return url; // leave as is
            }
        }
        catch (Exception e)
        {
            System.err.println(e);
        }
        return fullPath;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.Rewriter#shouldRemoveTag(java.lang.String)
     */
    public boolean shouldRemoveTag(String tag)
    {
        if (tag.equalsIgnoreCase("html"))
        {
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.Rewriter#shouldStripTag(java.lang.String)
     */
    public boolean shouldStripTag(String tag)
    {
        if (tag.equalsIgnoreCase("head"))
        {
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.Rewriter#shouldRemoveComments()
     */
    public boolean shouldRemoveComments()
    {
        return true;
    }
    
}
