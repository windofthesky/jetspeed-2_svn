/* Copyright 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
 * <p>{@link BasePrincipal} interface implementation.</p>
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class BasePrincipalImpl implements BasePrincipal
{
    private final String name;
    private final String fullPath;

    /**
     * <p>Principal constructor given a name and preferences root.</p>
     * @param name The principal name.
     * @param prefsRoot The preferences root node.
     */
    public BasePrincipalImpl(String name, String prefsRoot)
    {
        this.name = name;
        this.fullPath = getFullPathFromPrincipalName(name, prefsRoot);
    }

    /**
     * @see org.apache.jetspeed.security.BasePrincipal#getFullPath()
     */
    public String getFullPath()
    {
        return this.fullPath;
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
     * <p>Returns a string representation of this principal.</p>
     * @return A string representation of this principal.
     */
    public String toString()
    {
        return this.name;
    }

    /**
     * <p>Gets the principal implementation full path from the principal name.</p>
     * <p>Hierarchical principal names should follow: {principal}.{subprincipal}.
     * "." is used as the separator for hierarchical elements.</p>
     * <p>The implementation path follow /PREFS_{PRINCIPAL}_ROOT/{principal}/{subprincipal}.</p>        
     * @param name The principal name.
     * @param prefsRoot The preferences root node.
     * @return The preferences full path / principal name.
     */
    public static String getFullPathFromPrincipalName(String name, String prefsRoot)
    {
        String fullPath = name;
        if (null != fullPath)
        {
            fullPath = prefsRoot + fullPath.replace('.', '/');
        }
        return fullPath;
    }

    /**
     * <p>Gets the principal name from the principal implementation full path.</p>
     * <p>Hierarchical principal names should follow: {principal}.{subprincipal}.
     * "." is used as the separator for hierarchical elements.</p>
     * <p>The implementation path follow /PREFS_{PRINCIPAL}_ROOT/{principal}/{subprincipal}.</p>        
     * @param fullPath The principal full path.
     * @param prefsRoot The preferences root node.
     * @return The principal name.
     */
    public static String getPrincipalNameFromFullPath(String fullPath, String prefsRoot)
    {
        String name = fullPath;
        if (null != name)
        {
            if (prefsRoot.equals(UserPrincipalImpl.PREFS_USER_ROOT))
            {
                name = name.substring(prefsRoot.length(), name.length());
            }
            else
            {
                name = name.substring(prefsRoot.length(), name.length());
            }
            name = name.replace('/', '.');
        }
        return name;
    }

    private boolean enabled = true;
    
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
}
