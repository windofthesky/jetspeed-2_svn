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

package org.apache.jetspeed.om.common.preference;

import java.io.Serializable;
import java.util.Locale;

import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.Preference;
import org.apache.pluto.om.common.PreferenceCtrl;

/**
 * 
 * PreferenceComposite
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface PreferenceComposite extends PreferenceCtrl, Preference, Serializable
{
    void addDescription(Locale locale, String Description);

    Description getDescription(Locale locale);

    /**
     * @throws java.lang.ArrayIndexOutofBounds if index is outside the constraints
     * @param index
     * @return The String value at the specified index or <code>null</code>
     * if no values are present.
     */
    String getValueAt(int index);

    /**
     * 
     * <p>
     * setValueAt
     * </p>
     * Sets the current Preference's value at <code>index</code>
     * to the specified <code>value</code> 
     * 
     * @param index Index hows value will be set.
     * @param value Value to set
     *
     */
    void setValueAt(int index, String value);

    /**
     * 
     * <p>
     * addValue
     * </p>
     * Adds a new value to this Preference.
     * @param value Vale to add to the preference
     *
     */
    void addValue(String value);

    /**
     * 
     * @return
     */
    String[] getValueArray();

    /**
     * 
     * <p>
     * setValues
     * </p>
     * 
     * Replaces the current set of values of this preference
     * with this one. 
     * 
     * @param stringValues 
     *
     */
    void setValues(String[] stringValues);

}
