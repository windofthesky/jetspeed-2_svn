/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.components.adapters;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public class ThreadLocalDelegationStrategy extends AbstractDelegationStrategy
{

    private ThreadLocal localInstance=new ThreadLocal();
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.AbstractDelegationStrategy#getDelegatedInstance()
     */
    public Object get()
    {        
        Object useInstance = localInstance.get();
        if(useInstance != null)
        {
            return useInstance;
        }
        else
        {
            useInstance = adapter.getComponentInstance();
            set(useInstance);
            return useInstance;
        }
        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.AbstractDelegationStrategy#setDelegatedInstance(java.lang.Object)
     */
    public void set( Object instance )
    {
        localInstance.set(instance);
    }

}
