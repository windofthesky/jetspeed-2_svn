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
package org.apache.portals.bridges.frameworks.model;

import java.util.Map;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;


/**
 * PortletApplicationModel
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface PortletApplicationModel
{
    void init(PortletConfig config)
    throws PortletException;
    
    ModelBean getBean(String view);

    String getTemplate(String view);
    
    Object createBean(ModelBean mb);
    
    Map createPrefsBean(ModelBean mb, Map prefs);
    
    boolean validate(Object bean, String view)
    throws PortletException;
        
    String getForward(String view, String status);
    
    String getForward(String actionForward);
    
}
