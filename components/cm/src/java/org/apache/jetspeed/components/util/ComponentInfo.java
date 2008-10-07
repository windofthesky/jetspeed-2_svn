/*
 * Created on Apr 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components.util;

import org.apache.commons.configuration.Configuration;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public interface ComponentInfo
{
    Object getComponentKey(ClassLoader cl);
    
    Class getComponentClass(ClassLoader cl) throws ClassNotFoundException;
    
    Configuration getConfiguration();
    
    boolean isSingleton();

}
