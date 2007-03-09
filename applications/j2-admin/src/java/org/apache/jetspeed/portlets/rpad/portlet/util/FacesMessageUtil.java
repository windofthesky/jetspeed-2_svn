/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.portlets.rpad.portlet.util;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class FacesMessageUtil
{
    public static void addMessage(FacesMessage.Severity severity,
            java.lang.String summary, java.lang.String detail)
    {
        FacesMessage facesMessage = new FacesMessage(severity, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
    }

    public static void addErrorMessage(java.lang.String summary, Throwable t)
    {
        addMessage(FacesMessage.SEVERITY_ERROR, summary, t.getMessage());
    }

    public static void addErrorMessage(java.lang.String summary,
            java.lang.String detail)
    {
        addMessage(FacesMessage.SEVERITY_ERROR, summary, detail);
    }

    public static void addErrorMessage(java.lang.String summary)
    {
        addMessage(FacesMessage.SEVERITY_ERROR, summary, null);
    }

    public static void addFatalMessage(java.lang.String summary, Throwable t)
    {
        addMessage(FacesMessage.SEVERITY_FATAL, summary, t.getMessage());
    }

    public static void addFatalMessage(java.lang.String summary,
            java.lang.String detail)
    {
        addMessage(FacesMessage.SEVERITY_FATAL, summary, detail);
    }

    public static void addFatalMessage(java.lang.String summary)
    {
        addMessage(FacesMessage.SEVERITY_FATAL, summary, null);
    }

    public static void addInfoMessage(java.lang.String summary, Throwable t)
    {
        addMessage(FacesMessage.SEVERITY_INFO, summary, t.getMessage());
    }

    public static void addInfoMessage(java.lang.String summary,
            java.lang.String detail)
    {
        addMessage(FacesMessage.SEVERITY_INFO, summary, detail);
    }

    public static void addInfoMessage(java.lang.String summary)
    {
        addMessage(FacesMessage.SEVERITY_INFO, summary, null);
    }

    public static void addWarnMessage(java.lang.String summary, Throwable t)
    {
        addMessage(FacesMessage.SEVERITY_WARN, summary, t.getMessage());
    }

    public static void addWarnMessage(java.lang.String summary,
            java.lang.String detail)
    {
        addMessage(FacesMessage.SEVERITY_WARN, summary, detail);
    }

    public static void addWarnMessage(java.lang.String summary)
    {
        addMessage(FacesMessage.SEVERITY_WARN, summary, null);
    }
}
