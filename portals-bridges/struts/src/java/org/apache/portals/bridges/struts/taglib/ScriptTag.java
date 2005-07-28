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
package org.apache.portals.bridges.struts.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Generate a script tag for use within a Portlet environment.
 * <p>
 * The src attribute is resolved to a context relative path and may contain
 * a relative path (prefixed with one or more ../ elements).
 * </p>
 * <p>
 * Note: works equally well within a Portlet context as a Web application context.
 * </p>
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class ScriptTag extends TagSupport 
{
    /**
     * The language attribute for the script tag.
     * <p>
     * Defaults to "Javascript1.1"
     * </p>
     */
    protected String language;

    /**
     * The script src path.
     * <p>
     * May contain a relative path (prefixed with one or more ../ elements).<br/>
     * </p>
     */
    protected String src;

    public String getLanguage()
    {
        return language;
    }
    public void setLanguage(String language)
    {
        this.language = language;
    }
    public String getSrc()
    {
        return src;
    }
    public void setSrc(String src)
    {
        this.src = src;
    }

    public int doStartTag() throws JspException
    {
        StringBuffer buffer = new StringBuffer("<script language=\"");
        if (language != null)
            buffer.append(language);
        else
            buffer.append("Javascript1.1");
        buffer.append("\" src=\"");
        if (src.startsWith("/"))
        {
            buffer.append(((HttpServletRequest) pageContext.getRequest())
                    .getContextPath());
        		buffer.append(src);
        }
        else
        {
            buffer.append(TagsSupport.getContextRelativeURL(pageContext,src,true));
        }
        buffer.append("\"/></script>");
        JspWriter writer = pageContext.getOut();
        try
        {
            writer.print(buffer.toString());
        } catch (IOException e)
        {
            throw new JspException(e);
        }
        return (SKIP_BODY);
    }

    public int doEndTag()
    {
        return EVAL_PAGE;
    }

    public void release() 
    {
        super.release();
        language = null;
        src = null;
    }
}
