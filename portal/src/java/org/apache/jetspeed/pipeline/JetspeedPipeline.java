/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.pipeline;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.descriptor.PipelineDescriptor;
import org.apache.jetspeed.descriptor.ValveDescriptor;
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
    public JetspeedPipeline()
    throws Exception
    {
    }
    
    /**
     * <p>Set the descriptor used to create this pipeline.</p>
     */
    public void setDescriptor(PipelineDescriptor descriptor)
    {
        this.descriptor = descriptor;
    }
    
    /**
     * <p>Get the descriptor used to create this pipeline.</p>
     */
    public PipelineDescriptor getDescriptor()
    {
        return descriptor;
    }
    
    /**
     * @see org.apache.plexus.summit.Pipeline#init()
     */
    public void initialize()
    throws PipelineException
    {
        setName(getDescriptor().getName());
        ArrayList valveDescriptors = (ArrayList) getDescriptor().getValveDescriptors();
        
        for (int i=0; i<valveDescriptors.size(); i++)
        {
            ValveDescriptor vDescriptor = (ValveDescriptor) valveDescriptors.get(i);
            String className = vDescriptor.getClassName();
            
            log.info("Adding Valve: " + className);
            
            Valve valve;
            
            try
            {
                valve = (Valve) Class.forName(className).newInstance();
            }
            catch (Exception e)
            {
                throw new PipelineException("Failed to create valve: " + className);
            }
            
            addValve(valve);
        }
        
        // Valve implementations are added to this Pipeline using the
        // Mapper.
        
        // Initialize the valves
        for (int i = 0; i < valves.length; i++)
        {
            //valves[i].setApplicationView(getApplicationView());
            valves[i].initialize();
        }
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