/**
 * Created on Jul 7, 2003
 *
 * 
 * @author
 */
package org.apache.jetspeed.services.persistence;

import org.odmg.OQLQuery;
import org.odmg.Transaction;

/**
 * ODMGPersistencePlugin
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface ODMGPersistencePlugin extends PersistencePlugin
{
		Transaction newODMGTransaction();
		
		OQLQuery newOQLQuery();		
}
