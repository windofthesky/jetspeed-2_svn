package org.apache.jetspeed.security;

import java.security.Policy;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This class is used to hold the security that will be used when applying security policies. It
 * uses a singleton pattern to maintain state of the policies configured in the consuming engine.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class SecurityPolicies
{
    /** The singleton instance. */
    private static SecurityPolicies instance = null;

    /** The list of wrapped policies. */
    private List wrappedPolicies = new ArrayList();

    /** The list of policies. */
    private List policies = new ArrayList();

    /** The list of used policies. */
    private List usedPolicies = new ArrayList();

    /**
     * Default contructor. Private to force singleton.
     */
    private SecurityPolicies()
    {
    }

    /**
     * <p>
     * Returns the singleton instance for SecurityPolicies.
     * </p>
     * 
     * @return The instance of SecurityPolicies
     */
    public static SecurityPolicies getInstance()
    {
        if (instance == null)
        {
            instance = new SecurityPolicies();
        }
        return instance;
    }

    /**
     * <p>
     * Adds a policy to the list of policies to enforces.
     * </p>
     * 
     * @param wrappedPolicy The {@link PolicyWrapper} to add.
     */
    public void addPolicy(PolicyWrapper wrappedPolicy)
    {
        if (null != wrappedPolicy)
        {
            wrappedPolicies.add(wrappedPolicy);
            policies.add(wrappedPolicy.getPolicy());
            if (wrappedPolicy.isUseAsPolicy())
            {
                usedPolicies.add(wrappedPolicy.getPolicy());
            }
        }

    }

    /**
     * <p>
     * Returns the security policies to enforce as list of {@link Policy}.
     * </p>
     * 
     * @return The policies.
     */
    public List getPolicies()
    {
        return policies;
    }

    /**
     * <p>
     * Returns the security policies to be enforced as list of {@link Policy}.
     * </p>
     * 
     * @return The used policies.
     */
    public List getUsedPolicies()
    {
        return usedPolicies;
    }

    /**
     * <p>
     * Returns the security policies to enforce as list of {@link PolicyWrapper}.
     * </p>
     * 
     * @return The policies.
     */
    public List getWrappedPolicies()
    {
        return wrappedPolicies;
    }

    /**
     * <p>
     * Removes a policy from the list of policies to enforces.
     * </p>
     * 
     * @param policy The {@link Policy} to add.
     */
    public void removePolicy(PolicyWrapper policy)
    {
        wrappedPolicies.remove(policy);
    }
}
