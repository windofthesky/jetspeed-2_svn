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
package org.apache.jetspeed.ajax;

import java.io.Reader;
import java.io.Writer;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

/**
 * Response object used for AJAX services.
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 *
 */
public class AJAXResponseImpl implements AJAXResponse
{   

    private Context context;
    private VelocityEngine engine;
    private Reader template;
    private Writer output;

    public AJAXResponseImpl(Context context, VelocityEngine engine, Reader template, Writer output)
    {
        this.context = context;
        this.engine = engine;
        this.template = template;
        this.output = output;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.ajax.AJAXResponse#complete()
     */
    public void complete() throws AJAXException
    {
        try
        {
            engine.evaluate(context, output, "AJAX processor", template);
        }
        catch (Exception e)
        {
            throw new AJAXException("Failed to render velocity xml template: "+e.getMessage(), e);
        }
     
    }

    

}
