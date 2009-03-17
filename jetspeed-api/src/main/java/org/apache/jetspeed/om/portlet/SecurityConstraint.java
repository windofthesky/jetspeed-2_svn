/*
 * Copyright 2008 The Apache Software Foundation
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
package org.apache.jetspeed.om.portlet;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

/**
 * 
 * @version $Id$
 *
 */
public interface SecurityConstraint extends org.apache.pluto.container.om.portlet.SecurityConstraint, Serializable
{    
    UserDataConstraint getUserDataConstraint();

    DisplayName getDisplayName(Locale locale);
    List<DisplayName> getDisplayNames();
    DisplayName addDisplayName(String lang);
}