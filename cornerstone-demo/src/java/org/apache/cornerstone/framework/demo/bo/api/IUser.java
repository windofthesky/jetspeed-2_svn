package org.apache.cornerstone.framework.demo.bo.api;

public interface IUser
{
    public static final String REVISION = "$Revision$";

    public Integer getId();
    public void setId(Integer id);

    public String getLoginName();
    public void setLoginName(String loginName);

    public String getFirstName();
    public void setFirstName(String firstName);

    public String getLastName();
    public void setLastName(String lastName);
}