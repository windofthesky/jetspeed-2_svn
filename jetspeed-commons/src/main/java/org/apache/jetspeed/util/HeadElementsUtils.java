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
package org.apache.jetspeed.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.portlet.HeadElement;
import org.apache.jetspeed.portlet.HeaderPhaseSupportConstants;

public class HeadElementsUtils
{
    
    private HeadElementsUtils()
    {
    }
    
    @SuppressWarnings("unchecked")
    public static void aggregateHeadElements(List<KeyValue<String, HeadElement>> aggregatedHeadElements, ContentFragment contentFragment)
    {
        List<ContentFragment> childContentFragments = (List<ContentFragment>) contentFragment.getFragments();
        
        if (childContentFragments != null && !childContentFragments.isEmpty())
        {
            for (ContentFragment child : childContentFragments)
            {
                if (!"hidden".equals(contentFragment.getState()))
                {
                    aggregateHeadElements(aggregatedHeadElements, child);
                }
            }
        }
        
        PortletContent portletContent = contentFragment.getPortletContent();
        
        // portletContent can be null if this method is invoked before the portlet window starts rendering
        if (portletContent != null)
        {
            aggregateHeadElements(aggregatedHeadElements, portletContent.getHeadElements());
        }
    }
    
    public static void aggregateHeadElements(List<KeyValue<String, HeadElement>> aggregatedHeadElements, List<KeyValue<String, HeadElement>> headElements)
    {
        // Brief explanation on head element aggregation algorithm (Thanks to Ate for the brilliant ideas!):
        // - Precondition: start from the zero as insertion index.
        // - Rule1: if there already exists an element with the key, 
        //              set the insertion index to the matching index + 1.
        // - Rule2: if there's no existing element with the key, 
        //              insert the element at the current insertion index 
        //              and increase the insertion index.
        
        if (!headElements.isEmpty())
        {
            int insertionIndex = 0;
            
            for (KeyValue<String, HeadElement> kvPair : headElements)
            {
                int offset = aggregatedHeadElements.indexOf(kvPair);
                
                if (offset != -1)
                {
                    insertionIndex = offset + 1;
                }
                else
                {
                    aggregatedHeadElements.add(insertionIndex++, kvPair);
                }
            }
        }
    }
    
    public static void mergeHeadElementsByHint( List<KeyValue<String, HeadElement>> headElements )
    {
        Map<String, HeadElement> firstElementByMergeHint = new HashMap<String, HeadElement>();
        Map<String, Set<String>> mergedTextContents = new HashMap<String, Set<String>>();
        
        for (Iterator<KeyValue<String, HeadElement>> it = headElements.iterator(); it.hasNext(); )
        {
            KeyValue<String, HeadElement> kvPair = it.next();
            HeadElement element = kvPair.getValue();
            
            if (element.hasAttribute(HeaderPhaseSupportConstants.HEAD_ELEMENT_CONTRIBUTION_MERGE_HINT_ATTRIBUTE))
            {
                String mergeHint = element.getAttribute(HeaderPhaseSupportConstants.HEAD_ELEMENT_CONTRIBUTION_MERGE_HINT_ATTRIBUTE);
                String textContent = element.getTextContent();
                
                if (textContent != null)
                {
                    textContent = textContent.trim();
                }
                
                if (firstElementByMergeHint.containsKey(mergeHint))
                {
                    if (textContent != null && !"".equals(textContent))
                    {
                        Set<String> textContentSet = mergedTextContents.get(mergeHint);
                        textContentSet.add(textContent);
                    }
                    
                    it.remove();
                }
                else
                {
                    firstElementByMergeHint.put(mergeHint, element);
                    Set<String> textContentSet = new TreeSet<String>();
                    mergedTextContents.put(mergeHint, textContentSet);
                    
                    if (textContent != null && !"".equals(textContent))
                    {
                        textContentSet.add(textContent);
                    }
                }
            }
        }
        
        for (Map.Entry<String, HeadElement> entry : firstElementByMergeHint.entrySet())
        {
            String mergeHint = entry.getKey();
            HeadElement firstElement = entry.getValue();
            Set<String> textContentSet = mergedTextContents.get(mergeHint);
            
            StringBuilder sb = new StringBuilder(80);
            boolean firstDone = false;
            
            for (String textContent : textContentSet)
            {
                if (firstDone)
                    sb.append("\r\n");
                else
                    firstDone = true;
                
                sb.append(textContent);
            }
            
            firstElement.setTextContent(sb.toString());
        }
    }
    
}
