/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.prefs;

import junit.framework.TestCase;
import java.sql.*; 

/**
 * TestOracle
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class TestOracle extends TestCase
{

    public void testDriver() throws Exception  
    {         
        // Load Oracle driver
        
        DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
         
        // Connect to the local database
        
        Connection conn = 
          DriverManager.getConnection ("jdbc:oracle:thin:@nirvana:1521:bluesun3", 
                                       "j2test", "digital");
        
        String sql = "SELECT A0.NODE_NAME, A0.MODIFIED_DATE, A0.NODE_TYPE, A0.PARENT_NODE_ID, A0.FULL_PATH, A0.NODE_ID, A0.CREATION_DATE " +
                     "FROM PREFS_NODE A0 WHERE (A0.FULL_PATH = ?) AND A0.NODE_TYPE = ?";
        
        PreparedStatement stmt = conn.prepareStatement (sql);
        stmt.setObject(1, "whatever");
        stmt.setObject(2, new Integer(393));
        
        ResultSet rset = stmt.executeQuery ("select NODE_ID from PREFS_NODE");
        
        // Print the name out 
        
        while (rset.next ())
        {
          System.out.println (rset.getString (1));
        }
    } 
}
