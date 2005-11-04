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
public interface AggregateStatistics extends Serializable
{
    public int getHitCount();
    public float getAvgProcessingTime();
    public float getMinProcessingTime();
    public float getMaxProcessingTime();
    public float getStdDevProcessingTime();
    public void setHitCount(int hitCount);
    public void setAvgProcessingTime(float time);
    public void setMinProcessingTime(float time);
    public void setMaxProcessingTime(float time);
    public void setStdDevProcessingTime(float time);
    
}
