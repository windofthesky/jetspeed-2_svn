<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%
	if(request.getParameter("setTheme") != null)
	{
		session.setAttribute("org.apache.jetspeed.theme", request.getParameter("setTheme"));
	}
%>
<html>
  <head>
    <title>Test content</title>
  </head>
  <body>
   <p>
   	 This is a normal, static image: <img src="images/myimage.gif" align="middle"/>
   </p>
      <p>
   	 This is  image was located and served by the content server filter: <img src="content/theme/images/myimage.gif" align="middle"/>
   </p>
   <p>
     Current theme is: <%=session.getAttribute("org.apache.jetspeed.theme")%>
     <br />
      <a href="index.jsp?setTheme=red">Set theme to Red</a>
     <br />
      <a href="index.jsp?setTheme=blue">Set theme to Blue</a>
   </p>
  </body>
 </html>  