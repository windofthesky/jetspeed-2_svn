/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
/*
 * Created on Aug 31, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.page.document.psml;

import java.util.Comparator;
import java.util.List;

/**
 * <p>
 * DocumentOrderCompartaor
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class NodeOrderComparator implements Comparator<String>
{
    private List<String> nodeIndex;
    private String relativePath="";

    /**
     *  
     */
    public NodeOrderComparator( List<String> nodeIndex, String relativePath )
    {
        super();
        this.nodeIndex = nodeIndex;        
        this.relativePath = relativePath;
    }

    /**
     * <p>
     * compare
     * </p>
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     * @param s1
     * @param s2
     * @return
     */
    public int compare( String s1, String s2 )
    {
        
            String node1 = null;
            String node2 = null;
       
            if(relativePath.length() < s1.length())
            {
                node1 =  s1.substring(relativePath.length());
            }
            else
            {
                node1 =  s1;
            }
            
            if(relativePath.length() < s2.length())
            {
                node2 =  s2.substring(relativePath.length());
            }
            else
            {
                node2 =  s2;
            }

            String c1 = null;
            String c2 = null;

            if (nodeIndex != null)
            {
                int index1 = nodeIndex.indexOf(node1);
                int index2 = nodeIndex.indexOf(node2);

                if (index1 > -1)
                {
                    c1 = String.valueOf(index1);
                }
                else
                {
                    c1 = node1;
                }

                if (index2 > -1)
                {
                    c2 = String.valueOf(index2);
                }
                else
                {
                    c2 = node2;
                }
            }
            else
            {
                c1 = node1;
                c2 = node2;
            }

            return c1.compareTo(c2);
        
    }

}
