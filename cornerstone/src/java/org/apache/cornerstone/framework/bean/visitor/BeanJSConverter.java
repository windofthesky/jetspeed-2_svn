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

import java.util.Stack;

import org.apache.cornerstone.framework.constant.Constant;
import org.apache.cornerstone.framework.core.BaseObject;
import org.apache.log4j.Logger;

public class BeanJSConverter extends BeanBaseJSConverter
{
    public static final String REVISION = "$Revision$";

    public static String convertToJS(Object bean)
    {
        BeanJSConverter visitor = new BeanJSConverter();
        visitor.visit(bean);
        return visitor.getJSCode();
    }

    /**
     * Converts a subset of a data bean to its JavaScript representation.
     * @param bean
     * @param loadOnDemandPropertyPath
     * @param loadOnDemandStartLineIndex
     * @param loadOnDemandEndLineIndex
     * @return
     */
    public static String convertToJS(
        Object bean,
        String loadOnDemandPropertyPath,
        int loadOnDemandStartLineIndex,
        int loadOnDemandEndLineIndex
    )
    {
        int lastDot = loadOnDemandPropertyPath.lastIndexOf(Constant.DOT);
        String bean0Path = loadOnDemandPropertyPath.substring(0, lastDot);
        Object bean0 = (Object) _beanHelper.getProperty(bean, bean0Path);

        String indexedPropertyName =
            loadOnDemandPropertyPath.substring(lastDot + 1);

/*
        _Logger.debug("bean=" + bean);
        _Logger.debug("bean0Path=" + bean0Path);
        _Logger.debug("bean0=" + bean0);
        _Logger.debug("loadOnDemandPropertyPath=" + loadOnDemandPropertyPath);
        _Logger.debug("indexedPropertyName=" + indexedPropertyName);
*/

        BeanJSConverter visitor = new BeanJSConverter();
//        visitor.setLoadOnDemand(true);

        visitor.visitIndexedProperty(
            bean0,
            indexedPropertyName,
            loadOnDemandStartLineIndex,
            loadOnDemandEndLineIndex
        );

        return "[" + visitor.getJSCode() + "]";
    }

    public BeanJSConverter()
    {
        super();
        _open.push(Boolean.FALSE);
    } 

    public void setLoadOnDemand(boolean loadOnDemand)
    {
        _loadOnDemand = loadOnDemand;
    }

    public void visitIndexedProperty(Object bean, String name)
    {
        // TODO: fix me
        if (bean instanceof java.math.BigDecimal)
        {
            super.visitIndexedProperty(bean, name);
            return;
        }

//        _Logger.debug("visitIndexedProperty(: bean=" + bean + " property=" + name);

        // set the current level of openness

        boolean open = false;
        try
        {
// TODO
//            throw new Exception("FIX IT");
//            AttachmentBean a = (AttachmentBean) _beanHelper.getAttachment(bean);
//            if (a != null)
//            {
//                PresentationStateMap map = a.getPresentationStateMap();
//                ListElementPState pstate = (ListElementPState) map.getCurrentPState();
//                open = pstate.isOpen();
//            }
        }
        catch (Exception e)
        {
            _Logger.error("", e);
        }

        _open.push(open ? Boolean.TRUE : Boolean.FALSE);

        boolean enteredLoadOnDemandInThisFrame = false;

        if (!_loadOnDemand)
        {
            String loadOnDemand = ((BaseObject) bean).getConfigProperty(
                Constant.LOAD_ON_DEMAND + Constant.CONF_DELIM + name
            );
    
            if (Constant.TRUE.equalsIgnoreCase(loadOnDemand))
            {
                _loadOnDemand = true;
                _loadOnDemandLevel = _level;
                enteredLoadOnDemandInThisFrame = true;
                // _Logger.debug("visitIndexedProperty: property=" + name + " _loadOnDemand:=TRUE");
            }

            if (_loadOnDemand && !isOpen())
            { 
                append("'loadOnDemand'");
            }
            else
            {
                super.visitIndexedProperty(bean, name);
            }

            if (enteredLoadOnDemandInThisFrame)
            {
                _loadOnDemand = false;
                _loadOnDemandLevel = -1;
                // _Logger.debug("visitIndexedProperty: property=" + name + " _loadOnDemand:=false");
            }
        }
        else
        {
            if (isOpen())
            {
                // _loadOnDemand and open
                super.visitIndexedProperty(bean, name);
            }
            else
            {
                // _loadOnDemand but not open
                append("'loadOnDemand'");
            }
        }

//        _Logger.debug("visitIndexedProperty)");
        _open.pop();
    }

    public boolean isOpen()
    {
        return _open.peek() == Boolean.TRUE;
    }

    private static Logger _Logger = Logger.getLogger(BeanJSConverter.class);
    protected boolean _loadOnDemand = false;
    protected int _loadOnDemandLevel = -1;
    protected Stack _open = new Stack();
}