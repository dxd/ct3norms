package tuplespace;

import java.sql.Timestamp;
import java.util.Date;

import oopl.DistributedOOPL;
import net.jini.core.entry.Entry;

public class GroupObl implements TimeEntry {
	

	public Integer id;
	public String obligation;
	public Integer sanction;
	public Integer clock;
	public Timestamp time;
	
	public GroupObl() {
		
	}
	
	public GroupObl(String agent, Integer sanction, Integer clock) {
		this.obligation = agent;
		this.sanction = sanction;
		this.clock = clock;
		this.time = new Timestamp(new Date().getTime());
	}
	
	public String getObligation() {
		return obligation;
	}

	public void setObligation(String agent) {
		this.obligation = agent;
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
	public Integer getClock() {
		return clock;
	}

	@Override
	public void setClock(int clock) {
		this.clock = clock;
	}

	@Override
	public String toPrologString() {
		return "groupObl(" + obligation + "," + sanction + "," + clock + ").";
	}

	@Override
	public String toString() {
		return "GroupObl [id=" + id + ", obligation=" + obligation
				+ ", sanction=" + sanction + ", clock=" + clock + ", time="
				+ time + "]";
	}


}
