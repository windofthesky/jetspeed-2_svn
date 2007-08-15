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

import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;

public class PageManagerLayoutEventListener implements LayoutEventListener
{
    private final PageManager pageManager;
    private final Page page;
    
    public PageManagerLayoutEventListener(PageManager pageManager, Page page, String layoutType)
    {
        this.pageManager = pageManager;
        this.page = page;
    }

    public void handleEvent(LayoutEvent event) throws LayoutEventException
    {
        try
        {
            if(event.getEventType() == LayoutEvent.ADDED)
            {
                page.getRootFragment().getFragments().add(event.getFragment());
                pageManager.updatePage(page);
            }
            else
            {
                Fragment fragment = event.getFragment();
                LayoutCoordinate coordinate = event.getNewCoordinate();
                fragment.getProperties().put(Fragment.COLUMN_PROPERTY_NAME, String.valueOf(coordinate.getX()));
                fragment.getProperties().put(Fragment.ROW_PROPERTY_NAME, String.valueOf(coordinate.getY()));
                pageManager.updatePage(page);
            }
        }
        catch (Exception e)
        {
            throw new LayoutEventException("Unable to update page.", e);
        }
    }

}
