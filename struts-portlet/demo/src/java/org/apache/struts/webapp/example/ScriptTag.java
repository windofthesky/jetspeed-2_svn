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
package org.apache.struts.webapp.example;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.util.MessageResources;

public class ScriptTag extends TagSupport {

    protected String language;

    protected String src;

    protected static MessageResources messages = MessageResources.getMessageResources("org.apache.struts.webapp.example.ApplicationResources");

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public int doStartTag() throws JspException {
        StringBuffer buffer = new StringBuffer("<script language=\"");
        if (language != null)
            buffer.append(language);
        else
            buffer.append("Javascript1.1");
        buffer.append("\" src=\"");
        if (src.startsWith("/"))
            buffer.append(((HttpServletRequest) pageContext.getRequest()).getContextPath());
        else {
            String requestURI = ((HttpServletRequest) pageContext.getRequest()).getRequestURI();
            buffer.append(requestURI.substring(0, requestURI.lastIndexOf('/')));
        }
        buffer.append(src);
        buffer.append("\"/></script>");
        JspWriter writer = pageContext.getOut();
        try {
            writer.print(buffer.toString());
        } catch (IOException e) {
            throw new JspException(messages.getMessage("script.io", e.toString()));
        }

        return (SKIP_BODY);
    }

    public int doEndTag() {
        return EVAL_PAGE;
    }
}
