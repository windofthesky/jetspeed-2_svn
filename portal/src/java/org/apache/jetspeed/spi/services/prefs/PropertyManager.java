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
package org.apache.jetspeed.spi.services.prefs;

import java.util.Collection;
import java.util.Map;

import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.spi.services.prefs.impl.PropertyException;

/**
 * Convenience static wrapper around {@link PropertyManagerService}.
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class PropertyManager
{

    /**
     * <p>Default Constructor.  This class contains only static
     * methods, hence users should not be allowed to instantiate it.</p>
     */
    protected PropertyManager()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>Returns the {@link PropertyManagerService}.</p>
     * @return The PropertyManagerService.
     */
    private static final PropertyManagerService getService()
    {
        return (PropertyManagerService) CommonPortletServices.getPortalService(PropertyManagerService.SERVICE_NAME);
    }

    /**
     * @see org.apache.jetspeed.spi.services.prefs.PropertyManagerService#addPropertySetDef(java.lang.String, short)
     */
    public static final int addPropertySetDef(String propertySetName, short propertySetType) throws PropertyException
    {
        return getService().addPropertySetDef(propertySetName, propertySetType);
    }

    /**
     * @see org.apache.jetspeed.spi.services.prefs.PropertyManagerService#getPropertySetDefIdByType(java.lang.String, short)
     */
    public static final int getPropertySetDefIdByType(String propertySetName, short propertySetType) throws PropertyException
    {
        return getService().getPropertySetDefIdByType(propertySetName, propertySetType);
    }

    /**
     * @see org.apache.jetspeed.spi.services.prefs.PropertyManagerService#removePropertySetDef(int)
     */
    public static final void removePropertySetDef(int propertySetDefId) throws PropertyException
    {
        getService().removePropertySetDef(propertySetDefId);
    }

    /**
     * @see org.apache.jetspeed.spi.services.prefs.PropertyManagerService#updatePropertySetDef(int, java.lang.String, short)
     */
    public static final void updatePropertySetDef(int propertySetDefId, String propertySetName, short propertySetType)
    {
        getService().updatePropertySetDef(propertySetDefId, propertySetName, propertySetType);
    }

    /**
     * @see org.apache.jetspeed.spi.services.prefs.PropertyManagerService#getAllPropertySetsByType(short)
     */
    public static final Map getAllPropertySetsByType(short propertySetType) throws PropertyException
    {
        return getService().getAllPropertySetsByType(propertySetType);
    }

    /**
     * @see org.apache.jetspeed.spi.services.prefs.PropertyManagerService#addPropertyKey(int, java.util.Collection)
     */
    public static final void addPropertyKeys(int propertySetDefId, Collection propertyKeys) throws PropertyException
    {
        getService().addPropertyKeys(propertySetDefId, propertyKeys);
    }

    /**
     * @see org.apache.jetspeed.spi.services.prefs.PropertyManagerService#removePropertyKeysBySetDef(int)
     */
    public static final void removePropertyKeysBySetDef(int propertySetDefId) throws PropertyException
    {
        getService().removePropertyKeysBySetDef(propertySetDefId);
    }

    /**
     * @see org.apache.jetspeed.spi.services.prefs.PropertyManagerService#getPropertyKeysBySetDef(int)
     */
    public static final Map getPropertyKeysBySetDef(int propertySetId) throws PropertyException
    {
        return getService().getPropertyKeysBySetDef(propertySetId);
    }

    /**
     * @see org.apache.jetspeed.spi.services.prefs.PropertyManagerService#removePropertyKey(int)
     */
    public static final void removePropertyKey(int propertyKeyId)
    {
        getService().removePropertyKey(propertyKeyId);
    }

    /**
     * @see org.apache.jetspeed.spi.services.prefs.PropertyManagerService#updatePropertyKey(int, java.lang.String)
     */
    public static final void updatePropertyKey(int propertyKeyId, String propertyKeyName)
    {
        getService().updatePropertyKey(propertyKeyId, propertyKeyName);
    }

}
