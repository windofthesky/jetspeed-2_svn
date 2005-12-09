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
package org.apache.jetspeed.headerresource.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.headerresource.HeaderResource;
import org.apache.jetspeed.request.RequestContext;

/**
 * Default implementation for HeaderResource
 * 
 * @author <a href="mailto:shinsuke@yahoo.co.jp">Shinsuke Sugaya</a>
 * @version $Id: PortalReservedParameters.java 188569 2005-05-13 13:35:18Z weaver $
 */
public class HeaderResourceImpl implements HeaderResource
{
    private RequestContext requestContext;

    /**
     * Default Constructor
     * 
     * @param context
     */
    public HeaderResourceImpl(RequestContext context)
    {
        requestContext = context;
    }

    /**
     * Gets HeaderInfo set from the request.
     * 
     * @return
     */
    private Set getHeaderInfoSet()
    {
        Set headerInfoSet = (Set) requestContext.getAttribute(PortalReservedParameters.HEADER_RESOURCE_ATTRIBUTE);
        if (headerInfoSet == null)
        {
            headerInfoSet = new LinkedHashSet();
            requestContext.setAttribute(PortalReservedParameters.HEADER_RESOURCE_ATTRIBUTE, headerInfoSet);
        }
        return headerInfoSet;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.headerresource.impl.HeaderResource#getString()
     */
    public String toString()
    {
        Set headerInfoSet = getHeaderInfoSet();
        StringBuffer header = new StringBuffer();
        for (Iterator ite = headerInfoSet.iterator(); ite.hasNext();)
        {
            header.append(((HeaderInfo) ite.next()).toString());
            header.append("\n");
        }
        return header.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.headerresource.impl.HeaderResource#addHeaderInfo(java.lang.String,
     *      java.util.Map)
     */
    public void addHeaderInfo(String elementName, Map attributes, String text)
    {
        HeaderInfo headerInfo = new HeaderInfo(elementName, attributes, text);
        if (!containsHeaderInfo(headerInfo))
        {
            Set headerInfoSet = getHeaderInfoSet();
            headerInfoSet.add(headerInfo);
        }
    }

    /**
     * Returns true if this set contains the specified HeaderInfo.
     * 
     * @param headerInfo
     * @return
     */
    private boolean containsHeaderInfo(HeaderInfo headerInfo)
    {
        Set headerInfoSet = getHeaderInfoSet();
        for (Iterator ite = headerInfoSet.iterator(); ite.hasNext();)
        {
            HeaderInfo hInfo = (HeaderInfo) ite.next();
            if (headerInfo.equals(hInfo))
            {
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.headerresource.impl.HeaderResource#addJavaScript(java.lang.String,
     *      boolean)
     */
    public void addJavaScript(String path, boolean defer)
    {
        HashMap attrs = new HashMap();
        attrs.put("src", requestContext.getResponse().encodeURL( path ) );
        attrs.put("type", "text/javascript");
        if (defer)
        {
            attrs.put("defer", "true");
        }
        addHeaderInfo("script", attrs, "");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.headerresource.impl.HeaderResource#addJavaScript(java.lang.String)
     */
    public void addJavaScript(String path)
    {
        addJavaScript(path, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.headerresource.impl.HeaderResource#addStyleSheet(java.lang.String)
     */
    public void addStyleSheet(String path)
    {
        HashMap attrs = new HashMap();
        attrs.put("rel", "stylesheet");
        attrs.put("href", requestContext.getResponse().encodeURL( path ) );
        attrs.put("type", "text/css");
        addHeaderInfo("link", attrs, null);
    }

    /**
     * This class represents tag information for HeaderResouce component
     * 
     * @author <a href="mailto:shinsuke@yahoo.co.jp">Shinsuke Sugaya</a>
     */
    private class HeaderInfo
    {
        /**
         * Tag's name
         */
        private String elementName;

        /**
         * Tag's attributes
         */
        private Map attributes;

        /**
         * Tag's content
         */
        private String text;

        public HeaderInfo(String elementName)
        {
            this(elementName, new HashMap());
        }

        public HeaderInfo(String elementName, Map attr)
        {
            this(elementName, attr, null);
        }

        public HeaderInfo(String elementName, Map attr, String text)
        {
            setElementName(elementName);
            setAttributes(attr);
            setText(text);
        }

        public void addAttribute(String key, String value)
        {
            attributes.put(key, value);
        }

        public String toString()
        {
            StringBuffer buf = new StringBuffer();
            buf.append("<");
            buf.append(getElementName());
            buf.append(" ");

            Set keySet = getAttributes().keySet();
            for (Iterator ite = keySet.iterator(); ite.hasNext();)
            {
                String key = (String) ite.next();
                buf.append(key);
                buf.append("=\"");
                buf.append((String) getAttributes().get(key));
                buf.append("\" ");
            }

            if (getText() != null)
            {
                buf.append(">" + getText() + "</" + getElementName() + ">");
            }
            else
            {
                buf.append("/>");
            }

            return buf.toString();
        }

        public boolean equals(Object o)
        {
            if (o instanceof HeaderInfo)
            {
                HeaderInfo headerInfo = (HeaderInfo) o;
                if (headerInfo.getElementName().equalsIgnoreCase(getElementName())
                        && compareString(headerInfo.getText(), getText())
                        && headerInfo.getAttributes().equals(getAttributes()))
                {
                    return true;
                }
            }
            return false;
        }

        private boolean compareString(String str0, String str1)
        {
            if (str0 == null)
            {
                if (str1 == null)
                {
                    return true;
                }

            }
            else
            {
                if (str0.equals(str1))
                {
                    return true;
                }
            }
            return false;
        }

        /**
         * @return Returns the attributes.
         */
        public Map getAttributes()
        {
            return attributes;
        }

        /**
         * @param attributes The attributes to set.
         */
        public void setAttributes(Map attributes)
        {
            this.attributes = attributes;
        }

        /**
         * @return Returns the elementName.
         */
        public String getElementName()
        {
            return elementName;
        }

        /**
         * @param elementName The elementName to set.
         */
        public void setElementName(String elementName)
        {
            this.elementName = elementName;
        }

        /**
         * @return Returns the text.
         */
        public String getText()
        {
            return text;
        }

        /**
         * @param text The text to set.
         */
        public void setText(String text)
        {
            this.text = text;
        }
    }
}
