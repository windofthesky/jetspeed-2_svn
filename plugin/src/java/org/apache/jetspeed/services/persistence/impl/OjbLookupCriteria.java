/**
 * Created on Jun 16, 2003
 *
 * 
 * @author
 */
package org.apache.jetspeed.services.persistence.impl;

import org.apache.jetspeed.services.persistence.LookupCriteria;
import org.apache.ojb.broker.query.Criteria;

/**
 *
 * Wraps an <code>org.apache.ojb.broker.query.Criteria</code> object
 * to provide the required functionallity for implementing <code>LookupCriteria</code>
 * interface. 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 */
public class OjbLookupCriteria implements LookupCriteria
{
	private Criteria ojbCriteria;
	
	public OjbLookupCriteria()
	{
		ojbCriteria = new Criteria();
	}

    /**
     * @see org.apache.ojb.broker.query.Criteria#addBetween(java.lang.String, java.lang.Object, java.lang.Object)
     */
    public void addBetween(String arg0, Object arg1, Object arg2)
    {
        
        ojbCriteria.addBetween(arg0, arg1, arg2);
    }

    /**
     * @see org.apache.ojb.broker.query.Criteria#addEqualTo(java.lang.String, java.lang.Object)
     */
    public void addEqualTo(String arg0, Object arg1)
    {
        ojbCriteria.addEqualTo(arg0, arg1);
    }

    /**
     * @see org.apache.ojb.broker.query.Criteria#addGreaterOrEqualThan(java.lang.String, java.lang.Object)
     */
    public void addGreaterOrEqualThan(String arg0, Object arg1)
    {
        ojbCriteria.addGreaterOrEqualThan(arg0, arg1);
    }

    /**
     * @see org.apache.ojb.broker.query.Criteria#addGreaterThan(java.lang.String, java.lang.Object)
     */
    public void addGreaterThan(String arg0, Object arg1)
    {
        ojbCriteria.addGreaterThan(arg0, arg1);
    }

    /**
     * @see org.apache.ojb.broker.query.Criteria#addLessOrEqualThan(java.lang.String, java.lang.Object)
     */
    public void addLessOrEqualThan(String arg0, Object arg1)
    {        
        ojbCriteria.addLessOrEqualThan(arg0, arg1);
    }

    /**
     * @see org.apache.ojb.broker.query.Criteria#addLike(java.lang.String, java.lang.Object)
     */
    public void addLike(String arg0, Object arg1)
    {        
        ojbCriteria.addLike(arg0, arg1);
    }

    /**
     * @see org.apache.ojb.broker.query.Criteria#addNotBetween(java.lang.String, java.lang.Object, java.lang.Object)
     */
    public void addNotBetween(String arg0, Object arg1, Object arg2)
    {        
        ojbCriteria.addNotBetween(arg0, arg1, arg2);
    }

    /**
     * @see org.apache.ojb.broker.query.Criteria#addNotEqualTo(java.lang.String, java.lang.Object)
     */
    public void addNotEqualTo(String arg0, Object arg1)
    {        
        ojbCriteria.addNotEqualTo(arg0, arg1);
    }

    /**
     * @see org.apache.ojb.broker.query.Criteria#addNotLike(java.lang.String, java.lang.Object)
     */
    public void addNotLike(String arg0, Object arg1)
    {        
        ojbCriteria.addNotLike(arg0, arg1);
    }

    /**
     * @see org.apache.ojb.broker.query.Criteria#addNotNull(java.lang.String)
     */
    public void addNotNull(String arg0)
    {        
        ojbCriteria.addNotNull(arg0);
    }

    /**
     * @see org.apache.ojb.broker.query.Criteria#addOrCriteria(org.apache.ojb.broker.query.Criteria)
     */
    public void addOrCriteria(Criteria arg0)
    {     
        ojbCriteria.addOrCriteria(arg0);
    }

    /**
     * @see org.apache.ojb.broker.query.Criteria#addOrderByAscending(java.lang.String)
     */
    public void addOrderByAscending(String arg0)
    {        
        ojbCriteria.addOrderByAscending(arg0);
    }

    /**
     * @see org.apache.ojb.broker.query.Criteria#addOrderByDescending(java.lang.String)
     */
    public void addOrderByDescending(String arg0)
    {        
        ojbCriteria.addOrderByDescending(arg0);
    }
    
    
	protected Criteria getOjbCriteria()
	{
		return ojbCriteria;
	}
}
