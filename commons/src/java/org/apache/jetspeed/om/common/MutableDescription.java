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
package org.apache.jetspeed.om.common;

import java.util.Locale;

import org.apache.pluto.om.common.Description;

/**
 * MutableDescription
 * <br/>
 * Extended version of <code>org.apache.pluto.om.common.Description</code>
 * that allows for setting the description text and Locale for the description
 * object.
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface MutableDescription extends Description
{

    String TYPE_PORTLET_APP = "org.apache.pluto.om.common.Description.portletApplication";
    String TYPE_WEB_APP = "org.apache.pluto.om.common.Description.webApplication";
    String TYPE_PORTLET = "org.apache.pluto.om.common.Description.portlet";
    String TYPE_PORTLET_ENTITY = "org.apache.pluto.om.common.Description.portletEntity";
    String TYPE_PARAMETER = "org.apache.pluto.om.common.Description.parameter";
    String TYPE_PREFERENCE = "org.apache.pluto.om.common.Description.preference";
    String TYPE_SEC_ROLE_REF = "org.apache.pluto.om.common.Description.securityRoleRef";

    void setDescription(String description);

    void setLocale(Locale locale);

}
