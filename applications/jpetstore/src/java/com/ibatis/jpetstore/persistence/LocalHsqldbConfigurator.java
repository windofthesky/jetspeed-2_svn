/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package com.ibatis.jpetstore.persistence;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class LocalHsqldbConfigurator implements ServletContextListener
{
    public void contextInitialized(ServletContextEvent sce)
    {
        ServletContext context = sce.getServletContext();
        try
        {
            File propertiesFile = new File(context.getRealPath("WEB-INF/classes/properties/database.properties"));
            if (propertiesFile.exists())
            {
              context.log("LocalHsqldbConfigurator: database.properties already exists");
              return;
            }
            String dbPath = context.getRealPath("/WEB-INF/db/jpetstore.script");
            FileWriter output = new FileWriter(propertiesFile);
            output.write("driver=org.hsqldb.jdbcDriver\n");
            output.write("url=jdbc:hsqldb:"+dbPath.substring(0,dbPath.length()-(".script".length())).replace('\\','/')+"\n");
            output.write("username=sa\n");
            output.write("password=\n");
            output.close();
            context.log("LocalHsqldbConfigurator: database.properties created");
        }
        catch (IOException e)
        {
            context.log("LocalHsqldbConfigurator: failed to create database.properties",e);
        }
    }

    public void contextDestroyed(ServletContextEvent sce)
    {
    }
} 