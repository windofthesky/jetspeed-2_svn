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

package org.apache.jetspeed.ui;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;

/**
 * Represents a page in a row/column oriented layout 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class JetuiPage implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Vector<ContentFragment> columns = null;
    private int numberOfColumns = 1;
    
    public JetuiPage(ContentPage page)
    {
        String jetspeedLayout = page.getRootFragment().getName();
        if (jetspeedLayout.indexOf("Two") > -1)
        {
            numberOfColumns = 2;
        }
        else if (jetspeedLayout.indexOf("Three") > -1)
        {
            numberOfColumns = 3;
        }    
        columns = new Vector<ContentFragment>(numberOfColumns);
        buildColumns(page.getRootFragment());
    }
    
    private boolean buildColumns(ContentFragment f)
    {
        List<ContentFragment> fragments = f.getFragments();
        if (fragments != null && !fragments.isEmpty())
        {
            for (ContentFragment child : fragments)
            {
                boolean found = buildColumns(child);
                if (found)
                    return true;
            }
        }
        return false;
    }

    protected final int getColumn(ContentFragment fragment)
    {
        String propertyValue = fragment.getProperty(ContentFragment.COLUMN_PROPERTY_NAME);
        if (propertyValue != null)
        {
            int columnNumber = Integer.parseInt(propertyValue);

            // Exceeded columns get put into the last column
            if (columnNumber >= numberOfColumns)
            {
                columnNumber = (numberOfColumns - 1);
            }
            // Columns less than 1 go in the first column
            else if (columnNumber < 0)
            {
                columnNumber = 0;
            }

            return columnNumber;
        }
        else
        {
            return (numberOfColumns - 1);
        }
    }
    
}

