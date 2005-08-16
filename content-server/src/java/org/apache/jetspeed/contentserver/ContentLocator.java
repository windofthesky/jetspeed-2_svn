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
package org.apache.jetspeed.contentserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;


/**
 * <p>
 * ContentLocator
 * </p>
 *
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface ContentLocator
{

    OutputStream getOutputStream() throws IOException;
    
    InputStream getInputStream() throws IOException;
    
    String getRealPath();
    
    long writeToOutputStream(OutputStream stream) throws IOException;
    
    String getBasePath();
    
    Date getLastModified();
}
