package tuplespace;

import java.sql.Timestamp;
import java.util.Date;

import oopl.DistributedOOPL;
import net.jini.core.entry.Entry;

public class Group implements TimeEntry {
	


	public String name;
	public String ra;
	public String type;
	public Integer clock;
	public Timestamp time;
	
	public Group() {
		
	}

	public Group(String name, String ra, String type, Integer clock) {
		this.name = name;
		this.ra = ra;
		this.type = type;
		this.clock = clock;
		this.time = new Timestamp(new Date().getTime());
	}
	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public Group(Object[] params) {


		//System.out.println(params[1].toString());
		//System.out.println(params[2].toString());
		//System.out.println(this);
	}
	@Override
	public int[] toIntArray(DistributedOOPL oopl) {
		int[] r = new int[18];
		JL.addPredicate(r,0,oopl.prolog.strStorage.getInt("group"),4, oopl); // points/2
		
		JL.addPredicate(r,3,JL.makeStringKnown(this.name, oopl),0, oopl); // the name
		JL.addPredicate(r,6,JL.makeStringKnown(this.ra, oopl),0, oopl); // the name
		JL.addPredicate(r,9,JL.makeStringKnown(this.type, oopl),0, oopl); // the name
		JL.addNumber(r, 12, this.clock, oopl);

		return r;
		// TODO Auto-generated method stub
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
		return "group(" + name + "," + ra +"," + type + "," + clock + ").";
	}

	@Override
	public String toString() {
		return "Group [name=" + name + ", ra=" + ra + ", type=" + type
				+ ", clock=" + clock + ", time=" + time + "]";
	}

	
}
