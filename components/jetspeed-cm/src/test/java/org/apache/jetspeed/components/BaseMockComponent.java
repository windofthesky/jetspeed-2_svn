/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.components;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public class BaseMockComponent implements MockComponent
{
    private int fieldValue1;
    private String fieldValue2;
  
    private int id;
    private String threadName;
    protected static int instanceCount;

    public BaseMockComponent(int inValue1, String inValue2)
    {
        id = instanceCount;
        instanceCount++;
        fieldValue1 = inValue1;
        fieldValue2 = inValue2;
        this.threadName = Thread.currentThread().getName();
        
    }

    /**
     * @return Returns the value1.
     */
    public int getValue1()
    {
        return fieldValue1;
    }
    /**
     * @param value1 The value1 to set.
     */
    public void setValue1( int value1 )
    {
        fieldValue1 = value1;
    }
    /**
     * @return Returns the value2.
     */
    public String getValue2()
    {
        return fieldValue2;
    }
    /**
     * @param value2 The value2 to set.
     */
    public void setValue2( String value2 )
    {
        fieldValue2 = value2;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.MockComponent#componentCount()
     */
    public int componentId()
    {        
        return id;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.MockComponent#getThreadName()
     */
    public String getThreadName()
    {        
        return threadName;
    }
    
    public Object getValue( String key )
    {
        if(key.equals("1"))
        {
            return new Integer(getValue1());
        }
        else if(key.equals("2"))
        {
            return getValue2();
        }
        else
        {
            return null;
        }
    }
    public void setValue( String key, Object value )
    {
        if(key.equals("1"))
        {
            setValue1(Integer.parseInt(value.toString()));
        }
        else if(key.equals("2"))
        {
            setValue2(value.toString());
        }
        

    }
}
