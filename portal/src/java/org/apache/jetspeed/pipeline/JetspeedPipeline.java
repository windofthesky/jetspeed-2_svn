/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.pipeline;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.pipeline.descriptor.PipelineDescriptor;
import org.apache.jetspeed.pipeline.valve.Valve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;

/**
 * Flexible implementation of a {@link org.apache.jetspeed.pipeline.Pipeline}.
 *
 * <br/><br/>
 * Suggested order of valves:
 * <ul>
 * <li>ContainerValve</li>
 * <li>CapabilityValve</li>
 * <li>UserProfilerValve</li>
 * <li>PageProfilerValve</li>
 * <li>ActionValve</li>
 * <li>LayoutValve</li>
 * <li>ContentValve</li>
 * <li>AggregateValve</li>
 * <li>CleanupValve</li>
 * </ul>
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedPipeline
implements Pipeline, ValveContext
{
    /** Logger */
    private Log log = LogFactory.getLog(JetspeedPipeline.class);
    
    /** Name of this pipeline. */
    protected String name;
    
    /** The set of Valves associated with this Pipeline. */
    protected Valve[] valves = new Valve[0];
    
    /**
     * The per-thread execution state for processing through this
     * pipeline.  The actual value is a java.lang.Integer object
     * containing the subscript into the <code>values</code> array, or
     * a subscript equal to <code>values.length</code> if the basic
     * Valve is currently being processed.
     *
     */
    protected ThreadLocal state = new ThreadLocal();
    
    /**
     * Descriptor for this pipeline
     */
    protected PipelineDescriptor descriptor;
    
    /**
     * Constructor that provides the descriptor for building
     * the pipeline
     */
    public JetspeedPipeline(String name, List valveList)
    throws Exception
    {
        valves = (Valve[]) valveList.toArray(new Valve[valveList.size()]);
        setName(name);
    }
    
    /**
     * @see org.apache.plexus.summit.Pipeline#init()
     */
    public void initialize()
    throws PipelineException
    {
        
       
    }
    
    /**
     * Set the name of this pipeline.
     *
     * @param name Name of this pipeline.
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    /**
     * Get the name of this pipeline.
     *
     * @return String Name of this pipeline.
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * @see org.apache.plexus.summit.Pipeline#addValve(Valve)
     */
    public void addValve(Valve valve)
    {
        // Add this Valve to the set associated with this Pipeline
        synchronized (valves)
        {
            Valve[] results = new Valve[valves.length + 1];
            System.arraycopy(valves, 0, results, 0, valves.length);
            results[valves.length] = valve;
            valves = results;
        }
    }
    
    /**
     * @see org.apache.plexus.summit.Pipeline#getValves()
     */
    public Valve[] getValves()
    {
        synchronized (valves)
        {
            Valve[] results = new Valve[valves.length];
            System.arraycopy(valves, 0, results, 0, valves.length);
            return results;
        }
    }
    
    /**
     * @see org.apache.plexus.summit.Pipeline#removeValve(Valve)
     */
    public void removeValve(Valve valve)
    {
        synchronized (valves)
        {
            // Locate this Valve in our list
            int index = -1;
            for (int i = 0; i < valves.length; i++)
            {
                if (valve == valves[i])
                {
                    index = i;
                    break;
                }
            }
            if (index < 0)
            {
                return;
            }
            
            // Remove this valve from our list
            Valve[] results = new Valve[valves.length - 1];
            int n = 0;
            for (int i = 0; i < valves.length; i++)
            {
                if (i == index)
                {
                    continue;
                }
                results[n++] = valves[i];
            }
            valves = results;
        }
    }
    
    /**
     * @see org.apache.plexus.summit.Pipeline#invoke(RequestContext)
     */
    public void invoke(RequestContext request)
    throws PipelineException
    {
        // Initialize the per-thread state for this thread
        state.set(new Integer(0));
        
        // Invoke the first Valve in this pipeline for this request
        invokeNext(request);
    }
    
    /**
     * @see org.apache.plexus.summit.ValveContext#invokeNext(RequestContext)
     */
    public void invokeNext(RequestContext request)
    throws PipelineException
    {
        // Identify the current subscript for the current request thread
        Integer current = (Integer) state.get();
        int subscript = current.intValue();
        
        if (subscript < valves.length)
        {
            // Invoke the requested Valve for the current request
            // thread and increment its thread-local state.
            state.set(new Integer(subscript + 1));
            valves[subscript].invoke(request, this);
        }
    }
}