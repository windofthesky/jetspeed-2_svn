/* Copyright 2005 Apache Software Foundation
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.jetspeed.statistics;

import java.io.Serializable;
import java.util.Map;

/**
 * AggregateStatistics
 * 
 * @author <a href="mailto:chris@bluesunrise.com">Chris Schaefer</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public interface AggregateStatistics extends Serializable
{
    public void addRow(Map row);
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
