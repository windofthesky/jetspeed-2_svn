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

package org.apache.jetspeed.capabilities.impl;

import org.apache.jetspeed.capabilities.Capability;

/**
 * Capability implementation class.
 *
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann</a>
 * @version $Id$
 */

public class CapabilityImpl implements Capability {
    private int capabilityId;
    private String name;

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.registry.Capability#setCapabilityId(int)
     */
    public void setCapabilityId(int id) {
        this.capabilityId = id;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.registry.Capability#getCapabilityId()
     */
    public int getCapabilityId() {
        return this.capabilityId;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.registry.Capability#setName(java.lang.String)
     */
    public void setName(String name) {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.registry.Capability#getName()
     */
    public String getName() {
        return this.name;
    }


    /**
     * Implements the hashCode calculation so two different objects with the content return the same hashcode....
     */
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Implements the equals operation so that 2 elements are equal if all their member values are equal.
     *
     * @param object to compare this one with
     * @return true if both objects represent the same (logical) content
     */
    public boolean equals(Object object) {
        if (!(object instanceof Capability)) {
            return false;
        }
        if (this == object) {
            return true;
        }
//    	 Don't check the ID - id is only set through OJB so this would not recognize equality correctly 
        /*     	if (this.capabilityId != ((Capability)object).getCapabilityId())
              return false;
           */
        String oName = ((Capability) object).getName();

        if (oName != null && name != null) {
            return oName.equals(name);
        } else if (oName == null && name == null) {
            return true;
        } else {
            return false;
        }
    }
}
