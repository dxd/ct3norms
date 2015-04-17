package tuplespace;

import java.sql.Timestamp;
import java.util.Date;

import oopl.DistributedOOPL;
import net.jini.core.entry.Entry;

public class Response implements TimeEntry {
	
	public Integer id;
	public String response;
	public Integer clock;
	public Timestamp time;
	
	public Response() {
		
	}
	public Response(Integer id, String agent, int clock) {
		this.id = id;
		this.response = agent;
		this.clock = clock;
		this.time = new Timestamp(new Date().getTime());
	}
	public Response(Integer clock) {
		this.clock = clock;
	}

	
	public String getResponse() {
		return response;
	}

	public void setResponse(String agent) {
		this.response = agent;
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
		return "Response [id=" + id + ", response=" + response + ", clock=" + clock + ", time="
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
		return "response("+id+"," + response + "," + clock + ").";
	}
}
