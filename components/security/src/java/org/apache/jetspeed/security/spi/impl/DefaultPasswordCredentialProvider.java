/* Copyright 2004 Apache Software Foundation
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.jetspeed.security.spi.impl;

import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.om.InternalCredential;
import org.apache.jetspeed.security.spi.CredentialPasswordEncoder;
import org.apache.jetspeed.security.spi.CredentialPasswordValidator;
import org.apache.jetspeed.security.spi.PasswordCredentialProvider;

/**
 * <p>
 * DefaultPasswordCredentialProvider
 * </p>
 * 
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public class DefaultPasswordCredentialProvider implements PasswordCredentialProvider
{
    private CredentialPasswordValidator validator;
    private CredentialPasswordEncoder   encoder;
    
    public DefaultPasswordCredentialProvider()
    {
        this(new DefaultCredentialPasswordValidator(),null);
    }
    
    public DefaultPasswordCredentialProvider(CredentialPasswordValidator validator, CredentialPasswordEncoder encoder)
    {
        this.validator = validator;
        this.encoder = encoder;
    }

    /**
     * @see org.apache.jetspeed.security.spi.PasswordCredentialProvider#getPasswordCredentailClass()
     */
    public Class getPasswordCredentialClass()
    {
        return DefaultPasswordCredentialImpl.class;
    }

    /**
     * @see org.apache.jetspeed.security.spi.PasswordCredentialProvider#getValidator()
     */
    public CredentialPasswordValidator getValidator()
    {
        return validator;
    }

    /**
     * @see org.apache.jetspeed.security.spi.PasswordCredentialProvider#getEncoder()
     */
    public CredentialPasswordEncoder getEncoder()
    {
        return encoder;
    }

    /**
     * @see org.apache.jetspeed.security.spi.PasswordCredentialProvider#create(java.lang.String, java.lang.String)
     */
    public PasswordCredential create(String userName, String password) throws SecurityException
    {
        validator.validate(password);
        PasswordCredential pc;
        if ( encoder != null )
        {
            pc = new DefaultPasswordCredentialImpl(userName, encoder.encode(userName, password).toCharArray());
        }
        else
        {
            pc = new DefaultPasswordCredentialImpl(userName, password.toCharArray());
        }
        return pc;
    }

    /**
     * @see org.apache.jetspeed.security.spi.PasswordCredentialProvider#create(java.lang.String, org.apache.jetspeed.security.om.InternalCredential)
     */
    public PasswordCredential create(String userName, InternalCredential credential) throws SecurityException
    {
        return new DefaultPasswordCredentialImpl(userName, credential);
    }
}
