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
package org.apache.jetspeed.profiler;

/**
 * ProfileLocatorElement
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface ProfileLocatorProperty
{    
    /**
     * Gets the value of the locator property.
     * 
     * @return The value of the property.
     */
    String getValue();
    
    /**
     * Sets the value of the locator property.
     * @param value The value of the property.
     */
    void setValue(String value);


    /**
     * Returns the fallback type of the property.
     * see 
     * 
     * @return
     */
    int getFallbackType();

    /**
     * @return
     */
    String getName();

    /**
     * @return
     */
    String getType();

    /**
     * @param i
     */
    void setFallbackType(int type);

    /**
     * @param string
     */
    void setName(String string);
    
    /**
     * @param string
     */
    void setType(String type);    

    /**
     * @return control classification flag
     */
    boolean isControl();
    
    /**
     * @return true if a property is a navigation
     */
    boolean isNavigation();
}
