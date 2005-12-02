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
 * GenericMetadata <br/>Interface that allows retrieving localized information
 * 
 * @author <a href="mailto:jford@apache.org">Jeremy Ford </a>
 * @version $Id$
 */
public interface GenericMetadata
{
    /**
     * 
     * <p>
     * addField
     * </p>
     *
     * @param locale
     * @param name
     * @param value
     */
    public void addField( Locale locale, String name, String value );
    
    /**
     * 
     * <p>
     * addField
     * </p>
     *
     * @param field
     */
    public void addField( LocalizedField field );
    
    /**
     * 
     * <p>
     * getFields
     * </p>
     *
     * @param name
     * @return
     */
    public Collection getFields( String name );
    
    /**
     * 
     * <p>
     * setFields
     * </p>
     *
     * @param name
     * @param values
     */
    public void setFields( String name, Collection values );
    
    /**
     * 
     * <p>
     * getFields
     * </p>
     *
     * @return
     */
    public Collection getFields();
    
    /**
     * 
     * <p>
     * setFields
     * </p>
     *
     * @param fields
     */
    public void setFields( Collection fields );
    
    /**
     * 
     * <p>
     * createLocalizedField
     * </p>
     *
     * @return
     */
    LocalizedField createLocalizedField();

    /**
     * 
     * <p>
     * copyFields
     * </p>
     *
     * @param fields
     */
    public void copyFields( Collection fields );
}
