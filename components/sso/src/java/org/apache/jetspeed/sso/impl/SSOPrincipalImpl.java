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

package org.apache.jetspeed.sso.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.jetspeed.security.om.InternalUserPrincipal;
import org.apache.jetspeed.sso.SSOPrincipal;

/**
* <p>
* 		SSOPrincipalImpl
* 		Class used for mapping Principal information for a site. This is the short form of
* 		the InternalPrincipalImpl
* .</p>
*
* @author <a href="mailto:rogerrut	@apache.org">Roger Ruttimann</a>
*/
public class SSOPrincipalImpl implements SSOPrincipal {
	 /** The principal id. */
	   private long principalId;

	   /** The class name. */
	   private String classname;

	   /** The is mapping only. */
	   private boolean isMappingOnly = false;

	   /** The full path. */
	   private String fullPath;

	   /** The creation date. */
	   private Timestamp creationDate;

	   /** The modified date. */
	   private Timestamp modifiedDate;
	   
	   /** The enabled state. */
	   private boolean enabled = true;
	   
	   /** Permissions not used by required by the interface*/
	   private Collection permissions;
	   
	   /** Remote principals for Principal */
	   private Collection remotePrincipals;
	   

	   /**
	    * <p>
	    * The special attribute telling OJB the object's concrete type.
	    * </p>
	    * <p>
	    * NOTE: this attribute MUST be called ojbConcreteClass
	    * </p>
	    */
	   protected String ojbConcreteClass;

	   /**
	    * <p>
	    * InternalPrincipal implementation default constructor.
	    * </p>
	    */
	   public SSOPrincipalImpl()
	   {
	   }

	   /**
	    * <p>
	    * InternalPrincipal constructor given a classname and name.
	    * </p>
	    * 
	    * @param classname The classname.
	    * @param fullPath The full path.
	    */
	   public SSOPrincipalImpl(String classname, String fullPath)
	   {
	       this.ojbConcreteClass = classname;
	       this.classname = classname;
	       this.fullPath = fullPath;
	       this.permissions = null;	// Not used
	       this.creationDate = new Timestamp(System.currentTimeMillis());
	       this.modifiedDate = this.creationDate;
	   }
	   
	   /**
	    * addRemotePrincipal()
	    * adds a principal to the list of remote principals
	    */
	   public void  addRemotePrincipal(InternalUserPrincipal principal)
	   {
	   		if (remotePrincipals == null)
	   		{
	   			remotePrincipals = new ArrayList(1);
	   		}
	   		
	   		remotePrincipals.add(principal);
	   }

	   /**
	    * @see org.apache.jetspeed.security.om.InternalPrincipal#getPrincipalId()
	    */
	   public long getPrincipalId()
	   {
	       return this.principalId;
	   }

	   /**
	    * @see org.apache.jetspeed.security.om.InternalPrincipal#setPrincipalId(int)
	    */
	   public void setPrincipalId(long principalId)
	   {
	       this.principalId = principalId;
	   }

	   /**
	    * @see org.apache.jetspeed.security.om.InternalPrincipal#getClassname()
	    */
	   public String getClassname()
	   {
	       return this.classname;
	   }

	   /**
	    * @see org.apache.jetspeed.security.om.InternalPrincipal#setClassname(java.lang.String)
	    */
	   public void setClassname(String classname)
	   {
	       this.ojbConcreteClass = classname;
	       this.classname = classname;
	   }

	   /**
	    * @return Returns the isMappingOnly.
	    */
	   public boolean isMappingOnly()
	   {
	       return isMappingOnly;
	   }

	   /**
	    * @param isMappingOnly The isMappingOnly to set.
	    */
	   public void setMappingOnly(boolean isMappingOnly)
	   {
	       this.isMappingOnly = isMappingOnly;
	   }

	   /**
	    * @see org.apache.jetspeed.security.om.InternalPrincipal#getFullPath()
	    */
	   public String getFullPath()
	   {
	       return this.fullPath;
	   }

	   /**
	    * @see org.apache.jetspeed.security.om.InternalPrincipal#setFullPath(java.lang.String)
	    */
	   public void setFullPath(String fullPath)
	   {
	       this.fullPath = fullPath;
	   }

	   /**
	    * @see org.apache.jetspeed.security.om.InternalPrincipal#getPermissions()
	    */
	   public Collection getPermissions()
	   {
	       return this.permissions;
	   }

	   /**
	    * @see org.apache.jetspeed.security.om.InternalPrincipal#setPermissions(java.util.Collection)
	    */
	   public void setPermissions(Collection permissions)
	   {
	       this.permissions = permissions;
	   }

	   /**
	    * @see org.apache.jetspeed.security.om.InternalPrincipal#getCreationDate()
	    */
	   public Timestamp getCreationDate()
	   {
	       return this.creationDate;
	   }

	   /**
	    * @see org.apache.jetspeed.security.om.InternalPrincipal#setCreationDate(java.sql.Timestamp)
	    */
	   public void setCreationDate(Timestamp creationDate)
	   {
	       this.creationDate = creationDate;
	   }

	   /**
	    * @see org.apache.jetspeed.security.om.InternalPrincipal#getModifiedDate()
	    */
	   public Timestamp getModifiedDate()
	   {
	       return this.modifiedDate;
	   }

	   /**
	    * @see org.apache.jetspeed.security.om.InternalPrincipal#setModifiedDate(java.sql.Timestamp)
	    */
	   public void setModifiedDate(Timestamp modifiedDate)
	   {
	       this.modifiedDate = modifiedDate;
	   }

	   /** 
	    * @see org.apache.jetspeed.security.om.InternalPrincipal#isEnabled()
	    */
	   public boolean isEnabled()
	   {
	       return enabled;
	   }
	   
	   /** 
	    * @see org.apache.jetspeed.security.om.InternalPrincipal#setEnabled(boolean)
	    */
	   public void setEnabled(boolean enabled)
	   {
	       this.enabled = enabled;
	   }    
	/**
	 * @return Returns the remotePrincipals.
	 */
	public Collection getRemotePrincipals() {
		return remotePrincipals;
	}
	/**
	 * @param remotePrincipals The remotePrincipals to set.
	 */
	public void setRemotePrincipals(Collection remotePrincipals) {
		this.remotePrincipals = remotePrincipals;
	}
}

