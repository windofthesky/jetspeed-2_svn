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
package org.apache.jetspeed.rewriter;

import java.util.HashMap;
import java.util.Map;

/**
 * TestRewriter
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class UnitTestRewriter extends BasicRewriter
{    
    private Map anchors = new HashMap();
    private String paragraph = null;
    private boolean inParagraph = false;
    
    public String getAnchorValue(String name)
    {
        return (String)anchors.get(name);
    }
    
    public String getParagraph()
    {
        return paragraph;
    }
    
    public boolean enterStartTagEvent(String tag, MutableAttributes attrs)
    {
        if (tag.equalsIgnoreCase("a"))
        {
            anchors.put(attrs.getValue("name"), attrs.getValue("href"));
        }
        if (tag.equalsIgnoreCase("p"))
        {
            inParagraph = true;
        }
        return true;
    }
        
    public boolean enterText(char[] values, int param)
    {
        if (inParagraph)
        {
            paragraph = new String(values);
        }
        return true;
    }
            
    public String exitEndTagEvent(String tag)
    {
        inParagraph = false;
        return "";
    }
    
    
}
