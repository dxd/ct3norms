<%@page import="webapp.SimpleLog"%>
<%@page import="org.apache.derby.impl.load.Import"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="java.util.logging.*"%>
<%@page
	import="edu.harvard.eecs.airg.coloredtrails.client.ClientGameStatus"%>
<%@page
	import="edu.harvard.eecs.airg.coloredtrails.client.ClientPlayerStatus"%>
	<%@page import="edu.harvard.eecs.airg.coloredtrails.client.*"%>
	<%@page import="webapp.ClinetControler"%>
<%@page import="webapp.GeneralFunction"%>	
<%@page import="java.util.ArrayList"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.net.URLDecoder"%>

<script type="text/javascript" src="js/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.9.custom.min.js"></script>
<script type="text/javascript" src="js/grid.locale-en.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/jquery.notify.js"></script>
<script type="text/javascript" src="js/jquery.dd.js"></script>
<%
String id =  session.getAttribute("id").toString();   
ClientGameStatus gs = ClinetControler.GetClinet(id).GameStat;
int role = 0;
if (gs.getMyPlayer().getRole().equals("ra")) role = 1;
else if (gs.getMyPlayer().getRole().equals("raaa")) role = 2;

ArrayList<String> questions =  new ArrayList<String>();
questions.add("I have enjoyed the game.");
questions.add("I have understood the rules of the game.");
questions.add("I have understood the goals of the game.");
if (gs.getMyPlayer().getScore() == 1000)
	questions.add("I understand how my score was calculated.");
else
	questions.add("I understand why I was penalized.");
questions.add("The penalization seemed fair.");
//questions.add("I was able to achieve my goals.");
questions.add("The team work well together.");

if (role == 0)
{
	questions.add("The prohibitions I received were reasonable.");
	questions.add("The obligations I received were reasonable.");
}
//questions.add("The additional norms I received were reasonable.");
//questions.add("The other players behaved predictably.");

if (role > 0)
{
	questions.add( "I was able to instruct the team members about how to reach the group goal.");
}

if (role == 2)
{
	questions.add( "As a game coordinator I felt I was able to ensure the goal is reached.");
}

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<HTML>
<HEAD><TITLE>Feedback form</TITLE></HEAD>
<BODY>
<H3>Please rate these statements</H3>
<table>
<FORM METHOD=POST id="form" action="submit.jsp" name="myform">
<input type="hidden" id="player" name="player" value="<%out.print(gs.getMyPlayer().getPerGameId());%>">
<input type="hidden" id="pin" name="pin" value="<%out.print(gs.getMyPlayer().getPin());%>">
<input type="hidden" id="role" name="role" value="<%out.print(gs.getMyPlayer().getRole());%>">
<th></th><td>Strongly agree</td><td>Slightly agree</td><td>Slightly disagree</td><td>Strongly disagree</td></th>
<%
for (int i=0; i < questions.size(); i++) {
%>
<tr>
    <td><legend><%out.print(questions.get(i));%></legend></td>
    <td><label><input type="radio" id="q<%out.print(i); %>" name="<%out.print(java.net.URLEncoder.encode(questions.get(i)));%>" value="1"></label></td>
    <td><label><input type="radio" id="q<%out.print(i); %>" name="<%out.print(java.net.URLEncoder.encode(questions.get(i)));%>" value="2"></label></td>
    <td><label><input type="radio" id="q<%out.print(i); %>" name="<%out.print(java.net.URLEncoder.encode(questions.get(i)));%>" value="3"></label></td>
    <td><label><input type="radio" id="q<%out.print(i); %>" name="<%out.print(java.net.URLEncoder.encode(questions.get(i)));%>" value="4"></label></td>

</tr>	

<%	
}
%>
<tr>
<td><label>Any comments?</label></td>
<td><textarea cols=40 rows=4 id="comment" name="comment"></textarea><td>
</tr>
<tr>
<td></td><td></td><td></td><td></td><td><INPUT TYPE="button" VALUE="Submit!" id="button"></td>
</tr>
</table>

</FORM>

</BODY>
</HTML>
<script type="text/javascript">
var howmany = <%out.print(questions.size());%>
    document.getElementById("button").onclick = validate;
    function validate() {
    	var radios;
    	 right = true;
    	 
    	 for(i = 0; i < howmany; i++) {
    		 
    		 if ($('input[id=q'+i+']:checked').length == 0) {
    			    right = false;
    			}
    	 }
 
	    if(right) {
	    	//document.getElementById("form").submit();
	    	document.forms["myform"].submit();
	    } else {
		    alert("Please rate all statements.");
	    }
    }
</script>
