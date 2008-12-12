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
package org.apache.jetspeed.util.interceptors;

import org.aopalliance.intercept.MethodInvocation;

/**
 * A interface which is akin to a <B>gateway</B> in BPMN notation. Concrete
 * implementations can make a decision as to whether or not a method invocation
 * should be replayed.
 * 
 * @author a336317
 */
public interface MethodReplayDecisionMaker
{
    /**
     * 
     * @param invocation
     *            The MethodInvocation object
     * @param exception
     *            Exception thrown on previous invocation attempt
     * @return True if we should replay the method, false otherwise
     */
    public boolean shouldReplay(MethodInvocation invocation, Exception exception);

}
