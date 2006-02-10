package org.apache.jetspeed.util.interceptors;

import java.sql.SQLException;
import java.util.StringTokenizer;

import org.aopalliance.intercept.MethodInvocation;

/**
 * MethodReplayDecisionMaker intended for use with methods marked as
 * transactional, where the decision to replay the method is based on the
 * content of the underlying exception from the resource.
 * 
 * @author a336317
 */
public class TransactionalMethodReplayDecisionMaker implements
        MethodReplayDecisionMaker
{

    private int[] sqlErrorCodes;

    public boolean shouldReplay(MethodInvocation invocation, Exception exception)
    {
        // TODO This needs to be a lot more elegant than it currently is - see
        // Spring code
        // for exception translators to see what we can do here.

        // exception must be of type SQLException and have an error code value,
        // else we keep
        // walking down the root cause tree to a maximum depth of 3
        if (exception.getCause() instanceof SQLException)
        {
            SQLException sqlExp = (SQLException) exception.getCause();

            int errorCode = sqlExp.getErrorCode();

            if (errorCode != 0) { return isErrorCodeListed(errorCode); }
        }

        if (exception.getCause().getCause() instanceof SQLException)
        {

            SQLException sqlExp = (SQLException) exception.getCause()
                    .getCause();
            int errorCode = sqlExp.getErrorCode();

            if (errorCode != 0) { return isErrorCodeListed(errorCode); }
        }

        if (exception.getCause().getCause().getCause() instanceof SQLException)
        {
            SQLException sqlExp = (SQLException) exception.getCause()
                    .getCause().getCause();
            int errorCode = sqlExp.getErrorCode();

            if (errorCode != 0) { return isErrorCodeListed(errorCode); }
        }

        return false;
    }

    public void setSqlErrorCodes(String sqlErrorCodesString)
    {
        StringTokenizer tokenizer = new StringTokenizer(sqlErrorCodesString,
                ",");

        this.sqlErrorCodes = new int[tokenizer.countTokens()];

        for (int i = 0; tokenizer.hasMoreTokens(); i++)
        {
            String token = tokenizer.nextToken();
            this.sqlErrorCodes[i] = new Integer(token.trim()).intValue();
        }
    }

    private boolean isErrorCodeListed(int errorCode)
    {
        for (int i = 0; i < this.sqlErrorCodes.length; i++)
        {

            if (this.sqlErrorCodes[i] == errorCode) return true;

        }
        return false;
    }

}
