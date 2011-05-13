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
package org.apache.jetspeed.security;

/**
 * Wrapper for the javax.security.auth.Subject class.
 * Due to a design oversight in JAAS 1.0, the javax.security.auth.Subject.getSubject method does not return the Subject 
 * that is associated with the running thread !inside! a java.security.AccessController.doPrivileged code block.
 * As a result, the current subject cannot be determined correctly.
 * This class uses the ThreadLocal mechanism to carry the thread-specific instance of the subject 
 * @author hajo
 *
 */

import javax.security.auth.*;

import org.apache.jetspeed.util.ServletRequestThreadLocalCleanupCallback;

import java.security.AccessControlContext;
import java.security.PrivilegedActionException;



public class JSSubject implements java.io.Serializable 
{

    private static final long serialVersionUID = -8308522755600156057L;

    static ThreadLocal threadLocal = new ThreadLocal();
    
    
    private static void setSubject(Subject subject)
    {
        if (threadLocal.get() == null && subject != null)
        {
            new ServletRequestThreadLocalCleanupCallback(threadLocal);
        }
        threadLocal.set(subject);
    }

    /**
     * Get the <code>Subject</code> associated with the provided
     * <code>AccessControlContext</code> fromn the current Thread or from the standard SUBJECT mechansim 
     * <p>
     *
     * @param  acc the <code>AccessControlContext</code> from which to retrieve
     *		the <code>Subject</code>. Only used if current thread doesn't carry subject
     *
     * @return  the <code>Subject</code> associated with the provided
     *		<code>AccessControlContext</code>, or <code>null</code>
     *		if no <code>Subject</code> is associated
     *		with the provided <code>AccessControlContext</code>.
     *
     * @exception SecurityException if the caller does not have permission
     *		to get the <code>Subject</code>. <p>
     *
     * @exception NullPointerException if the provided
     *		<code>AccessControlContext</code> is <code>null</code>.
     */
    public static Subject getSubject(final AccessControlContext acc) 
    {
    	Subject s = null;
    		try
    	{
    		s=  (Subject)threadLocal.get();
    	}
    	catch (Exception e)
    	{}
    	if (s == null)
    		return Subject.getSubject(acc);
    	else
    		return s;
    }

    /**
     * Perform work as a particular <code>Subject</code> after setting subject reference in current thread 
     *
     * @param subject the <code>Subject</code> that the specified
     *			<code>action</code> will run as.  This parameter
     *			may be <code>null</code>. <p>
     *
     * @param action the code to be run as the specified
     *			<code>Subject</code>. <p>
     *
     * @return the <code>Object</code> returned by the PrivilegedAction's
     *			<code>run</code> method.
     *
     * @exception NullPointerException if the <code>PrivilegedAction</code>
     *			is <code>null</code>. <p>
     *
     * @exception SecurityException if the caller does not have permission
     *			to invoke this method.
     */
    public static Object doAs(final Subject subject1,
			final java.security.PrivilegedAction action) 
    {
    	Subject subject = subject1;
    	if (subject == null)
    		subject = JSSubject.getSubject(null);
    	setSubject(subject);
    	return Subject.doAs(subject,action);	
    }

    /**
     * Perform work as a particular <code>Subject</code> after setting subject reference in current thread.
     *
     *
     * @param subject the <code>Subject</code> that the specified
     *			<code>action</code> will run as.  This parameter
     *			may be <code>null</code>. <p>
     *
     * @param action the code to be run as the specified
     *			<code>Subject</code>. <p>
     *
     * @return the <code>Object</code> returned by the
     *			PrivilegedExceptionAction's <code>run</code> method.
     *
     * @exception PrivilegedActionException if the
     *			<code>PrivilegedExceptionAction.run</code>
     *			method throws a checked exception. <p>
     *
     * @exception NullPointerException if the specified
     *			<code>PrivilegedExceptionAction</code> is
     *			<code>null</code>. <p>
     *
     * @exception SecurityException if the caller does not have permission
     *			to invoke this method.
     */
    public static Object doAs(final Subject subject1,
			final java.security.PrivilegedExceptionAction action)
			throws java.security.PrivilegedActionException 
			{
    	Subject subject = subject1;
    	if (subject == null)
    		subject = JSSubject.getSubject(null);
    	setSubject(subject);
    	if (subject != null)
    		return Subject.doAs(subject,action);
    	else
    		return Subject.doAs(subject,action);
			}
    /**
     * Perform privileged work as a particular <code>Subject</code> after setting subject reference in current thread.
     *
     *
     * @param subject the <code>Subject</code> that the specified
     *			<code>action</code> will run as.  This parameter
     *			may be <code>null</code>. <p>
     *
     * @param action the code to be run as the specified
     *			<code>Subject</code>. <p>
     *
     * @param acc the <code>AccessControlContext</code> to be tied to the
     *			specified <i>subject</i> and <i>action</i>. <p>
     *
     * @return the <code>Object</code> returned by the PrivilegedAction's
     *			<code>run</code> method.
     *
     * @exception NullPointerException if the <code>PrivilegedAction</code>
     *			is <code>null</code>. <p>
     *
     * @exception SecurityException if the caller does not have permission
     *			to invoke this method.
     */
    public static Object doAsPrivileged(final Subject subject1,
			final java.security.PrivilegedAction action,
			final java.security.AccessControlContext acc) {
    	Subject subject = subject1;
    	if (subject == null)
    		subject = JSSubject.getSubject(acc);
    	setSubject(subject);
    	if (subject != null)
    		return Subject.doAsPrivileged(subject,action,acc);
    	else
    		return Subject.doAsPrivileged(subject,action,acc);
    		
	}


    /**
     * Perform privileged work as a particular <code>Subject</code> after setting subject reference in current thread.
     *
     *
     * @param subject the <code>Subject</code> that the specified
     *			<code>action</code> will run as.  This parameter
     *			may be <code>null</code>. <p>
     *
     * @param action the code to be run as the specified
     *			<code>Subject</code>. <p>
     *
     * @param acc the <code>AccessControlContext</code> to be tied to the
     *			specified <i>subject</i> and <i>action</i>. <p>
     *
     * @return the <code>Object</code> returned by the
     *			PrivilegedExceptionAction's <code>run</code> method.
     *
     * @exception PrivilegedActionException if the
     *			<code>PrivilegedExceptionAction.run</code>
     *			method throws a checked exception. <p>
     *
     * @exception NullPointerException if the specified
     *			<code>PrivilegedExceptionAction</code> is
     *			<code>null</code>. <p>
     *
     * @exception SecurityException if the caller does not have permission
     *			to invoke this method.
     */
    public static Object doAsPrivileged(final Subject subject,
			final java.security.PrivilegedExceptionAction action,
			final java.security.AccessControlContext acc)
			throws java.security.PrivilegedActionException {
    	Subject s = subject;
    	if (s == null)
    		s = JSSubject.getSubject(acc);
    	setSubject(s);
    	if (s != null)
    		return Subject.doAsPrivileged(s,action,acc);
    	else
    		return Subject.doAsPrivileged(s,action,acc);

	}

    /**
     * Clear subject reference in current thread.
     */
    public static void clearSubject() {
        threadLocal.remove();
    }
}
