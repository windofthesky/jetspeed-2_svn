/*
 * Created on Oct 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
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
 * @version $Id$
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
