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
package org.apache.jetspeed.om.common.preference;

/**
 * PreferenceValue
 * <br />
 * Represents an individual value for a preference which could
 * either be the default preferences from a portlet's deployment descriptor
 * or a preference value for a specific user.  This class should only be
 * accessed by Jetspeed internals as Preference values are really
 * only String values.  The use of preference value objects helps
 * facilitate the use object relational tools in terms of persistence operations. 
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface PreferenceValue
{
    /**
     * @return
     */
    public abstract String getValue();
    /**
     * @param string
     */
    public abstract void setValue(String string);   
    
}