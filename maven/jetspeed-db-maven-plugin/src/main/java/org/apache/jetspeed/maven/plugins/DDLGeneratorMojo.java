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
package org.apache.jetspeed.maven.plugins;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;
import org.apache.ddlutils.platform.CreationParameters;
import org.apache.ddlutils.platform.EnhancedSqlBuilder;
import org.apache.ddlutils.platform.SqlBuilder;
import org.apache.ddlutils.platform.oracle.Oracle8Platform;
import org.apache.ddlutils.task.TableSpecificParameter;
import org.apache.jetspeed.maven.plugins.db.ddlutils.io.DatabaseIO;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;

/**
 * @version $Id:DDLGeneratorMojo.java 523594 2007-03-29 07:07:32Z ate $
 * @goal ddl
 * @phase process-resources
 */
public class DDLGeneratorMojo extends AbstractMojo
{
    /** The DDLUtils databases
     * @parameter
     * @required
     */
    private String[] databases;

    /** Whether to use delimited SQL identifiers.
     * @parameter default-value="false"
     */
    private boolean useDelimitedSqlIdentifiers;
    
    /** Whether read foreign keys shall be sorted.
     * @parameter default-value="false"
     */
    private boolean sortForeignKeys;
    
    /** The set of ddl schema filenames. Schema names may
     * include wildcards.
     * @parameter
     */
    private String[] schemas;
    
    /**
     * The output directory where the ddl scripts will be generated
     * @parameter expression="${project.build.directory}/generated-sources/schema-ddl"
     */
    private String outputTarget;
    
    /**
     * @parameter
     */
    private TableSpecificParameter[] parameters;
    
    /**
     * @parameter default-value="false"
     */
    private boolean validateXml;
    
    /**
     * @parameter default-value="true"
     */
    private boolean useInternalDtd;
    
    /** The Maven project.
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;
    
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        File[] schemaFiles = getSchemaFiles();
        if ( schemaFiles.length > 0)
        {
            Database model = readModel(schemaFiles);
            Platform platforms[] = new Platform[databases.length];
            if ( databases.length == 0 )
            {
                getLog().warn("No databases specified, no ddl generated");
            }
            else
            {
                for ( int i = 0; i < databases.length; i++ )
                {            
                    try
                    {
                        platforms[i] = PlatformFactory.createNewPlatformInstance(databases[i]);
                    }
                    catch (Exception ex)
                    {
                        throw new MojoExecutionException("Database type "+databases[i]+" is not supported.", ex);
                    }
                    if (platforms[i] == null)
                    {
                        throw new MojoExecutionException("Database type "+databases[i]+" is not supported.");
                    }
                    platforms[i].setDelimitedIdentifierModeOn(useDelimitedSqlIdentifiers);
                    platforms[i].setForeignKeysSorted(sortForeignKeys);
                }
                
                for (int i = 0; i < platforms.length; i++ )
                {
                    Platform platform = platforms[i];
                    platform.setScriptModeOn(true);
                    File outputDir = new File(outputTarget,databases[i].toLowerCase());
                    if ( !outputDir.exists() )
                    {
                        outputDir.mkdirs();
                    }
                    if (platform.getPlatformInfo().isSqlCommentsSupported())
                    {
                        // we're generating SQL comments if possible
                        platform.setSqlCommentsOn(true);
                    }
                    if ( platform instanceof Oracle8Platform )
                    {
                        // hack to map LONGVARCHAR to VARCHAR2(4000) on Oracle, the predefined CLOB type really isn't usable
                        platform.getPlatformInfo().addNativeTypeMapping("LONGVARCHAR", "VARCHAR2(4000)");
                    }
                    CreationParameters params = getFilteredParameters(model, platform.getName(), useDelimitedSqlIdentifiers);
                    try
                    {
                        StringWriter stringWriter = new StringWriter();
                        platform.getSqlBuilder().setWriter(stringWriter);
                        // use cglib enhanced wrapper for SqlBuilder to allow overriding ddlutils 1.0 behavior at runtime for 
                        // https://issues.apache.org/jira/browse/DDLUTILS-75 which is fixed in ddlutils 1.1 but hasn't been released yet.
                        SqlBuilder sqlBuilder = new EnhancedSqlBuilder(platform.getSqlBuilder());
                        sqlBuilder.createTables(model, params, false);
                        int createSchemaLength = stringWriter.getBuffer().length();
                        writeOutput(new File(outputDir,"create-schema.sql"),stringWriter.toString());
                        stringWriter.getBuffer().setLength(0);
                        sqlBuilder.createTables(model, params, true);
                        stringWriter.getBuffer().setLength(stringWriter.getBuffer().length()-createSchemaLength);
                        writeOutput(new File(outputDir,"drop-schema.sql"), stringWriter.toString());
                    }
                    catch (IOException ioe)
                    {
                        throw new MojoExecutionException("Failed to generate ddl for "+databases[i], ioe);
                    }
                    getLog().info("Written "+databases[i]+" schema sql to " + outputDir.getAbsolutePath());
                }
            }
        }
    }
    
    protected void writeOutput(File outputFile, String content) throws MojoExecutionException
    {
        if (outputFile.exists() && (!outputFile.isFile() || !outputFile.canWrite())) 
        {
            throw new MojoExecutionException("Cannot write to output file "+outputFile.getAbsolutePath());
        }
        FileWriter outputWriter = null;
        try
        {
            outputWriter = new FileWriter(outputFile);
            outputWriter.append(content);
            outputWriter.close();
            outputWriter = null;
        }
        catch (IOException ioe)
        {
            throw new MojoExecutionException("Cannot open output file "+outputFile.getAbsolutePath(), ioe);
        }
        finally
        {
            if ( outputWriter != null )
            {
                try
                {
                    outputWriter.close();
                }
                catch (IOException ioe)
                {
                }
            }
        }
    }
    
    protected File[] getSchemaFiles()
    {
        if (schemas == null  ||  schemas.length == 0)
        {
            schemas = new String[]{"src/main/ddl-schema/*.xml"};
        }
        DirectoryScanner ds = new DirectoryScanner();
        final File baseDir = project.getBasedir();
        ds.setBasedir(baseDir);
        ds.setIncludes(schemas);
        ds.scan();
        String[] files = ds.getIncludedFiles();
        File[] schemaFiles = new File[files.length];
        for (int i = 0;  i < schemaFiles.length;  i++)
        {
            schemaFiles[i] = new File(baseDir, files[i]);
        }
        if (schemaFiles.length == 0) {
            StringBuffer sb = new StringBuffer();
            sb.append("Schema specification returns no result: ");
            for (int i = 0;  i < schemas.length;  i++)
            {
                if (i > 0) 
                {
                    sb.append(",");
                }
                sb.append(schemas[i]);
            }
            getLog().warn(sb);
        }
        return schemaFiles;
    }

    protected Database readModel(File[] files) throws MojoExecutionException
    {
    	DatabaseIO reader = new DatabaseIO();
        Database   model  = null;

        reader.setValidateXml(validateXml);
        reader.setUseInternalDtd(useInternalDtd);
        
        for (int idx = 0; (files != null) && (idx < files.length); idx++)
        {
            Database curModel = null;
            if (!files[idx].isFile())
            {
                throw new MojoExecutionException("Path " + files[idx].getAbsolutePath() + " does not denote a file");
            }
            else if (!files[idx].canRead())
            {
                throw new MojoExecutionException("Could not read schema file " + files[idx].getAbsolutePath());
            }
            else
            {
                try
                {
                    curModel = reader.read(files[idx]);
                    getLog().info("Read schema file " + files[idx].getAbsolutePath());
                }
                catch (Exception ex)
                {
                    throw new MojoExecutionException("Could not read schema file "+files[idx].getAbsolutePath()+": "+ex.getLocalizedMessage(), ex);
                }
            }

            if (model == null)
            {
                model = curModel;
            }
            else if (curModel != null)
            {
                try
                {
                    model.mergeWith(curModel);
                }
                catch (IllegalArgumentException ex)
                {
                    throw new MojoExecutionException("Could not merge with schema from file "+files[idx]+": "+ex.getLocalizedMessage(), ex);
                }
            }
        }
        return model;
    }
    
    /**
     * Filters the parameters for the given model and platform.
     * 
     * @param model           The database model
     * @param platformName    The name of the platform
     * @param isCaseSensitive Whether case is relevant when comparing names of tables
     * @return The filtered parameters
     */
    protected CreationParameters getFilteredParameters(Database model, String platformName, boolean isCaseSensitive)
    {
        CreationParameters creationParameters = new CreationParameters();

        if ( parameters != null ) {
            for (int i=0; i < parameters.length; i++ )
            {
                if (parameters[i].isForPlatform(platformName))
                {
                    for (int idx = 0; idx < model.getTableCount(); idx++)
                    {
                        Table table = model.getTable(idx);

                        if (parameters[i].isForTable(table, isCaseSensitive))
                        {
                            creationParameters.addParameter(table, parameters[i].getName(), parameters[i].getValue());
                        }
                    }
                }
            }
        }
        return creationParameters;
    }
}
