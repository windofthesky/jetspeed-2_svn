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

package org.apache.cornerstone.framework.bean.helper;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.cornerstone.framework.core.BaseObject;
import org.apache.log4j.Logger;

public class BeanHelper extends BaseObject
{
    public static final String REVISION = "$Revision$";

    public static BeanHelper getSingleton()
    {
        return _Singleton;
    }

    public Set getPropertyNameSet(Object bean)
    {
        Class c = bean.getClass();
        Set propertyNameSet = (Set) _PropertyNameSetMap.get(c);
        if (propertyNameSet == null)
        {
            PropertyDescriptor[] propertyDescriptors =
                PropertyUtils.getPropertyDescriptors(bean);

            // TODO
            // Set hiddenPropertyNameSet = bean.getHiddenPropertyNameSet();
            Set hiddenPropertyNameSet = new HashSet();

            propertyNameSet = new HashSet(propertyDescriptors.length);
            for (int i = 0; i < propertyDescriptors.length; i++)
            {
                String name = propertyDescriptors[i].getName();
                if (hiddenPropertyNameSet.contains(name)) continue;
                propertyNameSet.add(name);
//                _Logger.debug("getPropertyNames: class=" + bean.getClass().getName() + " property=" + name);
            }

            propertyNameSet.removeAll(DEFAULT_HIDDEN_NAME_SET);
            _PropertyNameSetMap.put(c, propertyNameSet);
        }

        return propertyNameSet;
    }

    public PropertyDescriptor getPropertyDescriptor(Object bean, String name)
    {
        try
        {
            return PropertyUtils.getPropertyDescriptor(bean, name);
        }
        catch(Exception ex)
        {
            _Logger.error(ex.getMessage(), ex);
            return null;
        }
    }

    public Object getProperty(Object bean, String path)
    {
        try
        {
            // path = removeInternalList(path);
            return PropertyUtils.getNestedProperty(bean, path);
        }
        catch (Exception ex)
        {
            String message = ex.getMessage();
            _Logger.error("failed to get property '" + path + "' of class " + bean.getClass().getName() + " (" + ex + (message == null ? "" : ":" + message) + ")");
            return null;
        }
    }

    public void setProperty(Object bean, String path, Object value)
    {
        try
        {
            PropertyUtils.setNestedProperty(bean, path, value);
        }
        catch (Exception ex)
        {
            String message = ex.getMessage();
            _Logger.error("failed to set property '" + path + "' of class " + bean.getClass().getName() + " (" + ex + (message == null ? "" : ":" + message)+ ")");
        }
    }

    private static Logger _Logger = Logger.getLogger(BeanHelper.class);
    private static BeanHelper _Singleton = new BeanHelper();
    protected static Map _PropertyNameSetMap = new HashMap();

    public static Set DEFAULT_HIDDEN_NAME_SET = new HashSet();
    static
    {
        DEFAULT_HIDDEN_NAME_SET.add("class");
    }
}
