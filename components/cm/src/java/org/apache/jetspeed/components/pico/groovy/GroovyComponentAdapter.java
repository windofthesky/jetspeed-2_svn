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

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.picocontainer.Parameter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AbstractComponentAdapter;
import org.picocontainer.defaults.UnsatisfiableDependenciesException;

/**
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 *  
 */
public class GroovyComponentAdapter extends AbstractComponentAdapter
{
    private static final String GROOVY_EXTENSION = ".groovy";
    private final Parameter[] parameters;

    private Script groovyScript;
    private boolean isSingleton;
    private Object instance;
    private ClassLoader cl;
    private Configuration config;

    public GroovyComponentAdapter( Object componentKey, Class componentImplementation, Parameter[] parameters, boolean isSingleton, ClassLoader cl, Configuration config )
    {
        super(componentKey, componentImplementation);
        this.config = config;
        this.parameters = parameters;
        this.isSingleton = isSingleton;
        if(cl != null)
        {
            this.cl = cl;
        }
        else
        {
            this.cl = getClass().getClassLoader();
        }
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
            binding.setVariable("config", config != null ? config : new PropertiesConfiguration());
            String scriptPath = getComponentImplementation().getName().replace('.', '/') + GROOVY_EXTENSION;
            InputStream scriptIs = this.cl.getResourceAsStream(scriptPath);

            if (scriptIs == null)
            {
                throw new PicoInitializationException("Couldn't load script at path " + scriptPath);
            }

            GroovyClassLoader loader = new GroovyClassLoader(this.cl);
                        	
            
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
        catch (Exception e)
        {
            throw new PicoInitializationException(e);
        }
     
    }

}