package org.apache.cornerstone.framework.demo.bo;

import org.apache.cornerstone.framework.demo.bo.api.IUser;

public class BaseUser implements IUser
{
	/* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.demo.bo.api.IUser#getId()
	 */
	public Integer getId()
	{
        return _id;
	}

	/* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.demo.bo.api.IUser#setId(java.math.BigDecimal)
	 */
	public void setId(Integer id)
	{
		_id = id;
	}

    /* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.demo.bo.api.IUser#getLoginName()
	 */
	public String getLoginName()
	{
		return _loginName;
	}

	/* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.demo.bo.api.IUser#getFirstName()
	 */
	public String getFirstName()
	{
		return _firstName;
	}

    /* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.demo.bo.api.IUser#getLastName()
	 */
	public String getLastName()
	{
		return _lastName;
	}

    /* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.demo.bo.api.IUser#setLoginName(java.lang.String)
	 */
	public void setLoginName(String loginName)
	{
		_loginName = loginName;
	}

	/* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.demo.bo.api.IUser#setFirstName(java.lang.String)
	 */
	public void setFirstName(String firstName)
	{
		_firstName = firstName;
	}

	/* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.demo.bo.api.IUser#setLastName(java.lang.String)
	 */
	public void setLastName(String lastName)
	{
		_lastName = lastName;
	}

    protected Integer _id;
    protected String _loginName;
    protected String _firstName;
    protected String _lastName;
}