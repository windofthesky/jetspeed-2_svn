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
