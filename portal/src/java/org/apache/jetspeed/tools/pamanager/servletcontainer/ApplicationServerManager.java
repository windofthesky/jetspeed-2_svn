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
package org.apache.jetspeed.tools.pamanager.servletcontainer;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpException;

/**
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 *
 */
public interface ApplicationServerManager
{
    String start( String appPath ) throws HttpException, IOException;

    String stop( String appPath ) throws HttpException, IOException;

    String reload( String appPath ) throws HttpException, IOException;

    String remove( String appPath ) throws HttpException, IOException;

    String install( String warPath, String contexPath ) throws HttpException, IOException;

    String deploy( String appPath, InputStream is, int size ) throws HttpException, IOException;

    /**
     * @return
     */
    int getHostPort();

    /**
     * @return
     */
    String getHostUrl();
    
    boolean isConnected();
}