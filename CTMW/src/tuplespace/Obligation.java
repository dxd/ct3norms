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
		String s = "Obligation for player "+agent+" received at clock "+clock+" to ";
		if (obligation.startsWith("[at")) { //[at(1, 2, a20)]
			String ss = obligation.substring(4);
			String[] o = ss.split(",");
			s+= "be at the grid tile [" +Integer.parseInt(o[0].trim())+ "," + Integer.parseInt(o[1].trim())+ "]";
		}
		s += " before clock " + deadline + " or sanction " + sanction + " will be applied";
		return s;
	}
}
