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

package org.apache.jetspeed.portlet.webcontent;

import java.io.Serializable;

import org.apache.commons.httpclient.Cookie;

/**
 * A cached resource object, stored in memory to optimize access to static resources
 * such as images and style sheets.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$ 
 */

public class WebContentResource implements Serializable
{
    private transient byte[] content = null;
    private transient Cookie cookie = null;
    private String url = null;
    private String lastUrl = null;

    /**
     * Constructor for a cached resource. 
     *
     * @param contentType The HTTP content type for a cached resource as defined 
     *        in WebPageHelper, i.e. WebPageHelper.CT_HTML, WebPageHelper.CT_IMAGE....
     * @param content The byte array of content this cached. This content can be
     *         binary images, or static text such as scripts and style sheets.
     *         
     */
    public WebContentResource(String url, byte[] content)
    {
        this.url = url;
        if (content != null)
        {
            this.content = new byte[content.length];
            System.arraycopy(content, 0, this.content, 0, this.content.length);
        }
    }

    /**
     * Gets the content of this resource in a byte array.
     *
     * @return A byte array of the resource's content.
     */
    public byte[] getContent()
    {
        return content;
    }


    /**
     * @return Returns the lastUrl.
     */
    public String getLastUrl()
    {
        return lastUrl;
    }
    /**
     * @param lastUrl The lastUrl to set.
     */
    public void setLastUrl(String lastUrl)
    {
        this.lastUrl = lastUrl;
    }
    /**
     * @return Returns the url.
     */
    public String getUrl()
    {
        return url;
    }
    /**
     * @param url The url to set.
     */
    public void setUrl(String url)
    {
        this.url = url;
    }
}
