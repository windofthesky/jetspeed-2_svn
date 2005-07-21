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
package org.apache.jetspeed.headerresource;

import java.util.Map;

/**
 * HeaderResource has tags information to put them into &lt;head&gt; tag.
 * 
 * @author <a href="mailto:shinsuke@yahoo.co.jp">Shinsuke Sugaya</a>
 * @version $Id: PortalReservedParameters.java 188569 2005-05-13 13:35:18Z weaver $
 */
public interface HeaderResource
{

    /**
     * Returns tags to put them into &lt;head&gt; tag.
     * 
     * @return
     */
    public abstract String toString();

    /**
     * Add tag information to this instance.
     * 
     * For example, if you want to add the following tag into &lt;head&gt;,
     * 
     * &lt;foo a="1" b="2"&gt;FOO FOO&lt;/foo&gt;
     * 
     * Java code is:
     * 
     * HashMap map=new HashMap();
     * map.put("a","1");
     * map.put("b","2");
     * headerResouce.addHeaderInfo("foo",map,"FOO FOO");
     * 
     * @param elementName Tag's name
     * @param attributes Tag's attributes
     * @param text Tag's content
     */
    public abstract void addHeaderInfo(String elementName, Map attributes, String text);

    /**
     * Convenient method to add &lt;script&gt; tag with defer option.
     * 
     * @param path Javascript file path
     * @param defer defer attributes for &lt;script&gt; tag.
     */
    public abstract void addJavaScript(String path, boolean defer);

    /**
     * Convenient method to add &lt;script&gt; tag.
     * 
     * @param path Javascript file path
     */
    public abstract void addJavaScript(String path);

    /**
     * Convenient method to add &lt;link&gt; tag.
     * 
     * @param path CSS file path
     */
    public abstract void addStyleSheet(String path);

}