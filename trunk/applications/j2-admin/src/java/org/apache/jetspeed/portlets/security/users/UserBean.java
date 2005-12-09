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
package org.apache.jetspeed.portlets.security.users;

/**
 * User state.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: UserBean.java 348264 2005-11-22 22:06:45Z taylor $
 */
public class UserBean
{
    private String first;
    private String last;
    
    public UserBean(String first, String last)
    {
        this.first = first;
        this.last = last;
    }
    
    public void setFirst(String first)
    {
        this.first = first;
    }

    
    /**
     * @return Returns the last.
     */
    public String getLast()
    {
        return last;
    }
    /**
     * @param last The last to set.
     */
    public void setLast(String last)
    {
        this.last = last;
    }
    /**
     * @return Returns the first.
     */
    public String getFirst()
    {
        return first;
    }
}