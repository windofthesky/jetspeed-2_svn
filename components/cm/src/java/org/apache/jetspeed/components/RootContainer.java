/*
 * Created on Apr 25, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components;

import org.picocontainer.MutablePicoContainer;

/**
 * @author Scott Weaver
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface RootContainer
{

    /**
     * @return Returns the script.
     */
    public abstract String[] getApplicationFolders();

    /**
     * @param script
     *            The script to set.
     */
    public abstract void setApplicationFolders(String[] appFolders);

    /**
     * @return Returns the container.
     */
    public abstract MutablePicoContainer getContainer();

    /**
     * @param container The container to set.
     */
    public abstract void setContainer(MutablePicoContainer container);
}