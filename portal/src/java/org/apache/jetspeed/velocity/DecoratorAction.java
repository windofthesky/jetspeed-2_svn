/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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

/**
 * DecoratorAction
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class DecoratorAction
{
    String name = null;
    String link = null;
    String alt = null;
    String action = null;

    /**
     * Constructor
     * 
     * @param name   Name of the action
     * @param alt    Alternative text description (localized)
     */
    public DecoratorAction(String name, String alt, String link)
    {
        this.name = name;
        this.alt = alt;
        this.link = link;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public String getLink()
    {
        return this.link;
    }
    
    public void setLink(String link)
    {
        this.link = link;
    }

    public String getAlt()
    {
        return this.alt;
    }

    public String getAction()
    {
        return this.action;
    }
    
    public void setAction(String action)
    {
        this.action = action;
    }
    
}
