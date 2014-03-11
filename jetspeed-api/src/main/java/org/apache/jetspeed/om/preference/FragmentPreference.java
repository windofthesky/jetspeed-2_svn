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
package org.apache.jetspeed.om.preference;

import java.util.List;

/**
 * This interface represents a fragment preference used to populate
 * portlet user preferences on initial access.
 *
 * @version $Id$
 */
public interface FragmentPreference
{
    /**
     * getName
     *
     * @return preference name
     */
    String getName();
    
    /**
     * setName
     *
     * @param name preference name
     */
    void setName(String name);
    
    /**
     * isReadOnly
     *
     * @return read only preference flag
     */
    boolean isReadOnly();
    
    /**
     * setReadOnly
     *
     * @param readOnly read only preference flag
     */
    void setReadOnly(boolean readOnly);
    
    /**
     * getValueList
     *
     * @return list of String preference values
     */
    List<String> getValueList();
    
    /**
     * setValueList
     *
     * @param values list of String preference values
     */
    void setValueList(List<String> values);
}
