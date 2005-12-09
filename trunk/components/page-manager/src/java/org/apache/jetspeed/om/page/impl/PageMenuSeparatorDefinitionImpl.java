/*
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.jetspeed.om.page.impl;

import java.util.Collection;

import org.apache.jetspeed.om.folder.MenuSeparatorDefinition;
import org.apache.jetspeed.om.folder.impl.BaseMenuSeparatorDefinitionImpl;
import org.apache.jetspeed.om.page.PageMetadataImpl;

/**
 * PageMenuSeparatorDefinitionImpl
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public class PageMenuSeparatorDefinitionImpl extends BaseMenuSeparatorDefinitionImpl implements MenuSeparatorDefinition, PageMenuDefinitionElement 
{
    // new class defined only to facilitate OJB table/class mapping

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.impl.BaseMenuDefinitionMetadata#newPageMetadata()
     */
    public PageMetadataImpl newPageMetadata(Collection fields)
    {
        PageMetadataImpl pageMetadata = new PageMetadataImpl(PageMenuMetadataLocalizedFieldImpl.class);
        pageMetadata.setFields(fields);
        return pageMetadata;
    }
}
