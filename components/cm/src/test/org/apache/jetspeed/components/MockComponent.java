/*
 * Created on Apr 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public class MockComponent
{
    private int fieldValue1;
    private String fieldValue2;

    public MockComponent(int inValue1, String inValue2)
    {
        fieldValue1 = inValue1;
        fieldValue2 = inValue2;
    }

    /**
     * @return Returns the value1.
     */
    protected int getValue1()
    {
        return fieldValue1;
    }
    /**
     * @param value1 The value1 to set.
     */
    protected void setValue1( int value1 )
    {
        fieldValue1 = value1;
    }
    /**
     * @return Returns the value2.
     */
    protected String getValue2()
    {
        return fieldValue2;
    }
    /**
     * @param value2 The value2 to set.
     */
    protected void setValue2( String value2 )
    {
        fieldValue2 = value2;
    }
}
