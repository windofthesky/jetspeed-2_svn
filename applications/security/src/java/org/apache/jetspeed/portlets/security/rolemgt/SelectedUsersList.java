/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package org.apache.jetspeed.portlets.security.rolemgt;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Selected users list.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class SelectedUsersList
{
    private List selectedUsers = new ArrayList();
    
    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public SelectedUsersList()
    {
        for (int i = 1; i < 7; i++)
        {
            selectedUsers.add(new RoleMgtUser("username" + i));
        }
    }

    /**
     * @return Returns the selectedUsers.
     */
    public List getSelectedUsers()
    {
        return this.selectedUsers;
    }
    /**
     * @param selectedUsers The selectedUsers to set.
     */
    public void setSelectedUsers(List selectedUsers)
    {
        this.selectedUsers = selectedUsers;
    }
}
