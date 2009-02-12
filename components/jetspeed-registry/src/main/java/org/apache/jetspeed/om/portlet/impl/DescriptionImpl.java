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
package org.apache.jetspeed.om.portlet.impl;

import java.util.Locale;

import org.apache.jetspeed.util.JetspeedLocale;
import org.apache.jetspeed.om.portlet.Description;

/**
 * DescriptionImpl
 * <br>
 * Implementation of the <code>Description</code>
 * interface.
 * 
 * @see org.apache.jetspeed.om.portlet.Description
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class DescriptionImpl implements Description 
{
    protected String owner;
    protected String description;
    protected String lang = null;
    protected Locale locale = null;
    
    public DescriptionImpl()
    {        
    }
    
    public DescriptionImpl(Object owner, String lang)
    {
        this.owner = owner.getClass().getName();
        this.lang = lang;
        locale = JetspeedLocale.convertStringToLocale(lang);
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String value)
    {
        this.description = value;
    }

    public String getLang()
    {
        return lang == null ? JetspeedLocale.getDefaultLocale().toString() : lang;
    }

    public Locale getLocale()
    {
        return locale == null ? locale = JetspeedLocale.convertStringToLocale(lang) : locale;
    }
}
