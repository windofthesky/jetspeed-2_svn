/*
 * Created on Apr 21, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public class MockDependentComponent
{
    private MockComponent dependency;

    public MockDependentComponent(MockComponent dep)
    {
        dependency = dep;
    }

    /**
     * @return Returns the dependency.
     */
    public MockComponent getDependency()
    {
        return dependency;
    }
    /**
     * @param dependency The dependency to set.
     */
    public void setDependency( MockComponent dependency )
    {
        this.dependency = dependency;
    }
}
