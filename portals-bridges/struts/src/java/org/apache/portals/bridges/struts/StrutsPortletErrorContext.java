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
package org.apache.portals.bridges.struts;

import java.io.Serializable;

/**
 * StrutsPortletErrorContext
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class StrutsPortletErrorContext implements Serializable
{
    private int errorCode;
    private String errorMessage;
    private Exception error;
    public Exception getError()
    {
        return error;
    }
    public void setError(Exception error)
    {
        this.error = error;
    }
    public int getErrorCode()
    {
        return errorCode;
    }
    public void setErrorCode(int errorCode)
    {
        this.errorCode = errorCode;
    }
    public String getErrorMessage()
    {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }
}
