/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.components.adapters;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DecoratingComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.NotConcreteRegistrationException;

/**
 * InterceptorAdapterFactory
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: InterceptorAdapterFactory.java,v 1.2 2004/03/08 00:42:51 jford
 *                Exp $
 */
public class InterceptorAdapterFactory
        extends
            DecoratingComponentAdapterFactory
{
    private Class delegationStrategy;
    
    public InterceptorAdapterFactory()
    {
        this(new DefaultComponentAdapterFactory(), StandardDelegationStrategy.class);
    }

    public InterceptorAdapterFactory( ComponentAdapterFactory delegate,
            Class delegationStrategy )
    {
        super(delegate);
        this.delegationStrategy = delegationStrategy;
    }

    public ComponentAdapter createComponentAdapter( Object componentKey,
            Class componentImplementation, Parameter[] parameters )
            throws PicoIntrospectionException,
            AssignabilityRegistrationException,
            NotConcreteRegistrationException
    {
        return new InterceptorAdapter(super.createComponentAdapter(
                componentKey, componentImplementation, parameters), delegationStrategy);
    }

}