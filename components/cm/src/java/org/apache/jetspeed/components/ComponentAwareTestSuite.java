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
package org.apache.jetspeed.components;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

/**
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class ComponentAwareTestSuite extends TestSuite
{
    private MutablePicoContainer container;
    private String script;
    private ComponentManager cm;


    /**
     *  
     */
    public ComponentAwareTestSuite()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     * @param arg1
     */
    public ComponentAwareTestSuite(Class arg0, String arg1)
    {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
        
    }

    /**
     * @param arg0
     */
    public ComponentAwareTestSuite(Class arg0)
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public ComponentAwareTestSuite(String arg0)
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }
    
    protected void buildContainer(String script) throws ClassNotFoundException
    {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream is = cl.getResourceAsStream(script);
        if (is == null)
        {
            throw new ClassNotFoundException("script not found: " + script);
        }
        Reader scriptReader = new InputStreamReader(is);
        
        cm = new ComponentManager(scriptReader, ComponentManager.GROOVY);
        
        ObjectReference containerRef = new SimpleReference();
        cm.getContainerBuilder().buildContainer(containerRef, null, "TEST_REGISTRY");
        container = (MutablePicoContainer) containerRef.get();
                
    }
        
    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.Test#run(junit.framework.TestResult)
     */
    public void run(TestResult arg0)
    {
        try
        {
            
            buildContainer(script);
            super.run(arg0);
            if (container != null)
            {
                container.stop();
            }
        }
        catch (Exception e)
        {
            if (container != null)
            {
                container.stop();
            }
            
            e.printStackTrace();
            
             throw new RuntimeException(e);            
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestSuite#runTest(junit.framework.Test,
     *      junit.framework.TestResult)
     */
    public void runTest(Test arg0, TestResult arg1)
    {
        // TODO Auto-generated method stub
        if (arg0 instanceof AbstractComponentAwareTestCase)
        {
            ((AbstractComponentAwareTestCase) arg0).setContainer(container);
            ((AbstractComponentAwareTestCase) arg0).setComponentManager(cm);
        }
        super.runTest(arg0, arg1);
    }

    /**
     * @return Returns the script.
     */
    public String getScript()
    {
        return script;
    }

    /**
     * @param script
     *            The script to set.
     */
    public void setScript(String script)
    {
        this.script = script;
    }
}
