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
package org.apache.jetspeed.services.plugin;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.BaseService;

/**
 * PathResolver implementation that is backed  by
 * <code>org.apache.fulcrum.BaseService.getRealPath(String)</code>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 */
public class FulcrumServicePathResloverImpl implements PathResolver
{
    private BaseService service;
    private static final Log log = LogFactory.getLog(FulcrumServicePathResloverImpl.class);

    public FulcrumServicePathResloverImpl()
    {
        super();
    }

    /**
     * Uses the supplied Fulcrum service to resolve resource pathes.
     * @param fulcrumService
     */

    public FulcrumServicePathResloverImpl(BaseService fulcrumService)
    {
        this();
        service = fulcrumService;
    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PathResolver#getRealPath(java.lang.String)
     */
    public String getRealPath(String path)
    {
        String path2 = null;
        try
        {
            //path2 = new File(path1).getCanonicalPath();
            path2 = new File(service.getRealPath(path)).getCanonicalPath();
        }
        catch (IOException e)
        {
            log.error("Path resolution encountered an IOException when attempting to resolve the path, " + path, e);
        }
        return path2;
    }

    /**
     * Sets the Fulcrum service to use for path resolution;
     * @param fulcrumService Fulcrum service to use for path resolution;
     */
    public void setBaseService(BaseService fulcrumService)
    {
        service = fulcrumService;
    }

}
