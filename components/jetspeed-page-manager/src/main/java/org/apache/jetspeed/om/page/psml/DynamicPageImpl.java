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

import org.apache.jetspeed.om.page.DynamicPage;

/**
 * Portal dynamic page definition implementation.
 * 
 * @version $Id:$
 */
public class DynamicPageImpl extends AbstractBaseConcretePageElement implements DynamicPage
{
    private static final long serialVersionUID = 1L;

    private String pageType;
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.DynamicPage#getPageType()
     */
    public String getPageType()
    {
        return pageType;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.DynamicPage#setPageType(java.lang.String)
     */
    public void setPageType(String pageType)
    {
        this.pageType = pageType;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getType()
     */
    public String getType()
    {
        return DOCUMENT_TYPE;
    }
}
