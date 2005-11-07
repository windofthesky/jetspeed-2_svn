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

/**
 * FragmentSecurityConstraintsImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class FragmentSecurityConstraintsImpl extends SecurityConstraintsImpl
{
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.SecurityConstraintsImpl#newSecurityConstraint()
     */
    public BaseSecurityConstraint newSecurityConstraint()
    {
        return new FragmentSecurityConstraint();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.SecurityConstraintsImpl#newSecurityConstraintsRef()
     */
    public BaseSecurityConstraintsRef newSecurityConstraintsRef()
    {
        return new FragmentSecurityConstraintsRef();
    }
}
