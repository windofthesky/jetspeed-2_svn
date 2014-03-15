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

package org.apache.jetspeed.om.page.psml;

import org.apache.jetspeed.om.page.Fragment;

import java.util.HashMap;
import java.util.Map;

/**
 * @version $Id$
 */
public class DefaultsImpl
{

    private String skin = null;
    private Map<String,String> decoratorMap = new HashMap<String,String>();

    /**
     * getSkin
     *
     * @return skin name used in decorators
     */
    public String getSkin()
    {
        return this.skin;
    }

    /**
     * setSkin
     *
     * @param skin name used in decorators
     */
    public void setSkin(String skin)
    {
        this.skin = skin;
    }

    /**
     * getDecorator
     *
     * @param type Fragment.LAYOUT or Fragment.PORTLET constants
     * @return decorator name
     */
    public String getDecorator(String type)
    {
        return decoratorMap.get(type);
    }

    /**
     * setDecorator
     *
     * @param type Fragment.LAYOUT or Fragment.PORTLET constants
     * @param decorator decorator name
     */
    public void setDecorator(String type, String decorator)
    {
        decoratorMap.put(type,decorator);
    }

    /**
     * getLayoutDecorator
     *
     * @return Fragment.LAYOUT decorator name
     */
    public String getLayoutDecorator()
    {
        return getDecorator(Fragment.LAYOUT);
    }

    /**
     * setLayoutDecorator
     *
     * @param decorator Fragment.LAYOUT decorator name
     */
    public void setLayoutDecorator(String decorator)
    {
        setDecorator(Fragment.LAYOUT,decorator);
    }

    /**
     * getPortletDecorator
     *
     * @return Fragment.PORTLET decorator name
     */
    public String getPortletDecorator()
    {
        return getDecorator(Fragment.PORTLET);
    }

    /**
     * setPortletDecorator
     *
     * @param decorator Fragment.PORTLET decorator name
     */
    public void setPortletDecorator(String decorator)
    {
        setDecorator(Fragment.PORTLET,decorator);
    }

}
