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
package org.apache.jetspeed.om.common.servlet;

import java.io.Serializable;
import java.util.Locale;

import org.apache.pluto.om.common.DescriptionSet;
import org.apache.pluto.om.common.DisplayNameSet;
import org.apache.pluto.om.common.SecurityRole;
import org.apache.pluto.om.servlet.WebApplicationDefinition;

/**
 * 
 * WebApplicationComposite
 * This interface is a combination of the two interface classes 
 * used to identify a web application.  It has additional methods
 * to make it easier to use/test within Jetspeed.
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */

public interface MutableWebApplication extends WebApplicationDefinition, Serializable
{
    void setContextRoot(String contextRoot);
    
    void setId(String id);

    void setDescriptionSet(DescriptionSet descriptions);

    void setDisplayNameSet(DisplayNameSet names);

    void addDisplayName(Locale locale, String name);

    void addDescription(Locale locale, String description);
    
    void addSecurityRole(SecurityRole securityRole);
    
}
