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
import javax.servlet.http.HttpSession;
import java.util.Enumeration;

/**
 * Map to wrap session scope attributes.
 * <p/>
 * Date: Mar 11, 2004 10:35:42 PM
 *
 * @author Clinton Begin
 */
public class SessionMap extends BaseHttpMap {

  private HttpSession session;

  public SessionMap(HttpServletRequest request) {
    this.session = request.getSession();
  }

  protected Enumeration getNames() {
    return session.getAttributeNames();
  }

  protected Object getValue(Object key) {
    return session.getAttribute(String.valueOf(key));
  }

  protected void putValue(Object key, Object value) {
    session.setAttribute(String.valueOf(key), value);
  }

  protected void removeValue(Object key) {
    session.removeAttribute(String.valueOf(key));
  }

}
