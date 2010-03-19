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

import org.apache.directory.server.core.DefaultDirectoryService;
import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.core.entry.ServerEntry;
import org.apache.directory.server.core.partition.Partition;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.store.LdifFileLoader;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.apache.directory.shared.ldap.name.LdapDN;

/**
 * @version $Id$
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 *
 */
public class EmbeddedApacheDSTestService
{
    private DirectoryService service;
    private LdapServer server;
    private boolean running;
    private File workingDir;
    private int port;
    private String baseDN;
    private long changeLogRevision;
    
    public EmbeddedApacheDSTestService(String baseDN, int port, File workingDir)
    {
        this.baseDN = baseDN;
        this.port = port;
        this.workingDir = workingDir;
    }
    
    public boolean isRunning()
    {
        return running;
    }
    
    public void start() throws Exception
    {
        if (workingDir.exists() && !deleteDir(workingDir))
        {
            throw new Exception("Cannot delete apacheds working Directory: "+workingDir.getAbsolutePath());
        }
        
        // Initialize the LDAP service
        service = new DefaultDirectoryService();
        
        // Disable the ChangeLog system
        service.getChangeLog().setEnabled( true );
        service.setDenormalizeOpAttrsEnabled( true );
        
        Partition partition = new JdbmPartition();
        partition.setId( "foo" );
        partition.setSuffix( baseDN );
        service.addPartition( partition );
        
        service.setWorkingDirectory(workingDir);
        server = new LdapServer();
        server.setDirectoryService(service);
        server.setTransports(new  TcpTransport(port));
        service.startup();
        server.start();
        
        // Inject the sevenSeas root entry if it does not already exist
        if (!service.getAdminSession().exists(partition.getSuffixDn()))
        {
            LdapDN dn = new LdapDN( baseDN );
            ServerEntry entry = service.newEntry( dn );
            entry.add( "objectClass", "top", "domain", "extensibleObject" );
            entry.add( "dc", "foo" );
            service.getAdminSession().add( entry );
        }
        running = true;
        changeLogRevision = service.getChangeLog().getCurrentRevision();
    }
    
    public void stop() throws Exception
    {
        server.stop();
        service.shutdown();
        server = null;
        service = null;
        if (workingDir.exists())
        {
            deleteDir(workingDir);
        }
        running = false;
    }
    
    public void loadLdif(File ldif) throws Exception
    {
        LdifFileLoader loader = new  LdifFileLoader(service.getAdminSession(), ldif.getAbsolutePath());
        loader.execute();
    }
    
    public void revert() throws Exception
    {
        if (changeLogRevision < service.getChangeLog().getCurrentRevision())
        {
            changeLogRevision = service.revert(changeLogRevision);
        }
    }

    private static boolean deleteDir(File dir)
    {        
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i=0; i < children.length; i++)
            {
                if (!deleteDir(new File(dir, children[i])))
                {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
