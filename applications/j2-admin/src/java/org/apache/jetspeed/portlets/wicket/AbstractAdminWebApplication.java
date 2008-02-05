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
package org.apache.jetspeed.portlets.wicket;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.wicket.Response;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.UrlResourceStream;
import org.apache.wicket.util.resource.locator.ResourceStreamLocator;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.file.IResourceFinder;

/**
 * Abstract Admin Wicket Application to load customized templates from /WEB-INF/templates/.
 * 
 * @author <a href="mailto:woonsan@apache.org">Woonsan Ko</a>
 * @version $Id: $
 */
public abstract class AbstractAdminWebApplication extends WebApplication
{

	public abstract Class getHomePage();
    
	protected void init()
	{
        super.init();

		// instruct the application to use our custom resource stream locator
		getResourceSettings().setResourceStreamLocator(new TemplatesResourceStreamLocator());
	}
    
    private class TemplatesResourceStreamLocator extends ResourceStreamLocator
    {
        
        protected String templatesPath = "/WEB-INF/templates/";

        public void setTemplatesPath(String templatesPath)
        {
            this.templatesPath = templatesPath;
        }
        
        public String getTemplatesPath()
        {
            return this.templatesPath;
        }

        /**
         * @see org.apache.wicket.util.resource.locator.ResourceStreamLocator#locate(java.lang.Class,
         *      java.lang.String)
         */
        public IResourceStream locate(Class clazz, String path)
        {
            String location = this.templatesPath + path;
            
            try
            {
                // try to load the resource from the web context
                URL url = getServletContext().getResource(location);
                
                if (url != null)
                {
                    return new UrlResourceStream(url);
                }
            }
            catch (MalformedURLException e)
            {
                throw new WicketRuntimeException(e);
            }

            // resource not found; fall back on class loading
            return super.locate(clazz, path);
        }

    }

}
