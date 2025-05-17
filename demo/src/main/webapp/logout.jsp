<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%
    // Invalidate the current session
    session.invalidate();

    // Redirect user to the login page
    response.sendRedirect("login.jsp");
%>
