/*
 * Created on Mar 2, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components.persistence;
import java.util.Collection;

/**
 * <p>
 * AInf
 * </p>
 *
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $ $
 *
 */
public interface AInf
{

    /**
     * @return Returns the bList.
     */
    public abstract Collection getBList()
    ;

    /**
     * @param list The bList to set.
     */
    public abstract void setBList(Collection list)
    ;

    /**
     * @return Returns the id.
     */
    public abstract int getId()
    ;

    /**
     * @param id The id to set.
     */
    public abstract void setId(int id)
    ;

    /**
     * @return Returns the name.
     */
    public abstract String getName()
    ;

    /**
     * @param name The name to set.
     */
    public abstract void setName(String name)
    ;
}