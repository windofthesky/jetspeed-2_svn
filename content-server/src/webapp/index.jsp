<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">



<%
	if(request.getParameter("setTheme") != null)
	{
		java.util.List contentPathes = new java.util.ArrayList(2);
        contentPathes.add(request.getParameter("setTheme"));
        request.getSession().setAttribute("org.apache.jetspeed.content.pathes", contentPathes);
        contentPathes.add("");
	}
%>
<html>
  <head>
    <title>Test content</title>
    <script language="JavaScript1.2" type="text/javascript" src="content/script/test.js"></script>
  </head>
  <body onLoad="Hello();">
   <h1 id="target">
   </h1>
   <p>
   	 This is a normal, static image: <img src="images/myimage.gif" align="middle"/>
   </p>
      <p>
   	 This is  image was located and served by the content server filter: <img src="content/images/myimage.gif" align="middle"/>
   </p>
   <p>
     Current theme is: <%=session.getAttribute("org.apache.jetspeed.theme")%>
     <br />
      <a href="index.jsp?setTheme=themes/red">Set theme to Red</a>
     <br />
      <a href="index.jsp?setTheme=themes/blue">Set theme to Blue</a>
   </p>
   <iframe src="content/page.html" width="300" height="300" />
  </body>
 </html>  