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

package org.apache.cornerstone.framework.api.persistence.factory;

public interface IPersistentObjectFactory extends IPersistenceFactory
{
    public static final String REVISION = "$Revision$";

    public static final String PRIMARY_KEY_PROPERTY_NAME = "primaryKey.propertyName";
    public static final String PRIMARY_KEY_COLUMN_NAME = "primaryKey.columnName";

    public String getPrimaryKeyPropertyName();

    public void store(Object object) throws PersistenceException;

    public void delete(Object object) throws PersistenceException;

    public String mapColumnNameToPropertyName(String columnName);
}