/**
 * Created on Jan 26, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.components.persistence.store.impl;

import java.util.Collection;
import java.util.Iterator;
import org.apache.jetspeed.components.persistence.store.Filter;


/**
 * <p>
 * JDOFilter
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class JDOFilter implements Filter
{
 
    
    private StringBuffer filter;
    private String ordering;

    public JDOFilter()
    {
   
        this.filter = new StringBuffer();
    }

    /**
     * @see org.apache.jetspeed.persistence.LookupCriteria#addBetween(java.lang.String, java.lang.Object, java.lang.Object)
     */
    public void addBetween(String arg0, Object arg1, Object arg2)
    {
        addAnd();
        filter.append(arg0 + " > " + arg1 + " && " + arg0 + " < " + arg2);
    }

    /**
     * @see org.apache.jetspeed.persistence.LookupCriteria#addEqualTo(java.lang.String, java.lang.Object)
     */
    public void addEqualTo(String arg0, Object arg1)
    {
        addAnd();
        filter.append(arg0 + " == " + arg1);
    }

    /**
     * @see org.apache.jetspeed.persistence.LookupCriteria#addGreaterOrEqualThan(java.lang.String, java.lang.Object)
     */
    public void addGreaterOrEqualThan(String arg0, Object arg1)
    {
        addAnd();
        filter.append(arg0 + " >= " + arg1);

    }

    /**
     * @see org.apache.jetspeed.persistence.LookupCriteria#addGreaterThan(java.lang.String, java.lang.Object)
     */
    public void addGreaterThan(String arg0, Object arg1)
    {
        addAnd();
        filter.append(arg0 + " > " + arg1);

    }

    /**
     * @see org.apache.jetspeed.persistence.LookupCriteria#addIn(java.lang.String, java.util.Collection)
     */
    public void addIn(String attribute, Collection values)
    {
        addAnd();
        Iterator itr = values.iterator();
        filter.append("(");
        String or = "";
        while (itr.hasNext())
        {
            filter.append(or);
            filter.append(attribute + " == " + itr.next());
            or = " || ";
        }
        filter.append(")");
    }

    /**
     * @see org.apache.jetspeed.persistence.LookupCriteria#addLessOrEqualThan(java.lang.String, java.lang.Object)
     */
    public void addLessOrEqualThan(String arg0, Object arg1)
    {
        addAnd();
        filter.append(arg0 + " >= " + arg1);
    }

    /**
     * @see org.apache.jetspeed.persistence.LookupCriteria#addLike(java.lang.String, java.lang.Object)
     */
    public void addLike(Object arg0, Object arg1)
    {
        addAnd();
        boolean both = false;

        if (arg1.toString().startsWith("*"))
        {
            String value = arg1.toString().substring(1);
            if (arg1.toString().endsWith("*"))
            {
                value = value.substring(0, value.length() - 1);
            }

            filter.append(arg0 + ".endsWith(\"" + value + "\")");
            both = true;            
        }

        if (arg1.toString().endsWith("*"))
        {
        	if(both)
        	{
        		addAnd();
        	}
        	
            String value = arg1.toString().substring(0, arg1.toString().length() - 1);
            if (arg1.toString().startsWith("*"))
            {
				value = arg1.toString().substring(1);
            }
            filter.append(arg0 + ".startsWith(\"" + value + "\")");
        }

    }

    /**
     * @see org.apache.jetspeed.persistence.LookupCriteria#addNotBetween(java.lang.String, java.lang.Object, java.lang.Object)
     */
    public void addNotBetween(String arg0, Object arg1, Object arg2)
    {
		addAnd();
        filter.append(arg0 + " < " + arg1 + " || " + arg0 + " > " + arg2);
	   

    }

    /**
     * @see org.apache.jetspeed.persistence.LookupCriteria#addNotEqualTo(java.lang.String, java.lang.Object)
     */
    public void addNotEqualTo(String arg0, Object arg1)
    {
		addAnd();
		filter.append(arg0 + " != " + arg1);

    }

    /**
     * @see org.apache.jetspeed.persistence.LookupCriteria#addNotLike(java.lang.String, java.lang.Object)
     */
    public void addNotLike(String arg0, Object arg1)
    {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.apache.jetspeed.persistence.LookupCriteria#addNotNull(java.lang.String)
     */
    public void addNotNull(String arg0)
    {
        addAnd();
        filter.append(arg0+" != null");

    }

    /**
     * @see org.apache.jetspeed.persistence.LookupCriteria#addOrCriteria(org.apache.ojb.broker.query.Criteria)
     */
    public void addOrFilter(Filter arg0)
    {		
		filter.append(" || ("+arg0.toString()+")"); 
    }

    /**
     * @see org.apache.jetspeed.persistence.LookupCriteria#addOrderByAscending(java.lang.String)
     */
    public void addOrderByAscending(String arg0)
    {
        ordering = arg0+" ascending";

    }

    /**
     * @see org.apache.jetspeed.persistence.LookupCriteria#addOrderByDescending(java.lang.String)
     */
    public void addOrderByDescending(String arg0)
    {
		ordering = arg0+" descending";

    }

    private void addAnd()
    {
        if (filter.length() > 0)
        {
            filter.append(" && ");
        }
    }

    /**
     * @see java.lang.Object#toString()
     * 
     * @return A string that is usable as a JDO OQL query
     */
    public String toString()
    {        
        return filter.toString();
    }
    
    public String getOrderingString()
    {
    	return ordering;
    }

}
