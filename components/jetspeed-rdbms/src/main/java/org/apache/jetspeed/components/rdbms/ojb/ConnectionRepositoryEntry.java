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
package org.apache.jetspeed.components.rdbms.ojb;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ojb.broker.PBKey;
import org.apache.ojb.broker.accesslayer.ConnectionFactoryDBCPImpl;
import org.apache.ojb.broker.accesslayer.ConnectionFactoryManagedImpl;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.apache.ojb.broker.metadata.ConnectionPoolDescriptor;
import org.apache.ojb.broker.metadata.ConnectionRepository;
import org.apache.ojb.broker.metadata.JdbcConnectionDescriptor;
import org.apache.ojb.broker.metadata.JdbcMetadataUtils;
import org.apache.ojb.broker.metadata.MetadataManager;
import org.apache.ojb.broker.util.ClassHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;

/**
 * A JavaBean that configures an entry in OJB's ConnectionRepository
 * according to its properties. If a JCD alias is not specified, it defaults
 * to the bean's name in the Spring configuration. If the JDBC connection
 * descriptor already exists (e.g. because it has been defined in OJB's
 * configuration) the properties are merged into the existing descriptor
 * (see note about "platform" below), else the JDBC connection descriptor
 * is created.<P>
 * 
 * If a JNDI name is set, the bean automatically configures a JDBC connection 
 * descriptor with a connection factory of type 
 * <code>ConnectionFactoryManagedImpl</code>, else it uses 
 * <code>ConectionFactoryDBCPImpl</code>. This may be overridden my setting 
 * the connection factory property explicitly.<P>
 * 
 * Properties "driverClassName", "url", "username" and "password" are used
 * only if no JNDI name is set, i.e. if the connection factory uses the
 * driver to create data sources.<P>
 * 
 * The bean derives the RDBMS platform setting from the configured 
 * data source or database driver using OJB's <code>JdbcMetadataUtils</code>
 * class. At least until OJB 1.0.3, however, this class does not properly 
 * distinguish the platforms "Oracle" and "Oracle9i"; it always assigns 
 * "Oracle". In case of "Oracle", this bean therefore opens a connection,
 * obtains the version information from the database server and adjusts the
 * platform accordingly. This behaviour may be overridden by setting the 
 * <code>platform</code> property of the bean explicitly. Note that the
 * attribute "platform" of an already existing JCD is ignored. An already
 * existing JCD stems most likely from a configuration file "repository.xml".
 * As the DTD for "repository.xml" ("repository.dtd") defines a default
 * value for attribute "platform" ("Hsqldb"), it is in general impossible 
 * to find out whether the platform attribute of an existing JCD has been set 
 * explicitly or has simply assumed its default value.      
 *
 * @author Michael Lipp
 * @version $Id$
 */
public class ConnectionRepositoryEntry
    extends BasicDataSource
    implements BeanNameAware, InitializingBean
{
    private static final Logger log = LoggerFactory.getLogger(ConnectionRepositoryEntry.class);
    
    // general properties
    private String jcdAlias = null;
    private String platform = null;
    private String connectionFactoryClass = null;
    // properties for obtaining data source from JNDI
    private String jndiName = null;
    // properties for creating independant data source 
    private String driverClassName = null;
    private String url = null;
    private String username = null;
    private String password = null;
    private boolean jetspeedEngineScoped = true;

	private DataSource externalDs;

    public ConnectionRepositoryEntry()
    {
        super();
    }
    
    /**
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
     */
    public void setBeanName(String beanName) 
    {
        // Use the bean's name as fallback if a JCD alias is not set
        // explicitly
        if (jcdAlias == null) 
        {
            jcdAlias = beanName;
        }
    }
    
    /**
     * @return Returns the jcdAlias.
     */
    public String getJcdAlias() 
    {
        return jcdAlias;
    }
    
    /**
     * @param jcdAlias The jcdAlias to set.
     */
    public void setJcdAlias(String jcdAlias)
    {
        this.jcdAlias = jcdAlias;
    }

    /**
     * @return Returns the jndiName.
     */
    public String getJndiName() 
    {
        return jndiName;
    }

    /**
     * @param jndiName The jndiName to set.
     */
    public void setJndiName(String jndiName) 
    {
        this.jndiName = jndiName;
    }

    /**
     * @return Returns the driverClassName.
     */
    public String getDriverClassName() 
    {
        return driverClassName;        
    }

    /**
     * @param driverClassName The driverClassName to set.
     */
    public void setDriverClassName(String driverClassName) 
    {
        super.setDriverClassName(driverClassName);
        this.driverClassName = driverClassName;
    }

    /**
     * @return Returns the password.
     */
    public String getPassword() 
    {
        return password;
    }

    /**
     * @param password The password to set.
     */
    public void setPassword(String password) 
    {
        super.setPassword(password);
        this.password = password;
    }

    /**
     * @return Returns the url.
     */
    public String getUrl() 
    {
        return url;
    }

    /**
     * @param url The url to set.
     */
    public void setUrl(String url) 
    {
        super.setUrl(url);
        this.url = url;
    }

    /**
     * @return Returns the username.
     */
    public String getUsername() 
    {
        return username;
    }

    /**
     * @param username The username to set.
     */
    public void setUsername(String username) 
    {
        super.setUsername(username);
        this.username = username;
    }
    
    /**
     * @return Returns the platform.
     */
    public String getPlatform() 
    {
        return platform;
    }

    /**
     * Set the platform attribute of the JCD. Setting this property overrides
     * the value derived from the data source or database driver. 
     * @param platform The platform to set.
     */
    public void setPlatform(String platform) 
    {        
        this.platform = platform;
    }

    /**
     * @return Returns if Jetspeed engine's ENC is used for JNDI lookups.
     */
    public boolean isJetspeedEngineScoped() 
    {
        return jetspeedEngineScoped;
    }

    /**
     * Sets the attribute "<code>org.apache.jetspeed.engineScoped</code>"
     * of the JDBC connection descriptor to "<code>true</code>" or
     * "<code>false</code>". If set, JNDI lookups of the connection will
     * be done using the environment naming context (ENC) of the Jetspeed 
     * engine.
     * @param jetspeedEngineScoped whether to use Jetspeed engine's ENC.
     */
    public void setJetspeedEngineScoped(boolean jetspeedEngineScoped) 
    {
        this.jetspeedEngineScoped = jetspeedEngineScoped;
    }

    public void afterPropertiesSet () throws Exception 
    {
        // Try to find JCD
        ConnectionRepository cr = MetadataManager.getInstance().connectionRepository();
        JdbcConnectionDescriptor jcd = cr.getDescriptor(new PBKey(jcdAlias));
        if (jcd == null)
        {
            jcd = new JdbcConnectionDescriptor();
            jcd.setJcdAlias(jcdAlias);
            cr.addDescriptor(jcd);
        }
        if (platform != null && platform.length() == 0)
        {
            platform = null;
        }
        DataSource ds = null;
        JdbcMetadataUtils jdbcMetadataUtils = new JdbcMetadataUtils ();
        if (jndiName != null)
        {
            // using "preconfigured" data source
            if (connectionFactoryClass == null) 
            {
                connectionFactoryClass = ConnectionFactoryManagedImpl.class.getName ();
            }
            Context initialContext = new InitialContext();
            ds = (DataSource) initialContext.lookup(jndiName);
            externalDs = ds;
			jcd.setDatasourceName(jndiName);
        } 
        else 
        {
            // have to get data source ourselves
            if (connectionFactoryClass == null) 
            {
                connectionFactoryClass = ConnectionFactoryDBCPImpl.class.getName ();
            }
            jcd.setDriver(driverClassName);
            Map conData = jdbcMetadataUtils.parseConnectionUrl(url);
            jcd.setDbms(platform);
            jcd.setProtocol((String)conData.get(JdbcMetadataUtils.PROPERTY_PROTOCOL));
            jcd.setSubProtocol((String)conData.get(JdbcMetadataUtils.PROPERTY_SUBPROTOCOL));
            jcd.setDbAlias((String)conData.get(JdbcMetadataUtils.PROPERTY_DBALIAS));
            jcd.setUserName(username);
            jcd.setPassWord(password);
            // Wrapping the connection factory in a DataSource introduces a bit 
            // of redundancy (url is parsed again and platform determined again).
            // But although JdbcMetadataUtils exposes the methods used in 
            // fillJCDFromDataSource as public (and these do not require a DataSource)
            // the method itself does more than is made available by the exposed methods.
            // ds = new MinimalDataSource (jcd);
            ds = this;             
        }
        ConnectionPoolDescriptor cpd = jcd.getConnectionPoolDescriptor();
        if (cpd == null)
        {
            cpd = new ConnectionPoolDescriptor();
            jcd.setConnectionPoolDescriptor(cpd);
        }
        Class conFacCls = ClassHelper.getClass(connectionFactoryClass);
        cpd.setConnectionFactory(conFacCls);

        jdbcMetadataUtils.fillJCDFromDataSource(jcd, ds, null, null);
        
        if (platform == null && JdbcMetadataUtils.PLATFORM_ORACLE.equals(jcd.getDbms())) {
            // Postprocess to find Oracle version.
            updateOraclePlatform (jcd, ds);
        }
        // if platform has explicitly been set, the value takes precedence
        if (platform != null) {
            if (!platform.equals(jcd.getDbms())) {
                log.warn ("Automatically derived RDBMS platform \"" + jcd.getDbms()
                          + "\" differs from explicitly set platform \"" + platform + "\""); 
            }
            jcd.setDbms(platform);
        } else {
            platform = jcd.getDbms();
        }
        
        // special attributes
        jcd.addAttribute("org.apache.jetspeed.engineScoped", 
                         Boolean.toString(jetspeedEngineScoped));
    }

    /**
     * @param jcd
     * @throws LookupException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * throws SQLException
     */
    private void updateOraclePlatform(JdbcConnectionDescriptor jcd, DataSource ds)
    	throws LookupException, IllegalAccessException, InstantiationException, SQLException 
    {
        Connection con = null;
        try 
        {
            con = ds.getConnection();
            DatabaseMetaData metaData = con.getMetaData();
            int rdbmsVersion = 0;
            try 
            {
                // getDatabaseMajorVersion exists since 1.4, so it may
                // not be defined for the driver used.
                rdbmsVersion = metaData.getDatabaseMajorVersion();
            } catch (Throwable t) {
                String dbVersion = metaData.getDatabaseProductVersion();
                String relKey = "Release";
                String major = dbVersion;
                int startPos = dbVersion.indexOf(relKey);
                if (startPos < 0)
                {
                    log.warn ("Cannot determine Oracle version, no \"Release\" in procuct version: \"" + dbVersion + "\"");
                    return;
                }
                startPos += relKey.length();
                int dotPos = dbVersion.indexOf('.', startPos);
                if (dotPos > 0) {
                    major = dbVersion.substring(startPos, dotPos).trim();
                }
                try
                {
                    rdbmsVersion = Integer.parseInt(major);
                }
                catch (NumberFormatException e)
                {
                    log.warn ("Cannot determine Oracle version, product version \"" + dbVersion + "\" not layed out as \"... Release N.M.....\"");
                    return;
                }
                if (log.isDebugEnabled())
                {
                    log.debug ("Extracted Oracle major version " + rdbmsVersion + " from product version \"" + dbVersion + "\"");
                }
            }
            if (rdbmsVersion >= 9) {
                jcd.setDbms(JdbcMetadataUtils.PLATFORM_ORACLE9I);
            }
        }
        finally
        {
            if (con != null) {
                con.close ();
            }
        }
    }

    /**
     * a minimal DataSource implementation that satisfies the requirements
     * of JdbcMetadataUtil.
     */
    public class MinimalDataSource implements DataSource
    {
        private JdbcConnectionDescriptor jcd = null;
        
        /**
         * Create a new instance using the given JCD.
         */
        public MinimalDataSource (JdbcConnectionDescriptor jcd)
        {
            this.jcd = jcd;
        }

        public java.util.logging.Logger getParentLogger()
                throws SQLFeatureNotSupportedException
        {
            return null;
        }

        public boolean isWrapperFor(Class<?> iface) throws SQLException
        {
            return getConnection().isWrapperFor(iface);
        }

        public <T> T unwrap(Class<T> iface) throws SQLException
        {
            return getConnection().unwrap(iface);
        }

        /* (non-Javadoc)
         * @see javax.sql.DataSource#getConnection()
         */
        public Connection getConnection() throws SQLException {
            // Use JDBC DriverManager as we may not rely on JCD to be sufficiently
            // initialized to use any of the ConnectionFactories.
            try {
                // loads the driver - NB call to newInstance() added to force initialisation
                ClassHelper.getClass(jcd.getDriver(), true);
                String url = jcd.getProtocol() + ":" + jcd.getSubProtocol() + ":" + jcd.getDbAlias();
                if (jcd.getUserName() == null)
                {
                    return DriverManager.getConnection(url);
                }
                else
                {
                    return DriverManager.getConnection(url, jcd.getUserName(), jcd.getPassWord());
                }
            }
            catch (ClassNotFoundException e)
            {
                throw (IllegalStateException)
                    (new IllegalStateException (e.getMessage ())).initCause (e);
            }
        }
        
        /**
         * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
         */
        public Connection getConnection(String username, String password)
                throws SQLException {
            return getConnection ();
        }

        /**
         * @see javax.sql.DataSource#getLoginTimeout()
         */
        public int getLoginTimeout() throws SQLException 
        {
            return 0;
        }

        /**
         * @see javax.sql.DataSource#getLogWriter()
         */
        public PrintWriter getLogWriter() throws SQLException {
            return null;
        }

        /**
         * @see javax.sql.DataSource#setLoginTimeout(int)
         */
        public void setLoginTimeout(int seconds) throws SQLException {
        }

        /**
         * @see javax.sql.DataSource#setLogWriter(java.io.PrintWriter)
         */
        public void setLogWriter(PrintWriter out) throws SQLException {
        }

    }

	public Connection getConnection() throws SQLException {
		if(externalDs != null)
		{
			return externalDs.getConnection();
		}
		else
		{
		   return super.getConnection();
		}
	}
	
	public Connection getConnection(String username, String password)
			throws SQLException {
		
		if(externalDs != null)
		{
			return externalDs.getConnection(username, password);
		}
		else
		{
		   return super.getConnection(username, password);
		}		
	}

}
