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