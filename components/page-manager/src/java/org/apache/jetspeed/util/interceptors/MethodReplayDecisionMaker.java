package org.apache.jetspeed.util.interceptors;

import org.aopalliance.intercept.MethodInvocation;

/**
 * A interface which is akin to a <B>gateway</B>
 * in BPMN notation. Concrete implementations can make a decision
 * as to whether or not a method invocation should be replayed.
 * 
 * @author a336317
 */
public interface MethodReplayDecisionMaker {
	
	/**
	 * 
	 * @param invocation The MethodInvocation object
	 * @param exception Exception thrown on previous invocation attempt
	 * @return True if we should replay the method, false otherwise
	 */
	public boolean shouldReplay(MethodInvocation invocation, Exception exception);
	
}
