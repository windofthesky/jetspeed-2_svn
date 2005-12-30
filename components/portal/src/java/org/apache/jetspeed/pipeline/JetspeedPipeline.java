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

import org.apache.jetspeed.pipeline.valve.Valve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;

import java.util.List;

/**
 * Flexible implementation of a {@link Pipeline}. <p/> <br/><br/> Suggested
 * order of valves:
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
public class JetspeedPipeline implements Pipeline
{

    /**
     * Name of this pipeline.
     */
    protected String name;

    /**
     * The set of Valves associated with this Pipeline.
     */
    protected Valve[] valves;

    /**
     * Constructor that provides the descriptor for building the pipeline
     */
    public JetspeedPipeline(String name, List valveList) throws Exception
    {
        valves = (Valve[]) valveList.toArray(new Valve[valveList.size()]);
        setName(name);
    }

    public void initialize() throws PipelineException
    {

    }

    /**
     * Set the name of this pipeline.
     * 
     * @param name
     *            Name of this pipeline.
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

    public synchronized void addValve(Valve valve)
    {
        // Add this Valve to the set associated with this Pipeline
        Valve[] results = new Valve[valves.length + 1];
        System.arraycopy(valves, 0, results, 0, valves.length);
        results[valves.length] = valve;
        valves = results;
    }

    public synchronized Valve[] getValves()
    {
        Valve[] results = new Valve[valves.length];
        System.arraycopy(valves, 0, results, 0, valves.length);
        return results;
    }

    public synchronized void removeValve(Valve valve)
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
        if (index < 0) { return; }

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

    public void invoke(RequestContext request) throws PipelineException
    {

        Invocation invocation;
        // TODO use java 5 locks or compare and swap if possible
        synchronized (this)
        {
            invocation = new Invocation(valves);
        }
        // Invoke the first Valve in this pipeline for this request
        invocation.invokeNext(request);
    }

    private static final class Invocation implements ValveContext
    {

        private final Valve[] valves;

        private int at = 0;

        public Invocation(Valve[] valves)
        {
            this.valves = valves;
        }

        public void invokeNext(RequestContext request) throws PipelineException
        {
            if (at < valves.length)
            {
                Valve next = valves[at];
                at++;
                next.invoke(request, this);
            }
        }
    }

}