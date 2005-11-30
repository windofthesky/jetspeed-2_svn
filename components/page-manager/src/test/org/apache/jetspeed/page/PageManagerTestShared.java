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
package org.apache.jetspeed.page;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * PageManagerTestShared
 * 
 * @author <a href="rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 *          
 */
interface PageManagerTestShared
{
    class Shared
    {
        /**
         * makeListFromCSV
         *
         * Create List of String values from CSV String for principals/permissions.
         * 
         * @param csv CSV string
         * @return values list
         */
        static List makeListFromCSV(String csv)
        {
            if (!csv.equals("*"))
            {
                List csvList = new ArrayList();
                if (csv.indexOf(',') != -1)
                {
                    StringTokenizer csvTokens = new StringTokenizer(csv, ",");
                    while (csvTokens.hasMoreTokens())
                    {
                        csvList.add(csvTokens.nextToken().trim());
                    }
                }
                else
                {
                    csvList.add(csv);
                }
                return csvList;
            }
            return null;        
        }

        /**
         * makeCSVFromList
         *
         * Create CSV String for principals/permissions from List of String values
         * 
         * @param list values list
         * @return CSV string
         */
        static String makeCSVFromList(List list)
        {
            if ((list != null) && !list.isEmpty())
            {
                StringBuffer csv = new StringBuffer();
                Iterator listIter = list.iterator();
                while (listIter.hasNext())
                {
                    if (csv.length() > 0)
                    {
                        csv.append(",");
                    }
                    csv.append((String)listIter.next());
                }
                return csv.toString();
            }
            return null;
        }
    }
}
