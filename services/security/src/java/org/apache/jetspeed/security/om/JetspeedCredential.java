/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2004 The Apache Software Foundation.  All rights
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
package org.apache.jetspeed.security.om;

import java.io.Serializable;
import java.util.Collection;
import java.sql.Timestamp;

/**
 * <p>Interface representing a security credential.</p>
 * <p>The credential value represents the value of the credential
 * such as a password.</p>
 * <p>For now, we do not have custom credentials classes and 
 * credentials support only 1 credential (i.e. 1 password).<p>
 * <p>The credential type represents whether a credential is private or
 * public:</p>
 * <ul>
 *  <li>Private credential: type == 0</li>
 *  <li>Public credential: type == 1</li>
 * </ul>
 * <p>The credential classname represent the class of credential.  For password
 * this field is null.
 * </p>
 * @author <a href="mailto:david@sensova.com">David Le Strat</a>
 * TODO Add multiple credentials support.
 */
public interface JetspeedCredential extends Serializable, Cloneable
{
    /**
     * <p>Getter for the credential id.</p>
     * @return The credential id.
     */
    int getCredentialId();

    /**
     * <p>Setter for the credential id.</p>
     * @param credentialId The credential id.
     */
    void setCredentialId(int credentialId);

    /**
     * <p>Getter for the principal id.</p>
     * @return The principal id.
     */
    int getPrincipalId();

    /**
     * <p>Setter for the principal id.</p>
     * @param principalId The principal id.
     */
    void setPrincipalId(int principalId);

    /**
     * <p>Getter for the credential value.</p>
     * @return The credential value.
     */
    String getValue();

    /**
     * <p>Setter for the credential value.</p>
     * @param value The credential value.
     */
    void setValue(String value);

    /**
     * <p>Getter for the credential type.</p>
     * <ul>
     *  <li>Private credential: type == 0</li>
     *  <li>Public credential: type == 1</li>
     * </ul>
     * @return The credential type.
     */
    short getType();

    /**
     * <p>Setter for the credential type.</p>
     * <ul>
     *  <li>Private credential: type == 0</li>
     *  <li>Public credential: type == 1</li>
     * </ul>
     * @param type The credential type.
     */
    void setType(short type);

    /**
     * <p>Getter for the principal classname.</p>
     * @return The principal classname.
     */
    String getClassname();

    /**
     * <p>Setter for the principal classname.</p>
     * @param classname The principal classname.
     */
    void setClassname(String classname);

    /**
     * <p>Getter for creation date.</p>
     * @return The creation date.
     */
    Timestamp getCreationDate();

    /**
     * <p>Setter for the creation date.</p>
     * @param creationDate The creation date.
     */
    void setCreationDate(Timestamp creationDate);

    /**
     * <p>Getter for the modified date.</p>
     * @return The modified date.
     */
    Timestamp getModifiedDate();

    /**
     * <p>Setter for the modified date.</p>
     * @param modifiedDate The modified date.
     */
    void setModifiedDate(Timestamp modifiedDate);

}
