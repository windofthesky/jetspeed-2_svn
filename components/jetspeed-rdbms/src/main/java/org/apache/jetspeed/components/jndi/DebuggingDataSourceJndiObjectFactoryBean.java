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
package org.apache.jetspeed.components.jndi;

import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.jndi.JndiObjectFactoryBean;

import org.apache.jetspeed.util.DelegatingObjectProxy;

/**
* @version $Id$
*/
public class DebuggingDataSourceJndiObjectFactoryBean extends JndiObjectFactoryBean implements DisposableBean
{
   private static final Logger statisticsLog = LoggerFactory.getLogger("org.apache.jetspeed.ds.statistics");
   private static final Logger openConnectionsLog = LoggerFactory.getLogger("org.apache.jetspeed.ds.connections");

   private Object mutex = new Object();
   private DataSourceWrapper cachedWrapper;
   private int connectionSequence;
   private boolean debug;
   private HashMap<Integer,ConnectionWrapper> cachedConnections = new HashMap<Integer,ConnectionWrapper>();
   private HashMap<Integer,ConnectionCallStatistics> connectionsCallStatistics = new HashMap<Integer,ConnectionCallStatistics>();
   private String[] debugCallStackPackages = new String[0];

   public void flush()
   {
       System.out.print("*** FLUSHING Datasource Wrapper now....");
       synchronized (mutex)
       {    
           DebuggingDataSourceJndiObjectFactoryBean.this.debug = true;  
           dumpLog();   
           openConnectionsLog.info("-------------------------------------------------------------------------------");
           openConnectionsLog.info("open connections log flushed.");
           statisticsLog.info("-------------------------------------------------------------------------------");
           statisticsLog.info("data source statistics log flushed.");           
        }
       System.out.println("...flush completed. ***");       
   }

   class ConnectionCallStatistics
   {
       String callStack;
       int    callCount;
       long   accumulatedDuration;
       long   minDuration;
       long   maxDuration;
       long   totalOverhead;
   }

   public class ConnectionWrapper
   {
       private Connection connection;
       private Integer connectionKey;
       private Integer statisticsKey;
       private long duration;
       private long overhead;
       private boolean closed;

       private ConnectionWrapper(Connection connection, Integer connectionKey, Integer statisticsKey, long startTime)
       {            
           this.connection = connection;
           this.connectionKey = connectionKey;
           this.statisticsKey = statisticsKey;
           duration = System.currentTimeMillis();
           this.overhead = startTime;
       }

       public void clearWarnings() throws SQLException
       {
           connection.clearWarnings();
       }

       public void close() throws SQLException
       {
           long endTime = System.currentTimeMillis();
           connection.close();
           if (!closed)
           {
               closed = true;

               synchronized (mutex)
               {
                   if (DebuggingDataSourceJndiObjectFactoryBean.this.debug)
                   {
                       duration = endTime - duration;
                       overhead = endTime - overhead - duration;
                       ConnectionCallStatistics statistics = connectionsCallStatistics.get(statisticsKey);
                       if (statistics == null)
                       {
                           System.out.println("failed to find " + statisticsKey);
                           return;
                       }
                       statistics.accumulatedDuration+=duration;
                       statistics.totalOverhead+=overhead;
                       if (duration < statistics.minDuration)
                       {
                           statistics.minDuration = duration;
                       }
                       if (duration > statistics.maxDuration)
                       {
                           statistics.maxDuration = duration;
                       }
                       cachedConnections.remove(connectionKey);
                   }
               }                
           }
       }
   }

   public class DataSourceWrapper
   {
       DataSource ds;

       public DataSourceWrapper(DataSource ds)
       {
           this.ds = ds;
       }

       public void flush()
       {
           DebuggingDataSourceJndiObjectFactoryBean.this.flush();           
       }
      
       private void checkHandleTimeoutException(SQLException e)
       {
           if (DebuggingDataSourceJndiObjectFactoryBean.this.debug)
           {
               // dump on any SQLException
               dumpLog();
/*                
               if (e.getMessage().contains("ConnectionWaitTimeoutException") || e.getCause() != null && e.getCause().getMessage().contains("ConnectionWaitTimeoutException"))
               {
                   // dump only on (IBM specific) WaitTimeoutException
                   dumpLog();
               }
*/                
           }
       }

       public Connection getConnection() throws SQLException
       {
           try
           {
               return DebuggingDataSourceJndiObjectFactoryBean.this.recordConnection(ds.getConnection());
           }
           catch (SQLException e)
           {
               checkHandleTimeoutException(e);
               throw e;
           }
       }

       public Connection getConnection(String username, String password) throws SQLException
       {
           try
           {
               return DebuggingDataSourceJndiObjectFactoryBean.this.recordConnection(ds.getConnection(username, password));
           }
           catch (SQLException e)
           {
               checkHandleTimeoutException(e);
               throw e;
           }
       }
   }

   public void setDebugCallStackPackages(List list)
   {
       if (list != null && !list.isEmpty())
       {
           this.debugCallStackPackages = (String[])list.toArray(new String[list.size()]);
       }
   }

//   public List getDebugCallStackPackages()
//   {
//       return Arrays.asList(this.debugCallStackPackages);
//   }
   
   public void afterPropertiesSet() throws IllegalArgumentException, NamingException
   {
       super.afterPropertiesSet();
       debug = statisticsLog.isDebugEnabled();
       if (debug)
       {
           statisticsLog.debug("Starting collecting Datasource connection call statistics");

           if (openConnectionsLog.isDebugEnabled())
           {
               openConnectionsLog.debug("Starting monitoring open Datasource connections");
           }
       }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.springframework.jndi.JndiObjectFactoryBean#getObject()
    */
   public Object getObject()
   {
       DataSource ds = (DataSource) super.getObject();
       if (debug)
       {
           synchronized (this)
           {
               if (cachedWrapper == null || cachedWrapper.ds != ds)
               {
                   cachedWrapper = new DataSourceWrapper(ds);
               }
               
               DataSource dsProxy = (DataSource) DelegatingObjectProxy.createProxy(new Class [] { DataSource.class }, cachedWrapper, ds);
               return dsProxy;
           }
       }
       return ds;
   }

   private Connection recordConnection(Connection connection)
   {
       long startTime = System.currentTimeMillis();
       synchronized (mutex)
       {
           if (DebuggingDataSourceJndiObjectFactoryBean.this.debug)
           {
               String callStack = getJetspeedCallStack(Thread.currentThread().getStackTrace());
               if (callStack != null)
               {
                   Integer statisticsKey = new Integer(callStack.hashCode());
                   ConnectionCallStatistics statistics = connectionsCallStatistics.get(statisticsKey);
                   if (statistics == null)
                   {
                       statistics = new ConnectionCallStatistics();
                       statistics.callStack = callStack;
                       connectionsCallStatistics.put(statisticsKey, statistics);
                   }
                   statistics.callCount++;
                   Integer connectionKey = new Integer(connectionSequence++);
                   ConnectionWrapper cw = new ConnectionWrapper(connection, connectionKey, statisticsKey, startTime);
                   connection = (Connection) DelegatingObjectProxy.createProxy(new Class [] { Connection.class }, cw, connection);
                   cachedConnections.put(connectionKey, cw);
               }
           }
       }
       return connection;
   }

   private void dumpLog()
   {
    
//       synchronized (mutex)
//       {
//           if (DebuggingDataSourceJndiObjectFactoryBean.this.debug)
//           {
//               DebuggingDataSourceJndiObjectFactoryBean.this.debug = false;
//           }
//           else
//           {
//               return;
//           }
//       }
    

       long currentTime = System.currentTimeMillis();

       ConnectionCallStatistics[] statistics = connectionsCallStatistics.values().toArray(new ConnectionCallStatistics[connectionsCallStatistics.size()]);
       if (statistics.length > 1)
       {
           Arrays.sort(statistics, new Comparator<ConnectionCallStatistics>(){

               public int compare(ConnectionCallStatistics o1, ConnectionCallStatistics o2)
               {
                   return o1.callCount == o2.callCount ? 0 : o1.callCount > o2.callCount ? -1 : 1;
               }});
       }

       StringBuffer buffer = new StringBuffer();
       buffer.append("The following "+statistics.length+" DataSource connection call stacks where recorded, sorted by number of calls: \n");
       int index = 1;
       for (ConnectionCallStatistics ccs : statistics)
       {
           buffer.append("\n");
           buffer.append("  call stack nr: ");
           buffer.append(index++);
           buffer.append(", calls: ");
           buffer.append(ccs.callCount);
           buffer.append(", min.duration: ");
           buffer.append(formatDuration(ccs.minDuration));
           buffer.append(", max.duration: ");
           buffer.append(formatDuration(ccs.maxDuration));
           buffer.append(", acc.duration: ");
           buffer.append(formatDuration(ccs.accumulatedDuration));
           buffer.append(", avg.duration: ");
           buffer.append(formatDuration(ccs.accumulatedDuration/ccs.callCount));
           buffer.append(", tot.overhead: ");
           buffer.append(formatDuration(ccs.totalOverhead));
           buffer.append(", avg.overhead: ");
           buffer.append(formatDuration(ccs.totalOverhead/ccs.callCount));
           buffer.append("\n");
           buffer.append(ccs.callStack);
           buffer.append("\n");
       }
       statisticsLog.debug(buffer.toString());

       if (openConnectionsLog.isDebugEnabled())
       {
           ConnectionWrapper[] connectionWrappers = cachedConnections.values().toArray(new ConnectionWrapper[cachedConnections.size()]);
           for (ConnectionWrapper cw : connectionWrappers)
           {
               cw.duration = currentTime - cw.duration;
           }
           if (connectionWrappers.length > 1)
           {
               Arrays.sort(connectionWrappers, new Comparator<ConnectionWrapper>(){

                   public int compare(ConnectionWrapper o1, ConnectionWrapper o2)
                   {
                       return o1.duration == o2.duration ? 0 : o1.duration > o2.duration ? -1 : 1;
                   }});
           }
           buffer.setLength(0);
           buffer.append("The following "+connectionWrappers.length+" connections where still active, sorted by the duration since creation: \n");
           index = 1;
           for (ConnectionWrapper cw : connectionWrappers)
           {
               buffer.append("\n");
               buffer.append("  connection nr: ");
               buffer.append(index++);
               buffer.append(", duration: ");
               buffer.append(formatDuration(cw.duration));
               buffer.append("\n");
               buffer.append(connectionsCallStatistics.get(cw.statisticsKey).callStack);
               buffer.append("\n");
           }
           openConnectionsLog.debug(buffer.toString());
       }

       connectionsCallStatistics.clear();
       cachedConnections.clear();
   }

   private static String formatDuration(long duration)
   {
       long seconds = duration/1000;
       long millis = duration-seconds;
       return ""+seconds+"."+(millis<100?millis<10?"00":"0":"")+millis+"s";
   }

   private String getJetspeedCallStack(StackTraceElement[] ste)
   {
       String callStack = null;
       for (int i = 1; ste != null && i < ste.length; i++)
       {
           if (ste[i].getClassName().startsWith("org.apache.jetspeed.components.jndi.DebuggingDataSourceJndiObjectFactoryBean$DataSourceWrapper"))
           {
               i++;
               for (; i < ste.length; i++)
               {
                   if (ste[i].getClassName().equals("org.apache.jetspeed.util.DelegatingObjectProxy") && ste[i].getMethodName().equals("invoke"))
                   {
                       continue;
                   }                                    
                   if (ste[i].getClassName().equals("org.apache.jetspeed.components.rdbms.ojb.ConnectionManagerImpl") && ste[i].getMethodName().equals("getConnection"))
                   {
                       continue;
                   }                                    
                   if (ste[i].getClassName().startsWith("org.apache.jetspeed"))
                   {
                       StringBuffer b = new StringBuffer();
                       boolean inJ2 = true;
                       for (; i < ste.length; i++)
                       {
                           boolean logSTE = isDebugCallStackPackage(ste[i].getClassName());
                           if (inJ2)
                           {
                               if (!logSTE)
                               {
                                   b.append("    ...\n");
                                   inJ2 = false;
                               }
                           }
                           else
                           {
                               if (logSTE)
                               {
                                   inJ2 = true;
                               }
                           }
                           if (inJ2)
                           {
                               if (ste[i].getClassName()
                                         .equals("org.apache.jetspeed.pipeline.JetspeedPipeline$Invocation") &&
                                   ste[i].getMethodName().equals("invokeNext"))
                               {
                                   b.append("    ...\n");
                                   break;
                               }
                               b.append("    ");
                               b.append(ste[i].toString());
                               b.append("\n");
                               if (ste[i].getClassName()
                                         .equals("org.apache.jetspeed.factory.JetspeedPortletInstance"))
                               {
                                   b.append("    ...\n");
                                   break;
                               }
                               if (ste[i].getClassName().equals("org.apache.jetspeed.engine.JetspeedEngine"))
                               {
                                   b.append("    ...\n");
                                   break;
                               }
                           }
                       }
                       callStack = b.toString();
                       break;
                   }
               }
               break;
           }
       }
       return callStack;
   }

   private boolean isDebugCallStackPackage(String className)
   {
       if (!className.startsWith("org.apache.jetspeed"))
       {
           for (int i = 0; i < debugCallStackPackages.length; i++)
           {
               if (className.startsWith(debugCallStackPackages[i]))
               {
                   return true;
               }                                
           }
           return false;
       }
       return true;
   }

   public void destroy() throws Exception
   {
       dumpLog();
   }
}
