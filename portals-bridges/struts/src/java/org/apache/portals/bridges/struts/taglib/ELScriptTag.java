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

import javax.servlet.jsp.JspException;

import org.apache.strutsel.taglib.utils.EvalHelper;

/**
 * Generate a script tag for use within a Portlet environment allowing JSTL expressions.
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
public class ELScriptTag extends ScriptTag
{
    /**
     * struts-el support for setting {@link #language}
     */
    protected String languageExpr = null;

    public String getLanguageExpr()
    {
        return languageExpr;
    }
    
    public void setLanguageExpr(String languageExpr)
    {
        this.languageExpr = languageExpr;
    }
    
    /**
     * struts-el support for setting {@link #src}
     */
    protected String srcLExpr = null;

    public String getSrcLExpr()
    {
        return srcLExpr;
    }
    
    public void setSrcLExpr(String srcExpr)
    {
        this.srcLExpr = srcExpr;
    }
    
    public int doStartTag() throws JspException
    {
        evaluateExpressions();
        return super.doStartTag();
    }

    /**
     * Resolve the {@link #languageExpr} and {@link #srcLExpr} attributes using the JSTL expression
     * evaluation engine ({@link EvalHelper}).
     * @exception JspException if a JSP exception has occurred
     */
    private void evaluateExpressions() throws JspException {
        String  string  = null;

        if ((string = EvalHelper.evalString("language", getLanguageExpr(),this, pageContext)) != null)
        {
            setLanguage(string);
        }
        if ((string = EvalHelper.evalString("src", getSrcLExpr(),this, pageContext)) != null)
        {
            setSrc(string);
        }
    }
    
    public void release() 
    {
        super.release();
        languageExpr = null;
        srcLExpr = null;
    }
}
