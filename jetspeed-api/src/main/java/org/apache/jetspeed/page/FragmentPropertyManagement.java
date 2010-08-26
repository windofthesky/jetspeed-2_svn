package org.apache.jetspeed.page;

import org.apache.jetspeed.om.page.BaseFragmentElement;

/**
 * Supplemental and optional service supported by Page Managers. Fragment Property lists can be manipulated with this interface
 * 
 * @author dtaylor
 *
 */
public interface FragmentPropertyManagement 
{
	 void updateFragmentPropertyList(BaseFragmentElement baseFragmentElement, String scope, FragmentPropertyList transientList);
	 void removeFragmentPropertyList(BaseFragmentElement baseFragmentElement, FragmentPropertyList transientList);
	 FragmentPropertyList getFragmentPropertyList(BaseFragmentElement baseFragmentElement, FragmentPropertyList transientList);
}
