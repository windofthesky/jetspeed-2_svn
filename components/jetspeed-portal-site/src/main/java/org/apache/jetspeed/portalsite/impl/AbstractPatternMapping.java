package org.apache.jetspeed.portalsite.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class specifies an abstract pattern mapping definition for use
 * by the portal-site content type mapper component mappings.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public class AbstractPatternMapping
{
    private String pattern;
    private Pattern compiledPattern;
    
    /**
     * Construct pattern mapping.
     * 
     * @param pattern mapping pattern
     */
    protected AbstractPatternMapping(String pattern)
    {
        this.pattern = pattern;
        this.compiledPattern = Pattern.compile(pattern);
    }
    
    /**
     * Allocate new matcher for mapping pattern.
     * 
     * @param input input string
     * @return allocated matcher
     */
    protected Matcher getPatternMatcher(String input)
    {
        return compiledPattern.matcher(input);
    }
}
