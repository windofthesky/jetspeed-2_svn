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
package org.apache.jetspeed.layout;

import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.om.portlet.InitParam;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.jmock.Mock;
import org.jmock.core.Constraint;
import org.jmock.core.InvocationMatcher;
import org.jmock.core.constraint.IsEqual;
import org.jmock.core.constraint.IsInstanceOf;
import org.jmock.core.matcher.InvokeAtLeastOnceMatcher;
import org.jmock.core.stub.ReturnStub;

/**
 * @version $Id$
 *
 */
public class MockPortletRegistryFactory
{
    public static PortletRegistry createMockPortletRegistry()
    {
        Mock portletRegistryMock;
        PortletRegistry portletRegistry;
        Mock portletDefMock;
        PortletDefinition portletDef;

        Mock portletSizesParamMock;
        InitParam portletSizesParam;
        
        portletRegistryMock = new Mock(PortletRegistry.class);
        portletRegistry = (PortletRegistry) portletRegistryMock.proxy();
        
        portletDefMock = new Mock(PortletDefinition.class);
        portletDef = (PortletDefinition) portletDefMock.proxy();

        portletSizesParamMock = new Mock(InitParam.class);
        portletSizesParam = (InitParam) portletSizesParamMock.proxy();

        expectAndReturn(new InvokeAtLeastOnceMatcher(), portletSizesParamMock, "getParamValue", "33%,66%");
        expectAndReturn(new InvokeAtLeastOnceMatcher(), portletRegistryMock, "getPortletDefinitionByUniqueName",new Constraint[] {new IsEqual("layout")}, portletDef);
        expectAndReturn(new InvokeAtLeastOnceMatcher(), portletRegistryMock, "getPortletDefinitionByUniqueName",new Constraint[] {new IsEqual("layout"), new IsInstanceOf(Boolean.class)}, portletDef);
        expectAndReturn(new InvokeAtLeastOnceMatcher(), portletDefMock, "getInitParam", new Constraint[] {new IsEqual("sizes")}, portletSizesParam);
        
        return portletRegistry;
    }
    
    protected static void expectAndReturn(InvocationMatcher matcher, Mock mock, String methodName, Constraint[] constraints, Object returnValue)
    {
        mock.expects(matcher).method(methodName)
                            .with(constraints)
                            .will(new ReturnStub(returnValue));
    }
    
    protected static void expectAndReturn(InvocationMatcher matcher, Mock mock, String methodName, Object returnValue)
    {
        mock.expects(matcher).method(methodName)
                            .withNoArguments()
                            .will(new ReturnStub(returnValue));
    }
}
