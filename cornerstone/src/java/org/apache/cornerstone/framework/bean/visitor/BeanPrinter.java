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

import org.apache.cornerstone.framework.constant.Constant;

public class BeanPrinter extends BeanJSConverter
{
    public static final String REVISION = "$Revision$";

    /**
     * Gets the print string of a data bean.  Useful for debugging.
     * @param bean
     * @return
     */
    public static String getPrintString(Object bean)
    {
        BeanPrinter printer = new BeanPrinter();
        printer.visit(bean);
        return printer.getJSCode();
    }

    public void beginObject()
    {
        append('\n');
        addIndentation();
        super.beginObject();
    }

    public void endObject()
    {
        append('\n');
        addIndentation();
        super.endObject();
    }

    public void beginArray()
    {
        append('\n');
        addIndentation();
        super.beginArray();
    }

    public void endArray()
    {
        append('\n');
        addIndentation();
        super.endArray();
    }

    public void beginProperty()
    {
        append('\n');
        addIndentation();
    }

    public void endProperty()
    {
    }

    protected void addIndentation()
    {
        for (int i = 0; i < _level * 4; i++)
            append(' ');
    }

    /**
     * Gets the print string of a subset of a data bean.
     * @param bean
     * @param loadOnDemandPropertyPath
     * @param loadOnDemandStartLineIndex
     * @param loadOnDemandEndLineIndex
     * @return
     */
    public static String getPrintString(
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

        BeanPrinter visitor = new BeanPrinter();
//        visitor.setLoadOnDemand(true);

        visitor.visitIndexedProperty(
            bean0,
            indexedPropertyName,
            loadOnDemandStartLineIndex,
            loadOnDemandEndLineIndex
        );

        return "[" + visitor.getJSCode() + "]";
    }
}