/* Copyright 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.trace;

import java.util.logging.*;

/**
 * @author <a href="">David Le Strat</a>
 *
 */
public aspect PerformanceTrace {

    pointcut publicMethodCall() : call(public * org.apache.jetspeed.security..*(..));

    /** 
     * <p>This is the advice code on the defined set of pointcuts in
     * publicMethodCall() - definition.</p>
     * <p>Wrap the method call(s) so we can log that it is about to be run,
     * the parameters it will run with and the result we get from it.
     * "around" is instead of the method call, alternatives are
     * "before" or "after".</p>
     */
    Object around() : publicMethodCall()
    {
        long startTime = System.currentTimeMillis();

        // Execute the wrapped method and catch the result
        Object result = proceed();

        long endTime = System.currentTimeMillis();
        long diff = endTime - startTime;
        if (diff > 100)
        {
            System.out.println("[INFO] " + thisJoinPoint);
            System.out.println("[INFO] Execution time: " + diff);
            System.out.println("[INFO] Arguments: " + thisJoinPoint.getArgs());
            if (result != null)
            {
                System.out.println("[INFO] Result: " + thisJoinPoint + ": " + result.toString());
            }
        }

        return result;
    }

}
