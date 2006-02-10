package org.apache.jetspeed.util.interceptors;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Generic aspect that will attempt to replay a method invocation if one of a
 * set of specified exceptions is thrown from its execution.
 * 
 * @author a336317
 */
public class MethodReplayInterceptor implements MethodInterceptor
{

    /** Log reference */
    private Log log = LogFactory.getLog(MethodReplayInterceptor.class);

    /** Serialization version identifier */
    private static final long serialVersionUID = -1316279974504594833L;

    /**
     * How many times we should attempt to retry the method invocation if it
     * fails
     */
    private int retryCount;

    /** How long we should wait before retrying - specified in milliseconds * */
    private int retryInterval;

    /**
     * Object which decides whether or not a method invocation should be
     * replayed
     */
    private TransactionalMethodReplayDecisionMaker replayDecisionMaker;

    /**
     * Encloses <code>super.invoke()</code> in a try/catch block, where the
     * catch block contains additional retry logic.
     */
    public Object invoke(MethodInvocation invocation) throws Throwable
    {
        // TODO Make this more elegant - this logic can be simpler
        try
        {
            return invocation.proceed();
        } catch (Exception exp)
        {

            // determine whether to retry or just throw the exception back up
            if (!this.isReplayable(invocation, exp)) { throw exp; }

            // TODO should this be at level WARN/ERROR?
            if (log.isDebugEnabled())
            {
                log
                        .debug("Invocation for method ["
                                + invocation.getMethod().toString()
                                + "] failed. Will attempt to replay method invocation ["
                                + retryCount + "] times with an interval of ["
                                + retryInterval + "] milliseconds");
            }

            int retryCounter = 1;
            Exception lastExp = null;

            while ((retryCounter < retryCount))
            {

                try
                {
                    if (log.isDebugEnabled())
                    {
                        log
                                .debug("Sleeping for ["
                                        + retryInterval
                                        + "] milliseconds before replaying invocation for method ["
                                        + invocation.getMethod().toString()
                                        + "].");
                    }
                    Thread.sleep(this.retryInterval);

                    if (log.isDebugEnabled())
                    {
                        log.debug("Attempt invocation [" + retryCounter
                                + "] for method ["
                                + invocation.getMethod().toString() + "].");
                    }
                    // returning from a finally block will discard the
                    // exception
                    return invocation.proceed();
                } catch (Exception exp2)
                {
                    // determine whether to retry or just throw the exception
                    // back up
                    if (!this.isReplayable(invocation, exp)) { throw exp; }

                    if (log.isDebugEnabled())
                    {
                        log.debug("Attempt [" + retryCounter
                                + "] to replay invocation for method ["
                                + invocation.getMethod().toString()
                                + "] failed. [" + (retryCount - retryCounter)
                                + "] attempts left.");
                    }

                    lastExp = exp2;
                    retryCounter++;
                }
            }
            if (log.isDebugEnabled())
            {
                log.debug("[" + retryCounter
                        + "] attempts to replay invocation for method ["
                        + invocation.getMethod().toString()
                        + "] failed. Throwing exception ["
                        + lastExp.getClass().getName() + "]");
            }
            throw lastExp;
        }

    }

    public int getRetryCount()
    {
        return retryCount;
    }

    public void setRetryCount(int retryCount)
    {
        this.retryCount = retryCount;
    }

    public int getRetryInterval()
    {
        return retryInterval;
    }

    public void setRetryInterval(int retryInterval)
    {
        this.retryInterval = retryInterval;
    }

    /**
     * Determine if we should attempt to replay the method given that the
     * previous invocation returned the passed exception.
     * 
     * @param exp
     * @return True if we should replay the method.
     */
    private boolean isReplayable(MethodInvocation invocation, Exception exp)
    {
        return replayDecisionMaker.shouldReplay(invocation, exp);
    }

    public void setReplayDecisionMaker(
            TransactionalMethodReplayDecisionMaker replayDecisionMaker)
    {
        this.replayDecisionMaker = replayDecisionMaker;
    }

}
