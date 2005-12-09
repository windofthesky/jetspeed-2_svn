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
package org.apache.jetspeed.demo.customerInfo;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @version $Id$
 */
public class CustomerInfo 
    implements
        Serializable
{
    
    /** Holds value of property name. */
    private String name;
    
    /** Holds value of property billingAddress. */
    private Address billingAddress;
    
    /** Holds value of property shippingAddress. */
    private Address shippingAddress;
    
    /** Holds value of property lastOrdered. */
    private Calendar lastOrdered;
    
    /** Creates a new instance of CustomerInfo */
    public CustomerInfo()
    {
    }
    
    /**
     * Getter for property name.
     * @return Value of property name.
     */
    public String getName()
    {
        return this.name;
    }
    
    /**
     * Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    /**
     * Getter for property billingAddress.
     * @return Value of property billingAddress.
     */
    public Address getBillingAddress()
    {
        return this.billingAddress;
    }
    
    /**
     * Setter for property billingAddress.
     * @param billingAddress New value of property billingAddress.
     */
    public void setBillingAddress(Address billingAddress)
    {
        this.billingAddress = billingAddress;
    }
    
    /**
     * Getter for property shippingAddress.
     * @return Value of property shippingAddress.
     */
    public Address getShippingAddress()
    {
        return this.shippingAddress;
    }
    
    /**
     * Setter for property shippingAddress.
     * @param shippingAddress New value of property shippingAddress.
     */
    public void setShippingAddress(Address shippingAddress)
    {
        this.shippingAddress = shippingAddress;
    }
    
    /**
     * Getter for property lastOrdered.
     * @return Value of property lastOrdered.
     */
    public Date getLastOrderedAsDate()
    {
        return this.lastOrdered.getTime();
    }

    /**
     * Getter for property lastOrdered.
     * @return Value of property lastOrdered.
     */
    public Calendar getLastOrdered()
    {
        return this.lastOrdered;
    }
    
    /**
     * Setter for property lastOrdered.
     * @param lastOrdered New value of property lastOrdered.
     */
    public void setLastOrdered(Calendar lastOrdered)
    {
        this.lastOrdered = lastOrdered;
    }
    
}
