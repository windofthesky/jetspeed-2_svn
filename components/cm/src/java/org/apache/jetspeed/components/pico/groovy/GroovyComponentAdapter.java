/*
 * Created on Apr 13, 2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components.pico.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;

import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.syntax.SyntaxException;
import org.picocontainer.Parameter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AbstractComponentAdapter;
import org.picocontainer.defaults.UnsatisfiableDependenciesException;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver </a>
 *  
 */
public class GroovyComponentAdapter extends AbstractComponentAdapter
{
    private static final String GROOVY_EXTENSION = ".groovy";
    private final Parameter[] parameters;

    private Script groovyScript;
    private boolean isSingleton;
    private Object instance;

    public GroovyComponentAdapter( Object componentKey, Class componentImplementation, Parameter[] parameters, boolean isSingleton )
    {
        super(componentKey, componentImplementation);
        this.parameters = parameters;
        this.isSingleton = isSingleton;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.picocontainer.ComponentAdapter#verify()
     */
    public void verify() throws UnsatisfiableDependenciesException
    {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.picocontainer.ComponentAdapter#getComponentInstance()
     */
    public Object getComponentInstance() throws PicoInitializationException, PicoIntrospectionException
    {
        try
        {
            if(isSingleton && instance != null)
            {
                return instance;
            }
            
            Binding binding = new Binding();
            binding.setVariable("adapter", this);
            binding.setVariable("picoContainer", getContainer());
            binding.setVariable("componentKey", getComponentKey());
            binding.setVariable("componentImplementation", getComponentImplementation());
            binding.setVariable("parameters", parameters != null ? Arrays.asList(parameters) : Collections.EMPTY_LIST);            
            binding.setVariable("parameterReader", parameters != null ? new ParameterReader(parameters, getContainer()) : null);

            String scriptPath = "/" + getComponentImplementation().getName().replace('.', '/') + GROOVY_EXTENSION;
            InputStream scriptIs = getComponentImplementation().getResourceAsStream(scriptPath);

            if (scriptIs == null)
            {
                throw new PicoInitializationException("Couldn't load script at path " + scriptPath);
            }

            GroovyClassLoader loader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());

            Class scriptClass = loader.parseClass(scriptIs);
            groovyScript = InvokerHelper.createScript(scriptClass, null);

            groovyScript.setBinding(binding);
            instance = groovyScript.run();
            if(instance == null)
            {
                instance = groovyScript.getBinding().getVariable("instance");
            }
                
            return instance;
            
        }
        catch (SyntaxException e)
        {
            throw new PicoInitializationException(e);
        }
        catch (IOException e)
        {
            throw new PicoInitializationException(e);
        }
    }

}