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
package org.apache.portals.applications.desktop;

import java.io.IOException;
import java.util.StringTokenizer;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

public class WeatherPortlet extends GenericVelocityPortlet
{

    public static final String WEATHER_CITY_INFO = "weather_city_info";

    public static final String WEATHER_STATE = "weather_state";

    public static final String WEATHER_CITY = "weather_city";

    public static final String WEATHER_STATION = "weather_station";

    public static final String WEATHER_STYLE = "weather_style";

    public void doView(RenderRequest request, RenderResponse response)
            throws PortletException, IOException
    {
        Context context = super.getContext(request);

        String cityInfo = (String) request.getPortletSession().getAttribute(
                WEATHER_CITY_INFO);

        PortletPreferences prefs = request.getPreferences();
        String city = prefs.getValue(WEATHER_CITY, "Bakersfield");
        String state = prefs.getValue(WEATHER_STATE, "CA");
        String station = prefs.getValue(WEATHER_STATION, null);
        cityInfo = getCityInfo(city, state, station);
        context.put(WEATHER_CITY_INFO, cityInfo);

        String style = prefs.getValue(WEATHER_STYLE, "infobox");
        context.put(WEATHER_STYLE, style);

        super.doView(request, response);
    }
    
    public void doEdit(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        response.setContentType("text/html");        
        doPreferencesEdit(request, response);
    }    
    /**
     * Builds the path for US cities. The format is US/ST/City.html, i.e. for
     * New York City, the city path is US/NY/New_York
     * 
     * @param city
     * @param state
     * @return
     */
    private String getUSInfo(String city, String state)
    {
        city = city.trim().toLowerCase() + " ";
        if (city.indexOf(" ") > 0)
        {
            String newCity = "";
            StringTokenizer st = new StringTokenizer(city, " ");
            while (st.hasMoreTokens())
            {
                String token = st.nextToken();
                newCity = newCity + token.substring(0, 1).toUpperCase()
                        + token.substring(1) + "_";
            }
            city = newCity.substring(0, newCity.length() - 1); // remove last
                                                                // '_'
        }
        state = state.toUpperCase();
        return "US/" + state + "/" + city;
    }

    /**
     * Builds the city path for US or other world cities. For world cities, the
     * city path is global/station/station_number, i.e. for Istanbul, Turkey, it
     * is global/stations/17060. The station numbers need to be obtained from
     * the Weather Underground's site.
     * 
     * @param city
     * @param state
     * @param station
     * @return
     */
    private String getCityInfo(String city, String state, String station)
    {
        String cityInfo = null;
        if (city != null && state != null && !city.equals("")
                && !state.equals(""))
        {
            cityInfo = getUSInfo(city, state);
        } else
            if (station != null && !station.equals(""))
            {
                cityInfo = "global/stations/" + station;
            }
        return cityInfo;
    }

    /* (non-Javadoc)
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException
    {
        String city = request.getParameter(WEATHER_CITY);
        String state = request.getParameter(WEATHER_STATE);
        String style = request.getParameter(WEATHER_STYLE);
        String station = request.getParameter(WEATHER_STATION);
        PortletPreferences prefs = request.getPreferences();
        prefs.setValue(WEATHER_CITY, city);
        prefs.setValue(WEATHER_STATE, state);
        prefs.setValue(WEATHER_STYLE, style);
        prefs.setValue(WEATHER_STATION, station);
        prefs.store();
        super.processAction(request, response);
    }
    
}
