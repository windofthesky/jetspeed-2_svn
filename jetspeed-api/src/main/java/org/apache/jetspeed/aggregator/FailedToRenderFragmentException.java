/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.jetspeed.aggregator;

import org.apache.jetspeed.exception.JetspeedException;

/**
 * Failed to Render Fragment exceptions denote error cases where a particular fragment failed to render during
 * content aggregation process.
 *
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 */
public class FailedToRenderFragmentException extends JetspeedException {

    /**
     *
     */
    public FailedToRenderFragmentException() {
        super();
    }

    /**
     * @param message
     */
    public FailedToRenderFragmentException(String message) {
        super(message);
    }

    /**
     * @param nested
     */
    public FailedToRenderFragmentException(Throwable nested) {
        super(nested);
    }

    /**
     * @param msg
     * @param nested
     */
    public FailedToRenderFragmentException(String msg, Throwable nested) {
        super(msg, nested);
    }

}
