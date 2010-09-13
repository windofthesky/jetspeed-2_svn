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

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.jetspeed.components.datasource.DBCPDatasourceComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jetspeed Migration application.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class JetspeedMigrationApplication
{
    private static final Logger log = LoggerFactory.getLogger(JetspeedMigrationApplication.class);
    
    private static final int ALL_MIGRATION_PHASE = 0;
    private static final int CREATE_SCHEMA_MIGRATION_PHASE = 1;
    private static final int DATA_MIGRATION_PHASE = 2;
    private static final int CONSTRAINTS_SCHEMA_MIGRATION_PHASE = 3;

    private static final int DEFAULT_ROWS_MIGRATED_PER_COMMIT = 500;
    private static final int DEFAULT_MIGRATION_PHASE = ALL_MIGRATION_PHASE;
    
    private String sourceDBUsername;
    private String sourceDBPassword;
    private String sourceJDBCUrl;
    private String sourceJDBCDriverClass;
    private String dbUsername;
    private String dbPassword;
    private String jdbcUrl;
    private String jdbcDriverClass;
    private int rowsMigratedPerCommit;
    private File dropSchemaSQLScriptFile;
    private File createSchemaSQLScriptFile;
    private int migrationPhase;
    private DBCPDatasourceComponent sourceDataSourceFactory;
    private DBCPDatasourceComponent targetDataSourceFactory;
    private DBCPDatasourceComponent targetTxnDataSourceFactory;
    private JetspeedMigration [] migrations;
    private int rowsCheckpointCommitted;
    private int rowsMigrated;
    
    /**
     * Construct application instance using arguments.
     * 
     * @param args application arguments
     */
    public JetspeedMigrationApplication(String[] args)
    {
        this.rowsMigratedPerCommit = DEFAULT_ROWS_MIGRATED_PER_COMMIT;
        this.migrationPhase = DEFAULT_MIGRATION_PHASE;
        
        for (String arg : args)
        {
            if (arg.startsWith("-migration-phase="))
            {
                this.migrationPhase = Integer.parseInt(arg.substring(17));
            }
            else if (arg.startsWith("-source-db-username="))
            {
                this.sourceDBUsername = arg.substring(20);
            }
            else if (arg.startsWith("-source-db-password="))
            {
                this.sourceDBPassword = arg.substring(20);
            }
            else if (arg.startsWith("-source-jdbc-url="))
            {
                this.sourceJDBCUrl = arg.substring(17);
            }
            else if (arg.startsWith("-source-jdbc-driver-class="))
            {
                this.sourceJDBCDriverClass = arg.substring(26);
            }
            else if (arg.startsWith("-db-username="))
            {
                this.dbUsername = arg.substring(13);
            }
            else if (arg.startsWith("-db-password="))
            {
                this.dbPassword = arg.substring(13);
            }
            else if (arg.startsWith("-jdbc-url="))
            {
                this.jdbcUrl = arg.substring(10);
            }
            else if (arg.startsWith("-jdbc-driver-class="))
            {
                this.jdbcDriverClass = arg.substring(19);
            }
            else if (arg.startsWith("-rows-migrated-per-commit="))
            {
                this.rowsMigratedPerCommit = Integer.parseInt(arg.substring(26));
            }
            else if (arg.startsWith("-drop-schema-sql="))
            {
                this.dropSchemaSQLScriptFile = new File(arg.substring(17));
                if (!this.dropSchemaSQLScriptFile.isFile())
                {
                    throw new RuntimeException("Cannot access -drop-schema-sql file: "+this.dropSchemaSQLScriptFile);
                }
            }
            else if (arg.startsWith("-create-schema-sql="))
            {
                this.createSchemaSQLScriptFile = new File(arg.substring(19));
                if (!this.createSchemaSQLScriptFile.isFile())
                {
                    throw new RuntimeException("Cannot access -create-schema-sql file: "+this.createSchemaSQLScriptFile);
                }
            }
        }
        
        if ((this.migrationPhase != ALL_MIGRATION_PHASE) && (this.migrationPhase != CREATE_SCHEMA_MIGRATION_PHASE) &&
            (this.migrationPhase != DATA_MIGRATION_PHASE) && (this.migrationPhase != CONSTRAINTS_SCHEMA_MIGRATION_PHASE))
        {
            throw new RuntimeException("Invalid -migration-phase argument");
        }
        if ((this.sourceDBUsername == null) && ((this.migrationPhase == ALL_MIGRATION_PHASE) ||
                                                (this.migrationPhase == DATA_MIGRATION_PHASE)))
        {
            throw new RuntimeException("Missing -source-db-username argument");
        }
        if ((this.sourceDBPassword == null) && ((this.migrationPhase == ALL_MIGRATION_PHASE) ||
                                                (this.migrationPhase == DATA_MIGRATION_PHASE)))
        {
            throw new RuntimeException("Missing -source-db-password argument");
        }
        if ((this.sourceJDBCUrl == null) && ((this.migrationPhase == ALL_MIGRATION_PHASE) ||
                                             (this.migrationPhase == DATA_MIGRATION_PHASE)))
        {
            throw new RuntimeException("Missing -source-jdbc-url argument");
        }
        if ((this.sourceJDBCDriverClass == null) && ((this.migrationPhase == ALL_MIGRATION_PHASE) ||
                                                     (this.migrationPhase == DATA_MIGRATION_PHASE)))
        {
            throw new RuntimeException("Missing -source-jdbc-driver-class argument");
        }
        if (this.dbUsername == null)
        {
            throw new RuntimeException("Missing -db-username argument");
        }
        if (this.dbPassword == null)
        {
            throw new RuntimeException("Missing -db-password argument");
        }
        if (this.jdbcUrl == null)
        {
            throw new RuntimeException("Missing -jdbc-url argument");
        }
        if (this.jdbcDriverClass == null)
        {
            throw new RuntimeException("Missing -jdbc-driver-class argument");
        }
        if ((this.dropSchemaSQLScriptFile == null) && ((this.migrationPhase == ALL_MIGRATION_PHASE) ||
                                                       (this.migrationPhase == CREATE_SCHEMA_MIGRATION_PHASE)))
        {
            throw new RuntimeException("Missing -drop-schema-sql argument required for phase");
        }
        if ((this.createSchemaSQLScriptFile == null) && ((this.migrationPhase == ALL_MIGRATION_PHASE) ||
                                                         (this.migrationPhase == CREATE_SCHEMA_MIGRATION_PHASE) ||
                                                         (this.migrationPhase == CONSTRAINTS_SCHEMA_MIGRATION_PHASE)))
        {
            throw new RuntimeException("Missing -create-schema-sql argument required for phase");
        }

        this.jdbcUrl = validateJDBCUrlOptions(this.jdbcUrl, this.jdbcDriverClass);
        this.sourceJDBCUrl = validateJDBCUrlOptions(this.sourceJDBCUrl, this.sourceJDBCDriverClass);
        if ((this.migrationPhase == ALL_MIGRATION_PHASE) || (this.migrationPhase == DATA_MIGRATION_PHASE))
        {
            if (this.jdbcUrl.equals(this.sourceJDBCUrl))
            {
                throw new RuntimeException("Source and target JDBC databases must be different: "+this.jdbcUrl);
            }
        
            this.sourceDataSourceFactory = new DBCPDatasourceComponent(this.sourceDBUsername, this.sourceDBPassword, this.sourceJDBCDriverClass, this.sourceJDBCUrl, 2, 0, GenericObjectPool.WHEN_EXHAUSTED_GROW, true);
            this.targetTxnDataSourceFactory = new DBCPDatasourceComponent(this.dbUsername, this.dbPassword, this.jdbcDriverClass, this.jdbcUrl, 2, 0, GenericObjectPool.WHEN_EXHAUSTED_GROW, false);
        }
        if ((this.migrationPhase == ALL_MIGRATION_PHASE) || (this.migrationPhase == CREATE_SCHEMA_MIGRATION_PHASE) || (this.migrationPhase == CONSTRAINTS_SCHEMA_MIGRATION_PHASE))
        {
            this.targetDataSourceFactory = new DBCPDatasourceComponent(this.dbUsername, this.dbPassword, this.jdbcDriverClass, this.jdbcUrl, 2, 0, GenericObjectPool.WHEN_EXHAUSTED_GROW, true);
        }
        
        this.migrations = new JetspeedMigration[]{new JetspeedCapabilitiesMigration(),
                                                  new JetspeedStatisticsMigration(),
                                                  new JetspeedDBPageManagerMigration(),
                                                  new JetspeedProfilerMigration(),
                                                  new JetspeedRegistryMigration(),
                                                  new JetspeedSecurityMigration(),
                                                  new JetspeedSSOSecurityMigration()};
    }
    
    /**
     * Validate required JDBC URL options.
     * 
     * @param jdbcUrl JDBC URL
     * @param jdbcDriverClass JDBC driver class name
     * @return validated JDBC URL
     */
    private String validateJDBCUrlOptions(String jdbcUrl, String jdbcDriverClass)
    {
        if ((jdbcUrl != null) && (jdbcDriverClass != null))
        {
            // add cursor fetch option for mysql, (assumes server and connector 5.0.3+)
            if (jdbcUrl.startsWith("jdbc:mysql://") && jdbcDriverClass.startsWith("com.mysql.jdbc."))
            {
                if (!jdbcUrl.contains("useCursorFetch="))
                {
                    jdbcUrl += (jdbcUrl.contains("?") ? "&" : "?")+"useCursorFetch=true";
                }
            }
        }
        
        // return validated URL
        return jdbcUrl;
    }
    
    /**
     * Perform application migration operation.
     * 
     * @throws IOException when migration error is encountered
     * @throws SQLException when SQL migration error is encountered
     */
    public void run() throws IOException, SQLException
    {
        rowsCheckpointCommitted = 0;
        rowsMigrated = 0;
        
        // setup data source pools and connections
        Connection targetConnection = null;
        Connection targetTxnConnection = null;
        Connection sourceConnection = null;
        switch (migrationPhase)
        {
            case ALL_MIGRATION_PHASE:
            case CREATE_SCHEMA_MIGRATION_PHASE:
            case CONSTRAINTS_SCHEMA_MIGRATION_PHASE:
            {
                targetDataSourceFactory.start();
                DataSource targetDataSource = targetDataSourceFactory.getDatasource();
                targetConnection = targetDataSource.getConnection();
            }
            break;
        }
        switch (migrationPhase)
        {
            case ALL_MIGRATION_PHASE:
            case DATA_MIGRATION_PHASE:
            {
                targetTxnDataSourceFactory.start();
                DataSource targetTxnDataSource = targetTxnDataSourceFactory.getDatasource();
                targetTxnConnection = targetTxnDataSource.getConnection();
                sourceDataSourceFactory.start();
                DataSource sourceDataSource = sourceDataSourceFactory.getDatasource();
                sourceConnection = sourceDataSource.getConnection();
            }
            break;
        }

        // clean target database
        switch (migrationPhase)
        {
            case ALL_MIGRATION_PHASE:
            case CREATE_SCHEMA_MIGRATION_PHASE:
            {
                log.info("Clean target database...");
                executeSQLScript(targetConnection, dropSchemaSQLScriptFile, true);
                // create tables and indices in target database
                log.info("Initialize target database schema tables and indices...");
                executeSQLScript(targetConnection, createSchemaSQLScriptFile, false, "^\\s*create\\s+(?:table|index|unique\\s+index)\\s", true);
            }
            break;
        }

        // migrate data from source to target database
        switch (migrationPhase)
        {
            case ALL_MIGRATION_PHASE:
            case DATA_MIGRATION_PHASE:
            {
                // determine and validate schema version
                int sourceVersion = JetspeedMigration.JETSPEED_SCHEMA_VERSION_UNKNOWN;
                for (JetspeedMigration migration : migrations)
                {
                    sourceVersion = migration.detectSourceVersion(sourceConnection, sourceVersion);
                }
                for (JetspeedMigration migration : migrations)
                {
                    sourceVersion = migration.detectSourceVersion(sourceConnection, sourceVersion);
                }
                log.info("Detected source schema version: "+sourceVersion);

                // migrate data from source to target database
                JetspeedMigrationListener migrationListener = new JetspeedMigrationListener()
                {
                    /* (non-Javadoc)
                     * @see org.apache.jetspeed.tools.migration.JetspeedMigrationListener#rowMigrated(java.sql.Connection)
                     */
                    public void rowMigrated(Connection targetTxnConnection) throws SQLException
                    {
                        rowsMigrated++;
                        // periodically checkpoint commit target connection based
                        // on rowsMigratedPerCommit configuration
                        if (rowsMigrated > rowsCheckpointCommitted)
                        {
                            if ((rowsMigratedPerCommit > 0) && ((rowsMigrated-rowsCheckpointCommitted) >= rowsMigratedPerCommit))
                            {
                                targetTxnConnection.commit();
                                rowsCheckpointCommitted = rowsMigrated;
                                log.info("Checkpoint commit of "+rowsCheckpointCommitted+" total data rows.");
                            }
                        }
                    }

                    /* (non-Javadoc)
                     * @see org.apache.jetspeed.tools.migration.JetspeedMigrationListener#rowDropped(java.sql.Connection)
                     */
                    public void rowDropped(Connection targetTxnConnection)
                    {
                    }
                };
                for (JetspeedMigration migration : migrations)
                {
                    // invoke migrate for each migration
                    log.info("Migrating "+migration.getName()+" data...");
                    JetspeedMigrationResult result = migration.migrate(sourceConnection, sourceVersion, targetTxnConnection, migrationListener);
                    if (result.getDroppedRows() == 0)
                    {
                        log.info("Migrated "+result.getMigratedRows()+" "+migration.getName()+" data rows.");
                    }
                    else
                    {
                        log.info("Migrated "+result.getMigratedRows()+" "+migration.getName()+" data rows, ("+result.getDroppedRows()+" dropped).");
                    }
                    // checkpoint commit target connection
                    if (rowsMigrated > rowsCheckpointCommitted)
                    {
                        targetTxnConnection.commit();
                        rowsCheckpointCommitted = rowsMigrated;
                        log.info("Checkpoint commit of "+rowsCheckpointCommitted+" total data rows.");
                    }
                }
            }
            break;
        }            

        // add constraints and remaining schema to target database
        switch (migrationPhase)
        {
            case ALL_MIGRATION_PHASE:
            case CONSTRAINTS_SCHEMA_MIGRATION_PHASE:
            {
                log.info("Setup target database schema constraints...");
                executeSQLScript(targetConnection, createSchemaSQLScriptFile, false, "^\\s*create\\s+(?:table|index|unique\\s+index)\\s", false);
            }
            break;
        }

        // close connections and stop data source pools
        switch (migrationPhase)
        {
            case ALL_MIGRATION_PHASE:
            case DATA_MIGRATION_PHASE:
            {
                sourceConnection.close();
                sourceDataSourceFactory.stop();
                targetTxnConnection.commit();
                targetTxnConnection.close();
                targetTxnDataSourceFactory.stop();
            }
            break;
        }
        switch (migrationPhase)
        {
            case ALL_MIGRATION_PHASE:
            case CREATE_SCHEMA_MIGRATION_PHASE:
            case CONSTRAINTS_SCHEMA_MIGRATION_PHASE:
            {
                targetConnection.close();
                targetDataSourceFactory.stop();
            }
            break;
        }
    }
    
    /**
     * Execute SQL script statements.
     * 
     * @param connection database connection
     * @param sqlScriptFile SQL script file
     * @param ignoreSQLErrors ignore SQL errors
     * @throws IOException when script read error is encountered
     * @throws SQLException when SQL statement error is encountered
     */
    private void executeSQLScript(Connection connection, File sqlScriptFile, boolean ignoreSQLErrors) throws IOException, SQLException
    {
        executeSQLScript(connection, sqlScriptFile, ignoreSQLErrors, null, true);
    }

    /**
     * Execute SQL script statements that match specified regexp.
     * 
     * @param connection database connection
     * @param sqlScriptFile SQL script file
     * @param ignoreSQLErrors ignore SQL errors
     * @param statementsRegexp statements matching regexp or null
     * @param include include matching statements
     * @throws IOException when script read error is encountered
     * @throws SQLException when SQL statement error is encountered
     */
    private void executeSQLScript(Connection connection, File sqlScriptFile, boolean ignoreSQLErrors, String statementsRegexp, boolean include) throws IOException, SQLException
    {
        Pattern statementsPattern = ((statementsRegexp != null) ? Pattern.compile(statementsRegexp, Pattern.CASE_INSENSITIVE) : null);
        
        SQLScriptReader reader = new SQLScriptReader(sqlScriptFile);
        for (;;)
        {
            String scriptStatement = reader.readSQLStatement();
            if (scriptStatement != null)
            {
                if ((statementsPattern == null) || (statementsPattern.matcher(scriptStatement).find() == include))
                {
                    Statement statement = connection.createStatement();
                    if (ignoreSQLErrors)
                    {
                        try
                        {
                            statement.execute(scriptStatement);                            
                        }
                        catch (SQLException sqle)
                        {
                        }
                    }
                    else
                    {
                        statement.execute(scriptStatement);
                    }
                }
            }
            else
            {
                break;
            }
        }
    }
    
    /**
     * Application main entry point.
     * 
     * @param args application arguments
     */
    public static void main(String[] args)
    {
        try
        {
            JetspeedMigrationApplication application = new JetspeedMigrationApplication(args);
            application.run();
            System.exit(0);
        }
        catch (Exception e)
        {
            log.error("Unexpected exception: "+e, e);
            System.exit(-1);
        }
    }
}
