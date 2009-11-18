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

import org.apache.jetspeed.idgenerator.IdGenerator;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.BaseConcretePageElement;
import org.apache.jetspeed.om.page.BaseFragmentElement;
import org.apache.jetspeed.om.page.BaseFragmentValidationListener;
import org.apache.jetspeed.om.page.PageFragment;

/**
 * AbstractBaseConcretePageElement
 *
 * @version $Id:$
 */
public abstract class AbstractBaseConcretePageElement extends AbstractBasePageElement implements BaseConcretePageElement
{
    private static final long serialVersionUID = 1L;

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getEffectiveDefaultDecorator(java.lang.String)
     */
    public String getEffectiveDefaultDecorator(String fragmentType)
    {
        // get locally defined decorator
        String decorator = getDefaultDecorator(fragmentType);
        if (decorator == null)
        {
            // delegate to parent folder
            Folder parentFolder = (Folder)getParent();
            if (parentFolder != null)
            {
                return parentFolder.getEffectiveDefaultDecorator(fragmentType);
            }
        }
        return decorator;
    }

    /**
     * unmarshalled - notification that this instance has been
     *                loaded from the persistent store
     * @param generator id generator
     * @return dirty flag
     */
    public boolean unmarshalled(IdGenerator generator)
    {
        // notify super class implementation
        boolean dirty = super.unmarshalled(generator);

        // default title of concrete pages to name
        if (getTitle() == null)
        {
            setTitle(getTitleName());
        }
        
        return dirty;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.psml.AbstractBaseFragmentsElement#newBaseFragmentValidationListener()
     */
    protected BaseFragmentValidationListener newBaseFragmentValidationListener()
    {
        return new BaseFragmentValidationListener()
        {
            /* (non-Javadoc)
             * @see org.apache.jetspeed.om.page.BaseFragmentValidationListener#validate(org.apache.jetspeed.om.page.BaseFragmentElement)
             */
            public boolean validate(BaseFragmentElement fragmentElement)
            {
                // PageFragments can only appear in PageTemplates
                return !(fragmentElement instanceof PageFragment);
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.om.page.BaseFragmentValidationListener#validate()
             */
            public boolean validate()
            {
                return true;
            }
        };
    }
}
