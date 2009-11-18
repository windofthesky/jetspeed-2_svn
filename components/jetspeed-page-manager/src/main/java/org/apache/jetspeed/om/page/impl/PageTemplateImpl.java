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

import org.apache.jetspeed.om.page.PageTemplate;
import org.apache.jetspeed.om.page.BaseFragmentElement;
import org.apache.jetspeed.om.page.BaseFragmentValidationListener;
import org.apache.jetspeed.om.page.PageFragment;

/**
 * PageTemplateImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class PageTemplateImpl extends BasePageElementImpl implements PageTemplate
{
    private static final long serialVersionUID = 1L;

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
            private boolean valid;
            
            /* (non-Javadoc)
             * @see org.apache.jetspeed.om.page.BaseFragmentValidationListener#validate(org.apache.jetspeed.om.page.BaseFragmentElement)
             */
            public boolean validate(BaseFragmentElement fragmentElement)
            {
                // one PageFragment required
                if (!valid)
                {
                    valid = (fragmentElement instanceof PageFragment);
                    return true;
                }
                else
                {
                    return !(fragmentElement instanceof PageFragment);
                }
            }

            /* (non-Javadoc)
             * @see org.apache.jetspeed.om.page.BaseFragmentValidationListener#validate()
             */
            public boolean validate()
            {
                // one PageFragment required
                return valid;
            }
        };
    }
}
