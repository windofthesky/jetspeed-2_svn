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
package org.apache.jetspeed.container.window;

import junit.framework.Test;

import org.apache.jetspeed.components.AbstractComponentAwareTestCase;
import org.apache.jetspeed.components.ContainerDeployerTestSuite;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public class TestWindowAccessComponent extends AbstractComponentAwareTestCase
{
    
 public static Test suite()
    {
        return new ContainerDeployerTestSuite(TestWindowAccessComponent.class);
    }
 
 public void testComponent() throws Exception
 {
     assertNotNull(getContainer().getComponentInstance(PortletWindowAccessor.class));
     
 }
}
