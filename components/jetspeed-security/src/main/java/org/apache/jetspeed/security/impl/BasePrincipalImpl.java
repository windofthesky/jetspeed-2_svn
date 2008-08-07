/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security.impl;

import org.apache.jetspeed.security.BasePrincipal;

/**
 * <p>
 * {@link BasePrincipal} interface implementation.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public abstract class BasePrincipalImpl implements BasePrincipal
{   
    /** The version uid. */
    private static final long serialVersionUID = 5687385387290144541L;

    /** The principal name. */
    protected final String name;

    /** is this principal enabled **/
    protected boolean enabled = true;
    
    /** is this principal a mapping **/
    protected boolean isMapping = false;

    protected long id;
    
    public BasePrincipalImpl(String name)
    {
        this.name = name;
    }
    
    public BasePrincipalImpl(long id, String name, boolean isEnabled, boolean isMapping)
    {
        this.id = id;
        this.name = name;
        this.enabled = isEnabled;
        this.isMapping = isMapping;
    }

    /**
     * @see java.security.Principal#getName()
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return this.name.hashCode();
    }

    /**
     * <p>
     * Returns a string representation of this principal.
     * </p>
     * 
     * @return A string representation of this principal.
     */
    public String toString()
    {
        return this.name;
    }

    /**
     * @see org.apache.jetspeed.security.BasePrincipal#isEnabled()
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * @see org.apache.jetspeed.security.BasePrincipal#setEnabled(boolean)
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
   
    public boolean isMapping()
    {
        return isMapping;
    }

    public long getId()
    {
        return id;
    }
}
