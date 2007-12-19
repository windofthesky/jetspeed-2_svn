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
package org.apache.jetspeed.om.common;

import java.util.Locale;

import org.apache.pluto.om.common.DisplayName;

/**
 * MutableDisplayName
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface MutableDisplayName extends DisplayName
{
    String TYPE_WEB_APP = "org.apache.pluto.om.common.DisplayName.webapp";
    String TYPE_PORTLET = "org.apache.pluto.om.common.DisplayName.portlet";

    void setDisplayName(String displayName);

    void setLocale(Locale locale);
    
}