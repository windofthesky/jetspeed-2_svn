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

package org.apache.jetspeed.components.test;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract Jexl scriptable test case.
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public abstract class AbstractJexlSpringTestCase extends TestCase {

    private static final long LOGGING_PUMP_WAIT = 50;

    private Logger log = LoggerFactory.getLogger(getClass());

    private String osExecutableExtension;
    private String fileSeparator;
    private File javaExecutablePath;
    private String classPathSeparator;
    private File projectDirectoryPath;
    private Map<String,String> systemProperties;
    private String classPath;

    @Override
    protected void setUp() throws Exception {
        // environment setup
        osExecutableExtension = (System.getProperty("os.name").startsWith("Windows") ? ".exe" : "");
        fileSeparator = System.getProperty("file.separator");
        javaExecutablePath = new File(System.getProperty("java.home")+fileSeparator+"bin"+fileSeparator+"java"+osExecutableExtension);
        classPathSeparator = System.getProperty("path.separator");
        projectDirectoryPath = new File(System.getProperty("basedir"));
        systemProperties = new HashMap<String,String>();
        for (Map.Entry<Object,Object> systemProperty : System.getProperties().entrySet()) {
            String propertyName = systemProperty.getKey().toString();
            String propertyValue = systemProperty.getValue().toString();
            if (propertyName.startsWith("org.apache.jetspeed.") || propertyName.startsWith("java.net.") || propertyName.equals("basedir")) {
                systemProperties.put(propertyName, propertyValue);
            }
        }

        // construct launcher classpath from current class loader
        StringBuilder classPathBuilder = new StringBuilder();
        ClassLoader loader = this.getClass().getClassLoader();
        assertTrue(loader instanceof URLClassLoader);
        URLClassLoader urlLoader = (URLClassLoader)loader;
        assertNotNull(urlLoader.getURLs());
        for (URL pathURL : urlLoader.getURLs()) {
            // convert path URL to file path
            String path = new File(pathURL.toURI()).getCanonicalPath();

            // build class path
            if (classPathBuilder.length() > 0) {
                classPathBuilder.append(classPathSeparator);
            }
            classPathBuilder.append(path);
        }
        classPath = classPathBuilder.toString();
        assertTrue(classPath.length() > 0);

        // continue setup
        super.setUp();
    }

    protected String testProgramSystemPropertyValueFilter(String propertyName, int index, String propertyValue) {
        // return original property value by default
        return propertyValue;
    }

    protected Map<String,String> testProgramSystemProperties() {
        return new HashMap<String,String>();
    }

    protected void sleep(TestProgram server0, TestProgram server1, long millis) throws IOException, InterruptedException {
        long slept = 0;
        while (slept < millis) {
            // poll servers for logging
            server0.poll();
            server1.poll();
            // sleep for interval
            long sleep = Math.min(millis-slept, LOGGING_PUMP_WAIT);
            Thread.sleep(sleep);
            slept += sleep;
        }
    }

    protected class TestProgram {

        private String name;
        private Class<?> mainClass;
        private int index;

        private Process process;
        private BufferedWriter processInput;
        private BufferedReader processOutput;

        public TestProgram(String name, Class<?> mainClass, int index) {
            this.name = name;
            this.mainClass = mainClass;
            this.index = index;
        }

        public synchronized void start() throws IOException {
            assertNull(process);

            // configure launcher with paths, properties, and indexed properties
            ProcessBuilder launcher = new ProcessBuilder();
            List<String> commandAndArgs = new ArrayList<String>();
            commandAndArgs.add(javaExecutablePath.getCanonicalPath());
            for (Map.Entry<String,String> systemProperty : systemProperties.entrySet()) {
                String propertyName = systemProperty.getKey();
                String propertyValue = testProgramSystemPropertyValueFilter(propertyName, index, systemProperty.getValue());
                commandAndArgs.add( "-D"+propertyName+"="+propertyValue);
            }
            for (Map.Entry<String,String> systemProperty : testProgramSystemProperties().entrySet()) {
                String propertyName = systemProperty.getKey();
                String propertyValue = testProgramSystemPropertyValueFilter(propertyName, index, systemProperty.getValue());
                commandAndArgs.add( "-D"+propertyName+"="+propertyValue);
            }
            commandAndArgs.add("-classpath");
            commandAndArgs.add(classPath);
            commandAndArgs.add(mainClass.getName());
            log.info("Launcher command for "+name+": "+commandAndArgs);
            launcher.command(commandAndArgs);
            launcher.directory(projectDirectoryPath);
            launcher.redirectErrorStream(true);

            // launch test programs
            process = launcher.start();

            // setup I/O for process
            processInput = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // read messages from process
            for (String line; (processOutput.ready() && ((line = processOutput.readLine()) != null));) {
                logProcessLine(line);
            }
        }

        public synchronized void poll() throws IOException
        {
            assertNotNull(process);

            // read messages from process
            for (String line; (processOutput.ready() && ((line = processOutput.readLine()) != null));) {
                logProcessLine(line);
            }
        }

        public synchronized String execute(String scriptLine) throws IOException
        {
            // poll to read messages from process
            poll();

            // write script line to process
            processInput.write(scriptLine);
            processInput.newLine();
            processInput.flush();

            // read result or messages from process
            String resultLine = null;
            for (String line; ((line = processOutput.readLine()) != null);) {
                if (! line.startsWith(AbstractJexlSpringTestServer.SCRIPT_RESULT_LINE_PREFIX)) {
                    logProcessLine(line);
                } else {
                    resultLine = line;
                    break;
                }
            }
            if ( resultLine == null) {
                throw new IOException("Unexpected EOF from process output");
            }
            return resultLine;
        }

        public synchronized void shutdown(final long millis) throws IOException, InterruptedException {
            assertNotNull(process);

            // start thread to destroy process on timeout
            Thread destroyThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(millis);
                        if ( process != null) {
                            log.warn( "Forcibly stopping "+name);
                            process.destroy();
                        }
                    } catch (Exception e) {
                    }
                }
            }, "DestroyThread");
            destroyThread.setDaemon( true);
            destroyThread.start();

            // close process input to shutdown server and read messages
            processInput.close();
            for (String line; ((line = processOutput.readLine()) != null);) {
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

        private void logProcessLine(String line)
        {
            if (line.contains("ERROR") || line.contains("Exception") || line.matches("\\s+at\\s.*")) {
                log.error("{"+name+"} "+line);
            } else {
                log.info("{"+name+"} "+line);
            }
        }
    }
}
