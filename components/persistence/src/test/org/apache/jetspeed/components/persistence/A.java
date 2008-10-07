/*
 * Created on Mar 2, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components.persistence;

import java.util.Collection;

import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * <p>
 * A
 * </p>
 *
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $ $
 *
 */
public class A implements AInf
{
    private Collection bList;
    private int id;
    private String name;
    /**
     * @return Returns the bList.
     */
    public Collection getBList()
    {
        return bList;
    }

    /**
     * @param list The bList to set.
     */
    public void setBList(Collection list)
    {
        bList = list;
    }

    /**
     * @return Returns the id.
     */
    public int getId()
    {
        return id;
    }

    /**
     * @param id The id to set.
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if (obj != null && obj.getClass().equals(getClass()))
        {
            A pd = (A) obj;
            boolean sameId = (id != 0 && id == pd.id);
            if (sameId)
            {
                return true;
            }            
            boolean sameName = (pd.getName() != null && name != null && pd.getName().equals(name));
            return sameName;
        }
        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        HashCodeBuilder hasher = new HashCodeBuilder(1, 99);
        hasher.append(name);
       
        return hasher.toHashCode();
    }

}
