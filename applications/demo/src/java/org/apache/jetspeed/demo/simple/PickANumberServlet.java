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

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.portals.bridges.common.GenericServletPortlet;

/**
 * This class only exists to maintain the Help and View page names.  As soon
 * as the container/engine will retain the preferences this class can be
 * replaced by configuring portlet preferences.
 *
 * @version $Id$
 * @task Remove this class when the container/engine retain preferences
 */
public class PickANumberServlet extends GenericServletPortlet
{
    /**
     * Default action page when preference does not exist
     *
     * @see org.apache.portals.bridges.common.GenericServletPortlet#processAction
     */
    private static final String DEFAULT_ACTION_PAGE = null;
    
    /**
     * Default custom page when preference does not exist
     *
     * @see org.apache.portals.bridges.common.GenericServletPortlet#doCustom
     */
    private static final String DEFAULT_CUSTOM_PAGE = null;
    
    /**
     * Default edit page when preference does not exist
     *
     * @see org.apache.portals.bridges.common.GenericServletPortlet#doEdit
     */
    private static final String DEFAULT_EDIT_PAGE = "/WEB-INF/demo/simple/PickANumberEdit.jsp";
    
    /**
     * Default help page when preference does not exist
     *
     * @see org.apache.portals.bridges.common.GenericServletPortlet#doHelp
     */
    private static final String DEFAULT_HELP_PAGE = "/WEB-INF/demo/simple/PickANumberHelp.jsp";
    
    /**
     * Default help page when preference does not exist
     *
     * @see org.apache.portals.bridges.common.GenericServletPortlet#doView
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
     * Attribute name of Top Range Value (in Edit Mode)
     */
    private static final String TOP_RANGE_NAME = "TopRange";
    
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

            
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        PortletSession session = request.getPortletSession();
        Long guessCount = null;
        Long targetValue = null;
        Long lastGuess = null;
        
        // Get target value
        lastGuess = (Long)session.getAttribute(LAST_GUESS_NAME, PortletSession.APPLICATION_SCOPE);
        if (lastGuess == null)
        {
            lastGuess = new Long(0);
            session.setAttribute(LAST_GUESS_NAME, guessCount, PortletSession.APPLICATION_SCOPE);            
        }

        // Get target value

        targetValue = (Long)session.getAttribute(TARGET_VALUE_NAME, PortletSession.APPLICATION_SCOPE);
        if (targetValue == null)
        {            
            targetValue = new Long(Math.round(Math.random() * getHighRange(request)));
            System.out.println("cheater: target value = " + targetValue);
            guessCount = new Long(0);
            session.setAttribute( TARGET_VALUE_NAME, targetValue, PortletSession.APPLICATION_SCOPE);
            long highRange = getHighRange(request);
            session.setAttribute( TOP_RANGE_NAME, new Long(highRange), PortletSession.APPLICATION_SCOPE);
        }

        guessCount = (Long)session.getAttribute(GUESS_COUNT_NAME, PortletSession.APPLICATION_SCOPE);
        if (guessCount == null)
        {
            guessCount = new Long(0);
            session.setAttribute( GUESS_COUNT_NAME, guessCount, PortletSession.APPLICATION_SCOPE);            
        }

        Long highRange = (Long)session.getAttribute(TOP_RANGE_NAME, PortletSession.APPLICATION_SCOPE);
        if (highRange == null)
        {
            long range = getHighRange(request);
            session.setAttribute( TOP_RANGE_NAME, new Long(range), PortletSession.APPLICATION_SCOPE);
        }
        
        super.doView(request, response);
    }
    
    /**
     * Increment attributes in different scopes
     *
     * @see javax.portlet.GenericPortlet#processActions
     *
     */
    public void processAction(ActionRequest request, ActionResponse actionResponse)
    throws PortletException, IOException
    {
        // Is it an edit (customize) action
        if (isEditAction(request))
        {
            savePreferences(request);
            return;
        }
        
        Long guessCount = null;
        Long targetValue = null;
        Long currentGuess = null;
        Long lastGuess = null;
        
        PortletSession session = request.getPortletSession();
        
        // Get target value
        lastGuess = (Long)session.getAttribute(LAST_GUESS_NAME, PortletSession.APPLICATION_SCOPE);

        // Get target value
        targetValue = (Long)session.getAttribute(TARGET_VALUE_NAME, PortletSession.APPLICATION_SCOPE);
        if ((targetValue != null) && (lastGuess != null))
        {
            if (targetValue.equals(lastGuess))
            {
                targetValue = null; // Since the number as guesed, start a new game
            }
        }
        if (targetValue == null)
        {
            long random = (Math.round(Math.random() * getHighRange(request)));
            if (random == 0)
            {
                random = 1; // don;t allow 0
            }
            targetValue = new Long(random);
            System.out.println("cheater: target value = " + targetValue);
            guessCount = new Long(0);
            session.setAttribute( TARGET_VALUE_NAME, targetValue, PortletSession.APPLICATION_SCOPE);
        }

        // Get the guessCount, if it has not already been set.
        if (guessCount == null)
        {
            guessCount = (Long)session.getAttribute(GUESS_COUNT_NAME, PortletSession.APPLICATION_SCOPE);
            if (guessCount == null)
            {
                guessCount = new Long(0);
            }
        }
        

        // Increment the guessCount
        guessCount = new Long(guessCount.longValue() + 1);
        
        try
        {
            String result = request.getParameter(GUESS_PARAMETER_NAME);
            // System.out.println("result = " + result);
            if (result != null)
            {
                currentGuess = new Long(result);
            }
        }
        catch (Exception e)
        {
            currentGuess = new Long(0);
        }

        // Update the attribute values
        session.setAttribute( GUESS_COUNT_NAME, guessCount, PortletSession.APPLICATION_SCOPE);
        session.setAttribute( LAST_GUESS_NAME, currentGuess, PortletSession.APPLICATION_SCOPE);
        //actionResponse.setRenderParameter(LAST_GUESS_NAME, lastGuess.toString());        
        return;
    }
    
    private long getHighRange(PortletRequest request)
    {
        PortletPreferences prefs = request.getPreferences();
        String highRangePref = prefs.getValue("TopRange", "102");
        long range = Long.parseLong(highRangePref);
        if (range < 2)
        {
            range = 102;
        }
        return range;
    }
    
    private boolean isEditAction(ActionRequest request)
    {
        return (request.getParameter(TOP_RANGE_NAME) != null);        
    }
    
    private void savePreferences(PortletRequest request)
    {
        String topRange = request.getParameter(TOP_RANGE_NAME);
        long range = Long.parseLong(topRange);
        if (range < 2)
        {
            // TODO: throw validation exception
            return;
        }
        PortletPreferences prefs = request.getPreferences();
        try
        {
            prefs.setValue(TOP_RANGE_NAME, topRange);
            prefs.store();
            PortletSession session = request.getPortletSession();            
            session.setAttribute( TOP_RANGE_NAME, new Long(range), PortletSession.APPLICATION_SCOPE);            
        }
        catch (Exception e)
        {
            // TODO: throw validation exception and redirect to error 
        }
    }
    
}
