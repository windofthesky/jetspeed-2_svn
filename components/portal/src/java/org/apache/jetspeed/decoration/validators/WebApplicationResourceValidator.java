/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.decoration.validators;

import java.net.MalformedURLException;

import javax.servlet.ServletContext;

import org.apache.jetspeed.decoration.ResourceValidator;

/**
 * This implementation uses <code>ServletContext.getResource()</code>
 * to verify the existence of a resource.
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 *
 */
public class WebApplicationResourceValidator implements ResourceValidator
{
    private ServletContext servletContext;
    
    public void setServletContext(ServletContext servletContext)
    {
        this.servletContext = servletContext;        
    }

    public boolean resourceExists(String path)
    {
        try
        {
            return servletContext.getResource(path) != null;
        }
        catch (MalformedURLException e)
        {
            IllegalArgumentException iae = new IllegalArgumentException(path+" is not a valid path.");
            iae.initCause(e);
            throw iae;            
        }
    }

}
