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
package org.apache.jetspeed.serializer;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.ddlutils.DatabaseOperationException;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.PlatformUtils;
import org.apache.ddlutils.io.DataReader;
import org.apache.ddlutils.io.DataToDatabaseSink;
import org.apache.ddlutils.io.DatabaseIO;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.JdbcTypeCategoryEnum;
import org.apache.ddlutils.model.Table;

/**
 * Jetspeed DDLUtil
 * <p>
 * The Jetspeed DDL Utility is capabale of extracting existing schema
 * information as well as recreating databases.
 * 
 * @author <a href="mailto:hajo@bluesunrise.com">Hajo Birthelmer</a>
 * @version $Id: $
 */
public class JetspeedDDLUtil
{
	public static final String DATASOURCE_DATABASENAME = "DATABASENAME".intern();
	public static final String DATASOURCE_CLASS = "DATASOURCE_CLASS".intern();
	public static final String DATASOURCE_DRIVER = "driverClassName".intern();
	public static final String DATASOURCE_URL = "url".intern();
	public static final String DATASOURCE_USERNAME = "username".intern();
	public static final String DATASOURCE_PASSWORD = "password".intern();

	
    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(JetspeedDDLUtil.class);

	JdbcTypeCategoryEnum temEnum = null;
	
	Map parameters;

	PlatformUtils utils;

	StringWriter writer;

	private Platform platform;

	/** The data source to test against. */
	private DataSource dataSource;
	/** The database name. */
	private String _databaseName;
	/** The database model. */
	private Database model;

	private boolean connected = false;

	public JetspeedDDLUtil()
	{

	}

	public void startUp()
	{

	}

	public void tearDown()
	{
		if (connected)
		{
			platform = null;
			// todo: closeup
		}
	}

	/**
	 * Tries to determine whether a the jdbc driver and connection url re
	 * supported.
	 * 
	 * @param driverName
	 *            The fully qualified name of the JDBC driver
	 * @param jdbcConnectionUrl
	 *            The connection url
	 * @return True if this driver/url is supported
	 */
	public boolean isDatabaseSupported(String driverName,
			String jdbcConnectionUrl)
	{
		if (utils.determineDatabaseType(driverName, jdbcConnectionUrl) != null)
			return true;
		else
			return false;

	}

	

	/**
	 * Parses the database defined in the given XML file and creates a database
	 * schema (model) object
	 * 
	 * @param fileName 
	 */
	public void writeDatabaseSchematoFile(String fileName)
	{
		new DatabaseIO().write(model, fileName);
	}

	
	/**
	 * Parses the database defined in the given XML file and creates a database
	 * schema (model) object
	 * 
	 * @param dbDef
	 *            The database XML definition
	 * @return The database model
	 */
	protected Database createDatabaseSchemaFromXML(String fileName)
	{
		DatabaseIO io = new DatabaseIO();
		io.setValidateXml(false);
		return io.read(fileName);
	}

	/**
	 * Parses the database defined in the given XML definition String and
	 * creates a database schema (model) object
	 * 
	 * @param dbDef
	 *            The database XML definition
	 * @return The database model
	 */
	protected Database createDatabaseSchemaFromString(String dbDef)
	{
		DatabaseIO dbIO = new DatabaseIO();

		dbIO.setUseInternalDtd(true);
		dbIO.setValidateXml(false);
		return dbIO.read(new StringReader(dbDef));
	}

	/**
	 * <p>
	 * Create a database connection (platform instance) from a data source
	 * </p>
	 * 
	 * @param dataSource
	 */
	protected Platform connectToDatabase(DataSource dataSource)
	{
		return PlatformFactory.createNewPlatformInstance(dataSource);
	}

	/**
	 * <p>
	 * Create a database connection (platform instance) from a (case
	 * insensitive) database type (like MySQL)
	 * </p>
	 * 
	 * @param dataSource
	 */
	protected Platform connectToDatabase(String databaseType)
	{
		return PlatformFactory.createNewPlatformInstance(databaseType);
	}
	/**
	 * <p>
	 * Update a given database schema to match the schema of targetModel If
	 * alterDB is true, the routine attempts to modify the existing database
	 * shcema while preserving the data (as much as possible). If not, the
	 * existing tables are dropped prior to recreate
	 * 
	 * @param targetModel
	 *            The new database model
	 * @param alterDb
	 *            if true, try to use alter database and preserve data
	 */
	protected void updateDatabaseSchema(Database targetModel, boolean alterDb)
			throws SerializerException
	{
		try
		{
			platform.setSqlCommentsOn(false);
			try
			{
				targetModel.resetDynaClassCache();
			} catch (Exception internalEx)
			{
				internalEx.printStackTrace();
			}
			if (alterDb)
			{
				model.mergeWith(targetModel);
				try
				{
					platform.alterTables(model, true);
				}
				catch (Exception aEX)
				{
					System.out.println("Error in ALTER DATABASE");
					aEX.printStackTrace();
					log.error("Error in ALTER DATABASE", aEX);
				}
			} else
			{
				try
				{
				
//					if (log.isDebugEnabled())
//					{
//						String s = platform.getDropTablesSql(model, true);
//						log.debug(s);
//					}
                    if (model == null)
                    {
                        model = targetModel;
                    }
                    platform.dropTables(model, true);				
				}
				catch (Exception aEX)
				{
					log.error("Error in DROP TABLES", aEX);
				}
				try
				{
				    platform.createTables(model, false, true);
                    if (this._databaseName.startsWith("oracle"))
                    {
                        model = this.readModelFromDatabase(null);
                        modifyVarBinaryColumn(model, "PA_METADATA_FIELDS", "COLUMN_VALUE");
                        modifyVarBinaryColumn(model, "PD_METADATA_FIELDS", "COLUMN_VALUE");
                        modifyVarBinaryColumn(model, "LANGUAGE", "KEYWORDS");
                        modifyVarBinaryColumn(model, "PORTLET_CONTENT_TYPE", "MODES");
                        modifyVarBinaryColumn(model, "PARAMETER", "PARAMETER_VALUE");
                        modifyVarBinaryColumn(model, "LOCALIZED_DESCRIPTION", "DESCRIPTION");
                        modifyVarBinaryColumn(model, "LOCALIZED_DISPLAY_NAME", "DISPLAY_NAME");
                        modifyVarBinaryColumn(model, "CUSTOM_PORTLET_MODE", "DESCRIPTION");
                        modifyVarBinaryColumn(model, "CUSTOM_WINDOW_STATE", "DESCRIPTION");
                        modifyVarBinaryColumn(model, "MEDIA_TYPE", "DESCRIPTION");
                        platform.alterTables(model, true);                        
                    }
				}
				catch (Exception aEX)
				{
                    aEX.printStackTrace();
					log.error("Error in CREATE TABLES", aEX);
				}
			}
			// TODO: DST: REMOVE, AINT WORKING IN ORACLE model = this.readModelFromDatabase(null);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			throw new SerializerException(
			// TODO: HJB create exception
					SerializerException.CREATE_OBJECT_FAILED.create(ex.getLocalizedMessage()));
		}
	}

    private void modifyVarBinaryColumn(Database targetModel, String tableName, String columnName)
    {
        Table table = targetModel.findTable(tableName);
        Column c = table.findColumn(columnName);
        c.setType("VARCHAR");        
        c.setSize("2000");
        System.out.println("updating column " + c.getName() + " for table " + table.getName());
    }
	/**
	 * Alter an existing database from the given model. Data is preserved as
	 * much as possible
	 * 
	 * @param model
	 *            The new database model
	 */
	public void alterDatabase(Database model) throws SerializerException
	{
		updateDatabaseSchema(model, true);
	}

	/**
	 * Creates a new database from the given model. Note that all data is LOST
	 * 
	 * @param model
	 *            The new database model
	 */
	public void createDatabase(Database model) throws SerializerException
	{
		updateDatabaseSchema(model, false);
	}

	/**
	 * <p>
	 * Inserts data into the database. Data is expected to be in the format
	 * </p>
	 * <p>
	 * <?xml version='1.0' encoding='ISO-8859-1'?> <data> <TABLENAME
	 * FIELD1='TEXTVALUE' FIELD2=INTVALUE .... /> <TABLENAME FIELD1='TEXTVALUE'
	 * FIELD2=INTVALUE .... /> </data>
	 * </p>
	 * 
	 * @param model
	 *            The database model
	 * @param dataXml
	 *            The data xml
	 * @return The database
	 */
	protected Database insertData(Database model, String dataXml)
			throws DatabaseOperationException
	{
		try
		{
			DataReader dataReader = new DataReader();

			dataReader.setModel(model);
			dataReader.setSink(new DataToDatabaseSink(platform, model));
			dataReader.parse(new StringReader(dataXml));
			return model;
		} catch (Exception ex)
		{
			throw new DatabaseOperationException(ex);
		}
	}

	/**
	 * Drops the tables defined in the database model on this connection.
	 * 
	 * @param model
	 *            The database model
	 * 
	 */
	protected void dropDatabaseTables(Database model)
			throws DatabaseOperationException
	{
		platform.dropTables(model, true);
	}

	/**
	 * Reads the database model from a live database.
	 * 
	 * @param platform
	 *            The physical database connection
	 * @param databaseName
	 *            The name of the resulting database
	 * @return The model
	 */
	public Database readModelFromDatabase(String databaseName)
	{
		return platform.readModelFromDatabase(databaseName);
	}

	/**
	 * datasource.class=org.apache.commons.dbcp.BasicDataSource
	 * datasource.driverClassName=com.mysql.jdbc.Driver
	 * datasource.url=jdbc:mysql://localhost/ddlutils datasource.username=root
	 * datasource.password=root123
	 * 
	 */

	/**
	 * Initializes the datasource and the connection (platform)
	 */
	public void init(Map parameters)
	{
		if (connected)
			tearDown();

		try
		{
			String dataSourceClass = (String) parameters.get(DATASOURCE_CLASS);
			if (dataSourceClass == null)
				dataSourceClass = BasicDataSource.class.getName();

			dataSource = (DataSource) Class.forName(dataSourceClass)
					.newInstance();

			for (Iterator it = parameters.entrySet().iterator(); it.hasNext();)
			{
				Map.Entry entry = (Map.Entry) it.next();
				String propName = (String) entry.getKey();

				if (!(propName.equals(DATASOURCE_CLASS)))
				{
					BeanUtils.setProperty(dataSource, propName, entry
							.getValue());
				}
			}
		} catch (Exception ex)
		{
			throw new DatabaseOperationException(ex);
		}
		String databaseName = null;
		_databaseName = null;        
		try
		{
			databaseName = (String) parameters.get(DATASOURCE_DATABASENAME);
			if (databaseName != null)
			{
				platform = PlatformFactory.createNewPlatformInstance(databaseName);
				if (platform != null)
					_databaseName = databaseName;
			}
		} catch (Exception ex)
		{
			log.warn("Exception in trying to establish connection to " + databaseName + " : " + ex.getLocalizedMessage(), ex);
		}
		if (_databaseName == null)
		{
			_databaseName = new PlatformUtils().determineDatabaseType(dataSource);
			if (_databaseName == null)
			{
				throw new DatabaseOperationException(
					"Could not determine platform from datasource, please specify it in the jdbc.properties via the ddlutils.platform property");
			}
			else
			{
				try
				{
					platform = PlatformFactory.createNewPlatformInstance(_databaseName);
				} catch (Exception ex)
				{
					throw new DatabaseOperationException(
					"Could not establish connection to " + _databaseName + " : " + ex.getLocalizedMessage(),ex);
				}
			}
		}
//		com.mysql.jdbc.Driver
		
		writer = new StringWriter();
		platform.getSqlBuilder().setWriter(writer);
//		if (platform.getPlatformInfo().isDelimitedIdentifiersSupported())
//		{
//			platform.setDelimitedIdentifierModeOn(true);
//		}

	
		platform.setDataSource(dataSource);
        System.out.println("reading model...");
		model = this.readModelFromDatabase(null);
        System.out.println("done reading model...");
/**		
		JdbcModelReader reader = platform.getModelReader();		
		try
		{
		model = reader.getDatabase(platform.borrowConnection(), null);
		} catch (Exception ex)
		{
			throw new DatabaseOperationException(ex);
		}
*/
		
		connected = true;
	}

	/**
	 * Returns the database model.
	 * 
	 * @return The model
	 */
	protected Database getModel()
	{
		return model;
	}

	/**
	 * Inserts data into the database.
	 * 
	 * @param dataXml
	 *            The data xml
	 * @return The database
	 */
	protected Database insertData(String dataXml)
			throws DatabaseOperationException
	{
		try
		{
			DataReader dataReader = new DataReader();

			dataReader.setModel(model);
			dataReader.setSink(new DataToDatabaseSink(platform, model));
			dataReader.parse(new StringReader(dataXml));
			return model;
		} catch (Exception ex)
		{
			throw new DatabaseOperationException(ex);
		}
	}

	/**
	 * Drops the tables defined in the database model.
	 */
	protected void dropDatabase() throws DatabaseOperationException
	{
		platform.dropTables(model, true);
	}

	/**
	 * Determines the value of the bean's property that has the given name.
	 * Depending on the case-setting of the current builder, the case of teh
	 * name is considered or not.
	 * 
	 * @param bean
	 *            The bean
	 * @param propName
	 *            The name of the property
	 * @return The value
	 */
	protected Object getPropertyValue(DynaBean bean, String propName)
	{
		if (platform.isDelimitedIdentifierModeOn())
		{
			return bean.get(propName);
		} else
		{
			DynaProperty[] props = bean.getDynaClass().getDynaProperties();

			for (int idx = 0; idx < props.length; idx++)
			{
				if (propName.equalsIgnoreCase(props[idx].getName()))
				{
					return bean.get(props[idx].getName());
				}
			}
			throw new IllegalArgumentException(
					"The bean has no property with the name " + propName);
		}
	}

	public DataSource getDataSource()
	{
		return dataSource;
	}

	public Platform getPlatform()
	{
		return platform;
	}
	
		 
	
    public  List getRows(String tableName)
    {
        Table table = getModel().findTable(tableName, getPlatform().isDelimitedIdentifierModeOn());
        
        return getPlatform().fetch(getModel(), getSelectQueryForAllString( table), new Table[] { table });
    }

    
    public  String getSelectQueryForAllString( Table table)
    {
    
	    StringBuffer query = new StringBuffer();
	
	    query.append("SELECT * FROM ");
	    if (getPlatform().isDelimitedIdentifierModeOn())
	    {
	        query.append(getPlatform().getPlatformInfo().getDelimiterToken());
	    }
	    query.append(table.getName());
	    if (getPlatform().isDelimitedIdentifierModeOn())
	    {
	        query.append(getPlatform().getPlatformInfo().getDelimiterToken());
	    }
	    return query.toString();
    }
	
    
  
    
}
