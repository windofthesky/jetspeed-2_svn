package org.apache.cornerstone.framework.demo.bo;

import java.util.List;
import org.apache.cornerstone.framework.demo.bo.api.IGroup;

public class BaseGroup implements IGroup
{
	public static final String REVISION = "$Revision$";

    /* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.demo.bo.api.IGroup#getId()
	 */
	public Integer getId()
	{
		return _id;
	}

	/* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.demo.bo.api.IGroup#setId(java.math.BigDecimal)
	 */
	public void setId(Integer id)
	{
		_id = id;
	}

    /* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.demo.bo.api.IGroup#getName()
	 */
	public String getName()
	{
		return _name;
	}

    /* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.demo.bo.api.IGroup#setName(java.lang.String)
	 */
	public void setName(String name)
	{
		_name = name;
	}

    /* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.demo.bo.api.IGroup#getUserList()
	 */
	public List getUserList()
	{
		return _userList;
	}

    /* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.demo.bo.api.IGroup#setUserList(java.util.List)
	 */
	public void setUserList(List userList)
	{
		_userList = userList;
	}

    protected Integer _id;
    protected String _name;
    protected List _userList;
}