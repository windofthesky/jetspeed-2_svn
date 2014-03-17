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

import java.util.List;
import java.util.Stack;

/**
 * SecurityConstraintsRefExpression
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class SecurityConstraintsRefExpression
{
    private String constraintsRef;
    private List<SecurityConstraintsRefToken> constraintsRefTokens;

    SecurityConstraintsRefExpression(String constraintsRef, List<SecurityConstraintsRefToken> constraintsRefTokens)
    {
        this.constraintsRef = constraintsRef;
        this.constraintsRefTokens = constraintsRefTokens;
    }

    /**
     * Check expression against action and principals. The security
     * constraints and embedded logical operations are evaluated and
     * the resulting permission is returned.
     *
     * @param action check action
     * @param userPrincipals check user principals
     * @param rolePrincipals check role principals
     * @param groupPrincipals check group principals
     * @return flag indicating permission grant
     * @throws RuntimeException if expression evaluation error occurs
     */
    public boolean checkExpression(String action, List<String> userPrincipals, List<String> rolePrincipals, List<String> groupPrincipals)
    {
        // evaluate postfix constraints ref tokens
        Stack<Boolean> operandsStack = new Stack<Boolean>();
        for (SecurityConstraintsRefToken token : constraintsRefTokens)
        {
            String tokenOperation = token.getOperation();
            if (tokenOperation != null)
            {
                if (tokenOperation.equals(SecurityConstraintsRefToken.NOT_OPERATION))
                {
                    if (operandsStack.size() >= 1)
                    {
                        boolean operand = operandsStack.pop();
                        operand = !operand;
                        operandsStack.push(operand);
                    }
                    else
                    {
                        throw new RuntimeException("Missing NOT expression operand in \""+constraintsRef+"\"");
                    }
                }
                else if (tokenOperation.equals(SecurityConstraintsRefToken.AND_OPERATION))
                {
                    if (operandsStack.size() >= 2)
                    {
                        boolean operand0 = operandsStack.pop();
                        boolean operand1 = operandsStack.pop();
                        boolean operand = (operand0 && operand1);
                        operandsStack.push(operand);
                    }
                    else
                    {
                        throw new RuntimeException("Missing AND expression operand in \""+constraintsRef+"\"");
                    }
                }
                else if (tokenOperation.equals(SecurityConstraintsRefToken.OR_OPERATION))
                {
                    if (operandsStack.size() >= 2)
                    {
                        boolean operand0 = operandsStack.pop();
                        boolean operand1 = operandsStack.pop();
                        boolean operand = (operand0 || operand1);
                        operandsStack.push(operand);
                    }
                    else
                    {
                        throw new RuntimeException("Missing OR expression operand in \""+constraintsRef+"\"");
                    }
                }
                else
                {
                    throw new RuntimeException("Unexpected expression operator "+tokenOperation+" in \""+constraintsRef+"\"");
                }
            }
            else if (token.getConstraint() != null)
            {
                // evaluate security constraint operand and place on stack
                boolean operand = checkExpressionConstraint(action, userPrincipals, rolePrincipals, groupPrincipals, token.getConstraint());
                operandsStack.push(operand);
            }
            else
            {
                throw new RuntimeException("Unexpected expression token in \""+constraintsRef+"\"");
            }
        }

        // return single operand left on stack
        if (operandsStack.size() == 1)
        {
            return operandsStack.pop();
        }
        else
        {
            throw new RuntimeException("Unexpected expression operand in \""+constraintsRef+"\"");
        }
    }

    /**
     * Check expression constraint against action and principals. Note that
     * expression constraints without permissions, denials, are treated as
     * simply negative grants: they do not necessarily imply the expression
     * check will fail as they do when specified or referenced as security
     * constraints proper.
     *
     * @param action check action
     * @param userPrincipals check user principals
     * @param rolePrincipals check role principals
     * @param groupPrincipals check group principals
     * @param constraint check constraint
     * @return flag indicating permission grant
     */
    private boolean checkExpressionConstraint(String action, List<String> userPrincipals, List<String> rolePrincipals, List<String> groupPrincipals, SecurityConstraintImpl constraint)
    {
        if (constraint.getPermissions() != null)
        {
            // permitted if action matches permissions and user/role/group match principals
            return (constraint.actionMatch(action) && constraint.principalsMatch(userPrincipals, rolePrincipals, groupPrincipals, true));
        }
        else
        {
            // permissions not specified: not permitted if any principal matched
            return !constraint.principalsMatch(userPrincipals, rolePrincipals, groupPrincipals, false);
        }
    }
}
