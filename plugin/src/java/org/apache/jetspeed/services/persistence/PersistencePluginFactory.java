/**
 * Created on Jul 3, 2003
 *
 * 
 * @author
 */
package org.apache.jetspeed.services.persistence;

import org.apache.jetspeed.commons.service.ServiceFactory;


/**
 * PluginFactory
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class PersistencePluginFactory
{
    public static PersistencePlugin getPlugin(String name)
    {
        // PersistenceService pServ = (PersistenceService) ServiceUtil.getServiceByName(PersistenceService.SERVICE_NAME);
        PersistenceService pServ = (PersistenceService) ServiceFactory.getInstance().getService(PersistenceService.SERVICE_NAME);
        return pServ.getPersistencePlugin(name);
    }
}
