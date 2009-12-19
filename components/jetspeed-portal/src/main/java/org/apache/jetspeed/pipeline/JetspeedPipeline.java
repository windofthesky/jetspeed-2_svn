/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.apache.jetspeed.pipeline.valve.CleanupValve;
import org.apache.jetspeed.pipeline.valve.Valve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;

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
     * The set of CleanupValves associated with this Pipeline.
     */
    protected Valve[] cleanupValves;

    /**
     * Constructor that provides the descriptor for building the pipeline
     */
    public JetspeedPipeline(String name, List valveList) throws Exception
    {
        // split valves into cleanup and normal valves lists
        List valvesList = new ArrayList();
        List cleanupValvesList = new ArrayList();
        Iterator valveIter = valveList.iterator();
        while (valveIter.hasNext())
        {
            Valve valve = (Valve)valveIter.next();
            if (valve instanceof CleanupValve)
            {
                cleanupValvesList.add(valve);
            }
            else
            {
                valvesList.add(valve);
            }
        }
        // configure pipeline
        valves = (Valve[]) valvesList.toArray(new Valve[valvesList.size()]);
        cleanupValves = (Valve[]) cleanupValvesList.toArray(new Valve[cleanupValvesList.size()]);
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
        if (valve instanceof CleanupValve)
        {
            cleanupValves = appendToValveArray(cleanupValves, valve);
        }
        else
        {
            valves = appendToValveArray(valves, valve);
        }
    }

    public synchronized Valve[] getValves()
    {
        return copyValveArray(valves);
    }

    public synchronized Valve[] getCleanupValves()
    {
        return copyValveArray(cleanupValves);
    }

    public synchronized void removeValve(Valve valve)
    {
        // Remove this Valve to the set associated with this Pipeline
        if (valve instanceof CleanupValve)
        {
            cleanupValves = removeFromValveArray(cleanupValves, valve);
        }
        else
        {
            valves = removeFromValveArray(valves, valve);
        }
    }

    public void invoke(RequestContext request) throws PipelineException
    {
        try
        {
            Invocation invocation;
            synchronized (this)
            {
                invocation = new Invocation(valves);
            }
            // Invoke the first Valve in this pipeline for this request
            invocation.invokeNext(request);
        }
        finally
        {
            // Invoke all cleanup valves swallowing any thrown exceptions
            // for this request
            Valve[] invokeCleanupValves;
            synchronized (this)
            {
                invokeCleanupValves = copyValveArray(cleanupValves);
            }
            for (int i = 0; (i < invokeCleanupValves.length); i++)
            {
                Invocation cleanupInvocation = new Invocation(invokeCleanupValves[i]);
                try
                {
                    cleanupInvocation.invokeNext(request);
                }
                catch (Throwable t)
                {
                }
            }
        }
    }
    
    private static Valve[] copyValveArray(Valve[] array)
    {
        Valve[] newArray = new Valve[array.length];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }

    private static Valve[] appendToValveArray(Valve[] array, Valve valve)
    {
        Valve[] newArray = new Valve[array.length+1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = valve;
        return newArray;
    }

    private static Valve[] removeFromValveArray(Valve[] array, Valve valve)
    {
        int index = -1;
        for (int i = 0; ((i < array.length) && (index == -1)); i++)
        {
            index = ((array[i] == valve) ? i : -1);
        }
        if (index != -1)
        {
            Valve[] newArray = new Valve[array.length-1];
            System.arraycopy(array, 0, newArray, 0, index);
            System.arraycopy(array, index+1, newArray, index, array.length-index-1);
            return newArray;
        }
        return array;
    }

    private static final class Invocation implements ValveContext
    {
        private final Valve[] valves;

        private int at = 0;

        public Invocation(Valve valve)
        {
            this.valves = new Valve[]{valve};
        }

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