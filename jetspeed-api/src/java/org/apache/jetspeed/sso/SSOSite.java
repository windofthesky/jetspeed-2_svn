/*
 * Created on Nov 19, 2004
 *
 * Copyright 2003-2004 Groundwork Open Source Solution.
 * 
 *      http://www.itgroundwork.com
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.sso;

import java.util.Collection;

import org.apache.jetspeed.security.om.InternalCredential;
import org.apache.jetspeed.sso.SSOPrincipal;

/**
 * @author rruttimann
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface SSOSite {

	/**
	 * @return Returns the credentials.
	 */
	public Collection getCredentials() ;
	
	/**
	 * @param credentials The credentials to set.
	 */
	public void setCredentials(Collection credentials);
	
	/**
	 * @return Returns the isAllowUserSet.
	 */
	public boolean isAllowUserSet() ;
	
	/**
	 * @param isAllowUserSet The isAllowUserSet to set.
	 */
	public void setAllowUserSet(boolean isAllowUserSet);
	
	/**
	 * @return Returns the isCertificateRequired.
	 */
	public boolean isCertificateRequired();
	
	/**
	 * @param isCertificateRequired The isCertificateRequired to set.
	 */
	public void setCertificateRequired(boolean isCertificateRequired);
	
	/**
	 * @return Returns the name.
	 */
	public String getName() ;
	
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) ;
	
	/**
	 * @return Returns the principals.
	 */
	public Collection getPrincipals() ;
	
	/**
	 * @param principals The principals to set.
	 */
	public void setPrincipals(Collection principals);
	
	/**
	 * @return Returns the siteId.
	 */
	public int getSiteId() ;
	
	/**
	 * @param siteId The siteId to set.
	 */
	public void setSiteId(int siteId) ;
	
	/**
	 * @return Returns the siteURL.
	 */
	public String getSiteURL() ;
	
	/**
	 * @param siteURL The siteURL to set.
	 */
	public void setSiteURL(String siteURL) ;
	
	/**
	 * Utility functions
	 * addCredential()
	 * Adds the credentail to the credentials collection
	 *
	 */
	public void addCredential(InternalCredential credential) throws SSOException;
	
	/**
	* removeCredential()
	 * removes a credentail from the credentials collection
	 *
	 */
	public void removeCredential(InternalCredential credential) throws SSOException;
	
	/**
	 * Adds the credentail to the credentials collection
	 *
	 */
	public void addPrincipal(SSOPrincipal principal) throws SSOException;
	
	/**
	* removePrincipal()
	 * removes a principal from the principals collection
	 *
	 */
	public void removePrincipal(long principalId) throws SSOException;
	
	 /**
     * getRemotePrincipals 
     */
	public Collection getRemotePrincipals();
	
    /**
     * setRemotePrincipals 
     */
    public void setRemotePrincipals(Collection remotePrincipals);
}
