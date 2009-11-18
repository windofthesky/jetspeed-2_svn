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
package org.apache.jetspeed.om.page;

/**
 * This interface represents a concrete page document used by Jetspeed
 * to define a portal page.
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public interface BaseConcretePageElement extends BasePageElement
{
    /**
     * Returns the name of the default decorator as set here or
     * in parent folders that applies in this page to fragments
     * of the specified type.
     *
     * @param fragmentType the type of fragment considered
     * @return the decorator name for the selected type
     */
    String getEffectiveDefaultDecorator(String fragmentType);    
}
