<html>
  <title>Login Error</title>
  <body>
    <a href='<%= response.encodeURL("index.jsp")%>'>Home</a>
    <form method="POST" action='<%= response.encodeURL("j_security_check")%>'>
      Username <input type="text" size="15" name="j_username">
      <br>
      Password <input type="password" size="15" name="j_password">
      <input type="submit" value="Login">
    </form>
  </body>
</html> 

