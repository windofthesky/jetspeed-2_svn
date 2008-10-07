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
package org.apache.jetspeed.portalsite;

import java.util.Locale;

/**
 * This interface describes the portal-site menu separator
 * elements constructed and returned to decorators.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public interface MenuSeparator extends MenuElement
{
    /**
     * getText - get default text for menu separator
     *
     * @return text
     */
    String getText();

    /**
     * getText - get locale specific text for menu separator
     *           from metadata
     *
     * @param locale preferred locale
     * @return text
     */
    String getText(Locale locale);
}
