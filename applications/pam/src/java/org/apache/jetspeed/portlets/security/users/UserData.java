package org.apache.jetspeed.portlets.security.users;


public class UserData
{
    private static final UserBean[] users = new UserBean[]     
    { 
            new UserBean("Taylor", "David"),
            new UserBean("Weaver", "Scott"),
            new UserBean("Ford", "Jeremy")
    };
    
    public UserBean[] getUsers()
    {
        return users;
    }
}