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
package org.apache.jetspeed.profiler.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;

import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.rules.RuleCriterion;

/**
 * ProfileLocatorImpl
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedProfileLocator implements ProfileLocatorControl
{    
    private LinkedList elements = new LinkedList();
        
    public List getElements()
    {
        return elements;
    }
    
    public Iterator iterator()
    {    
        return new ProfileFallbackIterator(this);
    }
    
    public String getValue(String name)
    {
        for (int ix = 0; ix < elements.size(); ix++)
        {
            ProfileLocatorPropertyImpl element = (ProfileLocatorPropertyImpl)elements.get(ix);
            String elementName = element.getName(); 
            if (elementName != null && elementName.equals(name))
            {
                return element.getValue();
            }
        }
        return null;
    }
    
    public void add(RuleCriterion criterion, String value)
    {
        elements.add(new ProfileLocatorPropertyImpl(criterion, value));
    }

    public void add(String name, String value)
    {
        elements.add(new ProfileLocatorPropertyImpl(name, value));        
    }
    
    public void createFromLocatorPath(String path)
    {
        elements.clear();
        StringTokenizer tokenizer = new StringTokenizer(path, ProfileLocator.PATH_SEPARATOR);
        while (tokenizer.hasMoreTokens())
        {
            String name = (String)tokenizer.nextToken();
            if (tokenizer.hasMoreTokens())
            {
                String value = tokenizer.nextToken();
                this.add(name, value);
            }
        }        
    }
                    
    public String getLocatorPath()
    {
        StringBuffer key = new StringBuffer();
        ListIterator it = elements.listIterator();
        while (it.hasNext())
        {
            ProfileLocatorPropertyImpl element = (ProfileLocatorPropertyImpl)it.next();
            key.append(element.getName());
            key.append(ProfileLocator.PATH_SEPARATOR);
            key.append(element.getValue());
            if (it.hasNext())
            {
                key.append(ProfileLocator.PATH_SEPARATOR);
            }
        }
        return key.toString();
    }
        
    public String toString()
    {
        return getLocatorPath();
    }
        
}
