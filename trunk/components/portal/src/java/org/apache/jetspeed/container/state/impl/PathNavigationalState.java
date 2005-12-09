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
package org.apache.jetspeed.container.state.impl;

import org.apache.jetspeed.request.RequestContext;

/**
 * PathNavigationalStateContext stores all navigational state in the URL.
 * This implementation does not currently support persisting navigational state.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PathNavigationalState extends AbstractNavigationalState 
{

    public PathNavigationalState(NavigationalStateCodec codec)
    {
        super(codec);
    }

    public void sync(RequestContext context)
    {        
    }

    public boolean isNavigationalParameterStateFull()
    {
        return false;
    }

    public boolean isRenderParameterStateFull()
    {
        return false;
    }
}
