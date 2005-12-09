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

/**
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @version $Id$
 */
public class Address
    implements
        Serializable
{
    
    /** Holds value of property name. */
    private String name;
    
    /** Holds value of property street. */
    private String street;
    
    /** Holds value of property city. */
    private String city;
    
    /** Holds value of property state. */
    private String state;
    
    /** Holds value of property country. */
    private String country;
    
    /** Holds value of property postalCode. */
    private String postalCode;
    
    /** Creates a new instance of Address */
    public Address()
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
     * Getter for property street.
     * @return Value of property street.
     */
    public String getStreet()
    {
        return this.street;
    }
    
    /**
     * Setter for property street.
     * @param street New value of property street.
     */
    public void setStreet(String street)
    {
        this.street = street;
    }
    
    /**
     * Getter for property city.
     * @return Value of property city.
     */
    public String getCity()
    {
        return this.city;
    }
    
    /**
     * Setter for property city.
     * @param city New value of property city.
     */
    public void setCity(String city)
    {
        this.city = city;
    }
    
    /**
     * Getter for property state.
     * @return Value of property state.
     */
    public String getState()
    {
        return this.state;
    }
    
    /**
     * Setter for property state.
     * @param state New value of property state.
     */
    public void setState(String state)
    {
        this.state = state;
    }
    
    /**
     * Getter for property country.
     * @return Value of property country.
     */
    public String getCountry()
    {
        return this.country;
    }
    
    /**
     * Setter for property country.
     * @param country New value of property country.
     */
    public void setCountry(String country)
    {
        this.country = country;
    }
    
    /**
     * Getter for property postalCode.
     * @return Value of property postalCode.
     */
    public String getPostalCode()
    {
        return this.postalCode;
    }
    
    /**
     * Setter for property postalCode.
     * @param postalCode New value of property postalCode.
     */
    public void setPostalCode(String postalCode)
    {
        this.postalCode = postalCode;
    }
    
}
