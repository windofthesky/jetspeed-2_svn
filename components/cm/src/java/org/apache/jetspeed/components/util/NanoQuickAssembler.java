/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.components.util;

import java.io.InputStreamReader;
import java.io.Reader;

import org.nanocontainer.NanoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

/**
 * <p>
 * NanoQuickAssembler
 * </p>
 * 
 * Simple uitlity for use in multi-script assemblies.
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $ $
 *  
 */
public class NanoQuickAssembler
{
    /**
     * 
     * <p>
     *  assemble
     * </p>
     * 
     * Utility method for assembling a script-based 
     * containers on fly.  Best used within aggregate scripts 
     * for easily merging other scripts into one, larger aggregate
     * container.
     * 
     * @param cl ClassLoader that will be used to assmble this container
     * @param scriptName Script that will be used to assemble this container
     * @param parent Parent PicoContiner for this container
     * @param scope String defining the scope of this container
     * @return MutablePicoContainer fully assmbled and start MutablePicoContainer
     * @throws ClassNotFoundException
     */
    public static MutablePicoContainer assemble(ClassLoader cl,
            String scriptName, PicoContainer parent, String scope) throws ClassNotFoundException
    {
        Reader scriptReader = new InputStreamReader(cl.getResourceAsStream(scriptName));
        ObjectReference parentRef = new SimpleReference();
        ObjectReference containerRef = new SimpleReference();
        NanoContainer nano = new NanoContainer(scriptReader, NanoContainer.GROOVY ,parent, cl);
        nano.getContainerBuilder().buildContainer(containerRef, parentRef, scope);
        return (MutablePicoContainer) containerRef.get();
    }
    
    /**
     * 
     * <p>
     *  assemble
     * </p>
     * 
     * same as {@link assemble(ClassLoader, String, PicoContainer, String)}
     * accept it uses a default scope of "jetspeed".
     * 
     * @param cl
     * @param scriptName
     * @param parent
     * @return
     * @throws ClassNotFoundException
     */
    public static MutablePicoContainer assemble(ClassLoader cl,
            String scriptName, PicoContainer parent) throws ClassNotFoundException
    {
       return assemble(cl, scriptName, parent, "jetspeed");
    }

}
