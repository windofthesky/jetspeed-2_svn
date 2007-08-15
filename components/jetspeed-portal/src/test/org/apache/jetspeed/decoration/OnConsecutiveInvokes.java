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
package org.apache.jetspeed.decoration;

import org.jmock.core.Constraint;

public class OnConsecutiveInvokes implements Constraint
{
    private final Constraint[] constraints;
    private int pointer = 0;
    private boolean toManyCalls = false;
    
    public OnConsecutiveInvokes(Constraint[] constraints)
    {
        this.constraints = constraints;
    }

    public boolean eval(Object arg0)
    {   
        if (pointer < constraints.length)
        {
            try
            {
                return constraints[pointer].eval(arg0);
            }
            finally
            {
                pointer++;
            }
        }
        else
        {
            toManyCalls = true;
            return false;
        }
    }

    public StringBuffer describeTo(StringBuffer buffer)
    {
        if(!toManyCalls)
        {
            return constraints[pointer].describeTo(buffer);
        }
        else
        {
            return buffer.append("Should be invoked "+constraints.length+" times.");
        }
    }

}
