<%
    java.util.Locale locale = request.getLocale();
    if ( locale == null )
    {
        locale = java.util.Locale.ENGLISH;
    }
%>
<%= java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.MEDIUM, java.text.DateFormat.MEDIUM, locale).format(new java.util.Date()) %>
