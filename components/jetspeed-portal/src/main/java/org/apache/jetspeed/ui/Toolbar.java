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

import java.util.List;

import org.apache.jetspeed.om.page.ContentFragment;

public class Toolbar
{
    public enum Orientation { LEFT, RIGHT };
    public static final String LEFT_TOOLBAR_ID = "jstbLeft";
    public static final String RIGHT_TOOLBAR_ID = "jstbRight";

    private String id;    
    private String cssClass;    
    private Orientation orientation;
    private boolean closed = false;
    private ContentFragment cf;

    public Toolbar(Orientation o, String id, ContentFragment cf)
    {
        this.orientation = o;
        this.id = id;
        this.cf = cf;
    }
    
    public String getCssClass()
    {
        return cssClass;
    }
    public void setCssClass(String cssClass)
    {
        this.cssClass = cssClass;
    }
    public boolean isClosed()
    {
        return closed;
    }
    public void setClosed(boolean closed)
    {
        this.closed = closed;
    }
    public String getId()
    {
        return id;
    }
    public void setId(String id)
    {
        this.id = id;
    }
    public Orientation getOrientation()
    {
        return orientation;
    }
    public void setOrientation(Orientation orientation)
    {
        this.orientation = orientation;
    }
    public List<ContentFragment> getTools()
    {
        return cf.getFragments(); 
    }    
}