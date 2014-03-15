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
package org.apache.jetspeed.om.page.impl;

import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.om.folder.MenuDefinitionElement;
import org.apache.jetspeed.om.folder.impl.BaseMenuDefinitionImpl;
import org.apache.jetspeed.om.page.PageMetadataImpl;
import org.apache.jetspeed.om.portlet.LocalizedField;

import java.util.Collection;
import java.util.List;

/**
 * PageMenuDefinitionImpl
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public class PageMenuDefinitionImpl extends BaseMenuDefinitionImpl implements MenuDefinition, PageMenuDefinitionElement
{
    // new class defined only to facilitate OJB table/class mapping

    private PageMenuDefinitionElementList menuElements;

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.impl.BaseMenuDefinitionMetadata#newPageMetadata()
     */
    public PageMetadataImpl newPageMetadata(Collection<LocalizedField> fields)
    {
        PageMetadataImpl pageMetadata = new PageMetadataImpl(PageMenuMetadataLocalizedFieldImpl.class);
        pageMetadata.setFields(fields);
        return pageMetadata;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuDefinition#getMenuElements()
     */
    public List<MenuDefinitionElement> getMenuElements()
    {
        // return mutable menu element list
        // by using list wrapper to manage
        // element order
        if (menuElements == null)
        {
            menuElements = new PageMenuDefinitionElementList(this);
        }
        return menuElements;
    }
}
