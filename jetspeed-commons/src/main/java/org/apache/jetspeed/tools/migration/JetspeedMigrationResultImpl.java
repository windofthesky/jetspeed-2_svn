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
package org.apache.jetspeed.tools.migration;

/**
 * Jetspeed Migration Result implementation.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class JetspeedMigrationResultImpl implements JetspeedMigrationResult
{
    private final int migratedRows;
    private final int droppedRows;
    
    /**
     * Constructor accepting migrated rows counts.
     * 
     * @param migratedRows migrated rows count
     */
    public JetspeedMigrationResultImpl(int migratedRows)
    {
        this.migratedRows = migratedRows;
        this.droppedRows = 0;
    }

    /**
     * Constructor accepting migrated and dropped rows counts.
     * 
     * @param migratedRows migrated rows count
     * @param droppedRows dropped rows count
     */
    public JetspeedMigrationResultImpl(int migratedRows, int droppedRows)
    {
        this.migratedRows = migratedRows;
        this.droppedRows = droppedRows;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.migration.JetspeedMigrationResult#getDroppedRows()
     */
    public int getDroppedRows()
    {
        return droppedRows;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.migration.JetspeedMigrationResult#getMigratedRows()
     */
    public int getMigratedRows()
    {
        return migratedRows;
    }
}
