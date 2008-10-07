/*
 * Created on Apr 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components;

import java.io.IOException;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public interface ContainerManager
{
    PicoContainer assembleContainer(MutablePicoContainer container) throws IOException;
    
    ClassLoader getContainerClassLoader();

}
