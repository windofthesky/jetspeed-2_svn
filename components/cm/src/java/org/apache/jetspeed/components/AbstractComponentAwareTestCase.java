/*
 * Created on Feb 22, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components;

import junit.framework.TestCase;

/**
 * @author Sweaver
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public abstract class AbstractComponentAwareTestCase extends TestCase
{
    private ComponentManager ncm;
    
    /**
     * @param arg0
     */
    public AbstractComponentAwareTestCase(String arg0)
    {
        super(arg0);
    }
    /**
     * @return Returns the ncm.
     */
    public ComponentManager getComponentManager()
    {
        return ncm;
    }

    /**
     * @param ncm The ncm to set.
     */
    public void setComponentManager(ComponentManager ncm)
    {
        this.ncm = ncm;
    }



}
