<%@page import="webapp.*"%>
<%@page import="java.util.logging.*"%>
<%	

	String IP = "";
	String Pin = "";
	
	if (session.getAttribute("id") == null) {
		IP = request.getParameter("ip");
		Pin = request.getParameter("pin");
		if (IP == null) {
			IP = "Localhost";
		}
		if (Pin == null) {
			//error
		} else {
			String id = ClinetControler.AddUserClinet(IP, Pin);
			
			session.setAttribute("id", id);								
		}
	} else {
		String id = session.getAttribute("id").toString();
		try {
		if (ClinetControler.GetClinet(id).game_initialized) {
			if (ClinetControler.GetClinet(id).GameStat.isEnded())
				out.print("ended");	
			else {
			SimpleLog.write("game started on session id: " + id);
			if (ClinetControler.GetClinet(id).PhaseNum < 2) //waiting for the norm phase
				out.print("waiting...");
			else
				out.print("true");		
			}
		} else {
			out.print(id);			
		}
		}
		catch(Exception ex) {
			session.setAttribute("id", null);	
		}
	}
%>
