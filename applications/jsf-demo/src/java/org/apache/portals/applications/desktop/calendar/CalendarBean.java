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
package org.apache.portals.applications.desktop.calendar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

/**
 * CalendarBean
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */

public class CalendarBean
{
    private Date date = new Date();
    private String notes = "";

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        if (date != null)
        {
            this.date = date;
        }
    }
    
    public String getNotes()
    {
        return notes;
    }
    
    public void setNotes(String notes)
    {
        this.notes = notes;
    }
    
   public String getDateKey(Date date)  
   {
       SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd", Locale.getDefault());
       return formatter.format(date);
       
   }
    /*
     * actions
     */
    
    public String save()
    {
        if (this.date != null)
        {
            PortletRequest request = (PortletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
            PortletPreferences prefs = request.getPreferences();
            try
            {
                
                prefs.setValue(getDateKey(this.date), this.notes);
                prefs.store();
            }
            catch (Exception e)
            {
                System.err.println("error storing prefs " + e);
            }
        }
        return "returnFromNotes";
    }
    
    public String selectDate()
    {
        if (this.date == null)
        {
            return "editNotes";
        }
        String selectedDate = getDateKey(this.date);
        PortletRequest request = (PortletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        notes = request.getPreferences().getValue(selectedDate, "");
        return "editNotes"; // goto the navigation rule
    }
}