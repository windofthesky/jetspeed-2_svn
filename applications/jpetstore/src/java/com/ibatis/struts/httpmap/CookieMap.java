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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * Map to wrap cookie names and values (READ ONLY).
 * <p/>
 * Date: Mar 11, 2004 11:31:35 PM
 *
 * @author Clinton Begin
 */
public class CookieMap extends BaseHttpMap {

  private Cookie[] cookies;

  public CookieMap(HttpServletRequest request) {
    cookies = request.getCookies();
  }

  protected Enumeration getNames() {
    return new CookieEnumerator(cookies);
  }

  protected Object getValue(Object key) {
    for (int i = 0; i < cookies.length; i++) {
      if (key.equals(cookies[i].getName())) {
        return cookies[i].getValue();
      }
    }
    return null;
  }

  protected void putValue(Object key, Object value) {
    throw new UnsupportedOperationException();
  }

  protected void removeValue(Object key) {
    throw new UnsupportedOperationException();
  }

  /**
   * Cookie Enumerator Class
   */
  private class CookieEnumerator implements Enumeration {

    private int i = 0;

    private Cookie[] cookieArray;

    public CookieEnumerator(Cookie[] cookies) {
      this.cookieArray = cookies;
    }

    public synchronized boolean hasMoreElements() {
      return cookieArray.length > i;
    }

    public synchronized Object nextElement() {
      Object element = cookieArray[i];
      i++;
      return element;
    }

  }

}
