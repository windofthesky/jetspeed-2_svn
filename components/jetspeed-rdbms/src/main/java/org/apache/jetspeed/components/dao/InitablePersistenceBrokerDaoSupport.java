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
package org.apache.jetspeed.components.dao;

import java.net.URL;

import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.MetadataManager;
import org.apache.ojb.broker.metadata.RepositoryPersistor;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * <p>
 * InitablePersistenceBrokerDaoSupport
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id: InitablePersistenceBrokerDaoSupport.java 224631 2005-07-24 17:03:46Z taylor $
 *
 */
public class InitablePersistenceBrokerDaoSupport extends PersistenceBrokerDaoSupport
{

    protected String repositoryPath;
    /**
     * 
     */
    public InitablePersistenceBrokerDaoSupport(String repositoryPath)
    {
        super();
        this.repositoryPath = repositoryPath;
        
    }
    
    
    /**
     * 
     * <p>
     * init
     * </p>
     * Loads the correct repository descriptor for InitablePersistenceBrokerDaoSupport
     *
     * @see org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport
     * @throws Exception
     */
    public void init() throws Exception
    {
        MetadataManager metaManager = MetadataManager.getInstance();
        RepositoryPersistor persistor = new RepositoryPersistor();
        URL descriptorUrl = getClass().getClassLoader().getResource(repositoryPath);

        logger.info("Merging OJB respository "+descriptorUrl+" for DAO class "+getClass().getName());
        DescriptorRepository repo = persistor.readDescriptorRepository(descriptorUrl.openStream());
        metaManager.mergeDescriptorRepository(repo);
    }

}
