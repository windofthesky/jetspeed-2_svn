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
package org.apache.jetspeed.pipeline.descriptor;

import java.util.ArrayList;
import java.util.List;

/**
 * This descriptor bean represents the configuration used to create a
 * Summit <code>Pipeline</code>.
 *
 * @author <a href="mailto:john@zenplex.com">John Thorhauer</a>
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @version $Id$
 */
public class PipelineDescriptor
    extends BaseDescriptor
{
    /**
     * List of valve descriptors
     */
    private List valveDescriptors;

    /**
     * Default contructor
     */
    public PipelineDescriptor()
    {
        valveDescriptors = new ArrayList();
    }

    /**
     * Add a ValveDescriptor to the Pipeline
     * descriptor
     *
     * @param ValveDescriptor
     */
    public void addValveDescriptor(ValveDescriptor valveDescriptor)
    {
        valveDescriptors.add(valveDescriptor);
    }

    /**
     * Return a list of ValveDesccriptors
     *
     * @return List of ValveDesccriptors
     */
    public List getValveDescriptors()
    {
        return this.valveDescriptors;
    }
}
