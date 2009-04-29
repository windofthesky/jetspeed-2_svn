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
package org.apache.jetspeed.tools.pamanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.AbstractRequestContextTestCase;
import org.apache.jetspeed.components.portletregistry.RegistryException;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class TestPortletApplicationManager extends AbstractRequestContextTestCase
{
    private static final Log log = LogFactory.getLog(TestPortletApplicationManager.class);

    public static final boolean TEST_CONCURRENT_PAM_ACCESS = true;
    public static final boolean TEST_USE_VERSIONED_PAM = false;
    public static final int TEST_PORTLET_APPLICATION_RESTARTS = 5;

    public static final String CONTEXT_NAME = "test-pa";
    public static final String CONTEXT_PATH = "/"+CONTEXT_NAME;

    private String osExecutableExtension;
    private String fileSeparator;
    private File javaExecutablePath;
    private String classPathSeparator;
    private File projectDirectoryPath;
    private Map systemProperties;
    private String classPath;

    private String baseDir;
    private PortletApplicationManagement portletApplicationManager;    
    
    /**
     * Configure test methods.
     * 
     * @return test suite.
     */
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestPortletApplicationManager.class);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.AbstractRequestContextTestCase#getConfigurations()
     */
    protected String[] getConfigurations()
    {
        String[] confs = super.getConfigurations();
        List confList = new ArrayList(Arrays.asList(confs));
        confList.add("deployment.xml");
        if (TEST_USE_VERSIONED_PAM)
        {
            confList.add("alternate/versioned-deployment/deployment.xml");
        }
        confList.add("pwa.xml");
        confList.add("security-managers.xml");
        confList.add("security-providers.xml");
        confList.add("security-spi-atn.xml");
        confList.add("security-spi.xml");
        confList.add("security-spi-atz.xml");
        confList.add("security-atz.xml");
        confList.add("search.xml");
        confList.add("cluster-node.xml");
        return (String[]) confList.toArray(new String[1]);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.util.RegistrySupportedTestCase#getPostProcessProperties()
     */
    protected Properties getPostProcessProperties()
    {
        // setup dummy autodeployment properties
        baseDir = System.getProperty("basedir", ".");
        if ((baseDir == null) || (baseDir.length() == 0))
        {
            baseDir = System.getProperty("user.dir");
        }
        // set test properties
        return setTestProperties(baseDir, super.getPostProcessProperties());
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.util.RegistrySupportedTestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        // environment setup
        osExecutableExtension = (System.getProperty("os.name").startsWith("Windows") ? ".exe" : "");
        fileSeparator = System.getProperty("file.separator");
        javaExecutablePath = new File(System.getProperty("java.home")+fileSeparator+"bin"+fileSeparator+"java"+osExecutableExtension);
        classPathSeparator = System.getProperty("path.separator");
        projectDirectoryPath = new File(System.getProperty("basedir"));
        systemProperties = new HashMap();
        for (Iterator iter = System.getProperties().entrySet().iterator(); iter.hasNext();)
        {
            final Map.Entry systemProperty = (Map.Entry)iter.next();
            final String propertyName = systemProperty.getKey().toString();
            final String propertyValue = systemProperty.getValue().toString();
            if (propertyName.startsWith("org.apache.jetspeed.") || propertyName.startsWith("java.net.") || propertyName.equals("basedir"))
            {
                systemProperties.put(propertyName, propertyValue);
            }
        }
        // construct launcher classpath from current class loader
        final StringBuffer classPathBuilder = new StringBuffer();
        final ClassLoader loader = this.getClass().getClassLoader();
        assertTrue(loader instanceof URLClassLoader);
        final URLClassLoader urlLoader = (URLClassLoader)loader;
        assertNotNull(urlLoader.getURLs());
        for (int i = 0; (i < urlLoader.getURLs().length); i++)
        {
            final URL pathURL = urlLoader.getURLs()[i];

            // convert path URL to file path
            final String path = new File(new URI(pathURL.toString())).getCanonicalPath();

            // build class path
            if (classPathBuilder.length() > 0)
            {
                classPathBuilder.append(classPathSeparator);
            }
            classPathBuilder.append(path);
        }
        classPath = classPathBuilder.toString();
        assertTrue(classPath.length() > 0);

        // setup test
        super.setUp();
        portletApplicationManager = (PortletApplicationManagement)ctx.getBean("PAM");
        assertTrue(portletApplicationManager.isStarted());
        Class portletApplicationManagerClass = ctx.getBean("org.apache.jetspeed.tools.pamanager.PortletApplicationManager").getClass();
        log.debug("PortletApplicationManager: "+portletApplicationManagerClass);
        // unregister portlet application
        try
        {
            portletApplicationManager.unregisterPortletApplication(CONTEXT_NAME);
        }
        catch (RegistryException re)
        {
        }
    }   

    /**
     * Test basic PortletApplicationManager operation.
     */
    public void testPortletApplicationManager()
    {
        // start portlet application manager test servers
        final TestProgram server0 = new TestProgram("server-0", PortletApplicationManagerServer.class);
        final TestProgram server1 = new TestProgram("server-1", PortletApplicationManagerServer.class);
        try
        {
            // start servers
            server0.start();
            server1.start();

            // wait until servers have started
            server0.execute("");
            server1.execute("");
            
            // test starting and stopping portlet application
            String result;
            for (int i = 0; (i < TEST_PORTLET_APPLICATION_RESTARTS); i++)
            {
                // start portlet application
                if (TEST_CONCURRENT_PAM_ACCESS)
                {
                    // start portlet application asynchronously in background threads per server
                    log.info("test concurrent register/start/stop portlet application, iteration "+i+"...");
                    TestExecuteThread startPortletApplication0 = new TestExecuteThread(server0, "portletApplicationManagerServer.startPortletApplication();");
                    TestExecuteThread startPortletApplication1 = new TestExecuteThread(server1, "portletApplicationManagerServer.startPortletApplication();");
                    startPortletApplication0.start();
                    startPortletApplication1.start();
                    result = startPortletApplication0.getResult();
                    assertTrue(result.indexOf("Exception") == -1);
                    result = startPortletApplication1.getResult();
                    assertTrue(result.indexOf("Exception") == -1);
                }
                else
                {
                    // stop portlet application synchronously
                    log.info("test serial register/start/stop portlet application, iteration "+i+"...");
                    result = server0.execute("portletApplicationManagerServer.startPortletApplication();");
                    assertTrue(result.indexOf("Exception") == -1);
                    result = server1.execute("portletApplicationManagerServer.startPortletApplication();");
                    assertTrue(result.indexOf("Exception") == -1);                    
                }
                // stop portlet application synchronously
                result = server1.execute("portletApplicationManagerServer.stopPortletApplication();");
                assertTrue(result.indexOf("Exception") == -1);
                result = server0.execute("portletApplicationManagerServer.stopPortletApplication();");
                assertTrue(result.indexOf("Exception") == -1);
                // unregister portlet application
                log.info("test unregister portlet application, iteration "+i+"...");
                try
                {
                    portletApplicationManager.unregisterPortletApplication(CONTEXT_NAME);
                }
                catch (RegistryException re)
                {
                }
            }
        }
        catch (final Exception e)
        {
            log.error("Server test exception: "+e, e);
            fail("Server test exception: "+e);            
        }        
        finally
        {
            // silently shutdown servers
            try
            {
                server0.shutdown();
            }
            catch (final Exception e)
            {
                log.error( "Server shutdown exception: "+e, e);
            }
            try
            {
                server1.shutdown();
            }
            catch (final Exception e)
            {
                log.error( "Server shutdown exception: "+e, e);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.util.DatasourceEnabledSpringTestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {       
        // unregister portlet application
        try
        {
            portletApplicationManager.unregisterPortletApplication(CONTEXT_NAME);
        }
        catch (RegistryException re)
        {
        }
        portletApplicationManager = null;
        // teardown test
        super.tearDown();
    }   

    /**
     * TestProgram
     * 
     * Implementation of test program executables.
     */
    private class TestProgram
    {
        private String name;
        private Class mainClass;

        private Process process;
        private BufferedWriter processInput;
        private BufferedReader processOutput;
        
        public TestProgram(final String name, final Class mainClass)
        {
            this.name = name;
            this.mainClass = mainClass;
        }
        
        public synchronized void start() throws IOException
        {
            assertNull(process);

            // configure launcher with paths and properties
            final List commandAndArgs = new ArrayList();
            commandAndArgs.add(javaExecutablePath.getCanonicalPath());
            for (Iterator iter = systemProperties.entrySet().iterator(); iter.hasNext();)
            {
                final Map.Entry systemProperty = (Map.Entry)iter.next();
                final String propertyName = (String)systemProperty.getKey();
                String propertyValue = (String)systemProperty.getValue();
                commandAndArgs.add( "-D"+propertyName+"="+propertyValue);
            }
            commandAndArgs.add("-Dlog4j.configuration=log4j-stdout.properties");
            commandAndArgs.add("-classpath");
            commandAndArgs.add(classPath);
            commandAndArgs.add(mainClass.getName());

            // launch test programs
            process = Runtime.getRuntime().exec((String []) commandAndArgs.toArray(new String[commandAndArgs.size()]), null, projectDirectoryPath);

            // setup I/O for process
            processInput = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // read messages from process
            for (String line; (processOutput.ready() && ((line = processOutput.readLine()) != null));)
            {
                logProcessLine(line);
            }
        }

        public synchronized String execute(final String scriptLine) throws IOException
        {
            assertNotNull(process);

            // read messages from process
            for (String line; (processOutput.ready() && ((line = processOutput.readLine()) != null));)
            {
                logProcessLine(line);
            }

            // write script line to process
            processInput.write(scriptLine);
            processInput.newLine();
            processInput.flush();

            // read result or messages from process
            String resultLine = null;
            for (String line; ((line = processOutput.readLine()) != null);)
            {
                if (! line.startsWith(PortletApplicationManagerServer.SCRIPT_RESULT_LINE_PREFIX))
                {
                    logProcessLine(line);
                }
                else
                {
                    resultLine = line;
                    break;
                }
            }
            if ( resultLine == null)
            {
                throw new IOException("Unexpected EOF from process output");
            }
            return resultLine;
        }
        
        public synchronized void shutdown() throws IOException, InterruptedException
        {
            assertNotNull( process);

            // start thread to destroy process on timeout
            final Thread destroyThread = new Thread(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        Thread.sleep(10000);
                        if ( process != null)
                        {
                            log.warn( "Forcibly stopping "+name);
                            process.destroy();
                        }
                    }
                    catch ( final Exception e)
                    {
                    }
                }
            }, "DestroyThread");
            destroyThread.setDaemon( true);
            destroyThread.start();

            // close process input to shutdown server and read messages
            processInput.close();
            for (String line; ((line = processOutput.readLine()) != null);)
            {
                logProcessLine(line);
            }

            // join on process completion
            process.waitFor();
            processOutput.close();
            process = null;

            // join on destroy thread
            destroyThread.interrupt();
            destroyThread.join();
        }
        
        private void logProcessLine(final String line)
        {
            if ((line.indexOf("INFO") == -1) && ((line.indexOf("ERROR") != -1) || (line.indexOf("Exception") != -1) || line.matches("\\s+at\\s.*")))
            {
                log.error("{"+name+"} "+line);
            }
            else
            {
                log.info("{"+name+"} "+line);                        
            }
        }
    }
    
    /**
     * TestExecuteThread
     *
     * Execute script against specified server asynchronously.
     */
    private class TestExecuteThread extends Thread
    {
        private TestProgram server;
        private String scriptLine;
        private String result;
        private Exception exception;
        
        private TestExecuteThread(TestProgram server, String scriptLine)
        {
            this.server = server;
            this.scriptLine = scriptLine;
        }
        
        public void run()
        {
            try
            {
                result = server.execute(scriptLine);
            }
            catch (Exception e)
            {
                exception = e;
            }
        }
        
        public String getResult() throws Exception
        {
            try
            {
                join();
            }
            catch (InterruptedException ie)
            {
            }
            if (exception != null)
            {
                throw exception;
            }
            return result;
        }
    }

    /**
     * Set test configuration properties.
     * 
     * @param baseDir project base directory path
     * @param properties properties set to configure
     */
    public static Properties setTestProperties(String baseDir, Properties properties)
    {
        properties.setProperty("autodeployment.catalina.base", baseDir+"/target");
        properties.setProperty("autodeployment.catalina.engine", "Catalina");
        properties.setProperty("autodeployment.delay", "10000");
        properties.setProperty("autodeployment.password", "test");
        properties.setProperty("autodeployment.port", "8080");
        properties.setProperty("autodeployment.server", "localhost");
        properties.setProperty("autodeployment.staging.dir", baseDir+"/target");
        properties.setProperty("autodeployment.target.dir", baseDir+"/target");
        properties.setProperty("autodeployment.user", "test");
        return properties;
    }

    /**
     * Start the tests.
     *
     * @param args the arguments. Not used
     */
    public static void main(String args[])
    {
        TestRunner.main(new String[] {TestPortletApplicationManager.class.getName()});
    }
}
