package tuplespace;

import java.sql.Timestamp;
import java.util.Date;

import oopl.DistributedOOPL;
import net.jini.core.entry.Entry;

public class Obligation implements TimeEntry {
	
	public String agent;
	public String obligation;
	public String sanction;
	public Integer deadline;
	public Timestamp time;
	public Integer clock;
	
	
	public Obligation() {
		
	}


	public Obligation(String agent, String obligation, String sanction,
			Integer deadline, Integer clock) {
		
		this.agent = agent;
		this.obligation = obligation;
		this.sanction = sanction;
		this.deadline = deadline;
		this.clock = clock;
		this.time = new Timestamp(new Date().getTime());
	}


	public Obligation(String agent) {
		this.agent = agent;
	}


	@Override
	public String toString() {
		return "Obligation [subject=" + agent + ", obligation=" + obligation
				+ ", sanction=" + sanction + ", deadline=" + deadline
				+ ", time=" + time + ", clock=" + clock + "]";
	}


	@Override
	public int[] toIntArray(DistributedOOPL oopl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTime() {
		this.time = new Timestamp(new Date().getTime());
	}
	@Override
	public Timestamp getTime() {
		return this.time;
	}

	@Override
	public Integer getClock() {
		return clock;
	}

	@Override
	public void setClock(int clock) {
		this.clock = clock;
	}

	@Override
	public String toPrologString() {
		return "obligation(" + agent + "," + obligation + "," + sanction + "," + deadline + "," + clock + ")";
	}
	
	public String toHumanString() {
		String s = "Obligation to ";
		if (obligation.startsWith("[at")) { //[at(1, 2, a20)]
			String ss = obligation.substring(4);
			String[] o = ss.split(",");
			s+= "be at the grid tile [" +Integer.parseInt(o[0].trim())+ "," + Integer.parseInt(o[1].trim())+ "]";
		}
		else if (obligation.startsWith("[surround")) { //[surround(2, 2)]
			String ss = obligation.substring(10);
			String[] o = ss.split(",");
			String p2 = o[1].trim().substring(0, o[1].trim().length()-2);
			s+= "coordinate other players (with the use of norms) to surround a goal [" +Integer.parseInt(o[0].trim())+ "," + p2+ "] from left, right, top and bottom ";
		}
		else if (obligation.startsWith("[color")) { //[color(red,a20)]
			String ss = obligation.substring(7);
			String[] o = ss.split(",");
			s+= "use a tile with color " +o[0];
		}
		else if (obligation.startsWith("[acceptRequests")) { //[acceptRequests(a20)]
			s+= "accept requests for chips from the other players";
		}
		else if (obligation.startsWith("[makeMove")) { //[makeMove(a20)]
			s+= "make a movement on the board";
		}
		else if (obligation.startsWith("[acceptRequest")) { //[acceptRequest(2100)]
			String ss = obligation.substring(15);
			String[] o = ss.split(",");
			int id = Integer.parseInt(o[0].substring(0, o[0].trim().length()-2));
			s+= "accept exchange request with id: " +id;
		}
		else if (obligation.startsWith("[respondToRequest")) { //[respondToRequest(2100)]
			String ss = obligation.substring(18);
			String[] o = ss.split(",");
			int id = Integer.parseInt(o[0].substring(0, o[0].trim().length()-2));
			s+= "respond to request with id: " +id;
		}
		String[] p1 = sanction.split(","); //[reduce(a20,500)]
		String p2 = p1[1].trim().substring(0, p1[1].trim().length()-2);
		s += " before the clock: " + deadline + " or " + p2 + " points will be deducted.";
		return s;
	}
}
