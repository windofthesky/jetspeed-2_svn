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
package org.apache.struts.portlet.taglib;

import org.apache.struts.portlet.StrutsPortletURL;

/**
 * FormTag
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class FormTag extends org.apache.struts.taglib.html.FormTag
{
    protected String renderFormStartElement()
    {
        String formStartElement = super.renderFormStartElement();
        int actionURLStart = formStartElement.indexOf("action=") + 8;
        int actionURLEnd = formStartElement.indexOf('"', actionURLStart);
        String actionURL = formStartElement.substring(actionURLStart,
                actionURLEnd);
        formStartElement = formStartElement.substring(0, actionURLStart)
                + StrutsPortletURL.createActionURL(pageContext.getRequest(),
                        actionURL).toString()
                + formStartElement.substring(actionURLEnd);
        return formStartElement;
    }
}
