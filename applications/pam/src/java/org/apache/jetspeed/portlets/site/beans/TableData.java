package org.apache.jetspeed.portlets.site.beans;


public class TableData
{
    private static final Name[] names = new Name[]     
    { 
            new Name("Taylor", "David"),
            new Name("Weaver", "Scott"),
            new Name("Ford", "Jeremy")
    };
    
    public Name[] getNames()
    {
        return names;
    }
}