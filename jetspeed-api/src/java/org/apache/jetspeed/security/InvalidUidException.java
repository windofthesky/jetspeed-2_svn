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
package org.apache.jetspeed.security;

/**
 * Exception thrown when the uid is invalid.
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class InvalidUidException extends SecurityException
{
    /** The serial version uid. */
    private static final long serialVersionUID = 8603304762095029084L;

    public InvalidUidException()
    {
        super(INVALID_UID);
    }
}
