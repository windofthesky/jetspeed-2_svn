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

/**
 * This descriptor bean represents the configuration used to create a
 * Summit <code>Valve</code>.
 *
 * @author <a href="mailto:john@zenplex.com">John Thorhauer</a>
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @version $Id$
 */
public class ValveDescriptor
    extends BaseDescriptor
{
    /** Class name of the valve. */
    private String className;

    /**
     * Default contructor
     */
    public ValveDescriptor()
    {
    }

    /**
     * This is the full package/class name of the
     * class used for the valve.
     *
     * @param s the full package/class name used for the valve
     */
    public void setClassName(String className)
    {
        this.className = className;
    }

    /**
     * @return the full package/class name used for the valve
     */
    public String getClassName()
    {
        return className;
    }
}
