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
package org.apache.jetspeed.om.page;

import org.apache.jetspeed.om.common.SecurityConstraint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * SecurityConstraintsRefParser
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class SecurityConstraintsRefParser
{
    public static final String OPEN_PAREN = "(";
    public static final String CLOSE_PAREN = ")";
    public static final String NOT_OPERATION = SecurityConstraintsRefToken.NOT_OPERATION;
    public static final String AND_OPERATION = SecurityConstraintsRefToken.AND_OPERATION;
    public static final String OR_OPERATION = SecurityConstraintsRefToken.OR_OPERATION;

    /**
     * Parse security constraints reference string. If the reference contains an infix logical
     * expression, return a security constraints reference expression. Otherwise, return a
     * list of security constraints from reference.
     *
     * @param constraintsRef constraints reference name or infix expression
     * @param pageSecurity page security context
     * @return security constraints reference expression or list of security constraints
     * @throws RuntimeException if expression parsing error occurs
     */
    public static Object parse(String constraintsRef, PageSecurity pageSecurity)
    {
        // parse infix constraints ref expression into postfix tokens
        List<String> postfixTokens = parseConstraintsRef(constraintsRef);
        if (postfixTokens.isEmpty())
        {
            return null;
        }

        // single reference expression check
        if (postfixTokens.size() == 1) {
            String postfixToken = postfixTokens.get(0);
            if (!postfixToken.equals(AND_OPERATION) && !postfixToken.equals(OR_OPERATION) && !postfixToken.equals(NOT_OPERATION))
            {
                // return definition security constraints
                SecurityConstraintsDef securityConstraintsDef = pageSecurity.getSecurityConstraintsDef(postfixToken);
                if ((securityConstraintsDef != null) && (securityConstraintsDef.getSecurityConstraints() != null))
                {
                    return new ArrayList<SecurityConstraint>(securityConstraintsDef.getSecurityConstraints());
                }
                return null;
            }
        }

        // convert postfix expression tokens into constraints reference tokens
        List<SecurityConstraintsRefToken> constraintsRefTokens = new ArrayList<SecurityConstraintsRefToken>();
        for (String postfixToken : postfixTokens)
        {
            if (postfixToken.equals(AND_OPERATION) || postfixToken.equals(OR_OPERATION) || postfixToken.equals(NOT_OPERATION))
            {
                // postfix operation
                constraintsRefTokens.add(new SecurityConstraintsRefToken(postfixToken));
            }
            else
            {
                // postfix security constraints reference
                SecurityConstraintsDef securityConstraintsDef = pageSecurity.getSecurityConstraintsDef(postfixToken);
                if ((securityConstraintsDef != null) && (securityConstraintsDef.getSecurityConstraints() != null))
                {
                    // multiple security constraints within an expression are treated as an
                    // implicit "or": insert these as a postfix "or" sub expression
                    boolean multipleConstraintsOrExpression = false;
                    for (SecurityConstraint securityConstraint : securityConstraintsDef.getSecurityConstraints())
                    {
                        // postfix security constraint
                        constraintsRefTokens.add(new SecurityConstraintsRefToken(postfixToken, (SecurityConstraintImpl)securityConstraint));
                        if (multipleConstraintsOrExpression)
                        {
                            // multiple security constraints postfix "or" operation
                            constraintsRefTokens.add(new SecurityConstraintsRefToken(OR_OPERATION));
                        }
                        else
                        {
                            multipleConstraintsOrExpression = true;
                        }
                    }
                }
                else
                {
                    throw new RuntimeException("Unable to find security constraints reference "+postfixToken+" in \""+constraintsRef+"\"");
                }
            }
        }

        // return security constraints reference expression
        return new SecurityConstraintsRefExpression(constraintsRef, constraintsRefTokens);
    }

    /**
     * Parse infix expression security constraints reference string into postfix
     * expression token strings.
     *
     * @param constraintsRef constraints reference name or infix expression
     * @return list of postfix expression token strings.
     * @throws RuntimeException if expression parsing error occurs
     */
    static List<String> parseConstraintsRef(String constraintsRef)
    {
        List<String> postfixTokens = new ArrayList<String>();
        Stack<String> infixToPostfixStack = new Stack<String>();
        int tokenIndex = -1;
        boolean token = false;
        int parseIndex = 0;
        int parseLimit = constraintsRef.length();
        while (parseIndex < parseLimit)
        {
            char parseChar = constraintsRef.charAt(parseIndex);
            if (token && !(Character.isJavaIdentifierPart(parseChar) || (parseChar == '-') || (parseChar == '.')))
            {
                infixToPostfix(constraintsRef, constraintsRef.substring(tokenIndex, parseIndex), parseIndex, infixToPostfixStack, postfixTokens);
                token = false;
            }
            if (!token)
            {
                if (Character.isJavaIdentifierStart(parseChar))
                {
                    tokenIndex = parseIndex;
                    token = true;
                }
                else if ((parseChar == '(') || (parseChar == ')') || (parseChar == '!'))
                {
                    infixToPostfix(constraintsRef, constraintsRef.substring(parseIndex, parseIndex+1), parseIndex, infixToPostfixStack, postfixTokens);
                }
                else if (((parseChar == '&') || (parseChar == '|')) && (parseIndex+1 < parseLimit) && (constraintsRef.charAt(parseIndex+1) == parseChar))
                {
                    infixToPostfix(constraintsRef, constraintsRef.substring(parseIndex, parseIndex+2), parseIndex, infixToPostfixStack, postfixTokens);
                    parseIndex++;
                }
                else if (!Character.isWhitespace(parseChar))
                {
                    throw new RuntimeException("Illegal character '"+parseChar+"' at position "+parseIndex+" in \""+constraintsRef+"\"");
                }
            }
            parseIndex++;
        }
        if (token)
        {
            infixToPostfix(constraintsRef, constraintsRef.substring(tokenIndex), parseIndex, infixToPostfixStack, postfixTokens);
        }
        infixToPostfix(constraintsRef, null, parseIndex, infixToPostfixStack, postfixTokens);
        return postfixTokens;
    }

    /**
     * Logical operator precedence map.
     */
    private static final Map<String,Integer> OPERATOR_PRECEDENCE = new HashMap<String,Integer>();
    static
    {
        OPERATOR_PRECEDENCE.put(NOT_OPERATION, new Integer(2));
        OPERATOR_PRECEDENCE.put(AND_OPERATION, new Integer(1));
        OPERATOR_PRECEDENCE.put(OR_OPERATION, new Integer(0));
    }

    /**
     * Process infix expression tokens into a postfix expression token list.
     *
     * @param expression infix expression
     * @param token infix expression token or null to indicate end of expression
     * @param parseIndex index of token in expression for error messages
     * @param stack infix to postfix operations stack
     * @param postfixTokens postfix expression tokens
     */
    private static void infixToPostfix(String expression, String token, int parseIndex, Stack<String> stack, List<String> postfixTokens)
    {
        // null token: end expression infix to postfix operation conversion
        if (token == null)
        {
            if (!stack.empty())
            {
                String peek = stack.peek();
                while (!peek.equals(OPEN_PAREN))
                {
                    postfixTokens.add(stack.pop());
                    if (stack.empty())
                    {
                        break;
                    }
                    peek = stack.peek();
                }
                if (peek.equals(OPEN_PAREN))
                {
                    throw new RuntimeException("Missing paren at position "+parseIndex+" in \""+expression+"\"");
                }
            }
            return;
        }
        // open paren: begin sub expression
        if (token.equals(OPEN_PAREN))
        {
            stack.push(token);
            return;
        }
        // close paren: end sub expression infix to postfix operation conversion
        if (token.equals(CLOSE_PAREN))
        {
            boolean matchedParen = false;
            if (!stack.empty())
            {
                String peek = stack.peek();
                while (!peek.equals(OPEN_PAREN))
                {
                    postfixTokens.add(stack.pop());
                    if (stack.empty())
                    {
                        break;
                    }
                    peek = stack.peek();
                }
                if (peek.equals(OPEN_PAREN))
                {
                    stack.pop();
                    matchedParen = true;
                }
            }
            if (!matchedParen)
            {
                throw new RuntimeException("Mismatched paren at position "+parseIndex+" in \""+expression+"\"");
            }
            return;
        }
        // map operation tokens
        if (token.equals("&&") || (token.equalsIgnoreCase(AND_OPERATION) && !token.equals(AND_OPERATION)))
        {
            token = AND_OPERATION;
        }
        else if (token.equals("||") || (token.equalsIgnoreCase(OR_OPERATION) && !token.equals(OR_OPERATION)))
        {
            token = OR_OPERATION;
        }
        else if (token.equals("!") || (token.equalsIgnoreCase(NOT_OPERATION) && !token.equals(NOT_OPERATION)))
        {
            token = NOT_OPERATION;
        }
        // push non-operation tokens
        Integer operatorPrecedence = OPERATOR_PRECEDENCE.get(token);
        if (operatorPrecedence == null)
        {
            postfixTokens.add(token);
            return;
        }
        // infix to postfix operation conversion
        if (!stack.empty())
        {
            String peek = stack.peek();
            while (!peek.equals(OPEN_PAREN) && ((OPERATOR_PRECEDENCE.get(peek)).intValue() >= operatorPrecedence.intValue()))
            {
                postfixTokens.add(stack.pop());
                if (stack.empty())
                {
                    break;
                }
                peek = stack.peek();
            }
        }
        stack.push(token);
    }
}
