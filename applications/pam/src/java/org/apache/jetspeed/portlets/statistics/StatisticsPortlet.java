/*
 * Created on Nov 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.jetspeed.portlets.statistics;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.statistics.AggregateStatistics;
import org.apache.jetspeed.statistics.InvalidCriteriaException;
import org.apache.jetspeed.statistics.PortalStatistics;
import org.apache.jetspeed.statistics.StatisticsQueryCriteria;
import org.apache.jetspeed.statistics.impl.AggregateStatisticsImpl;
import org.apache.jetspeed.statistics.impl.StatisticsQueryCriteriaImpl;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;


/**
 * Statistics Portlet
 * 
 * @author <a href="mailto:chris@bluesunrise.com">Chris Schaefer</a>
 * @version $Id: $
 */
public class StatisticsPortlet extends GenericVelocityPortlet
{
    private PortalStatistics statistics;
    
    private static final String SESSION_CRITERIA = "criteria";
    private static final String SESSION_RESULTS = "results";
    
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
        PortletContext context = getPortletContext();
        statistics = (PortalStatistics)context.getAttribute(CommonPortletServices.CPS_PORTAL_STATISTICS);
        if (statistics == null)
            throw new PortletException("Could not get instance of portal statistics component");
    }

    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        Context velocityContext = getContext(request);
        PortletSession session = request.getPortletSession();
        
        
        StatisticsQueryCriteria test = (StatisticsQueryCriteria) session.getAttribute(SESSION_CRITERIA);
        if(test == null) {
            test = new StatisticsQueryCriteriaImpl();
            test.setQueryType("user");
        }
        session.setAttribute(SESSION_CRITERIA,test);
        
        StatisticsQueryCriteria sqc = (StatisticsQueryCriteria)session.getAttribute(SESSION_CRITERIA);
        AggregateStatistics stats = (AggregateStatistics)session.getAttribute(SESSION_RESULTS);
        if(stats == null) {
            if(sqc != null) {
                try
                {
                    stats = statistics.queryStatistics(sqc);
                } catch (InvalidCriteriaException e)
                {
                    //TODO:  need to handle situation where we pass in invalid criteria also SQL problem will come in here
                }
            }
        }
        velocityContext.put(SESSION_CRITERIA, sqc);
        velocityContext.put(SESSION_RESULTS,stats);
        velocityContext.put("mytest","this is a test");
        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException, IOException
    {
        PortletSession session = request.getPortletSession();
        // TODO: build query
        StatisticsQueryCriteria criteria = new StatisticsQueryCriteriaImpl();
        String user = request.getParameter("user");
        criteria.setUser(user);
        String timeperiod = request.getParameter("timeperiod");
        criteria.setTimePeriod(timeperiod);
        String queryType = request.getParameter("queryType");
        
        criteria.setQueryType(queryType);
        // TODO: validate
            // this copy will probalby get tosses.
        AggregateStatistics stats = new AggregateStatisticsImpl();
        try
        {
            stats = statistics.queryStatistics(criteria);
        } catch (InvalidCriteriaException e)
        {
            //TODO:  need to handle situation where we pass in invalid criteria also SQL problem will come in here
        }
        // save this to session for later display/edit
        session.setAttribute(SESSION_CRITERIA,criteria);
        session.setAttribute(SESSION_RESULTS,stats);
        
    }
    
}
