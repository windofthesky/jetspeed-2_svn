/* <SENSOVA-COPYRIGHT>
 * Copyright (C) 2004 Sensova, LLC.
 * All Rights Reserved. No use, copying or distribution
 * of this work may be made except with a valid agreement
 * from Sensova, LLC.
 * This notice must be included on all copies, 
 * modifications and derivatives of this work.
 * </SENSOVA-COPYRIGHT>
 * 
 * File Created: org.apache.jetspeed.trace.Trace.java
 * Creation Date: Feb 2, 2004
 *
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
