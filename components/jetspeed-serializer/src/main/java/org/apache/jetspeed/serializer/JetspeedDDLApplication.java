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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import javolution.xml.XMLBinding;
import javolution.xml.XMLObjectReader;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;
import org.apache.jetspeed.serializer.objects.JSGroup;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
/**
 * Jetspeed Serializer DDL- Application
 * 
 * invoke with mandatory either
 * <p>
 * -I directory or filename of schema files for input, if directory all xml
 * files will be processed
 * </p>
 * and/or
 * <p>
 * -O schema file for output log of current database
 * </p>
 * and (if -I denotes a directory:)
 * <p>
 * -x if directory list provided for input schemas this pattern is excluded
 * </p>
 * <p>
 * -s name of the order file (if schema directory)
 * </p>
 * 
 * <p>
 * -m if directory list provided this is the merge file to use. If not set here
 * or in the properties file, a hardcoded version is used
 * </p>
 * 
 * <p>
 * note that - if -I and -O are specified, the output file will contain the
 * UPDATED database
 * </p>
 * invoke with (optional) parameters
 * <p>
 * -P propertyFileName, for settings
 * <p>
 * -R (flag) replace : overwrites default "UPDATE" and clears out the database
 * before processing (ignored with -O option above)
 * </p>
 * 
 * <p>
 * -dn databaseName, for example MYSQL or ORACLE10
 * </p>
 * <p>
 * -dc driverClass, for example com.mysql.jdbc.Driver
 * </p>
 * <p>
 * -ds url, ruls according to the driver used, URL needs to point to the correct
 * database
 * </p>
 * <p>
 * -du user, user with create/drop etc. rights on the database
 * </p>
 * <p>
 * -dp password
 * </p>
 * <p>
 * -l log4j-level, ERROR (default), WARN, INFO
 * </p>
 * 
 * @author <a href="mailto:hajo@bluesunrise.com">Hajo Birthelmer</a>
 * @version $Id: $
 */
public class JetspeedDDLApplication
{
	public static final String JNDI_DS_NAME = "jetspeed";

	String propertyFileName = null;
	String exludeFileName = null;
	String orderFileName = null;

	String logLevel = null;

	PropertiesConfiguration configuration = null;

	boolean doImport = false;
	boolean doExport = false;
	String schemaDirectory = null; // if specified all xml files in that
	// directory will be processed
	String outputFile = null; // if specified the database schema will be
	// exported to that file
	boolean overwrite = false; // default, do not overwrite the database
	// (ignored if only output)
	String driverClass = null; // jdbc driver
	String url = null; // jdbc url to database
	String user = null; // user
	String password = null; // password
	String databaseName = null;
	
	String[] filesToProcess = null;

	String mergeFile = null; //name of XSLT merge file
	String[] args = null;

	public static void main(String[] args) throws Exception
	{
		JetspeedDDLApplication app = new JetspeedDDLApplication();
		app.processArguments(args);
	}

	public JetspeedDDLApplication()
	{
	}

	/**
	 * ensure that we have valid database settings
	 * 
	 */
	private void checkDBSettings()
	{
		if (databaseName == null)
			databaseName = System.getProperty(
					"org.apache.jetspeed.database.databaseName",
					"mysql");
		if (driverClass == null)
			driverClass = System.getProperty(
					"org.apache.jetspeed.database.driverClass",
					"com.mysql.jdbc.Driver");
		if (url == null)
			url = System.getProperty("org.apache.jetspeed.database.url",
					"jdbc:mysql://localhost/j2test");
		if (user == null)
			user = System.getProperty("org.apache.jetspeed.database.user",
					"user");
		if (password == null)
			password = System.getProperty(
					"org.apache.jetspeed.database.password", "password");

		if (driverClass == null)
			throw new IllegalArgumentException(
					"Can't proceed without a valid driver");
		if (url == null)
			throw new IllegalArgumentException(
					"Can't proceed without a valid url to the target database");
		if (user == null)
			throw new IllegalArgumentException(
					"Can't proceed without a valid database user");
		return;
	}

	/**
	 * parse arguments for process instructions, order and exclude files as well
	 * as optional database arguments
	 * 
	 */
	private void parseArguments()
	{
		// Parse all the command-Oine arguments
		for (int n = 0; n < args.length; n++)
		{
			if (args[n].equals("-I"))
			{
				doImport = true;
				schemaDirectory = args[++n];
			} else if (args[n].equals("-O"))
			{
				doExport = true;
				outputFile = args[++n];
			} else if (args[n].equals("-s"))
			{
				orderFileName = args[++n];
			}
			else if (args[n].equals("-x"))
			{
				exludeFileName = args[++n];
			} 
			else if (args[n].equals("-m"))
			{
				mergeFile = args[++n];
			} 
			else if (args[n].equals("-R"))
				overwrite = true;
			else if (args[n].equals("-dn"))
            {
				databaseName = args[++n];
            }
			else if (args[n].equals("-dc"))
				driverClass = args[++n];
			else if (args[n].equals("-ds"))
				url = args[++n];
			else if (args[n].equals("-du"))
            {
                if (((n + 1) >= args.length) || args[n + 1].startsWith("-d"))
                {
                    user = "";
                } else
                {
                    user = args[++n];
                }
            } 
			else if (args[n].equals("-dp"))
            {
                if (((n + 1) >= args.length) || args[n + 1].startsWith("-d"))
                {
                    password = "";
                } else
                {
                    password = args[++n];
                }
            } 
			else if (args[n].equals("-P"))
				propertyFileName = args[++n];
			else if (args[n].equals("-l"))
				logLevel = args[++n];

			else
				throw new IllegalArgumentException("Unknown argument: "
						+ args[n]);
		}

	}

	/**
	 * process provided filename or directory name
	 * 
	 * @return one or more files to be processed
	 */
	private String[] parseFiles()
	{
		String[] fileList = null;
		try
		{
			File dir = new File(schemaDirectory);
			if (!(dir.exists()))
				return fileList;
			if (!(dir.isDirectory()))
			{
				fileList = new String[1];
				fileList[0] = schemaDirectory;
				return fileList;
			}
			// Handling a directory
			LocalFilenameFilter filter = new LocalFilenameFilter(
					exludeFileName, orderFileName);
			File[] files = dir.listFiles(filter);
			if (files == null)
				return fileList;

			fileList = new String[files.length];
			String sortorderFile = filter.getSortFile();
			if (sortorderFile == null)
			{
				for (int i = 0; i < files.length; i++)
					fileList[i] = files[i].getAbsolutePath();
				return fileList;
			}
			try
			{
				ArrayList list = readOrderFile(sortorderFile);
				fileList = new String[files.length];
				if ((list == null) || (list.size() == 0))
				{
					for (int i = 0; i < files.length; i++)
						fileList[i] = files[i].getAbsolutePath();
					return fileList;
				}
				String[] tempList = new String[files.length];
				for (int i = 0; i < files.length; i++)
					tempList[i] = files[i].getName();
				Iterator _it = list.iterator();
				int j = 0;
				while (_it.hasNext())
				{
					String filename = null;
					try
					{
						filename = ((JSGroup) _it.next()).getName();
					} catch (Exception eeee)
					{
					}
					if (filename != null)
					{
						for (int i = 0; i < files.length; i++)
						{
							if (filename.equalsIgnoreCase(tempList[i]))
							{
								fileList[j++] = files[i].getAbsolutePath();
								tempList[i] = null;
							}
						}
					}
				}
				for (int i = 0; i < files.length; i++)
				{
					if (tempList[i] != null)
						fileList[j++] = files[i].getAbsolutePath();
				}
				return fileList;
			} catch (Exception eee)
			{
				eee.printStackTrace();
				return null;
			}

		} catch (Exception e)
		{
			e.printStackTrace();
			throw new IllegalArgumentException(
					"Processing the schema-directory " + schemaDirectory
							+ " caused exception " + e.getLocalizedMessage());
		}

	}

	/**
	 * setup environment by processing all arguments and call requested process
	 * routine
	 * 
	 * @param arguments
	 * @throws Exception
	 */
	private void processArguments(String[] arguments) throws Exception
	{
		this.args = arguments;
		if (args == null)
			throw new IllegalArgumentException(
					"Either a schema directory, a schema file or an output filename have to be defined (-D followed by a driectory, -I or -O  followed by the filename");

		parseArguments();

		processPropertyFile();

		checkDBSettings();

		/**
		 * The only required argument is the filename for either export or
		 * import
		 */
		if ((!doImport) && (!doExport))
			throw new IllegalArgumentException(
					"Either a schema directory, a schema file or an output filename have to be defined (-I or -O  followed by the directory-/filename");

		if (doImport)
		{
			filesToProcess = parseFiles();
			if (filesToProcess == null)
				return;
		}

		/** create the instruction map */
		JetspeedDDLUtil ddlUtil = null;

		HashMap context = new HashMap();
		context.put(JetspeedDDLUtil.DATASOURCE_DATABASENAME, databaseName);
		context.put(JetspeedDDLUtil.DATASOURCE_DRIVER, driverClass);
		context.put(JetspeedDDLUtil.DATASOURCE_URL, url);
		context.put(JetspeedDDLUtil.DATASOURCE_USERNAME, user);
		context.put(JetspeedDDLUtil.DATASOURCE_PASSWORD, password);

		Logger logger = Logger.getLogger("org.apache.ddlutils");
		Level level = logger.getLevel();
		if (logLevel == null)
			logger.setLevel(Level.ERROR);
		else if (logLevel.equalsIgnoreCase("INFO"))
			logger.setLevel(Level.INFO);
		else if (logLevel.equalsIgnoreCase("WARN"))
			logger.setLevel(Level.WARN);
		else
			logger.setLevel(Level.ERROR);

		try
		{
			ddlUtil = new JetspeedDDLUtil();
			ddlUtil.startUp();
			ddlUtil.init(context);
		} catch (Exception e)
		{
			System.err.println("Failed to initialize Utility!!!!!");
			e.printStackTrace();
			System.exit(-1);
		}
		try
		{
			if (doImport)
				processImport(ddlUtil);
			if (doExport)
				processExport(ddlUtil);
		} catch (Exception e)
		{
			System.err.println("Failed to process XML "
					+ (doExport ? "export" : "import") + ":" + e);
			e.printStackTrace();
		} finally
		{
			try
			{
				logger.setLevel(level);
				if (ddlUtil != null)
					ddlUtil.tearDown();
			} catch (Exception e1)
			{
				System.out
						.println("starter framework teardown caused exception "
								+ e1.getLocalizedMessage());
				e1.printStackTrace();

			}
		}

	}

	/**
	 * create/alter database
	 * 
	 * @param ddlUtil
	 */
	private void processImport(JetspeedDDLUtil ddlUtil)
	{
		String file = null;
		if ((filesToProcess == null) || (filesToProcess.length == 0))
			return;
		if (filesToProcess.length > 1)
			file = mergeFiles(filesToProcess);

		System.out.println("Importing " + file);
		Database db = ddlUtil.createDatabaseSchemaFromXML(file);
		try
		{
			if (overwrite)
				ddlUtil.createDatabase(db); // overwrite existing
			// database
			else
				ddlUtil.alterDatabase(db);
			System.out.println("Importing " + file + " completed");
		} catch (Exception ePr)
		{
			ePr.printStackTrace();
			// continue with the process despite that one of the files was
			// bad...
		}

	}

	/**
	 * Helper routine to create a temporary file
	 * 
	 * @param suffix
	 * @return
	 */
	private File createTemp(String suffix)
	{
		try
		{
			// Create temp file.
			File temp = File.createTempFile("tmp", suffix);

			// Delete temp file when program exits.
			temp.deleteOnExit();
			return temp;
		} catch (IOException e)
		{
			System.out.println("Failed to create temproary file with "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			return null;
		}

	}
	/**
	 * Open the merge file from disk
	 * 
	 * @param fileName
	 * @return
	 */
	private File createXSLTFromFile(String fileName)
	{
		if (fileName == null)
			return null;
		try
		{
			File f = new File(fileName);
			if (f.exists())
				return f;
			return null;
		}
		catch (Exception e)
		{
			System.out.println("Failed to open merge template " + e.getLocalizedMessage());
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * If everything else fails, use a hardcoded XSLT here
	 * 
	 * @return
	 */
	private File createXSLTFromMemory()
	{
		StringBuffer buffer = new StringBuffer();

		buffer.append("<?xml version=\"1.0\"?>");
		buffer
				.append("<xslt:transform version=\"1.0\" xmlns:xslt=\"http://www.w3.org/1999/XSL/Transform\">");
		buffer
				.append("<!-- Simple template to merge two database schemas into one  -->");
		buffer.append("<xslt:param name=\"fileTwo\" />");
		buffer.append("<xslt:template match=\"/\">");

		buffer.append("<xslt:message>");
		buffer
				.append("<xslt:text /> Merging input with '<xslt:value-of select=\"$fileTwo\"/>");
		buffer.append("<xslt:text>'</xslt:text>");
		buffer.append("</xslt:message>");
		buffer.append("<xslt:if test=\"string($fileTwo)=''\">");
		buffer.append("<xslt:message terminate=\"yes\">");
		buffer
				.append("<xslt:text>No input file specified (parameter 'fileTwo')</xslt:text>");
		buffer.append("</xslt:message>");
		buffer.append("</xslt:if>");
		buffer.append("<database name=\"generic\">");
		buffer.append("<xslt:apply-templates />");
		buffer.append("</database>");
		buffer.append("</xslt:template>");
		buffer.append("<xslt:template match=\"database\">");
		buffer.append("<xslt:apply-templates />");
		buffer.append("<xslt:apply-templates select=\"document($fileTwo)/database/table\"/>");
		buffer.append("</xslt:template>");

		buffer.append("<xslt:template match=\"@*|node()\">");
		buffer.append("<xslt:copy>");
		buffer.append("<xslt:apply-templates select=\"@*|node()\"/>");
		buffer.append("</xslt:copy>");
		buffer.append("</xslt:template>");
		buffer.append("</xslt:transform>");

		File xslt = createTemp(".xslt");
		try
		{
			// Write to temp file

			BufferedWriter out = new BufferedWriter(new FileWriter(xslt));
			out.write(buffer.toString());
			out.close();
			return xslt;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * process of merging two or more schema files into one schema file.
	 *  
	 * @param fileList The filelist contains a (potentially) ordered list of schemas
	 * @return The name of the created temporary schema file
	 */
	private String mergeFiles(String[] fileList)
	{
		try
		{
			File xsltFile = createXSLTFromFile(mergeFile);
			if (xsltFile == null)
				xsltFile = createXSLTFromMemory();
			Source xslt = new StreamSource(xsltFile);
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer(xslt);

			String sourceName = fileList[0];
			File target = null;
			for (int i = 1; i < fileList.length; i++)
			{
				File soureFile = new File(sourceName);
				Source source = new StreamSource(soureFile);
				// JAXP reads data using the Source interface
				target = createTemp(".xml");

				Result targetResult = new StreamResult(target);
				File f = new File(fileList[i]);
				String other = "file:///" + f.getCanonicalPath();  // required on Win-platforms
				other = other.replace('\\', '/');

				transformer.setParameter("fileTwo", other);
				transformer.transform(source, targetResult);
				sourceName = target.getAbsolutePath();
			}
			return sourceName;

		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * read database schema to file
	 * 
	 */
	private void processExport(JetspeedDDLUtil ddlUtil)
	{
		// TODO: implement
		ddlUtil.writeDatabaseSchematoFile(this.outputFile);

	}

	/**
	 * read the property file and read what has not yet been defined
	 */
	private void processPropertyFile()
	{
		/** get system property definition */
		if (propertyFileName == null)
			propertyFileName = System.getProperty(
					"org.apache.jetspeed.xml.ddlUtil.configuration", null);

		if (propertyFileName == null)
			return;
		try
		{
			configuration = new PropertiesConfiguration(propertyFileName);
		} catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
		if (configuration != null)
		{
			/** only read what was not defined on the command line */

			if (driverClass == null)
				driverClass = configuration.getString("driverClass");
			if (url == null)
				url = configuration.getString("url");
			if (user == null)
				user = configuration.getString("user");
			if (password == null)
				password = configuration.getString("password");
			if (mergeFile == null)
				mergeFile = configuration.getString("mergeFile");
			if (!(doImport))
			{
				schemaDirectory = configuration.getString("schema");
				if (schemaDirectory != null)
					doImport = true;
			}
			if (!(doExport))
			{
				outputFile = configuration.getString("outputFile");
				if (outputFile != null)
					doExport = true;
			}
			if (logLevel == null)
				logLevel = configuration.getString("loglevel");

		}

	}

    /*
	private static String[] getTokens(String _line)
	{
		if ((_line == null) || (_line.length() == 0))
			return null;

		StringTokenizer st = new StringTokenizer(_line, ",");
		ArrayList list = new ArrayList();

		while (st.hasMoreTokens())
			list.add(st.nextToken());
		String[] s = new String[list.size()];
		for (int i = 0; i < list.size(); i++)
			s[i] = (String) list.get(i);
		return s;
	}
    */

	public List getRows(JetspeedDDLUtil ddlUtil, String tableName)
	{
		Table table = ddlUtil.getModel().findTable(tableName,
				ddlUtil.getPlatform().isDelimitedIdentifierModeOn());

		return ddlUtil.getPlatform().fetch(ddlUtil.getModel(),
				getSelectQueryForAllString(ddlUtil, table), new Table[]
				{table});
	}

	public String getSelectQueryForAllString(JetspeedDDLUtil ddlUtil,
			Table table)
	{

		StringBuffer query = new StringBuffer();

		query.append("SELECT * FROM ");
		if (ddlUtil.getPlatform().isDelimitedIdentifierModeOn())
		{
			query.append(ddlUtil.getPlatform().getPlatformInfo()
					.getDelimiterToken());
		}
		query.append(table.getName());
		if (ddlUtil.getPlatform().isDelimitedIdentifierModeOn())
		{
			query.append(ddlUtil.getPlatform().getPlatformInfo()
					.getDelimiterToken());
		}
		System.out.println(query.toString());
		return query.toString();
	}

	/**
	 * read an xml file describing the basic order of the files to be processed
	 * 
	 * @param importFileName
	 * @return
	 * @throws SerializerException
	 */

	private ArrayList readOrderFile(String importFileName)
	{
		XMLObjectReader reader = null;

		XMLBinding binding = new XMLBinding();
		binding.setAlias(ArrayList.class, "ProcessOrder");
		binding.setAlias(JSGroup.class, "File");

		ArrayList snap = null;
		try
		{
			reader = XMLObjectReader.newInstance(new FileInputStream(
					importFileName));
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		try
		{
			reader.setBinding(binding);
			snap = (ArrayList) reader.read("ProcessOrder", ArrayList.class);

		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			/** ensure the reader is closed */
			try
			{
				reader.close();
			} catch (Exception e1)
			{
				/**
				 * don't do anything with this exception - never let the bubble
				 * out of the finally block
				 */
			}
		}
		return snap;
	}

	class LocalFilenameFilter implements FilenameFilter
	{

		String exclude = null;
		String sortFile = null;
		String sort = null;

		String getSortFile()
		{
			return sortFile;
		}
		LocalFilenameFilter(String exclude, String sort)
		{
			this.exclude = exclude;
			this.sort = sort;

		}
		public boolean accept(File dir, String name)
		{
			if (exclude != null)
				if (name.equalsIgnoreCase(exclude))
					return false;
			if (sort != null)
				if (name.equalsIgnoreCase(sort))
				{
					sortFile = dir.getAbsolutePath() + "/" + sort;
					return false;
				}

			return name.endsWith(".xml");
		}

	}

}
