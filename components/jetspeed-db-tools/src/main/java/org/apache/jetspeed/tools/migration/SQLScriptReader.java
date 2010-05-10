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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * SQL Script Reader
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class SQLScriptReader
{
    private BufferedReader reader;
    
    public SQLScriptReader(File scriptFile) throws FileNotFoundException
    {
        this.reader = new BufferedReader(new FileReader(scriptFile));
    }
    
    public String readSQLStatement() throws IOException
    {
        StringBuilder sqlStatement = new StringBuilder();
        boolean comment = false;
        for (;;)
        {
            String line = reader.readLine();
            if (line != null)
            {
                line = line.trim();
                if (comment)
                {
                    comment = !line.endsWith("*/");
                }
                else
                {
                    comment = line.startsWith("/*");
                    if (!comment && !line.startsWith("--") && !line.startsWith("//") && !line.startsWith("#") && (line.length() > 0))
                    {
                        if (sqlStatement.length() > 0)
                        {
                            sqlStatement.append(' ');
                        }
                        sqlStatement.append(line);
                        if (line.endsWith(";"))
                        {
                            break;
                        }
                    }
                }
            }
            else
            {
                sqlStatement.setLength(0);
                break;
            }
        }
        return ((sqlStatement.length() > 0) ? sqlStatement.toString() : null);
    }

    public void close() throws IOException
    {
        reader.close();
    }
}
