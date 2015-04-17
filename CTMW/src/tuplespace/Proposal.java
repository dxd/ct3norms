package tuplespace;

import java.sql.Timestamp;
import java.util.Date;

import oopl.DistributedOOPL;
import net.jini.core.entry.Entry;

public class Proposal implements TimeEntry {
	
	public Integer id;
	public String agent;
	public String agent2;
	public Integer clock;
	public Timestamp time;
	
	public Proposal() {
		
	}
	public Proposal(Integer id, String agent, String agent2, int clock) {
		this.id = id;
		this.agent = agent;
		this.agent2 = agent2;
		this.clock = clock;
		this.time = new Timestamp(new Date().getTime());
	}
	public Proposal(Integer clock) {
		this.clock = clock;
	}

	public Proposal(String agent, String agent2) {
		this.agent = agent;
		this.agent2 = agent2;
	}
	
	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}



	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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
	public String toString() {
		return "Proposal [id=" + id + ", agent=" + agent + ", agent2=" + agent2 + ", clock=" + clock + ", time="
				+ time + "]";
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
		return "proposal("+id+"," + agent + "," + agent2 + "," + clock + ").";
	}
}
