/*
 * Copyright 2000-2005 The Apache Software Foundation.
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
package org.apache.jetspeed.tools.deploy;

/**
 * Factory component used to create JetspeedDeploy instances
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class JetspeedDeployFactory implements DeployFactory
{
    /**
     * JetspeedDeployFactory
     */
    public JetspeedDeployFactory()
    {
    }

    /**
     * getInstance
     *
     * @param inputWarPath
     * @param outputWarPath
     * @param stripLoggers
     * @return JetspeedDeploy instance
     */
    public Deploy getInstance(String inputWarPath, String outputWarPath, boolean stripLoggers) throws Exception
    {
        return new JetspeedDeploy(inputWarPath, outputWarPath, stripLoggers);
    }
}
