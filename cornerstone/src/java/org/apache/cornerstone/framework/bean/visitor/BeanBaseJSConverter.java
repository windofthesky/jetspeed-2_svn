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

package org.apache.cornerstone.framework.bean.visitor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.apache.cornerstone.framework.bean.helper.BeanHelper;
import org.apache.log4j.Logger;

public class BeanBaseJSConverter extends BaseBeanVisitor
{
    public static final String REVISION = "$Revision$";

    /**
     * Converts a data bean to its JavaScript representation.
     * @param bean
     * @return String of JavaScript version of data bean.
     */
    public static String convertToJS(Object bean)
    {
        BeanBaseJSConverter visitor = new BeanBaseJSConverter();
        visitor.visit(bean);
        return visitor.getJSCode();
    }

    public String getJSCode()
    {
        return _jsCode.toString();
    }

    public void visit(Object bean)
    {
        if (bean == null)
        {
            append("null");
        }
        else
        {
            super.visit(bean);
        }
    }

    /**
     * Handles beginning of an object.
     */
    public void beginObject()
    {
        append("{");
    }

    /**
     * Handles end of an object.
     */
    public void endObject()
    {
        append("}");
    }

    /**
     * Handles beginning of a property.
     */
    public void beginProperty()
    {
    }

    /**
     * Handles end of a property.
     */
    public void endProperty()
    {
    }

    public void visitProperties(Object bean)
    {
        _isFirstProperty.push(Boolean.TRUE);
        super.visitProperties(bean);
        _isFirstProperty.pop();
    }

    public void enterProperty(Object bean, String name)
    {
        super.enterProperty(bean, name);
        beginProperty();
    }

    public void exitProperty(Object bean, String name)
    {
        endProperty();
        super.exitProperty(bean, name);
    }

    public void visitProperty(Object bean, String name)
    {
// TODO
//        if (Object.PROPERTY_ATTACHMENT.equals(name) && _beanHelper.getProperty(bean, name) == null)
//        {
//            // skip empty attachment
//        }
//        else
//        {
            writeComma();

//            Object value = _beanHelper.getProperty(bean, name);
//            if (value instanceof BeanList)
//            {
//                enterSimpleProperty(bean, name, value);
//                beginObject();
//
//                _isFirstProperty.push(Boolean.TRUE);
//                super.visitProperties((BeanList) value);
//
//                writeComma();
//
//                enterIndexedProperty(bean, Constant.INTERNAL_LIST);
//                visitIndexedProperty(bean, name);
//                exitIndexedProperty(bean, name);
//
//                _isFirstProperty.pop();
//
//                endObject();
//                exitSimpleProperty(bean, name, value);
//            }
//            else
//            {
                super.visitProperty(bean, name);
//            }
//        }
    }

    public void enterSimpleProperty(Object bean, String name, Object value)
    {
        append(name);
        append(":");
    }

    public void exitSimpleProperty(Object bean, String name, Object value)
    {
    }

    /**
     * Handles beginning of an array.
     */
    public void beginArray()
    {
        append('[');
    }

    /**
     * Handles end of an array.
     */
    public void endArray()
    {
        append(']');
    }

    public void enterIndexedProperty(Object bean, String name)
    {
        super.enterIndexedProperty(bean, name);
        append(name + ":");
        beginArray();
    }

    public void exitIndexedProperty(Object bean, String name)
    {
        endArray();
        super.exitIndexedProperty(bean, name);
    }

    public void visitIndexedPropertyElement(Object bean, String name, int startIndex, int index, Object value)
    {
        if (index > startIndex)
            append(",");

        super.visitIndexedPropertyElement(bean, name, startIndex, index, value);
    }

    public void visitSpecialObject(Object value)
    {
        if (value instanceof String || value instanceof Date)
        {
            append("\"");
            append(value);
            append("\"");
        }
        else if (value instanceof List)
        {
            List list = (List) value;
            beginArray();
            boolean first = true;
            for (Iterator itr = list.iterator(); itr.hasNext();)
            {
                if (first)
                {
                    first = false;
                }
                else
                {
                    append(",");
                }

                Object next = itr.next();
                visit(next);
            }
            endArray();
        }
        else
        {
            append(value);
        }
    }

    protected void writeComma()
    {
        Boolean isFirstProperty = (Boolean) _isFirstProperty.peek();
        if (isFirstProperty == Boolean.TRUE)
        {
            _isFirstProperty.pop();
            _isFirstProperty.push(Boolean.FALSE);
        }
        else
        {
            append(",");
        }
    }

    protected void append(String s)
    {
        _jsCode.append(s);
//        _Logger.debug("_jsCode=" + _jsCode);
    }

    protected void append(char c)
    {
        _jsCode.append(c);
//        _Logger.debug("_jsCode=" + _jsCode);
    }

    protected void append(Object o)
    {
        _jsCode.append(o);
//        _Logger.debug("_jsCode=" + _jsCode);
    }

    // TODO: add other primitive type wrapper types
    private static Class[] _SpecialTypes =
    {
        Date.class, Integer.class, List.class,
        Long.class,    /* Map.class,*/ String.class,
        BigDecimal.class, Boolean.class
    };

    protected boolean isSpecialObject(Object o)
    {
        Class c = o.getClass();
        for (int i = 0; i < _SpecialTypes.length; i++)
        {
            if (_SpecialTypes[i].isAssignableFrom(c))
                return true;            
        }

        return false;
    }

    protected static BeanHelper _beanHelper = BeanHelper.getSingleton();
    private static Logger _Logger = Logger.getLogger(BeanBaseJSConverter.class);
    protected StringBuffer _jsCode = new StringBuffer();
    protected Stack _isFirstProperty = new Stack();
}