package org.apache.jetspeed.portlets.security.users;


public class UserBean
{
    private String first;
    private String last;
    
    public UserBean(String first, String last)
    {
        this.first = first;
        this.last = last;
    }
    
    public void setFirst(String first)
    {
        this.first = first;
    }

    
    /**
     * @return Returns the last.
     */
    public String getLast()
    {
        return last;
    }
    /**
     * @param last The last to set.
     */
    public void setLast(String last)
    {
        this.last = last;
    }
    /**
     * @return Returns the first.
     */
    public String getFirst()
    {
        return first;
    }
}