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

import org.apache.jetspeed.om.page.BaseFragmentElement;
import org.apache.jetspeed.om.page.FragmentDefinition;
import org.apache.jetspeed.om.page.FragmentReference;
import org.apache.jetspeed.om.page.BaseFragmentValidationListener;
import org.apache.jetspeed.om.page.PageFragment;

/**
 * FragmentDefinitionImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class FragmentDefinitionImpl extends BaseFragmentsElementImpl implements FragmentDefinition
{
    private static final long serialVersionUID = 1L;

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.FragmentDefinition#getDefId()
     */
    public String getDefId()
    {
        BaseFragmentElement rootFragment = getRootFragment();
        return ((rootFragment != null) ? rootFragment.getId() : null);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.impl.NodeImpl#getType()
     */
    public String getType()
    {
        return DOCUMENT_TYPE;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.impl.NodeImpl#isHidden()
     */
    public boolean isHidden()
    {
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.impl.NodeImpl#setHidden(boolean)
     */
    public void setHidden(boolean hidden)
    {
        throw new UnsupportedOperationException("PageTemplate.setHidden()");
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.BaseFragmentsElementImpl#newBaseFragmentValidationListener()
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
                // PageFragments can only appear in PageTemplates; recursive FragmentReference not supported
                return (!(fragmentElement instanceof PageFragment) && !(fragmentElement instanceof FragmentReference));
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
