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
