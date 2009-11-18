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
package org.apache.jetspeed.portlets.layout;

import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;

public class PageLayoutEventListener implements LayoutEventListener
{
    public PageLayoutEventListener(String layoutType)
    {
    }

    public void handleEvent(LayoutEvent event) throws LayoutEventException
    {
        try
        {
            if (event.getEventType() == LayoutEvent.ADDED)
            {
                ContentPage page = event.getPage();
                ContentFragment fragment = event.getFragment();
                if (fragment == null)
                {
                    page.addPortlet(event.getPortletType(), event.getPortletName());
                }
                else
                {
                    LayoutCoordinate coordinate = event.getNewCoordinate();
                    page.addFragmentAtRowColumn(fragment, coordinate.getY(), coordinate.getX());                    
                }
            }
            else
            {
                LayoutCoordinate coordinate = event.getNewCoordinate();
                ContentFragment fragment = event.getFragment();
                fragment.updateRowColumn(coordinate.getY(), coordinate.getX());
            }
        }
        catch (Exception e)
        {
            throw new LayoutEventException("Unable to update page.", e);
        }
    }
}
