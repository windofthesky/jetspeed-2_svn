package org.apache.jetspeed.portlet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.jetspeed.rewriter.ParserAdaptor;
import org.apache.jetspeed.rewriter.RewriterException;
import org.apache.jetspeed.rewriter.TicketParamRewriter;
import org.apache.jetspeed.rewriter.html.SwingParserAdaptor;

public class SSOTicketPortlet extends SSOWebContentPortlet 
{
    public final static String SSO_PREF_TICKET_NAME = "ticket.name";
    // sso.type
	protected Class adaptorHtmlClass = SwingParserAdaptor.class;
        
    //form Constants
    public static final String FORM_POST_METHOD = "post";
    public static final String FORM_GET_METHOD = "get";
    public static final String FORM_MULTIPART_METHOD = "multipart";
    
    protected HttpMethodBase getHttpMethod(HttpClient client, String uri, Map params, String formMethod, RenderRequest request) throws IOException
    {
        String postURI = (String)request.getPreferences().getValue(SSO_TYPE_FORM_ACTION_URL, "");
        String ticketName = (String)request.getPreferences().getValue(SSO_PREF_TICKET_NAME, "");        
    	if(uri.startsWith(postURI))
        {
            if(!params.containsKey(ticketName))
            {
            	params.put(ticketName, new String[]
                                        { requestTicket(uri, request, null) });
            }
        }        
        return super.getHttpMethod(client, uri, params, formMethod, request);
    }

    private String requestTicket(String url, RenderRequest request, RenderResponse response)
    {
        // ...set up URL and HttpClient stuff
    	String ticket = "";
    	HttpClient client = new HttpClient();
        HttpMethodBase httpMethod = null;
        httpMethod = new PostMethod();
        //String useragentProperty = request.getProperty("User-Agent");
        httpMethod.addRequestHeader( "User-Agent", "Firefox" );
        httpMethod.setPath(url);
        try
        {
            client.executeMethod(httpMethod);
            int responseCode  = httpMethod.getStatusCode();
            if (responseCode >= 300 && responseCode <= 399)
            {
                // redirection that could not be handled automatically!!! (probably from a POST)
                Header locationHeader = httpMethod.getResponseHeader("location");
                String redirectLocation = locationHeader != null ? locationHeader.getValue() : null ;
                if (redirectLocation != null)
                {
                    // System.out.println("WebContentPortlet.doHttpWebContent() >>>handling redirect to: "+redirectLocation+"<<<");                    
                    // one more time (assume most params are already encoded & new URL is using GET protocol!)
                    return requestTicket( redirectLocation,null,null) ;
                }
                else
                {
                    // The response is a redirect, but did not provide the new location for the resource.
                    throw new PortletException("Redirection code: "+responseCode+", but with no redirectionLocation set.");
                }
            }
            else if (responseCode == 200)
            {        	
    //        	String body = httpMethod.getResponseBodyAsString();
    //        	Header [] head =  httpMethod.getResponseHeaders();
            	TicketParamRewriter ticketWriter =  new TicketParamRewriter();
                String ticketName = (String)request.getPreferences().getValue(SSO_PREF_TICKET_NAME, null);
                if (ticketName != null)
                {
                    ticketWriter.setTicketName(ticketName);
                    Reader reader = new InputStreamReader(httpMethod.getResponseBodyAsStream());
                    createParserAdaptor().parse(ticketWriter, reader);
                    ticket = ticketWriter.getTicket();
                }
            }
        }
        catch (Exception e) 
        {
			e.printStackTrace();
		}	
    	return ticket;
    }

    public ParserAdaptor createParserAdaptor() throws RewriterException
    {
        try
        {
                return (ParserAdaptor) adaptorHtmlClass.newInstance();
         
        }
        catch (Exception e)
        {
            log.error("Error creating rewriter class", e);
        }
        return null;
    }    
}
