/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.demo.simple;

import java.io.IOException;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import org.apache.jetspeed.portlet.ServletPortlet;

/**
 * This class only exists to maintain the Help and View page names.  As soon
 * as the container/engine will retain the preferences this class can be
 * replaced by configuring portlet preferences.
 *
 * @version $Id$
 * @task Remove this class when the container/engine retain preferences
 */
public class PickANumberServlet extends ServletPortlet
{
    /**
     * Default action page when preference does not exist
     *
     * @see org.apache.jetspeed.portlet.ServletPortlet#processAction
     */
    private static final String DEFAULT_ACTION_PAGE = null;
    
    /**
     * Default custom page when preference does not exist
     *
     * @see org.apache.jetspeed.portlet.ServletPortlet#doCustom
     */
    private static final String DEFAULT_CUSTOM_PAGE = null;
    
    /**
     * Default edit page when preference does not exist
     *
     * @see org.apache.jetspeed.portlet.ServletPortlet#doEdit
     */
    private static final String DEFAULT_EDIT_PAGE = null;
    
    /**
     * Default help page when preference does not exist
     *
     * @see org.apache.jetspeed.portlet.ServletPortlet#doHelp
     */
    private static final String DEFAULT_HELP_PAGE = "/WEB-INF/demo/simple/PickANumberHelp.jsp";
    
    /**
     * Default help page when preference does not exist
     *
     * @see org.apache.jetspeed.portlet.ServletPortlet#doView
     */
    
    private static final String DEFAULT_VIEW_PAGE = "/WEB-INF/demo/simple/PickANumber.jsp";
    
    /**
     * Attribute name of Guess Count
     */
    private static final String GUESS_COUNT_NAME = "GuessCount";
    
    /**
     * Paramter name of current guess
     */
    private static final String GUESS_PARAMETER_NAME = "Guess";
    
    /**
     * Attribute name of the last guess
     */
    private static final String LAST_GUESS_NAME = "LastGuess";

    /**
     * Attribute name of Target Value
     */
    private static final String TARGET_VALUE_NAME = "TargetValue";
    
    /**
     * Set default page values when class is created
     */
    public PickANumberServlet()
    {
        setDefaultActionPage(DEFAULT_ACTION_PAGE);
        setDefaultCustomPage(DEFAULT_CUSTOM_PAGE);
        setDefaultEditPage(DEFAULT_EDIT_PAGE);
        setDefaultHelpPage(DEFAULT_HELP_PAGE);
        setDefaultViewPage(DEFAULT_VIEW_PAGE);
    }
    
    /**
     * Increment attributes in different scopes
     *
     * @see javax.portlet.GenericPortlet#processAction
     *
     */
    public void processAction(PortletRequest request, ActionResponse actionResponse)
    throws PortletException, IOException
    {
        Long guessCount = null;
        Long targetValue = null;
        Long currentGuess = null;
        Long lastGuess = null;
        
        PortletSession session = request.getPortletSession();
        
        // Get target value
        lastGuess = (Long)session.getAttribute(LAST_GUESS_NAME, session.PORTLET_SCOPE);

        // Get target value
        targetValue = (Long)session.getAttribute(TARGET_VALUE_NAME, session.PORTLET_SCOPE);
        if ((targetValue != null) && (lastGuess != null))
        {
            if (targetValue.equals(lastGuess))
                targetValue = null; // Since the number as guesed, start a new game
        }
        if (targetValue == null)
        {
            targetValue = new Long(Math.round(Math.random() * 10.0));
            guessCount = new Long(0);
            session.setAttribute( TARGET_VALUE_NAME, targetValue, session.PORTLET_SCOPE);
        }

        // Get the guessCount, if it has not already been set.
        if (guessCount == null)
        {
            guessCount = (Long)session.getAttribute(GUESS_COUNT_NAME, session.PORTLET_SCOPE);
            if (guessCount == null)
            {
                guessCount = new Long(0);
            }
        }
        

        // Increment the guessCount
        guessCount = new Long(guessCount.longValue() + 1);
        
        try
        {
            currentGuess = new Long(request.getParameter(GUESS_PARAMETER_NAME));
        }
        catch (Exception e)
        {
            currentGuess = new Long(0);
        }

        // Update the attribute values
        session.setAttribute( GUESS_COUNT_NAME, guessCount, session.PORTLET_SCOPE);
        session.setAttribute( LAST_GUESS_NAME, currentGuess, session.PORTLET_SCOPE);
        
        return;
    }
}
