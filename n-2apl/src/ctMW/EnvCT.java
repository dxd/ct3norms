package ctMW;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.*; 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jini.core.transaction.TransactionException;
import oopl.DistributedOOPL;
import oopl.GUI.GUI;
import tuplespace.*;
import tuplespace.Prohibition;
import tuplespace.Sanction;
import apapl.Environment;
import apapl.ExternalActionFailedException;
import apapl.data.*;
import aplprolog.prolog.IntHarvester;
import aplprolog.prolog.Prolog;
import aplprolog.prolog.builtins.ExternalActions;
import aplprolog.prolog.builtins.ExternalTool;

import org.openspaces.core.GigaSpace;
import org.openspaces.core.space.UrlSpaceConfigurer;

import com.j_spaces.core.IJSpace;
import com.gigaspaces.events.DataEventSession;
import com.gigaspaces.events.EventSessionConfig;
import com.gigaspaces.events.EventSessionFactory;

import ctagents.recipagents.PhaseWaiter;
import ctagents.recipagents.RecipAgentAdaptor;
import edu.harvard.eecs.airg.coloredtrails.agent.ColoredTrailsClientImpl;
import edu.harvard.eecs.airg.coloredtrails.alglib.BestUse;
import edu.harvard.eecs.airg.coloredtrails.alglib.ShortestPaths;
import edu.harvard.eecs.airg.coloredtrails.client.ClientGameStatus;
import edu.harvard.eecs.airg.coloredtrails.shared.Scoring;
import edu.harvard.eecs.airg.coloredtrails.shared.ScoringUtility;
import edu.harvard.eecs.airg.coloredtrails.shared.discourse.BasicProposalDiscourseMessage;
import edu.harvard.eecs.airg.coloredtrails.shared.discourse.BasicProposalDiscussionDiscourseMessage;
import edu.harvard.eecs.airg.coloredtrails.shared.discourse.DiscourseMessage;
import edu.harvard.eecs.airg.coloredtrails.shared.types.ChipSet;
import edu.harvard.eecs.airg.coloredtrails.shared.types.Goal;
import edu.harvard.eecs.airg.coloredtrails.shared.types.Path;
import edu.harvard.eecs.airg.coloredtrails.shared.types.Phases;
import edu.harvard.eecs.airg.coloredtrails.shared.types.PlayerStatus;
import edu.harvard.eecs.airg.coloredtrails.shared.types.RowCol;


/*
 * Extends Environment to be compatible with 2APL and implements ExternalTool to 
 * be compatible with my Prolog engine. 
 */
public class EnvCT  extends Environment implements ExternalTool {
	//public static JavaSpace space; // shared data
	public int clock = 0;
	public DistributedOOPL oopl; // norm interpreter
	public static String TYPE_STATUS="status", TYPE_PROHIBITION="prohibition", 
			TYPE_OBLIGATION="obligation", TYPE_READINGREQ = "readingRequest",TYPE_READING = "reading",TYPE_INVESTIGATE = "investigate",TYPE_CARGO = "cargo",TYPE_COIN = "coin",TYPE_POINTS = "points",
			TYPE_OBJECT="object", TYPE_INVENTORY="inventory", TYPE_SANCTION="sanction", TYPE_GROUPCOIN="groupCoin", NULL="null"; // for matching string with class type
	public int[] ar_true, ar_null, ar_state_change, ar_false; // precalculated IntProlog data 
	public int INT_TUPLE=0, INT_POINT=0, INT_NULL=0;
	public APAPLTermConverter converter; // Converts between IntProlog and 2APL
	private Prolog2Java p2j;
	private GigaSpace space;
	private DataEventSession session;
	//public static String[] agents1 = {"agent10","agent20","agent30","agent40"};
	private Utilities utilities;

	protected static Map<String, CTAgentHandler> agents = new HashMap();


	/*
	 * Just for testing.
	 */
	public static void main(String[] args){ 
		EnvCT st = new EnvCT();
	}

	/*
	 * A kickoff function to begin the system.
	 */
	public void initializeGS() throws RemoteException {

		//IJSpace ispace = new UrlSpaceConfigurer("jini://*/*/myGrid").space();
		// use gigaspace wrapper to for simpler API
		//this.space = new GigaSpaceConfigurer(ispace).gigaSpace();
		this.space=DataGridConnectionUtility.getSpace("myGrid");
		//space.clear(null);
		dumpGSdata();
		EventSessionConfig config = new EventSessionConfig();
		config.setFifo(true);
		//config.setBatch(100, 20);
		IJSpace ispace = new UrlSpaceConfigurer("jini://*/*/myGrid").space();
		EventSessionFactory factory = EventSessionFactory.getFactory(ispace);
		session = factory.newDataEventSession(config); 
	}



	public void initializeOOPL() throws RemoteException {
		registerOrg();
		p2j = new Prolog2Java();
		// Starting the normative system:
		oopl = new DistributedOOPL(); // Create interpreter object
		//GUI g = new GUI(oopl,"SpaceOrg.2opl","OOPL",null,6677); // Make a GUI for the interpreter
		GUI g = new GUI(oopl,"CTnormsOrg.2opl","OOPL",null,6677); // Make a GUI for the interpreter
		converter = new APAPLTermConverter(oopl.prolog); // Make a term converter (relies on Prolog engine for string storage)
		utilities = new Utilities(oopl,converter,this,p2j);
		//INT_POINT =makeStringKnown("cell");
		//INT_POINT =makeStringKnown("position");
		INT_NULL =makeStringKnown("null"); 
		makeStringKnown("notifyAgent"); 
		makeStringKnown("clock"); 
		makeStringKnown("obligation"); 
		makeStringKnown("prohibition"); 
		makeStringKnown("position");
		makeStringKnown("reading");
		makeStringKnown("investigate");
		makeStringKnown("cargo");
		makeStringKnown("coin");
		makeStringKnown("points");
		makeStringKnown("read"); 
		makeStringKnown("write"); 
		registerActions(oopl.prolog); // Register the possible actions on this ExternalTool (such as @external(space,theAction(arg1,arg2),Result).)
		// Precompute some data: ('true.', 'null.', 'tuple_space_changed.')
		ar_true = oopl.prolog.mp.parseFact("true.", oopl.prolog.strStorage, false); 
		ar_false = oopl.prolog.mp.parseFact("false.", oopl.prolog.strStorage, false); 
		ar_null = oopl.prolog.mp.parseFact("null.", oopl.prolog.strStorage, false);
		ar_state_change = oopl.prolog.mp.parseFact("tuple_space_changed.", oopl.prolog.strStorage, false);
		// To create a IntProlog structure out of a string use the above lines (but replace the fact string such as "true.")
		// Starting the clock 
		Thread t = new Thread(new ClockTicker(this));
		t.start(); 
		//this.insertTestData();


	} 

	/*
	 * Both used for increasing or just reading the clock. 
	 */
	public synchronized int updateClock(int amount){
		//if(amount>0)  oopl.handleEvent(ar_state_change, false); // clock ticked so deadlines can be passed, handleEvent causes the interpreter to check the norms
		clock++;
		Time t = new Time(clock);
		//TimeEntry e = new TimeEntry();		
		//System.out.println(clock);
		//t.setClock(clock);
		space.write(t);
		return clock;
	}

	/*
	 * Constructor immediately initializes the space. 
	 */
	public EnvCT(){
		super();
		
		try {
			File file = new File("./log/"+ new Date(System.currentTimeMillis()) +".log");

			// Create file if it does not exist
			boolean success = file.createNewFile();
			if (success) {
				// File did not exist and was created
			} else {
				// File already exists
			}

			PrintStream printStream;
			try {
				printStream = new PrintStream(new FileOutputStream(file));
				System.setOut(printStream);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (IOException e) {

		}
		try { initializeGS(); 
		initializeOOPL();} 
		catch (Exception e) 
		{ e.printStackTrace(); }
	}



	/////////////////////////CT
	public void throwEvents(APLFunction event, String ... receivers) {
		throwEvent(event, receivers);
	}



	/**
	 * Add a new agent to the game by creating a new CT FairMaxAgent object.
	 * @Override inherited method from Environment class.
	 * Is called immediately after initialization.
	 * @param String agentname containing the name of the agent calling this method.
	 */
	public void addAgent(String agentname) {
		CTAgentHandler client = new CTAgentHandler(this,agentname);
		agents.put(agentname, client);
		System.out.println("[ENV] added agent "+ agentname);  
		register(agentname);
	}

	public Term sendProposal(String agentname) {
		return agents.get(agentname).sendProposal(agentname);
	}


	/**
	 * Make an APLList of an APLFunction. Is called by the 2APL
	 * agent.
	 * @param funct APLFunction (for example thrown by event)
	 * @return APLList
	 */
	public Term functionToList(String agentname, APLFunction funct) throws
	ExternalActionFailedException {
		//System.out.println("[CTAH] APLFunction: " + funct);

		ArrayList<Term> arraylist = funct.getParams();
		//System.out.println("[CTAH] Arraylist: " + arraylist);

		APLList apllist;
		Term[] t = new Term[arraylist.size()];
		arraylist.toArray(t);
		System.out.println("[CTAH] Array of Terms: " + t);

		apllist = new APLList(t);
		System.out.println("[CTAH] The created APLList : " + apllist);

		return apllist;
	}

	/**
	 * Return a list of chips of a specified player
	 * @param agentname
	 * @return Term apllist with color, #chips pairs
	 */
	public Term getOpponentChips(String agentname, APLNum pin) throws ExternalActionFailedException {
		return agents.get(agentname).getOpponentChips(agentname,pin);
	}


	/**
	 * Return a list of chips of the agent
	 * @param agentname
	 * @return Term apllist with color, #chips pairs
	 */
	public Term getAgentChips(String agentname) throws ExternalActionFailedException {
		CTAgentHandler client = agents.get(agentname);
		return client.getAgentChips(agentname);
	}



	/**
	 *
	 * @param agentname
	 */
	public Term getChipsNeeded(String agentname, APLNum opponentid) throws ExternalActionFailedException {
		CTAgentHandler client = agents.get(agentname);
		return client.getChipsNeeded(agentname,opponentid);
	}


	/**
	 *
	 * @param agentname
	 */
	public Term getChipsRedundant(String agentname, APLIdent agentid) throws ExternalActionFailedException {
		CTAgentHandler client = agents.get(agentname);
		return client.getChipsRedundant(agentname,agentid);
	}


	/**
	 * Get the id of an agent
	 * @param agentname The name of the agent
	 * @return id of the agent
	 * @throws apapl.ExternalActionFailedException
	 */
	public Term getAgentId(String agentname)
			throws ExternalActionFailedException {
		CTAgentHandler client = agents.get(agentname);
		return client.getAgentId(agentname);

	}

	/**
	 * Get the id of an agent that is not this agent
	 * @param agentname The name of the agent
	 * @return id (int) the id of the opponent
	 * @throws apapl.ExternalActionFailedException
	 */
	public Term getOpponentId(String agentname)
			throws ExternalActionFailedException {
		CTAgentHandler client = agents.get(agentname);
		return client.getOpponentId(agentname);

	}



	/**
	 * Get the pin of an agent that is not this agent
	 * @param agentname The pin of the agent
	 * @return pin (int) the pin of the opponent
	 * @throws apapl.ExternalActionFailedException
	 */
	public Term getOpponentPin(String agentname)
			throws ExternalActionFailedException {
		CTAgentHandler client = agents.get(agentname);
		return client.getOpponentPin(agentname);

	}


	/**
	 * Get an agent's position in the game.
	 * @returns APLList when succesful containing APLNum(col) and APLNum(row). 
	 * @param String agentname containing the name of the agent calling this method.
	 * @throws ExternalActionFailedException when the function cannot
	 * be executed.
	 * TODO: can only deal with one opponent... 
	 */
	public Term getOpponentPos(String agentname)
			throws ExternalActionFailedException {
		CTAgentHandler client = agents.get(agentname);
		return client.getOpponentPos(agentname);
	}


	/**
	 * Return the current phase of the game
	 */
	public Term getPhase(String agentname)
			throws ExternalActionFailedException {

		CTAgentHandler client = agents.get(agentname);
		return client.getPhase(agentname);

	}



	/**
	 * TODO: extract to chipset conversion in seperate method
	 * */
	public Term getScoreAfterExchange(String agentname, APLNum id, APLList requestedchips) {
		int perId = id.toInt();

		CTAgentHandler client = agents.get(agentname);
		return client.getScoreAfterExchange(agentname,id,requestedchips);

	}


	/**
	 * is GENERIC!
	 */
	public Term getScore(String agentname, APLNum id) {
		CTAgentHandler client = agents.get(agentname);
		return client.getScore(agentname,id);
	}

	// 





	/**
	 * Get the pin of an agent
	 * @param agentname The pin of the agent
	 * @return
	 * @throws apapl.ExternalActionFailedException
	 */
	public Term getAgentPin(String agentname)
			throws ExternalActionFailedException {
		return  agents.get(agentname).getAgentPin(agentname);
	}


	/**
	 * Get the agent's position in the game.
	 * @returns APLList when succesful containing APLNum(col) and APLNum(row). 
	 * @param String agentname containing the name of the agent calling this method.
	 * @throws ExternalActionFailedException when the function cannot
	 * be executed.
	 */
	public Term getAgentPos(String agentname)
			throws ExternalActionFailedException {
		CTAgentHandler client = agents.get(agentname);
		return client.getAgentPos(agentname);
	}

	public Term getGoalId(String agentname, APLNum type, APLNum xcoor, APLNum ycoor) {
		CTAgentHandler client = agents.get(agentname);
		return client.getGoalId(agentname,type,xcoor,ycoor);
	}

	// Return the position of a goal of a specific type
	public Term getGoalPos(String agentname, APLNum type)
			throws ExternalActionFailedException {
		CTAgentHandler client = agents.get(agentname);
		return client.getGoalPos(agentname,type);
	}

	public Term getRole(String agentname, APLNum apl_id)
			throws ExternalActionFailedException {
		CTAgentHandler client = agents.get(agentname);
		return client.getRole(agentname,apl_id);
	}

	public Term makeProposal(String agentname, APLNum responder) {
		CTAgentHandler client = agents.get(agentname);
		client.makeProposal(agentname,responder);
		return new APLNum(1);
	}
	
	public Term setGoal(String agentname, APLIdent agent, APLNum xcoor, APLNum ycoor,APLNum gx, APLNum gy,APLNum sanction) {
		//CTAgentHandler client = agents.get(agentname);
		//client.setGoal(agentname,apl_id,xcoor,ycoor,gx,gy);
		
		SetGoal st = new SetGoal(agent.getName(),new Cell(xcoor.toInt(),ycoor.toInt()),new Cell(gx.toInt(),gy.toInt()),sanction.toInt(),clock);
		System.out.println("agent writes: "+st.toString());
		space.write(st);
		return new APLNum(1);
	}
	
	public Term setColor(String agentname, APLIdent agent, APLIdent color, APLIdent type,APLNum sanction) {
		CTAgentHandler client = agents.get(agentname);
		Color c = new Color(agent.getName(),color.getName(),type.getName(),sanction.toInt(),clock);
		System.out.println("agent writes: "+c.toString());
		space.write(c);
		return new APLNum(1);
	}

	/**
	 * called by 2APL agent, convert APLNums to ints
	 * @param gx: x coordinate of goal
	 * @param gy: y coordinate of goal
	 */

	public Term moveStepToGoal(String agentname, APLNum ax,
			APLNum ay) throws
			ExternalActionFailedException {
		return agents.get(agentname).moveStepToGoal(agentname,ax,ay);
	}



	//////////////////////// 2OPL TO JAVASPACE AND 2APL
	/*
	 * Make sure String s has an index in the Prolog engine.
	 */
	public int makeStringKnown(String s){
		if(oopl.prolog.strStorage.getInt(s)==null) oopl.prolog.strStorage.add(s);
		return oopl.prolog.strStorage.getInt(s);
	}
	
	/*
	 * Make the possible external actions known to the Prolog engine. These will be the actions that
	 * the organization can do.
	 */
	@Override
	public void registerActions(Prolog p) { 
		oopl.prolog.builtin.external.registerAction("read", this, ExternalActions.INTAR, ExternalActions.INTAR);
		oopl.prolog.builtin.external.registerAction("write", this, ExternalActions.INTAR, ExternalActions.INTAR);
		oopl.prolog.builtin.external.registerAction("notifyAgent", this, ExternalActions.INTAR, ExternalActions.INTAR);
		oopl.prolog.builtin.external.registerAction("clock", this, ExternalActions.INTAR, ExternalActions.INTAR);
	}

	/*
	 * Handle a call from the organization (actually: from Prolog). These calls are in IntProlog datatypes (int arrays). 
	 * ExternalActions ea is a part of the Prolog engine which reads returns ea.intResult after the
	 * external call.
	 */
	@Override
	public void handleCall(int[] call, ExternalActions ea, int returnType) {  
		/*
		 * For JavaSpace calls: the integer array is first transformed to an Entry object, then passed
		 * to the JavaSpaced using the appropriate method call, and then the result is converted back
		 * to an integer array.
		 */
		if(call[1] == oopl.prolog.strStorage.getInt("read")){
			try {
				//TODO rewrite
				TimeEntry a = utilities.createEntry(call);
				//System.out.println(a.toString());
				TimeEntry e = getLast(a);
				//System.out.println(e.toString());
				ea.intResult = utilities.entryToArray(null);
			} catch (Exception e) {e.printStackTrace();}
		} else if(call[1] == oopl.prolog.strStorage.getInt("write")){
			//System.out.println("write (points)");
			try {
				//long lease = utilities.get_number(call,oopl.prolog.harvester.scanElement(call, 3, false, false)+1);
				//if(lease <= 0) lease = Lease.FOREVER;

				TimeEntry e = utilities.createEntry(call);
				//System.out.println(e);
				if (e.getTime() == null)
					e.setTime();
				if (e.getClock() == null) {
					//updateClock(0);
					e.setClock(clock);
				}
				System.out.println("Organization writes: "+e.toString());
				space.write(e);
				//System.out.println(e+"  "+lease+"   "+Lease.FOREVER);
				ea.intResult = ar_true;
			} catch (Exception e) {e.printStackTrace();}
			/*
			 * The next case throws towards the object an event that its status is changed.
			 */
		} else if(call[1] == oopl.prolog.strStorage.getInt("notifyAgent")){ // notifyAgent(name,obligation(blabla)).

			String recipient = oopl.prolog.strStorage.getString(call[4]);
			APLFunction event = (APLFunction)converter.get2APLTerm(Arrays.copyOfRange(call, 6, call.length));
			TimeEntry e = utilities.createEntry(recipient, event);
			if (e.getTime() == null)
				e.setTime();
			if (e.getClock() == null) {
				//updateClock(0);
				e.setClock(clock);
			}
			System.out.println("Organization notifies object (write): "+e.toString());
			space.write(e);

			//throwEvent(event, new String[]{recipient});
			ea.intResult = ar_true;
		} else if(call[1] == oopl.prolog.strStorage.getInt("clock")){ // Read the clock
			int[] r = new int[3];
			utilities.addNumber(r, 0, updateClock(0)); // Use updateClock because of synchronization
			ea.intResult = r;
		}
	}


	@Override
	public void handleCall(Object[] call, ExternalActions p, int returnType) { }






	//from object program
	public Term read(String sAgent, APLFunction call, APLNum timeOut){

		try{ 
			TimeEntry te = utilities.createEntry(sAgent,call);
			System.out.println("Agent reads: "+te.toString());
			TimeEntry te1 = space.read(te, 2000);
			System.out.println("Agent finds: "+te1.toString());
			return utilities.entryToTerm(te1); 
		} catch(Exception e){ e.printStackTrace(); return new APLIdent("null"); }
	}

	public Term write(String sAgent, APLFunction call, APLNum lease){ 
		System.out.println("write " + sAgent);
		try{

			TimeEntry e = utilities.createEntry(sAgent,call);
			if (e.getTime() == null)
				e.setTime();
			if (e.getClock() == null) {
				//updateClock(0);
				e.setClock(clock);
			}
			System.out.println("Agent writes: "+e.toString());
			space.write(e);

			return new APLIdent("true");
		}catch (Exception e){ e.printStackTrace(); return new APLIdent("null"); }
	}


	/*
	 * ENVIRONMENT OVERRIDES

	@Override
	public void addAgent(String sAgent) {
		System.out.println("register " + sAgent);

			register(sAgent);

	}
	 */
	private void register(String agent) {

		AgentHandler handler;
		try {
			handler = new AgentHandler(this, agent);
			try {
				session.addListener(new Prohibition(agent), handler);
				session.addListener(new Obligation(agent), handler); 
				session.addListener(new Points(agent), handler);
				session.addListener(new Chip(agent), handler);
				session.addListener(new Time(), handler);
				session.addListener(new Tile(), handler);
				session.addListener(new Position(agent), handler);
				
				//session.addListener(new Proposal(), handler);
				//session.addListener(new Response(), handler);
				
			} catch (TransactionException e) {
				e.printStackTrace();
			} 

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private TimeEntry getLast(TimeEntry a) {
		try {
			TimeEntry[] result = space.readMultiple(a);
			if (result.length > 0)
				return getLatest(result);
		} catch (Exception e) {
			e.printStackTrace();
		} 

		return null;
	}

	private TimeEntry getLatest(TimeEntry[] result) {
		Arrays.sort(result, new Comparator<TimeEntry>(){
			@Override
			public int compare(TimeEntry t1, TimeEntry t2) {
				return t1.getTime().compareTo(t2.getTime());
			}

		});
		System.out.println("latest      "+result[result.length-1]);
		return result[result.length-1];
	}

	public void notifyAgent(String agent, TimeEntry e) {
		Term t = utilities.entryToTerm(e);
		if (t.toString() == "null")
			return;
		throwEvent((APLFunction) t, new String[]{agent});
		System.out.println("Event sent to object      "+agent+ " " +t.toString());

	}
	public void notifyAgent(String agent, ArrayList<TimeEntry> r) {
		for (TimeEntry te : r) {
			notifyAgent(agent,te);
		}
	}

	public void notifyOrg(TimeEntry te) {
		//System.out.println("org notified found "+te.toString());
		System.out.println("org notified sent "+te.toPrologString());
		//int[] OOPLformat = te.toIntArray(oopl);
		//oopl.handleEvent(ar_state_change, false);
		oopl.handleEvent(oopl.getProlog().mp.parseFact(te.toPrologString(),oopl.getProlog().strStorage,false),false);
	}

	private void registerOrg() throws RemoteException {

		OrgHandler handler = new OrgHandler(this);

		try {

			session.addListener(new Position(), handler); 
			session.addListener(new tuplespace.Goal(), handler); 
			session.addListener(new Chip(), handler); 
			session.addListener(new Time(), handler); 
			session.addListener(new Tile(), handler); 
			session.addListener(new Proposal(), handler);
			session.addListener(new Response(), handler);
			session.addListener(new Color(), handler);
			session.addListener(new SetGoal(), handler);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransactionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}


	/* private void insertTestData()
    {
    	Cargo cargo = new Cargo(5, new Cell(10,10), 1);
    	Points p1 = new Points("a1", 1000, 1);
    	Points p2 = new Points("a2", 1000, 1);
    	Points p3 = new Points("a3", 1000, 1);
    	Points p4 = new Points("c1", 1000, 1);
    	Points p5 = new Points("t1", 1000, 1);
    	Reading r1 = new Reading(11, "a1", new Cell(11,11), 1, 50);
    	Reading r2 = new Reading(12, "a2", new Cell(1,11), 1, 60);
    	Reading r3 = new Reading(13, "a3", new Cell(11,1), 1, 10);
    	Coin c1 = new Coin(10, new Cell(15,15), "a1", 1);
    	Coin c2 = new Coin(20, new Cell(1,15), "a2", 1);
    	Coin c3 = new Coin(30, new Cell(15,1), "a3", 1);
    	Time t1 = new Time(0);
    	Prohibition px = new Prohibition("t1","[at(5, 5, t1)]", "[reduce_300(t1)]",0);
    	try {
			space.write(cargo, null, Lease.FOREVER);
			space.write(p1, null, Lease.FOREVER);
			space.write(p2, null, Lease.FOREVER);
			space.write(p3, null, Lease.FOREVER);
			space.write(p4, null, Lease.FOREVER);
			space.write(p5, null, Lease.FOREVER);
			space.write(r1, null, Lease.FOREVER);
			space.write(r2, null, Lease.FOREVER);
			space.write(r3, null, Lease.FOREVER);
			space.write(c1, null, Lease.FOREVER);
			space.write(c2, null, Lease.FOREVER);
			space.write(c3, null, Lease.FOREVER);
			space.write(t1, null, Lease.FOREVER);
			space.write(px, null, Lease.FOREVER);

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransactionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



    }

	 */

	private void dumpGSdata() {
		TimeEntry entry;
		//Entry temp = new Time();
		System.out.println("-------------------------last log tuples start--------------------------------");
		ArrayList<TimeEntry> result = new ArrayList<TimeEntry>();
		while ((entry = (TimeEntry) space.take(null)) != null){
			System.out.println(entry.toString());
			result.add(entry);
		}
		System.out.println("-------------------------last log tuples end----------------------------------");
		return;
	}


}
