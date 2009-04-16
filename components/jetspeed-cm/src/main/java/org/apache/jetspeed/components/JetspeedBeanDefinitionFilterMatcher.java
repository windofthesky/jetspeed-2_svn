/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jetspeed.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The JetspeedBeanDefinitionFilterMatcher is constructed with a set of bean category names
 * that are used to evaluate boolean expressions composed of category names and traditional
 * AND, OR, and NOT logical operators. The expression is evaluated by replacing all category
 * names that are specified in the set used to construct this matcher with TRUE. Other
 * names are replaced with FALSE and the expression is then evaluated conventionally.
 */
public class JetspeedBeanDefinitionFilterMatcher
{
    private Set<String> categories;
    
    /**
     * Construct category expression matcher with specified category names
     * set that will be used to match bean definitions against.
     * 
     * @param categories set of category names enabled for this matcher.
     */
    public JetspeedBeanDefinitionFilterMatcher(Set<String> categories)
    {
        this.categories = categories;
    }
    
    /**
     * Match a bean expression against the set of category names that were
     * used to construct this matcher.
     * 
     * @param categoriesMatchExpression bean expression to be matched.
     * @return matching result.
     */
    public boolean match(String categoriesMatchExpression)
    {
        List<String> expression = parse(categoriesMatchExpression);
        if (expression != null)
        {
            return evaluate(expression, 0, expression.size());
        }
        return false;
    }
    
    /**
     * Lexicographically parse a specified expression string. This simple
     * parser decomposes the input string into a list of identifiers or
     * parentheses.
     * 
     * @param expression expression string to parse.
     * @return list of expression elements.
     */
    private List<String> parse(String expression)
    {
        List<String> parsedExpression = new ArrayList<String>();
        int parseIndex = 0;
        int parseLimit = expression.length();
        do
        {
            while ((parseIndex < parseLimit) && Character.isWhitespace(expression.charAt(parseIndex)))
            {
                parseIndex++;
            }
            if (parseIndex < parseLimit)
            {
                char expressionChar = expression.charAt(parseIndex++);
                if (Character.isLetter(expressionChar))
                {
                    int parseIdentifierIndex = parseIndex-1;
                    while ((parseIndex < parseLimit) && Character.isLetterOrDigit(expression.charAt(parseIndex)))
                    {
                        parseIndex++;
                    }
                    parsedExpression.add(expression.substring(parseIdentifierIndex, parseIndex));
                }
                else if (expressionChar == '(')
                {
                    parsedExpression.add("(");
                }
                else if (expressionChar == ')')
                {
                    parsedExpression.add(")");
                }
                else
                {
                    throw new RuntimeException("Unable to parse expression: \""+expression+"\" at position "+(parseIndex-1));
                }
            }
        }
        while (parseIndex < parseLimit);
        return parsedExpression;
    }

    /**
     * Parse and evaluate an expression parsed lexicographically. The specified
     * expression elements are parsed and evaluated as a simple logical expression
     * composed of category names and parenthesized subexpressions. Unary NOT and
     * binary AND and OR operators are support. AND operators bind more tightly
     * than OR operators and thus take precedence.
     *
     * @param expression expression to evaluate.
     * @param startIndex start index of expression to evaluate.
     * @param endIndex end index of expression to evaluate.
     * @return value of evaluated expression.
     */
    private boolean evaluate(List<String> expression, int startIndex, int endIndex)
    {
        int evaluateIndex = startIndex;
        if (evaluateIndex < endIndex)
        {
            // evaluate expression left term
            int [] termEndIndex = new int[1];
            boolean left = evaluateTerm(expression, evaluateIndex, endIndex, termEndIndex);
            evaluateIndex = termEndIndex[0];
            // evaluate binary expression operator and right term pairs
            while (evaluateIndex < endIndex)
            {
                // evaluate operator
                String expressionElement = expression.get(evaluateIndex);
                String operator;
                if (expressionElement.equalsIgnoreCase("AND") || expressionElement.equalsIgnoreCase("OR"))
                {
                    operator = expressionElement;
                    evaluateIndex++;
                    if (evaluateIndex < endIndex)
                    {
                        expressionElement = expression.get(evaluateIndex);
                    }
                    else
                    {
                        throw new RuntimeException("Unable to parse expression: "+expression);                    
                    }
                }
                else
                {
                    throw new RuntimeException("Unable to parse expression: "+expression);                    
                }
                // evaluate right term
                boolean right;
                if (operator.equalsIgnoreCase("AND"))
                {
                    // evaluate right expression term
                    right = evaluateTerm(expression, evaluateIndex, endIndex, termEndIndex);
                    evaluateIndex = termEndIndex[0];
                    // apply 'and' operator to left and right
                    left = left && right;
                }
                else if (operator.equalsIgnoreCase("OR"))
                {
                    // evaluate remaining expression
                    right = evaluate(expression, evaluateIndex, endIndex);
                    evaluateIndex = endIndex;
                    // apply 'or' operator to left and right
                    left = left || right;
                }
            }
            // return left value as expression value
            return left;
        }
        else
        {
            throw new RuntimeException("Unable to parse expression: "+expression);
        }
    }

    /**
     * Parse and evaluate a single expression term. A term is optionally preceded
     * by a unary NOT operator and is composed of either a single category name or
     * a parenthesized subexpression. Category names that appear in the categories
     * set specified as part of this matcher evaluate as TRUE. All other category
     * names evaluate as FALSE.
     * 
     * @param expression expression to evaluate.
     * @param startIndex start index of term to evaluate.
     * @param endIndex end index of expression to evaluate.
     * @param termEndIndex returned end index of evaluated term.
     * @return value of evaluated term.
     */
    private boolean evaluateTerm(List<String> expression, int startIndex, int endIndex, int [] termEndIndex)
    {
        int evaluateIndex = startIndex;
        if (evaluateIndex < endIndex)
        {
            boolean term;
            // evaluate term not unary operator
            boolean invertTerm = false;
            String expressionElement = expression.get(evaluateIndex);
            while (expression.get(evaluateIndex).equalsIgnoreCase("NOT"))
            {
                invertTerm = !invertTerm;
                evaluateIndex++;
                if (evaluateIndex < endIndex)
                {
                    expressionElement = expression.get(evaluateIndex);
                }
                else
                {
                    throw new RuntimeException("Unable to parse expression: "+expression);                    
                }
            }
            // evaluate expression term
            if (expressionElement.equals("("))
            {
                int closeIndex = indexOfClose(expression, evaluateIndex, endIndex);
                term = evaluate(expression, evaluateIndex+1, closeIndex);
                evaluateIndex = closeIndex+1;
            }
            else if (!expressionElement.equalsIgnoreCase("AND") && !expressionElement.equalsIgnoreCase("OR") &&
                     !expressionElement.equalsIgnoreCase("NOT") && !expressionElement.equalsIgnoreCase(")"))
            {
                term = categories.contains(expressionElement);
                evaluateIndex++;
            }
            else
            {
                throw new RuntimeException("Unable to parse expression: "+expression);                    
            }
            // invert term value if necessary and return along with evaluation index
            termEndIndex[0] = evaluateIndex;
            if (invertTerm)
            {
                term = !term;
            }
            return term;
        }
        else
        {
            throw new RuntimeException("Unable to parse expression: "+expression);            
        }
    }
    
    /**
     * Search expression for next close paren at same nested depth as open
     * paren found at specified index.
     * 
     * @param expression expression to search.
     * @param openIndex open paren index.
     * @param endIndex end index of expression to search.
     * @return index of close paren.
     */
    private static int indexOfClose(List<String> expression, int openIndex, int endIndex)
    {
        // find next close paren at same nested depth
        int nestedDepth = 1;
        int parseIndex = openIndex+1;
        while (parseIndex < endIndex)
        {
            if (expression.get(parseIndex).equals(")"))
            {
                if (--nestedDepth == 0)
                {
                    return parseIndex;
                }
            }
            else if (expression.get(parseIndex).equals("("))
            {
                nestedDepth++;
            }
            parseIndex++;
        }
        throw new RuntimeException("Unmatched parens found in expression: "+expression);
    }
}
