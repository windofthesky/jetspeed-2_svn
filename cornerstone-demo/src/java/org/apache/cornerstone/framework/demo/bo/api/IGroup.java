package org.apache.cornerstone.framework.demo.bo.api;

import java.util.List;

public interface IGroup
{
    public static final String REVISION = "$Revision$";

    public Integer getId();
    public void setId(Integer id);

    public String getName();
    public void setName(String name);

    public List getUserList();
    public void setUserList(List userList);
}