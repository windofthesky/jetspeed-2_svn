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

import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.JexlException;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.jexl2.Script;
import org.apache.jetspeed.components.JetspeedBeanDefinitionFilter;
import org.apache.jetspeed.components.SpringComponentManager;
import org.apache.jetspeed.security.JSSubject;

import javax.security.auth.Subject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.PrivilegedAction;
import java.util.Map;

/**
 * Abstract Jexl scriptable test server.
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public abstract class AbstractJexlSpringTestServer {

    public static final String SCRIPT_RESULT_LINE_PREFIX = "> ";
    public static final String SCRIPT_RESULT_RETURN_VALUE_SEPARATOR = " -> ";

    protected String baseDir;
    protected SpringComponentManager scm;
    protected JexlContext jexlContext;
    protected boolean exit;

    /**
     * Initialize server component manager and script context.
     *
     * @throws Exception
     */
    public void initialize() throws Exception {
        // setup jetspeed test component manager
        JetspeedBeanDefinitionFilter beanDefinitionFilter = new JetspeedBeanDefinitionFilter(getBeanDefinitionFilterCategories());
        String [] bootConfigurations = getBootConfigurations();
        String [] configurations = getConfigurations();
        baseDir = System.getProperty("basedir");
        if ((baseDir == null) || (baseDir.length() == 0)) {
            baseDir = System.getProperty("user.dir");
        }
        String appRoot = baseDir+"/target/test-classes/webapp";
        scm = new SpringComponentManager(beanDefinitionFilter, bootConfigurations, configurations, appRoot, false);
        scm.start();

        // create jexl context
        jexlContext = new MapContext(getContextVars());
    }

    /**
     * Get Jetspeed Spring filter categories list.
     *
     * @return filter categories CSV list
     */
    protected abstract String getBeanDefinitionFilterCategories();

    /**
     * Get array of Spring boot configurations to load.
     *
     * @return Spring boot configurations array
     */
    protected String[] getBootConfigurations() {
        return null;
    }

    /**
     * Get array of Spring configurations to load.
     *
     * @return Spring configurations array
     */
    protected abstract String[] getConfigurations();

    /**
     * Get list of top level objects to add to Jexl context vars.
     *
     * @return map of context variables
     */
    protected abstract Map<String,Object> getContextVars();

    /**
     * Terminate server component manager.
     *
     * @throws Exception
     */
    public void terminate() throws Exception {
        // tear down jetspeed component manager
        scm.stop();
    }

    /**
     * Execute a single line script against server context.
     *
     * @param scriptLine jexl script
     * @return script result line
     */
    public String execute(String scriptLine) {
        // execute script line and return result line
        String resultLine = scriptLine;
        try {
            JexlEngine jexl = new JexlEngine();
            jexl.setSilent(false);
            jexl.setLenient(false);
            Script jexlScript = jexl.createScript(scriptLine);
            Object result = jexlScript.execute(jexlContext);
            if (result != null) {
                resultLine += SCRIPT_RESULT_RETURN_VALUE_SEPARATOR+result;
            }
        } catch (JexlException je) {
            if (je.getCause() != null) {
                resultLine += SCRIPT_RESULT_RETURN_VALUE_SEPARATOR+je.getCause();
            } else {
                resultLine += SCRIPT_RESULT_RETURN_VALUE_SEPARATOR+je;
            }
        } catch (Exception e) {
            resultLine += SCRIPT_RESULT_RETURN_VALUE_SEPARATOR+e;
        }
        return resultLine;
    }

    /**
     * Sets server exit flag.
     */
    public void exit() {
        exit = true;
    }

    /**
     * Get server exit flag.
     *
     * @return server exit flag
     */
    public boolean isExit() {
        return exit;
    }

    /**
     * Get or create and cache user subject.
     *
     * @return user subject
     */
    protected Subject getUserSubject() {
        return null;
    }

    /**
     * Server main entry point.
     *
     * @return runtime exception or null
     */
    public Throwable run() {
        try {
            // initialize server
            initialize();

            // simple server reads script lines from standard
            // input and writes results on standard output
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(System.out, true);
            do {
                // read single line scripts to execute
                String scriptLine = in.readLine();
                if (scriptLine != null) {
                    scriptLine = scriptLine.trim();
                    String resultLine = "";
                    if (scriptLine.length() > 0) {
                        // get user and execute script
                        Subject userSubject = getUserSubject();
                        if (userSubject != null) {
                            // execute script as user
                            final String executeScriptLine = scriptLine;
                            final String [] executeResultLine = new String[]{null};
                            Exception executeException = (Exception) JSSubject.doAsPrivileged(userSubject, new PrivilegedAction() {
                                public Object run() {
                                    try {
                                        executeResultLine[0] = execute(executeScriptLine);
                                        return null;
                                    } catch (Exception e) {
                                        return e;
                                    } finally {
                                        JSSubject.clearSubject();
                                    }
                                }
                            }, null);
                            if (executeException != null) {
                                throw executeException;
                            }
                            resultLine = executeResultLine[0];
                        } else {
                            // execute script anonymously
                            resultLine = execute(scriptLine);
                        }
                    }

                    // write prefixed single line results
                    out.println(SCRIPT_RESULT_LINE_PREFIX+resultLine);
                } else {
                    // exit server on input EOF
                    exit();
                }
            }
            while (!isExit());

            // terminate server and return
            terminate();
            return null;
        } catch (Throwable t) {
            return t;
        }
    }
}
