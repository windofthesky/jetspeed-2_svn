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
package org.apache.jetspeed.profiler;

/**
 * Profile locator properties represent the individual path elements in a {@link ProfileLocator}
 * An example locator path with locator properties as each name value segment pairs in the path:
 *
 *      <pre>page:default.psml:artist:al-stewart:song:on-the-border</pre>
 *      <pre>path:/sports/football/nfl/chiefs:language:en</pre>
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
     *
     * @param value The value of the property.
     */
    void setValue(String value);

    /**
     * Returns the fallback type of the property.
     * @see org.apache.jetspeed.profiler.rules.RuleCriterion#FALLBACK_CONTINUE
     * @see org.apache.jetspeed.profiler.rules.RuleCriterion#FALLBACK_LOOP
     * @see org.apache.jetspeed.profiler.rules.RuleCriterion#FALLBACK_STOP
     *
     * @return the fallback type of the property
     */
    int getFallbackType();

    /**
     * Returns the fallback type of the property.
     * @see org.apache.jetspeed.profiler.rules.RuleCriterion#FALLBACK_CONTINUE
     * @see org.apache.jetspeed.profiler.rules.RuleCriterion#FALLBACK_LOOP
     * @see org.apache.jetspeed.profiler.rules.RuleCriterion#FALLBACK_STOP
     *
     * @param type the fallback type of the property
     */
    void setFallbackType(int type);

    /**
     * The name of the locator property
     *
     * @return the name of the locator property
     */
    String getName();

    /**
     * The name of the locator property
     *
     * @param string the name of the locator property
     */
    void setName(String string);

    /**
     * @return
     * @deprecated
     */
    String getType();

    /**
     *
     * @param type
     * @deprecated
     */
    void setType(String type);

    /**
     * Determines if this locator property is a control
     *
     * @return control classification flag, is it a control
     */
    boolean isControl();
    
    /**
     * Determines if this locator property is a navigation
     *
     * @return true if a property is a navigation
     */
    boolean isNavigation();
}
