package tuplespace;

import java.sql.Timestamp;
import java.util.Date;

import oopl.DistributedOOPL;
import net.jini.core.entry.Entry;

public class Color implements TimeEntry {
	
	public Integer id;
	public String color;
	public String agent;
	public String type;
	public Integer sanction;
	public Timestamp time;
	public Integer clock;
	
	public Color() {

	}
	
public Color(String agent, String color, String type, Integer sanction,
			Integer clock) {
		this.color = color;
		this.agent = agent;
		this.type = type;
		this.sanction = sanction;
		this.clock = clock;
		this.time = new Timestamp(new Date().getTime());
	}

public Color(String color, Integer clock) {
		
		this.color = color;
		this.clock = clock;
		this.time = new Timestamp(new Date().getTime());
	}


	public Color(Integer clock) {
		this.clock = clock;
	}

	public Color(String agent) {
		this.color = agent;
	}
	
    public Color(Object[] params) {
    	if (params[0] != null)
    		this.agent = params[0].toString();
		if (params[1] != null)
			this.color = params[1].toString();
	}
    
    
	public int[] toIntArray(DistributedOOPL oopl) {
		int[] r = new int[18];
		JL.addPredicate(r,0,oopl.prolog.strStorage.getInt("coin"),3, oopl); // cargo/2

		
		JL.addPredicate(r, 3, oopl.prolog.strStorage.getInt("cell"), 2, oopl);
		//JL.addNumber(r, 6, this.cell.x, oopl);
		//JL.addNumber(r, 9, this.cell.y, oopl);
			
		JL.addNumber(r,12,this.clock, oopl);
		JL.addPredicate(r,15, JL.makeStringKnown(this.color,oopl),0, oopl);
		//addPredicate(r,3,makeStringKnown(t.agent==null?"null":t.agent),0); // the name
		//for (int i = 0;  i<r.length; i++){
		//	System.out.println("to array: " + oopl.prolog.strStorage.getString(r[i]));
			
		//}
		
		//addNumber(r, c,t.i);
		return r;
	}
	@Override
	public String toPrologString() {
		return "color("+agent+"," + color + ","+type+","+sanction+"," + clock + ").";
	}


	@Override
	public String toString() {
		return "Color [id=" + id + ", color=" + color + ", agent=" + agent
				+ ", type=" + type + ", sanction=" + sanction + ", time="
				+ time + ", clock=" + clock + "]";
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}


	public void setClock(Integer clock) {
		this.clock = clock;
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
}
