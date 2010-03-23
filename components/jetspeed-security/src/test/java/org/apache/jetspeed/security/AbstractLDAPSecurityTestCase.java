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
package org.apache.jetspeed.security;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @version $Id$
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 *
 */
public abstract class AbstractLDAPSecurityTestCase extends AbstractSecurityTestcase
{
    private static EmbeddedApacheDSTestService ldapService;
    private boolean ldapTestSetupRun = false;
    
    public AbstractLDAPSecurityTestCase()
    {
        ldapService = new EmbeddedApacheDSTestService(getLdapBaseDN(), getLdapPort(), getLdapWorkingDir());
    }
    
    public void ldapTestSetup() throws Exception
    {
        if (ldapService != null)
        {
            ldapService.start();
        }
        ldapTestSetupRun = true;
    }
    
    public void ldapTestTeardown() throws Exception
    {
        if (ldapService != null)
        {
            ldapService.stop();
        }
        ldapService = null;
        ldapTestSetupRun = true;
    }

    @Override
    public void setUp() throws Exception
    {        
        if (ldapService != null && !ldapTestSetupRun)
        {
            if (ldapService.isRunning())
            {
                File[] ldifs = getLdifs();
                for (int i = 0; i < ldifs.length; i++)
                {
                    ldapService.loadLdif(ldifs[i]);
                }
                super.setUp();
            }
        }
    }

    @Override
    public void tearDown() throws Exception
    {
        if (ldapService != null && !ldapTestSetupRun)
        {
            if (ldapService.isRunning())
            {
                super.tearDown();
                ldapService.revert();
            }
        }
    }
    
    protected String getLdapBaseDN()
    {
        return "o=sevenSeas";
    }
    
    protected int getLdapPort()
    {
        return 10389;
    }
    
    protected File getLdapWorkingDir()
    {
        return new File(getBaseDir()+"target/_apacheds");
    }

    protected File[] getLdifs() throws Exception
    {
        return new File[] {new File(getBaseDir()+"target/test-classes/JETSPEED-INF/directory/config/apacheds/init.ldif")};
    }

    protected String getBeanDefinitionFilterCategories()
    {
        return "security,ldapSecurity,transaction,cache,jdbcDS";
    }

    /**
     * Override the location of the test properties by using the jetspeed properties found in the default package.
     * Make sure to have your unit test copy in jetspeed properties into the class path root like:
     <blockquote><pre>
        &lt;resource&gt;
            &lt;path&gt;conf/jetspeed&lt;/path&gt;
            &lt;include&gt;*.properties&lt;/include&gt;                                        
        &lt;/resource&gt;                                         
     </pre></blockquote>
     */
    @Override    
    protected Properties getInitProperties()
    {
        Properties props = new Properties();
        try 
        {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("jetspeed.properties");
            if (is != null)
                props.load(is);
        } catch (FileNotFoundException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return props;
    }
           
    protected String[] getConfigurations()
    {
        String[] confs = super.getConfigurations();
        List<String> confList = new ArrayList<String>(Arrays.asList(confs));
        confList.add("security-ldap.xml");
        return confList.toArray(new String[0]);
    }
}
