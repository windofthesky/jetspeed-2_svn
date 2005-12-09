/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.layout;

import java.util.List;
import java.util.Map;

import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.psml.FragmentImpl;
import java.util.ArrayList;

/**
 * Test for Fragment placement
 * 
 * @author <a>David Gurney </a>
 * @version $Id: $
 */
public class LocalFragmentImpl extends FragmentImpl
{
    private ArrayList m_oFragments = new ArrayList();

    private String m_sName = null;

    private String m_sType = null;

    private String m_sID = null;

    public LocalFragmentImpl()
    {
    }

    public void addFragment(Fragment p_oFragment)
    {
        m_oFragments.add(p_oFragment);
    }

    public List getFragments()
    {
        return m_oFragments;
    }

    public String getId()
    {
        return m_sID;
    }

    public String getName()
    {
        return m_sName;
    }

    public String getType()
    {
        return m_sType;
    }

    public void setId(String p_sID)
    {
        m_sID = p_sID;
    }

    public void setName(String p_sName)
    {
        m_sName = p_sName;
    }

    public void setType(String p_sType)
    {
        m_sType = p_sType;
    }
}
