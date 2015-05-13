
<%@page
	import="edu.harvard.eecs.airg.coloredtrails.shared.discourse.NormColorDiscourseMessage"%>
<%@page
	import="edu.harvard.eecs.airg.coloredtrails.shared.types.CTStateContainer"%>
<%@page
	import="edu.harvard.eecs.airg.coloredtrails.shared.types.PlayerStatus"%>
<%@page import="java.sql.RowId"%>
<%@page
	import="edu.harvard.eecs.airg.coloredtrails.client.ClientPlayerStatus"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Random"%>


<%@page import="webapp.WebAgentAdaptor"%>
<%@page
	import="edu.harvard.eecs.airg.coloredtrails.shared.discourse.BasicProposalDiscourseMessage"%>
<%@page import="webapp.ColorConverter"%>
<%@page
	import="edu.harvard.eecs.airg.coloredtrails.shared.types.ChipSet"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="java.util.logging.*"%>

<%@page import="webapp.*"%>
<%@page import="net.sf.json.JSONObject"%>
<%@page
	import="edu.harvard.eecs.airg.coloredtrails.client.ClientGameStatus"%><%@ page
	language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%	

	//this will send A discourse message to the server
	String jsonMsg = request.getParameter("json");
	//out.print(ClinetControler.GetClinet("1234").getUpdates());
	SimpleLog.write("send NormColor, jsonMsg: " + jsonMsg);

	if (jsonMsg != null) {
		JSONObject object = JSONObject.fromObject(jsonMsg);
		// System.out.print(object);
		Integer playerID = Integer.parseInt(object.optString("player"));
		Integer recipientID = Integer.parseInt(object.optString("recipient"));
		
		SimpleLog.write("send NormColor, player ID: " + playerID + "recipientID: " + recipientID);

		//ChipSet RevelationChipSet = new ChipSet();

		String color = object.optString("color");
		String norm = object.getString("norm");
		Integer sanction =Integer.parseInt(object.optString("sanction"));

		SimpleLog.write("send NormColor, color: " + color);

		//conect to session and send the message
		String id = session.getAttribute("id").toString();

		//generate msg ID
		Random rnd = new Random();
		int msgID = rnd.nextInt(10000);

		//create revelation message
		NormColorDiscourseMessage ncdm = new NormColorDiscourseMessage(
				playerID,
				recipientID,
				msgID, color, norm, sanction);
		
		//send revelation message (to myself)
		ClinetControler.GetClinet(id).client.communication.sendDiscourseRequest(ncdm);
		
		SimpleLog.write("send NormColor, session id: " + id);

	}
%>