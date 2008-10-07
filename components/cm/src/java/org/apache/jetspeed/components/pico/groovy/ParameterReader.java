/*
 * Created on Apr 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components.pico.groovy;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public class ParameterReader
{
    private PicoContainer fieldContainer;
    private Parameter[] fieldParameters;

    public ParameterReader(Parameter[] inParameters, PicoContainer inContainer)
    {
        fieldContainer = inContainer;
        fieldParameters = inParameters;
    }
    
    public Object getValue(int index, Class expectedType)
    {
        ComponentAdapter adp = fieldParameters[index].resolveAdapter(fieldContainer, expectedType);
        if(adp != null)
        {
            return adp.getComponentInstance();
        }
        else
        {
            return null;
        }
    }

}
