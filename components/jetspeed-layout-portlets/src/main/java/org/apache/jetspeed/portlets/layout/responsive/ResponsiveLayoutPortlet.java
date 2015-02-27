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
package org.apache.jetspeed.portlets.layout.responsive;

import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.portlets.layout.MultiColumnPortlet;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

public class ResponsiveLayoutPortlet extends MultiColumnPortlet {

    public int getNumColumns(ContentFragment fragment) {
        int columns = 0;
        for (ContentFragment cf : fragment.getFragments()) {
            String propertyValue = cf.getProperty(ContentFragment.COLUMN_PROPERTY_NAME);
            if (propertyValue != null) {
                int columnProperty = Integer.parseInt(propertyValue);
                if (columnProperty > columns)
                    columns = columnProperty;
            }
        }
        return columns + 1;
    }

    public void init( PortletConfig config ) throws PortletException {
        super.init(config);
    }

}
