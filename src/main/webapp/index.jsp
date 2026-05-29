<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // immediately forward the user straight to the scanner page on initial load
    request.getRequestDispatcher("scan_qr.jsp").forward(request, response);
%>