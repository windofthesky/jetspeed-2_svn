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
package org.apache.jetspeed.om.common;

import java.util.Collection;
import java.util.Locale;

/**
 * GenericMetadata
 * <br/>
 * Interface that allows retrieving localized information
 * 
 * @author <a href="mailto:jford@apache.org">Jeremy Ford</a>
 * @version $Id$
 */
public interface GenericMetadata {

    public void addField(Locale locale, String name, String value);
    public void addField(LocalizedField field);
    public Collection getFields(String name);
    public void setFields(String name, Collection values);
    
    public Collection getFields();
    public void setFields(Collection fields);
    
    LocalizedField createLocalizedField();
}
