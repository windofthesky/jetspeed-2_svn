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
package org.apache.jetspeed.velocity;

import java.lang.reflect.Constructor;
import javax.portlet.PortletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.request.RequestContext;

public class JetspeedPowerToolFactory
{
    protected static final Log log = LogFactory.getLog(JetspeedPowerToolFactory.class);
    
    private Class jptClass;
    private Constructor constructor;
    private String jptClassName;
    
    public JetspeedPowerToolFactory(String jptClassName)
    throws ClassNotFoundException, NoSuchMethodException
    {
        this.jptClassName = jptClassName;
        jptClass = Thread.currentThread().getContextClassLoader().loadClass(jptClassName);
        constructor =
            jptClass.getConstructor(
                new Class[] {RequestContext.class});        
    }
    
    public JetspeedPowerTool getJetspeedPowerTool(RequestContext requestContext)
    throws PortletException
    {
        try
        {
            Object[] initArgs = { requestContext };
            return (JetspeedPowerTool)constructor.newInstance(initArgs);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new PortletException(e);
        }
    }
}
