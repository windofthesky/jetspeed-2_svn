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
package org.apache.ddlutils.platform;

import java.io.IOException;
import java.sql.DatabaseMetaData;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.ddlutils.Platform;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.ForeignKey;
import org.apache.ddlutils.model.Table;
import org.apache.ddlutils.platform.CreationParameters;
import org.apache.ddlutils.platform.SqlBuilder;

/**
 * SqlBuilder enhanced through cglib modifying ddlutils 1.0 behavior at runtime for 
 * https://issues.apache.org/jira/browse/DDLUTILS-75 which is fixed in ddlutils 1.1 but hasn't been released yet.
 * 
 * @version $Id$
 */
public class EnhancedSqlBuilder extends SqlBuilder
{
	private SqlBuilder internal;
	
	private class SqlBuilderEnhancer implements MethodInterceptor
	{
	    public Object intercept(Object obj, java.lang.reflect.Method method, Object[] args, MethodProxy proxy) throws Throwable 
	    {
	    	if (method.getName().equals("writeEmbeddedForeignKeysStmt"))
	    	{
	    		EnhancedSqlBuilder.this.writeEmbeddedForeignKeysStmt((Database)args[0], (Table)args[1]);
	    		return null;
	    	}
	    	else if (method.getName().equals("writeExternalForeignKeyCreateStmt"))
	    	{
	    		EnhancedSqlBuilder.this.writeExternalForeignKeyCreateStmt((Database)args[0], (Table)args[1], (ForeignKey)args[2]);
	    		return null;
	    	}
	    	else
	    	{	    		
	    		return proxy.invokeSuper(obj, args);
	    	}
		}
	}
	
	public EnhancedSqlBuilder(SqlBuilder sqlBuilder)
	{
		super(null);
		Enhancer e = new Enhancer();
		e.setSuperclass(sqlBuilder.getClass());
		e.setCallback(new SqlBuilderEnhancer());
		internal = (SqlBuilder)e.create(new Class[]{Platform.class}, new Object[]{sqlBuilder.getPlatform()});
		internal.setIndent(sqlBuilder.getIndent());
		internal.setValueLocale(sqlBuilder.getValueLocale());
		internal.setWriter(sqlBuilder.getWriter());
	}
	
	public void createTables(Database database, CreationParameters params,
			boolean dropTables) throws IOException 
	{
		internal.createTables(database, params, dropTables);
	}

	/**
     * {@inheritDoc}
     */
    protected String getOnDeleteClauseForCode(int deleteRuleCode)
    {
    	String platform = internal.getPlatform().getName();
    	
    	if (	platform.equals("Firebird") || 
    			platform.equals("SapDB") ||
    			platform.equals("PostgreSql") ||
    			platform.equals("Derby") ||
    			platform.startsWith("MySQL") ||
    			platform.startsWith("DB2"))
    	{
            switch (deleteRuleCode)
            {
                case DatabaseMetaData.importedKeyCascade:
                    return "ON DELETE CASCADE";
                case DatabaseMetaData.importedKeyRestrict:
                    return "ON DELETE RESTRICT";
                case DatabaseMetaData.importedKeySetNull:
                    return "ON DELETE SET NULL";
                default:
                    return ""; // No action case
            }
    	}
    	else if (platform.startsWith("HsqlDb") || platform.startsWith("Oracle"))
    	{
            switch (deleteRuleCode)
            {
                case DatabaseMetaData.importedKeyCascade:
                    return "ON DELETE CASCADE";
                case DatabaseMetaData.importedKeyRestrict:
                    return ""; // not supported
                case DatabaseMetaData.importedKeySetNull:
                    return "ON DELETE SET NULL";
                default:
                    return ""; // No action case
            }
    	}
    	else if (platform.equals("MsSql"))
    	{
            switch (deleteRuleCode)
            {
                case DatabaseMetaData.importedKeyCascade:
                    return "ON DELETE CASCADE";
                case DatabaseMetaData.importedKeyRestrict:
                    return ""; // not supported
                case DatabaseMetaData.importedKeySetNull:
                    return "ON DELETE SET NULL";
                default:
                    return "ON DELETE NO ACTION"; // No action case
            }
    	}
    	return "";
    }
    
    /**
     * Writes the foreign key constraints inside a create table () clause.
     * 
     * @param database The database model
     * @param table    The table
     */
    protected void writeEmbeddedForeignKeysStmt(Database database, Table table) throws IOException
    {
        for (int idx = 0; idx < table.getForeignKeyCount(); idx++)
        {
            ForeignKey key = table.getForeignKey(idx);

            if (key.getForeignTableName() == null)
            {
                internal._log.warn("Foreign key table is null for key " + key);
            }
            else
            {
            	internal.printStartOfEmbeddedStatement();
                if (internal.getPlatformInfo().isEmbeddedForeignKeysNamed())
                {
                	internal.print("CONSTRAINT ");
                	internal.printIdentifier(internal.getForeignKeyName(table, key));
                	internal.print(" ");
                }
                internal.print("FOREIGN KEY (");
                internal.writeLocalReferences(key);
                internal.print(") REFERENCES ");
                internal.printIdentifier(internal.getTableName(database.findTable(key.getForeignTableName())));
                internal.print(" (");
                internal.writeForeignReferences(key);
                internal.print(")");
                
                String onDeleteClause = getOnDeleteClauseForCode(((org.apache.jetspeed.maven.plugins.db.ddlutils.model.ForeignKey)key).getDeleteRuleCode());
                if (onDeleteClause.trim().length() > 0)
                {
                	internal.print(' ' + onDeleteClause);
                }
            }
        }
    }

    /**
     * Writes a single foreign key constraint using a alter table statement.
     * 
     * @param database The database model
     * @param table    The table 
     * @param key      The foreign key
     */
    protected void writeExternalForeignKeyCreateStmt(Database database, Table table, ForeignKey key) throws IOException
    {
        if (key.getForeignTableName() == null)
        {
        	internal._log.warn("Foreign key table is null for key " + key);
        }
        else
        {
        	internal.writeTableAlterStmt(table);

        	String platform = internal.getPlatform().getName();
        	
        	if (platform.equals("SapDB") || platform.equals("MaxDB"))
        	{
        		internal.print(" ADD FOREIGN KEY ");
        		internal.printIdentifier(internal.getForeignKeyName(table, key));
        		internal.print(" (");
        	}
        	else
        	{
            	internal.print("ADD CONSTRAINT ");
            	internal.printIdentifier(internal.getForeignKeyName(table, key));
            	internal.print(" FOREIGN KEY (");
        	}
        	
        	internal.writeLocalReferences(key);
        	internal.print(") REFERENCES ");
        	internal.printIdentifier(internal.getTableName(database.findTable(key.getForeignTableName())));
        	internal.print(" (");
        	internal.writeForeignReferences(key);
        	internal.print(")");
            String onDeleteClause = getOnDeleteClauseForCode(((org.apache.jetspeed.maven.plugins.db.ddlutils.model.ForeignKey)key).getDeleteRuleCode());
            if (onDeleteClause.trim().length() > 0)
            {
            	internal.print(' ' + onDeleteClause);
            }
            internal.printEndOfStatement();
        }
    }
}
