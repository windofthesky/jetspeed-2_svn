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
package com.ibatis.struts.httpmap;

import com.ibatis.struts.httpmap.BaseHttpMap;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * Map to wrap form parameters.
 * <p/>
 * Date: Mar 11, 2004 10:35:52 PM
 *
 * @author Clinton Begin
 */
public class ParameterMap extends BaseHttpMap {

  private HttpServletRequest request;

  public ParameterMap(HttpServletRequest request) {
    this.request = request;
  }

  protected Enumeration getNames() {
    return request.getParameterNames();
  }

  protected Object getValue(Object key) {
    return request.getParameter(String.valueOf(key));
  }

  protected Object[] getValues(Object key) {
    return request.getParameterValues(String.valueOf(key));
  }

  protected void putValue(Object key, Object value) {
    throw new UnsupportedOperationException("Cannot put value to ParameterMap.");
  }

  protected void removeValue(Object key) {
    throw new UnsupportedOperationException("Cannot remove value from ParameterMap.");
  }

}
