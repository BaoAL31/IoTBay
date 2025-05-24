<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, model.AccessLog" %>
<%
    List<AccessLog> accessLogs = (List<AccessLog>) request.getAttribute("accessLogs");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Access Logs</title>
    <link rel="stylesheet" href="css/admin.css" />
</head>
<body>
    <h2>Access Logs</h2>
    <table>
        <tr>
            <th>Access Type</th>
            <th>Date and Time</th>
        </tr>
        <% if (accessLogs != null && !accessLogs.isEmpty()) {
            for (AccessLog log : accessLogs) { %>
                <tr>
                    <td><%= log.getAccessType() %></td>
                    <td><%= log.getAccessTime() %></td>
                </tr>
        <%  } } else { %>
            <tr><td colspan="2">No access logs found.</td></tr>
        <% } %>
    </table>

    <a href="welcome_page.jsp">Back to Profile</a>
</body>
</html>
