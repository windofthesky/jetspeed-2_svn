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