/*
 * Created on Nov 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.jetspeed.statistics;

import java.io.Serializable;


/**
 * @author david
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface StatisticsQueryCriteria extends Serializable
{

    public String getIpAddress();
    public void setIpAddress(String ipAddress);
    public String getUser();
    public void setUser(String user);
    public void setQueryType(String queryType);
    public String getQueryType();
}
