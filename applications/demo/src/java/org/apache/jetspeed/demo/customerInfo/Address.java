/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.demo.customerInfo;

/**
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @version $Id$
 */
public class Address
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
