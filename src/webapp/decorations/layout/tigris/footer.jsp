<%@ include file="../initLayoutDecorators.jsp" %>
  <%
      //contextPath
      String _cPath = (String) request.getContextPath();
      pageContext.setAttribute("imgFooter", _cPath + "/" +
              getLayoutResource(_layoutDecoration,"images/Jetspeed_blue_sm.png"), PAGE_SCOPE);
  %>



          </td>   <!--  E: all portlet content -->
        </tr>    <!--  E: Main row -->
      </table> <!--  S: ALL CONTENT TABLE -->

    </div> <!-- END: body div wrapper -->
    <p>
      <img src="<c:out escapeXml='false' value='${imgFooter}'/>" alt="Jetspeed 2 Powered" border="0" />
    </p>
  </body>
</html>

