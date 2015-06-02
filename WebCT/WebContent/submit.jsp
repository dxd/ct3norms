
<%@page import="java.sql.RowId"%>
<%@page
	import="edu.harvard.eecs.airg.coloredtrails.client.ClientPlayerStatus"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Random"%>
<%@ page import="java.io.*"  %>
<%@ page import="java.util.*"  %>

<%@page import="webapp.*"%>
<%@page
	import="edu.harvard.eecs.airg.coloredtrails.shared.discourse.BasicProposalDiscourseMessage"%>
<%@page import="webapp.ColorConverter"%>
<%@page
	import="edu.harvard.eecs.airg.coloredtrails.shared.types.ChipSet"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="java.util.logging.*"%>

<%@page import="webapp.ClinetControler"%>
<%@page import="webapp.listen"%>
<%@page import="net.sf.json.JSONObject"%>
<%@page
	import="edu.harvard.eecs.airg.coloredtrails.client.ClientGameStatus"%><%@ page
	language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%
String nameOfTextFile = "/Users/dxd/git/ct3/WebCT/logsh/CTserver/"+new Date()+".txt";
String pin = request.getParameter("pin");
try {   
    PrintWriter pw = new PrintWriter(new FileOutputStream(nameOfTextFile));
    Enumeration paramNames = request.getParameterNames();
	while(paramNames.hasMoreElements()) {
	   String paramName = (String)paramNames.nextElement();
	   pw.println(paramName + " : ");
	   String paramValue = request.getParameter(paramName);
	   pw.println(paramValue);
	}
    pw.close();
} catch(IOException e) {
   out.println(e.getMessage());
}

response.sendRedirect(request.getContextPath() + "/login.html?pin="+pin);
	
%>