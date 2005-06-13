package org.apache.jetspeed.ajax;

import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.i18n.KeyedMessage;

public class AJAXException extends JetspeedException
{

    public AJAXException()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    public AJAXException(KeyedMessage keyedMessage, Throwable nested)
    {
        super(keyedMessage, nested);
        // TODO Auto-generated constructor stub
    }

    public AJAXException(KeyedMessage typedMessage)
    {
        super(typedMessage);
        // TODO Auto-generated constructor stub
    }

    public AJAXException(String msg, Throwable nested)
    {
        super(msg, nested);
        // TODO Auto-generated constructor stub
    }

    public AJAXException(String message)
    {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public AJAXException(Throwable nested)
    {
        super(nested);
        // TODO Auto-generated constructor stub
    }

}
