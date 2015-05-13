package tuplespace;

import java.sql.Timestamp;
import java.util.Date;

import oopl.DistributedOOPL;
import net.jini.core.entry.Entry;

public class SetGoal implements TimeEntry {
	

	public Integer id;
	public String agent;
	public Cell cell;
	public Cell goal;
	public Integer sanction;
	public Integer clock;
	public Timestamp time;
	
	public SetGoal() {
		
	}
	
	public SetGoal(String agent, Cell cell, Cell goal, Integer sanction, Integer clock) {
		this.agent = agent;
		this.cell = cell;
		this.goal = goal;
		this.sanction = sanction;
		this.clock = clock;
		this.time = new Timestamp(new Date().getTime());
	}
	public SetGoal(String agent, Cell cell, int clock) {
		this.agent = agent;
		this.cell = cell;
		this.clock = clock;
		this.time = new Timestamp(new Date().getTime());
	}
	public SetGoal(Integer clock) {
		this.clock = clock;
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
	public Integer getClock() {
		return clock;
	}

	@Override
	public void setClock(int clock) {
		this.clock = clock;
	}

	@Override
	public String toPrologString() {
		return "setGoal(" + agent + "," + cell.x +"," + cell.y +"," + goal.x +"," + goal.y +"," + sanction + "," + clock + ").";
	}

	@Override
	public String toString() {
		return "SetGoal [id=" + id + ", agent=" + agent + ", cell=" + cell
				+ ", goal=" + goal + ", sanction=" + sanction + ", clock="
				+ clock + ", time=" + time + "]";
	}

}
