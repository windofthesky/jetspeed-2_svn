/*
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the  "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" 
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package org.apache.jetspeed.anttasks;

/**
 * @version $Id$
 *
 */
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.JDBCTask;

public class AntInstallerCheckConnection extends JDBCTask
{
    public void setDriver(String driver)
    {
        super.setDriver(driver);
    }
    
    public void execute() throws BuildException
    {
        setDriver(getProject().getUserProperty("jdbcDriverClass"));
        setUserid(getProject().getUserProperty("dbUser"));
        setPassword(getProject().getUserProperty("dbPassword"));
        setUrl(getProject().getUserProperty("jdbcUrl"));
        try
        {
            getConnection();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Connection failed",e);
        }
    }
}
