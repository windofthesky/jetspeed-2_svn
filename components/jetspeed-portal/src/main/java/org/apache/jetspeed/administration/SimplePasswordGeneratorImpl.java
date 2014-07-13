/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.administration;

import org.apache.jetspeed.security.CredentialPasswordValidator;
import org.apache.jetspeed.security.SecurityException;
import org.apache.taglibs.random.RandomStrg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * @version $Id$
 */
public class SimplePasswordGeneratorImpl implements PasswordGenerator {
    Logger log = LoggerFactory.getLogger(SimplePasswordGeneratorImpl.class);

    /**
     * the list of characters from which a password can be generatored.
     */
    protected static final char[] DEFAULT_PASS_CHARS = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
            't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
            'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
    // removed these for aesthetic purposes
    // '!', '&', '-', '_', '=',
    // '*','@', '#', '$', '%', '^',
    // '+',
    protected char[] passwordChars = SimplePasswordGeneratorImpl.DEFAULT_PASS_CHARS;
    protected ArrayList<Character> upper = new ArrayList<Character>();
    protected ArrayList<Character> lower = new ArrayList<Character>();
    protected Integer length = new Integer(12);
    protected CredentialPasswordValidator validator;
    protected int maximumValidationAttempts = 1000;

    public SimplePasswordGeneratorImpl() {
    }

    public SimplePasswordGeneratorImpl(String defaultChars) {
        this.passwordChars = defaultChars.toCharArray();
    }

    protected RandomStrg newRandomStrg() {
        RandomStrg rs = new RandomStrg();
        try {
            rs.generateRandomObject();
        } catch (Exception e) {
            // this would only get thrown if we tried a secure random and the provider
            // was not available.
            e.printStackTrace();
        }
        return rs;
    }

    protected void initRandomStrg(RandomStrg rs) {
        rs.setLength(new Integer(12));
        rs.setSingle(passwordChars, passwordChars.length);
        rs.setRanges(upper, lower);
    }

    /**
     * @param length the length to set
     */
    public void setLength(Integer length) {
        this.length = length;
    }

    /**
     * @param validator the validator to set
     */
    public void setValidator(CredentialPasswordValidator validator) {
        this.validator = validator;
    }

    public void setPasswordChars(String passwordChars) {
        if (passwordChars != null && passwordChars.length() > 1) {
            this.passwordChars = passwordChars.toCharArray();
        }
    }

    public void setLowerRange(String lowerChars) {
        if (lowerChars != null) {
            lower.clear();
            for (char c : lowerChars.toCharArray()) {
                lower.add(new Character(c));
            }
        }
    }

    public void setUpperRange(String upperChars) {
        if (upperChars != null) {
            upper.clear();
            for (char c : upperChars.toCharArray()) {
                upper.add(new Character(c));
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.apache.jetspeed.administration.PasswordGenerator#generatePassword()
     */
    public String generatePassword() {
        String retval = null;
        RandomStrg rs = newRandomStrg();
        initRandomStrg(rs);
        int validationAttemptCount = 0;
        while (retval == null) {
            retval = rs.getRandom();
            if (validator != null) {
                try {
                    if (log.isDebugEnabled()) {
                        log.debug("passing string " + retval + " to credential validator");
                    }
                    validator.validate(retval);
                } catch (SecurityException sex) {
                    if (validationAttemptCount >= maximumValidationAttempts) {
                        log.warn("Unable to validate generated password after " + maximumValidationAttempts +
                                " returning last generated password string unvalidated");
                        break;
                    }
                    retval = null;
                }
            }

            validationAttemptCount++;
        }
        if (log.isDebugEnabled()) {
            log.debug("returning string " + retval + " after " + validationAttemptCount + " validation attempts");
        }
        return retval;
    }

    public char[] getPasswordChars() {
        return passwordChars;
    }

    public void setPasswordChars(char[] passwordChars) {
        if (log.isDebugEnabled()) {
            log.debug("setting up password character array " + Arrays.toString(passwordChars));
        }
        this.passwordChars = passwordChars;
    }

    public int getMaximumValidationAttempts() {
        return maximumValidationAttempts;
    }

    public void setMaximumValidationAttempts(int maximumValidationAttempts) {
        this.maximumValidationAttempts = maximumValidationAttempts;
    }
}
