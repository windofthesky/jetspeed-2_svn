/**
 * Created on Jan 22, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.util.ojb;


import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.accesslayer.QueryCustomizer;
import org.apache.ojb.broker.metadata.CollectionDescriptor;

import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;


/**
 * <p>
 * CollectionDebugger
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class CollectionDebugger implements QueryCustomizer
{

    /**
     * @see org.apache.ojb.broker.accesslayer.QueryCustomizer#customizeQuery(java.lang.Object, org.apache.ojb.broker.PersistenceBroker, org.apache.ojb.broker.metadata.CollectionDescriptor, org.apache.ojb.broker.query.QueryByCriteria)
     */
    public Query customizeQuery(Object arg0, PersistenceBroker pb, CollectionDescriptor arg2, QueryByCriteria arg3)
    {        
        return arg3;
    }

    /**
     * @see org.apache.ojb.broker.metadata.AttributeContainer#addAttribute(java.lang.String, java.lang.String)
     */
    public void addAttribute(String arg0, String arg1)
    {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.apache.ojb.broker.metadata.AttributeContainer#getAttribute(java.lang.String, java.lang.String)
     */
    public String getAttribute(String arg0, String arg1)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.apache.ojb.broker.metadata.AttributeContainer#getAttribute(java.lang.String)
     */
    public String getAttribute(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
