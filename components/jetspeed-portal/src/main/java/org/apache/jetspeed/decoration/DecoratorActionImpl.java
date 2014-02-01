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
package org.apache.jetspeed.decoration;

import java.io.Serializable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * DecoratorActionImpl
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class DecoratorActionImpl implements Serializable, DecoratorAction {
    public static final String RESOURCE_BUNDLE = "org.apache.jetspeed.decoration.resources.DecoratorActions";

    String actionName = null;
    String actionType = null;
    String name = null;
    String link = null;
    String alt = null;
    String action = null;
    String target;
    boolean custom;
    
    public static ResourceBundle getResourceBundle(Locale locale)
    {
        return getBundle(RESOURCE_BUNDLE, locale);
    }
    
    private static ResourceBundle getBundle(String base, Locale locale)
    {
        ResourceBundle bundle = null;
        try
        {
            if ( locale != null )
            {
                bundle = ResourceBundle.getBundle(base, locale);
            }
            else
            {
                bundle = ResourceBundle.getBundle(base);
            }        
        }
        catch (MissingResourceException mre)
        {            
        }
        return bundle;
    }
    
    public static String getResourceString(ResourceBundle bundle, String key, String defaultValue)
    {
        String value = defaultValue;
        
        if ( key != null && bundle != null )
        try
        {
            value = bundle.getString(key);
        }
        catch (MissingResourceException mre)
        {            
        }
        return value;
    }

    public DecoratorActionImpl(String actionName, String name, String alt, Locale locale, String link, String action, boolean custom, String actionType)
    {
        ResourceBundle bundle = getBundle(RESOURCE_BUNDLE, locale);
        this.actionName = actionName;
        this.actionType = actionType;
        this.name = getResourceString(bundle,name,name);
        this.alt = getResourceString(bundle,alt,alt);
        this.link = link;
        this.action = action;
        this.custom = custom;
    }
    
    public DecoratorActionImpl(String actionName, String name, String alt, String link, String action, boolean custom, String actionType)
    {
        this.actionName = actionName;
        this.actionType = actionType;
        this.name = name;
        this.alt = alt;
        this.link = link;
        this.action = action;
        this.custom = custom;
    }
    
    public DecoratorActionImpl(String name, Locale locale, String link, String action, boolean custom, String actionType)
    {
        this(name,name,name,locale,link,action,custom,actionType);
    }
    
    public DecoratorActionImpl(String name, Locale locale, String link, String action, String actionType)
    {
        this(name,name,name,locale,link,action,false,actionType);
    }
    
    public DecoratorActionImpl(String actionName, String name, String alt, String link, String actionType)
    {
        this(actionName, name,alt,null,link,null,false,actionType);
    }

    @Override
    public String getActionName()
    {
        return this.actionName;
    }
    @Override
    public void setActionName(String actionName)
    {
        this.actionName = actionName;
    }

    @Override
    public String getActionType()
    {
        return this.actionType;
    }
    @Override
    public void setActionType(String actionType)
    {
        this.actionType = actionType;
    }
    
    @Override
    public String getName()
    {
        return this.name;
    }
    
    @Override
    public void setName(String name)
    {
        this.name = name;
    }
    
    @Override
    public String getLink()
    {
        return this.link;
    }
    
    @Override
    public void setLink(String link)
    {
        this.link = link;
    }

    @Override
    public String getAlt()
    {
        return this.alt;
    }
    
    @Override
    public void setAlt(String alt)
    {
        this.alt = alt;
    }

    @Override
    public String getAction()
    {
        return this.action;
    }
    
    @Override
    public void setAction(String action)
    {
        this.action = action;
    }
    
    @Override
    public String getTarget()
    {
        return this.target;
    }
    
    @Override
    public void setTarget(String target)
    {
        this.target = target;
    }
    
    @Override
    public boolean isCustom()
    {
        return custom;
    }

    @Override
    public void setCustom(boolean custom)
    {
        this.custom = custom;
    }
}
