package ctMW;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import ctagents.recipagents.PhaseWaiter;
import ctagents.recipagents.RecipAgentAdaptor;
import RecipExperiment.RecipConstants;
import apapl.ExternalActionFailedException;
import apapl.data.APLFunction;
import apapl.data.APLIdent;
import apapl.data.APLList;
import apapl.data.APLNum;
import apapl.data.Term;
import aplprolog.prolog.builtins.ExternalTool;
import edu.harvard.eecs.airg.coloredtrails.agent.ColoredTrailsClientImpl;
import edu.harvard.eecs.airg.coloredtrails.agent.events.PhasesUpdatedEventListener;
import edu.harvard.eecs.airg.coloredtrails.agent.events.PlayersUpdatedEventListener;
import edu.harvard.eecs.airg.coloredtrails.alglib.BestUse;
import edu.harvard.eecs.airg.coloredtrails.alglib.ShortestPaths;
import edu.harvard.eecs.airg.coloredtrails.client.ClientGameStatus;
import edu.harvard.eecs.airg.coloredtrails.shared.Scoring;
import edu.harvard.eecs.airg.coloredtrails.shared.ScoringUtility;
import edu.harvard.eecs.airg.coloredtrails.shared.discourse.BasicProposalDiscourseMessage;
import edu.harvard.eecs.airg.coloredtrails.shared.discourse.BasicProposalDiscussionDiscourseMessage;
import edu.harvard.eecs.airg.coloredtrails.shared.discourse.DiscourseMessage;
import edu.harvard.eecs.airg.coloredtrails.shared.types.Board;
import edu.harvard.eecs.airg.coloredtrails.shared.types.ChipSet;
import edu.harvard.eecs.airg.coloredtrails.shared.types.Goal;
import edu.harvard.eecs.airg.coloredtrails.shared.types.Path;
import edu.harvard.eecs.airg.coloredtrails.shared.types.Phases;
import edu.harvard.eecs.airg.coloredtrails.shared.types.PlayerStatus;
import edu.harvard.eecs.airg.coloredtrails.shared.types.RowCol;

public class CTAgentHandler implements RecipAgentAdaptor{
	private ColoredTrailsClientImpl client;
	private EnvCT env;

	ClientGameStatus cgs = null;
	private double bestScore = -1;
	private Hashtable<Integer, DiscourseMessage> messages;
	private boolean game_initialized = false;
	private int MyPerGameId;
	private int OppPerGameId;
	private String agentname;
	private String[] proposals = new String[] {null,null,null,null,null}; 
	private String[] nogo = new String[] {null,null,null,null,null};
	private ArrayList<RowCol> nopoints = new ArrayList<RowCol>();

	public CTAgentHandler(EnvCT envCT, String agentname) {
		env = envCT;
		this.agentname = agentname.substring(1);	
		addAgent();
		messages = new Hashtable<Integer, DiscourseMessage>();
	}

	private void addAgent() {

		client = new ColoredTrailsClientImpl();
		setClientName(agentname);
		start();
		System.out.println("[CTAH] started agent "+ agentname);  
		//cgs = client.getGameStatus();
		//System.out.println("[CTAH] scoring "+ cgs.getScoring());   

		client.addDiscourseMessageEventListener(this);
		client.addGameEndedEventListener(this);
		client.addGameInitializedEventListener(this);		
		client.addGameStartEventListener(this);		
		client.addPhasesAdvancedEventListener(this);
		//client.addPhasesUpdatedEventListener(this);
		//client.addPlayersUpdatedEventListener(this);

		System.out.println("[CTAH] Added a new agent to the game by the name of "
				+ agentname + ".");   

	}
	/**
	 * Called by the server when the game configuration class' run() method completes
	 */
	//NEVER CALLED
	public void gameInitialized()
	{
		System.out.println("#########################Game Initialized");
		//game_initialized = true;
		cgs = client.getGameStatus();

		String phaseName = cgs.getPhases().getCurrentPhaseName();
		System.out.println("AGENT " + client.getName() + ": current phase name: " + phaseName);
		System.out.println("we have " + client.getGameStatus().getBoard().getGoals().size() + " goals");
		System.out.println("we have " + client.getGameStatus().getScoring() + " scoring");
	}
	/**
	 * Called when a game ends
	 */
	public void gameEnded() {
		System.out.println("Game ended ");
		System.out.println("My PlayerStatus is: " + client.getGameStatus().getMyPlayer());
		APLFunction event = new APLFunction("message",
				new APLIdent("game_ended"));
		env.throwEvents(event,"a"+agentname);
	}

	/**
	 * Gets the client name
	 */
	public String getClientName() {
		return client.getPin();
	}




	public Term sendProposal(String agentname) {
		//makeProposal(agentname,new APLNum(OppPerGameId));
		//return new APLNum(-1);


		ArrayList<ChipSet> exchange = (ArrayList<ChipSet>) strategy(OppPerGameId);
		ChipSet senderChips;
		ChipSet recipientChips;

		if(exchange == null) {
			System.out.println("No beneficial " +
					"exchanges found among the ones that are beneficial for the responder");
			return new APLNum(-1);
		}
		else {
			System.out.println("EXCHANGE: " + exchange);
			senderChips = exchange.get(1);
			recipientChips = exchange.get(0);
			BasicProposalDiscourseMessage proposal= new BasicProposalDiscourseMessage(
					cgs.getPerGameId(), OppPerGameId, -1, senderChips, recipientChips);
			//            sending = senderChips;

			// PhaseWaiter waiter = new PhaseWaiter(cgs.getPhases());
			// waiter.doWait(RecipConstants.minProposeTime, RecipConstants.maxProposeTime);


			client.communication.sendDiscourseRequest(proposal);

			int messageId = proposal.getMessageId();
			System.out.println("message id: " + messageId);
			APLNum msgId = new APLNum(messageId);
			return msgId; 
		}
	}
	public int makeProposal(String agentname, int i, String color) {

		ChipSet senderChips = new ChipSet();
		ChipSet recipientChips = new ChipSet();
		recipientChips.add(color, 1);

		Random rnd = new Random();
		int msgID = rnd.nextInt(10000);

		BasicProposalDiscourseMessage proposal= new BasicProposalDiscourseMessage(
				cgs.getPerGameId(), i, msgID, senderChips, recipientChips);
		//            sending = senderChips;

		// PhaseWaiter waiter = new PhaseWaiter(cgs.getPhases());
		// waiter.doWait(RecipConstants.minProposeTime, RecipConstants.maxProposeTime);


		client.communication.sendDiscourseRequest(proposal);
		return msgID;


	}
	/**
	 * Called when a discourse message is received
	 * @param dm discourse message received
	 */
	public void onReceipt(DiscourseMessage dm) {
		System.out.println("Received a " + dm.getClass() );
		// check if it is a basic proposal discourse message
		String type = dm.getMsgType();
		System.out.println("[MSG] Message is of type: " + type);
		System.out.println("[MSG] Message is for: " + dm.getToPerGameId());
		System.out.println("[MSG] i'm: " + cgs.getPerGameId());

		
		if (dm.getToPerGameId() == cgs.getPerGameId()) {

			messages.put(dm.getMessageId(), dm);
			BasicProposalDiscourseMessage proposal = (BasicProposalDiscourseMessage) dm;
			if (type.equals("basicproposaldiscussion")) {
				BasicProposalDiscussionDiscourseMessage response = (BasicProposalDiscussionDiscourseMessage) dm;
				System.out.println("AGENT " + ": got response to offer");
				//			for (String color : response.getChipsSentByResponder().getColors()) {
				proposals[dm.getFromPerGameId()] = null;
				APLFunction event = null;
				if(!response.accepted() ) {
					// The proposal is rejected and we still have more paths to propose, make an offer
					event = new APLFunction("message",
							new APLIdent("response"), new APLNum(dm.getMessageId()),
							new APLIdent("reject"));
					for (String color : response.getChipsSentByResponder().getColors()) {
						if (response.getChipsSentByResponder().getNumChips(color) > 0)
							nogo[dm.getFromPerGameId()]=color;
					}
				}
				else if(response.accepted()) {
					// The proposal is accepted
					event = new APLFunction("message",
							new APLIdent("response"), new APLNum(dm.getMessageId()),
							new APLIdent("accept"));
				}
				env.throwEvents(event,"a"+agentname);
			}

			else if (type.equals("basicproposal")) {
				//BasicProposalDiscussionDiscourseMessage responseMessage = new BasicProposalDiscussionDiscourseMessage(proposal );
				// check if the proposal is beneficial

				//boolean offerResponse = RespondStrategy(ChipSet.subChipSets(proposal.getChipsSentByResponder(), proposal.getChipsSentByProposer() ),dm.getFromPerGameId());
				System.out.println("Received a proposal creating event");
				APLFunction event = null;

					event = new APLFunction("proposal",
							new APLIdent("proposal"),new APLNum(dm.getFromPerGameId()), new APLNum(dm.getMessageId()),
							new APLIdent("open"));
	
				env.throwEvents(event,"a"+agentname);
				//client.communication.sendDiscourseRequest(responseMessage);
			}

		}

		// System.out.println("Received a message not for me");


	}






	/**
	 * Called when a phase advances
	 */
	public void phaseAdvanced(Phases ph) {
		Scoring scoring = cgs.getScoring();
		String phaseName = cgs.getPhases().getCurrentPhaseName();
		if(bestScore == -1) {
			BestUse bu = new BestUse(cgs, cgs.getMyPlayer(), scoring, 0);     // calculate the best use of player's chips
			bestScore = bu.getBestState().getScore();
		}

		APLFunction event = new APLFunction("message",
				new APLIdent("phasechange"));
		env.throwEvents(event,"a"+agentname);
		if(phaseName.equals("Offer Phase")) {

		}
	}

	/**
	 * Sets the client name
	 * @param name client name
	 */
	public void setClientName(String name) {
		client.setPin(name);
	}

	/**
	 * Starts the client
	 */
	public void start() {
		client.start();
	}



	/**
	 * Strategy of the proposer
	 * @param o null
	 * @param id 
	 * @return An exchange to propose
	 */
	public ArrayList<ChipSet> strategy( int id) {
		// Get all possible unique exchanges between the players
		Set<ArrayList<ChipSet>> allExchanges = ChipSet.getAllExchanges(
				cgs.getMyPlayer().getChips(), cgs.getPlayerByPerGameId(id).getChips());

		//System.out.println("Total number of unique exchanges: " + allExchanges.size());
		ArrayList<ChipSet> mostBeneficialExchange = null;

		//basic sanity checking
		//System.out.println("my player info: " + cgs.getMyPlayer().toString());
		//System.out.println("my opponent info: " + cgs.getPlayerByPerGameId(id).toString());
		// System.out.println("scoring: " + cgs.getScoring());

		ScoringUtility SU = new ScoringUtility(cgs, cgs.getPerGameId(), id);
		// System.out.println("here " );
		ChipSet offer = SU.getFairMaxOffer();
		//System.out.println("offer: " + offer.toString());
		ChipSet propChips = ChipSet.getNegation(offer);
		ChipSet respChips = new ChipSet(offer);
		for(String color : propChips.getColors()){
			if(propChips.getNumChips(color) < 0)
				propChips.set(color, 0);
		}

		for(String color : respChips.getColors()){
			if(respChips.getNumChips(color) < 0)
				respChips.set(color, 0);
		}

		mostBeneficialExchange = new ArrayList<ChipSet>();
		mostBeneficialExchange.add(propChips);
		mostBeneficialExchange.add(respChips);

		return mostBeneficialExchange;
	}

	public boolean RespondStrategy(ChipSet proposal,int id) {
		//        // our input is a proposal
		//        System.out.println("Received proposal: " + proposal);
		//
		//        BestUse bu = new BestUse(cgs, cgs.getMyPlayer(), scoring, 0);
		//        double MyDefaultScore = bu.getBestState().getScore();
		//
		//        bu = new BestUse(cgs, cgs.getPlayerByPerGameId(OppPerGameId), scoring, 0);
		//        double OppDefaultScore = bu.getBestState().getScore();
		//
		//        if(payoff(proposal) > MyDefaultScore*OppDefaultScore)
		//            return(true);
		//        else
		//            return(false);

		ScoringUtility SU = new ScoringUtility(cgs, id, cgs.getPerGameId());
		//        double oppBenefit = SU.getOfferScore(proposal, OppPerGameId) - SU.getDefaultScore(OppPerGameId);
		double myBenefit = SU.getOfferScore(proposal, cgs.getPerGameId()) - SU.getDefaultScore(cgs.getPerGameId());
		if(  (myBenefit >= 0))
			return(true);
		else
			return(false);

	}
	public void gameStarted() {
		System.out.println("#########################Game started");
		cgs = client.getGameStatus();
		MyPerGameId = cgs.getMyPlayer().getPerGameId();
		for(PlayerStatus ps : cgs.getPlayers()){
			if(ps.getPerGameId() == MyPerGameId)
				continue;
			else
				OppPerGameId = ps.getPerGameId();
		}
		String initialize = "game_initialized";
		APLFunction event = new APLFunction("message",
				new APLIdent(initialize));
		env.throwEvents(event,"a"+agentname);       
	}




	//Broken
	public Term requestChips(String agentname, APLNum opppin, APLNum oppid) throws ExternalActionFailedException {
		System.out.println("[CTAH] ==========> IN REQUEST CHIPS");
		int opponentid = oppid.toInt();
		// String goalid = id.toString();
		int opponentpin = opppin.toInt();
		ChipSet myChips = cgs.getMyPlayer().getChips();
		//ChipSet chips = myChips.getMissingChips(cs)

		//Term apllist = convertChipSet(chips);

		//System.out.println("[CTAH] REQUEST CHIPS: RETURNING LIST");
		return null; 
	}


	/**
	 * Convert a ChipSet to an APLlist with [colorname, colornr] pairs
	 * @param c the ChipSet to be converted
	 * @return
	 * TODO: rewrite such that it constructs lists with linkedlist? 
	 */
	public Term convertChipSet(ChipSet chips) throws ExternalActionFailedException {
		// create a list for the [color, #chips] pairs
		APLList pair;
		Term[] pr = new Term[2];

		// create a list for all the pairs: [[color, nr],[color,nr],..]
		APLList apllist;
		Term[] t = new Term[chips.getColors().size()];

		// check whether chipset is empty
		if (chips.isEmpty()) {
			throw new ExternalActionFailedException("[CTAH] Empty list");
			//return new APLIdent("empty");
		}
		else {
			// create a counter to add items to a list
			int i = 0;

			//cycle through chipset
			for (String clr : chips.getColors()) {
				String color = clr.toLowerCase();
				APLIdent colorname = new APLIdent(color);
				pr[0] = colorname;
				APLNum nr = new APLNum(chips.getNumChips(clr));
				pr[1] = nr;
				//System.out.println("[CTAH] List pr with the pair is now: " + pr);
				pair = new APLList(pr);
				t[i] = pair;
				//System.out.println("[CTAH] List t with pairs is now: " + t);
				i++;
			}
			// create list of all color,nr pairs
			apllist = new APLList(t);
			return apllist;
		}
	}


	/**
	 * Find the player closest to the given coordinates. These can also
	 * be the coordinates of the current agent.
	 * @param x APLNum X-coordinate
	 * @param y APLNum Y-coordinate
	 * @return An APLList of APLLists of [playerpin, posx, posy]
	 */

	//     public Term findPlayerClosestTo(String agentname, APLNum x, APLNum y)
	//                        throws ExternalActionFailedException {
	//        int xcoor = x.toInt();
	//        int ycoor = y.toInt();
	//        HashMap players = agents.get(agentname).findPlayerClosestTo(xcoor, ycoor);
	//        Set<Integer> playerPins = players.keySet();
	//        APLList closestPlayers;
	//        // make a list of players that corresponds with the size of the CT HashMap
	//        Term[] t = new Term[playerPins.size()];
	//        int i = 0;
	//
	//        // convert each <pin, RowCol> pair to an APLList and put this in the
	//        // list of players
	//        for (int pin : playerPins) {
	//            RowCol pos = (RowCol) players.get(pin);
	//            int row = pos.row;
	//            int col = pos.col;
	//            APLList position = new APLList(new Term[]
	//                    {new APLNum(pin), new APLNum(row), new APLNum(col)});
	//            t[i] = position;
	//            i++;
	//        }
	//
	//        closestPlayers = new APLList(t);
	//        System.out.println("[CTAH] This is what closestPlayers looks like: " + closestPlayers);
	//        return closestPlayers;
	//     }



	/**
	 * Return a list of chips of a specified player
	 * @param agentname
	 * @return Term apllist with color, #chips pairs
	 */
	public Term getOpponentChips(String agentname, APLNum pin) throws ExternalActionFailedException {

		int p = pin.toInt();
		//System.out.println("[CTAH] Name (PIN!) of the player who's chipset we want: " +p);

		ChipSet chips = cgs.getPlayerByPerGameId(getPerGameID(p)).getChips();
		Term aplterm = convertChipSet(chips);
		return aplterm;
	}

	private int getPerGameID(int pin) {

		ClientGameStatus cgs = client.getGameStatus();
		for( PlayerStatus player : cgs.getPlayers() ) {
			int playerID = player.getPin();
			if( playerID != pin )
				return player.getPerGameId();
		}
		throw new RuntimeException("Responder ID not found.");
		//return -1;
	}

	/**
	 * Return a list of chips of the agent
	 * @param agentname
	 * @return Term apllist with color, #chips pairs
	 */
	public Term getAgentChips(String agentname) throws ExternalActionFailedException {

		int p = this.cgs.getMyPlayer().getPin();
		//System.out.println("[CTAH] Name (PIN!) of the player who's chipset we want: " +p);
		return getOpponentChips(agentname, new APLNum(p));
	}



	/**
	 *
	 * @param agentname
	 */
	public Term getChipsNeeded(String agentname, APLNum opponentid) throws ExternalActionFailedException {
		int opp_id = opponentid.toInt();
		ChipSet myChips = cgs.getMyPlayer().getChips();
		ArrayList<Path> shortestPaths = ShortestPaths.getShortestPathsToFirstGoal(cgs.getMyPlayer().getPosition(),
				cgs.getBoard(), cgs.getScoring());
		Path chosenPath = shortestPaths.remove(0); // Get the best path available
		ChipSet requiredChipsForPath = chosenPath.getRequiredChips(cgs.getBoard());
		ChipSet missing = myChips.getMissingChips(requiredChipsForPath);
		//ChipSet missing = agents.get(agentname).getChipsNeeded(opp_id);
		System.out.println("[CTAH] Received the missing chipset: " + missing);

		Term aplterm = convertChipSet(missing);

		return aplterm;
	}


	/**
	 *
	 * @param agentname
	 */
	public Term getChipsRedundant(String agentname, APLIdent agentid) throws ExternalActionFailedException {
		ChipSet myChips = cgs.getMyPlayer().getChips();
		ArrayList<Path> shortestPaths = ShortestPaths.getShortestPathsToFirstGoal(cgs.getMyPlayer().getPosition(),
				cgs.getBoard(), cgs.getScoring());
		Path chosenPath = shortestPaths.remove(0); // Get the best path available
		ChipSet requiredChipsForPath = chosenPath.getRequiredChips(cgs.getBoard());
		ChipSet extra = myChips.getExtraChips(requiredChipsForPath);
		System.out.println("[CTAH] Received the redundant chipset: " + extra);

		Term aplterm = convertChipSet(extra);

		return aplterm;
	}


	/**
	 * Get the id of an agent
	 * @param agentname The name of the agent
	 * @return id of the agent
	 * @throws apapl.ExternalActionFailedException
	 */
	public Term getAgentId(String agentname)
			throws ExternalActionFailedException {
		int id = cgs.getPerGameId();

		APLNum aplid = new APLNum(id);
		return aplid;

	}

	/**
	 * Get the id of an agent that is not this agent
	 * @param agentname The name of the agent
	 * @return id (int) the id of the opponent
	 * @throws apapl.ExternalActionFailedException
	 */
	public Term getOpponentId(String agentname)
			throws ExternalActionFailedException {

		int proposerID = cgs.getPerGameId();
		ClientGameStatus cgs = client.getGameStatus();
		for( PlayerStatus player : cgs.getPlayers() ) {
			int playerID = player.getPerGameId();
			if( playerID != proposerID )
				return new APLNum(playerID);
		}
		throw new RuntimeException("Responder ID not found.");
		//return -1;
	}



	/**
	 * Get the pin of an agent that is not this agent
	 * @param agentname The pin of the agent
	 * @return pin (int) the pin of the opponent
	 * @throws apapl.ExternalActionFailedException
	 */
	public Term getOpponentPin(String agentname)
			throws ExternalActionFailedException {
		int pin = Integer.parseInt(getOpponentId(agentname).toString());

		APLNum aplid = new APLNum(cgs.getPlayerByPerGameId(pin).getPin());
		return aplid;

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
		APLList agentPos;
		int id = Integer.parseInt(getOpponentId(agentname).toString());
		RowCol rc = cgs.getPlayerByPerGameId(id).getPosition();
		try {
			int col = rc.col;
			//System.out.println("[CTAH] " + agentname + " received AgentPosCol: " + col);
			int row = rc.row;
			//System.out.println("[CTAH] " + agentname + " received AgentPosRow: " + row);
			agentPos = new APLList(new Term[] {
					new APLNum(col), new APLNum(row)
			});
			//System.out.println("[CTAH] New APLList Position for " + agentname + ":" + agentPos);
		}
		catch (Exception e) {
			//System.out.println("[CTAH] An exception was caught: " + e.getMessage());
			return new APLNum(-1);
		}
		System.out.println("[CTAH] Agentposition: " + agentPos);
		return agentPos;
	}


	/**
	 * Return the current phase of the game
	 */
	public Term getPhase(String agentname)
			throws ExternalActionFailedException {

		String phase = cgs.getPhases().getCurrentPhaseName();
		if (phase.contains(" ")) {
			String[] ph = phase.split(" ");
			phase = ph[0] + ph[1];
		}

		APLIdent p = new APLIdent(phase.toLowerCase());
		return p;

	}



	/**
	 * TODO: extract to chipset conversion in seperate method
	 * */
	public Term getScoreAfterExchange(String agentname, APLNum id, APLList requestedchips) {
		int perId = id.toInt();


		LinkedList<Term> chips = requestedchips.toLinkedList();
		//  HashMap<String, Integer> proposal = new HashMap();
		ChipSet chipset = new ChipSet();

		Set<String> ctclr = cgs.getBoard().getColors();
		String[] ctcolors = new String[ctclr.size()];
		ctclr.toArray(ctcolors);

		HashMap<String, String> colorsmap = new HashMap();

		// link the normal ct colors to their lower case versions
		for (String color: ctcolors) {
			String colorLC = color.toLowerCase();
			colorsmap.put(colorLC, color);
		}

		for (int i = 0; i<chips.size(); i++) {
			APLList item = (APLList) chips.get(i);
			LinkedList<Term> itemlist = item.toLinkedList();

			// modify color
			APLIdent colorapl = (APLIdent) itemlist.get(0);
			String clr = colorapl.toString();
			String originalcolor = colorsmap.get(clr);

			// modify amount of chips
			APLNum amountapl = (APLNum) itemlist.get(1);
			int amount = amountapl.toInt();

			// add to the proposal chips
			chipset.add(originalcolor, amount);
		}

		//int score = agents.get(agentname).getScoreAfterExchange(perId, chipset);

		APLNum aplscore = new APLNum(0);
		return aplscore;
	}


	/**
	 * is GENERIC!
	 */
	public Term getScore(String agentname, APLNum id) {
		int p = id.toInt();
		int score = cgs.getPlayerByPerGameId(p).getScore();

		APLNum aplscore = new APLNum(score);
		return aplscore;
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
		int pin = cgs.getMyPlayer().getPin();

		APLNum aplpin = new APLNum(pin);
		return aplpin;

	}

	/*
	public APLList getBoard() {
		ClientGameStatus cgs = agent.getGameStatus();
		return cgs.getBoard();
	}

	plic APLList getPathtoGoal() {

	}
	 */

	/**
	 * Get the agent's position in the game.
	 * @returns APLList when succesful containing APLNum(col) and APLNum(row). 
	 * @param String agentname containing the name of the agent calling this method.
	 * @throws ExternalActionFailedException when the function cannot
	 * be executed.
	 */
	public Term getAgentPos(String agentname)
			throws ExternalActionFailedException {
		APLList agentPos;
		RowCol rc = cgs.getMyPlayer().getPosition();
		try {
			int col = rc.col;
			//System.out.println("[CTAH] " + agentname + " received AgentPosCol: " + col);
			int row = rc.row;
			//System.out.println("[CTAH] " + agentname + " received AgentPosRow: " + row);
			agentPos = new APLList(new Term[] {
					new APLNum(col), new APLNum(row)
			});
			//System.out.println("[CTAH] New APLList Position for " + agentname + ":" + agentPos);
		}
		catch (Exception e) {
			//System.out.println("[CTAH] An exception was caught: " + e.getMessage());
			return new APLNum(-1);
		}
		System.out.println("[CTAH] Agentposition: " + agentPos);
		return agentPos;
	}


	//    public Term getAgentRole(String agentname) {
	//        int pin = agents.get(agentname).getAgentRole();
	//
	//        String agentrole = agents.get(agentname).getRole(pin).toLowerCase();
	//
	//        APLIdent role = new APLIdent(agentrole);
	//        return role;
	//
	//    }


	public Term getGoalId(String agentname, APLNum type, APLNum xcoor, APLNum ycoor) {
		int t = type.toInt();
		int x = xcoor.toInt();
		int y = ycoor.toInt();

		RowCol goalloc = new RowCol(x, y);
		Set<Goal> goals = cgs.getBoard().getGoals();
		String id = null;
		if (goals != null)

			for (Goal g : goals) {
				if (g.getLocation().equals(goalloc)) {
					id = g.getID();
					return new APLIdent(id);
				}
			}


		return new APLNum(-1);

	}

	// Return the position of a goal of a specific type
	public Term getGoalPos(String agentname, APLNum type)
			throws ExternalActionFailedException {

		int t = type.toInt();

		APLList goalPos;

		try {
			RowCol rc;
			if (t == -1)
				rc = cgs.getBoard().getGoalLocations().get(0);
			else			
				rc = cgs.getBoard().getGoalLocations(t).get(0);

			int col = rc.col;
			//System.out.println("[CTAH] " + agentname + " received GoalPosCol: " + col);
			int row = rc.row;
			//System.out.println("[CTAH] " + agentname + " received GoalPosRow: " + row);
			goalPos = new APLList(new Term[] {
					new APLNum(col), new APLNum(row)
			});
			//System.out.println("[CTAH] New APLList GoalPosition for " + agentname + ":" + goalPos);
		}
		catch (Exception e) {
			//System.out.println("[CTAH] An exception was caught: " + e.getMessage());
			return new APLNum(-1);
		}
		return goalPos;
	}

	public Term getRole(String agentname, APLNum apl_id)
			throws ExternalActionFailedException {

		int id = apl_id.toInt();
		String role = cgs.getPlayerByPerGameId(id).getRole();

		if (role != "")
			return new APLIdent(role);
		else
			return new APLNum(-1);


	}


	//   public Term getResponseAnswer(String agentname, APLNum messageId) {
	//    agents.get(agentname).getResponseAnswer
	//  }


	public void makeProposal(String agentname, APLNum responder) {
		System.out.println("[CTAH] trying to make a proposal: " + agentname);
		System.out.println("I'm the proposer, wheeeeee");
		int id = responder.toInt();
		ArrayList<ChipSet> exchange = (ArrayList<ChipSet>) strategy(id);
		ChipSet senderChips;
		ChipSet recipientChips;

		if(exchange == null) {
			System.out.println("No beneficial " +
					"exchanges found among the ones that are beneficial for the responder");
		}
		else {
			System.out.println("EXCHANGE: " + exchange);
			senderChips = exchange.get(0);
			recipientChips = exchange.get(1);
			BasicProposalDiscourseMessage proposal= new BasicProposalDiscourseMessage(
					cgs.getPerGameId(), id, -1, senderChips, recipientChips);
			//             sending = senderChips;

			//PhaseWaiter waiter = new PhaseWaiter(cgs.getPhases());
			// waiter.doWait(RecipConstants.minProposeTime, RecipConstants.maxProposeTime);


			client.communication.sendDiscourseRequest(proposal);

		}
	}

	/**
	 * called by 2APL agent, convert APLNums to ints
	 * @param ax: x coordinate of agent
	 * @param ay: y coordinate of agent
	 * @param gx: x coordinate of goal
	 * @param gy: y coordinate of goal
	 */

	public Term moveStepToGoalTest(String agentname,APLIdent goal, APLIdent norm, APLNum x, APLNum y, APLNum steps) throws
	ExternalActionFailedException {

		ClientGameStatus cgs = client.getGameStatus();
		Scoring scoring = cgs.getScoring();
		//Scoring scoring = new Scoring(0,0,0);
		String colorGoal = goal.getName();
		//Board b = (Board) cgs.getBoard().clone();
		//b.setGoal(new RowCol(x.toInt(),y.toInt()), true);
		System.out.println(agentname+"[CTAH] going goal: " +x+","+y+" color: "+ colorGoal);
		ArrayList<Path> shortestPaths = ShortestPaths.getShortestPaths(cgs.getMyPlayer().getPosition(), new RowCol(x.toInt(),y.toInt()), cgs.getBoard(), scoring, 1000);
		System.out.println(agentname+"[CTAH] paths: " + shortestPaths.size());
		
//		ArrayList<Path> paths = new ArrayList<Path>();
//			for (Path p : shortestPaths) {		
//				if (p.getNumPoints() > steps.toInt() + 1)
//					continue;
//				if (cgs.getBoard().getColors().contains(colorGoal)) {
//					ChipSet c = p.getRequiredChips(cgs.getBoard());
//					if (c.getColors().contains(colorGoal))
//						paths.add(p);
//				}
//			}
//		shortestPaths = paths;
		
		PhaseWaiter waiter = new PhaseWaiter(cgs.getPhases());
		waiter.doWait(3, 5);
		for (int i=0; i<1000; i++) {
			// Get the best path available
				
			System.out.println(agentname+"[CTAH] path: " +i);
			Path chosenPath = shortestPaths.remove(0); // why remove(0)??
			
			if (cgs.getBoard().getColors().contains(colorGoal)) {
				ChipSet c = chosenPath.getRequiredChips(cgs.getBoard());
				if (c.getColors().contains(colorGoal)) {
					if (!cgs.getBoard().getSquare(chosenPath.getPoint(0)).getColor().equals(colorGoal)) {
						if (norm.getName().equals("no"))
							continue;
					}
				} else {
					if (norm.getName().equals("yes"))
						continue;
				}
			}
			
			RowCol point= chosenPath.getPoint(1);
			System.out.println(agentname+"[CTAH] path: " +chosenPath.toString());
			String color = cgs.getBoard().getSquare(point).getColor();
			ChipSet myChips = cgs.getMyPlayer().getChips();
			if (myChips.getNumChips(color) > 0) {
				APLList uTD = new APLList(new APLNum(point.row),new APLNum(point.col),new APLIdent("no"),new APLIdent("no"));
				System.out.println(agentname+"[CTAH] moveStepToGoalTest returns I have chip: " + uTD);
				return uTD; 
				//System.out.println(agentname+"[CTAH] going to: " + point + "goal: " +x+","+y+" color: "+ colorGoal);
				// Send move request
			} else
			{
					
				for(PlayerStatus ps : cgs.getPlayers()){
					System.out.println(agentname+"[CTAH] moveStepToGoalTest proposals: " + proposals[ps.getPerGameId()]);
						if (ps.getChips().getNumChips(color) > 1 )
							if (proposals[ps.getPerGameId()] == null || !proposals[ps.getPerGameId()].equals(color))
								if (nogo[ps.getPerGameId()] == null || !nogo[ps.getPerGameId()].equals(color))
								{
									System.out.println(agentname+"[CTAH] found a chip....: " +point);
									int msgid = makeProposal(agentname,ps.getPerGameId(),color);								
									proposals[ps.getPerGameId()] = color;
									APLList uTD = new APLList(new APLNum(point.row),new APLNum(point.col),new APLNum(ps.getPerGameId()),new APLNum(msgid));
									System.out.println(agentname+"[CTAH] moveStepToGoalTest sent proposal for: " + uTD);
									return uTD; 
								}
						//proposalSent = false;					
					}
					for(PlayerStatus ps : cgs.getPlayers()){
						if (ps.getChips().getNumChips(color) > 0 )
								{
									System.out.println(agentname+"[CTAH] found any chip....: " +point);
									int msgid = makeProposal(agentname,ps.getPerGameId(),color);								
									proposals[ps.getPerGameId()] = color;
									APLList uTD = new APLList(new APLNum(point.row),new APLNum(point.col),new APLNum(ps.getPerGameId()),new APLNum(msgid));
									System.out.println(agentname+"[CTAH] moveStepToGoalTest sent proposal for: " + uTD);
									return uTD; 
								}
						//proposalSent = false;					
					}
					//nopoints.add(point);
	//			}
			}
			//			}
			//if (client.communication.sendMoveRequest(point)) {}
			//env.write(agentname, point.row, point.col);

		}
		System.out.println(agentname+"[CTAH] moveStepToGoalTest havent found anything");
		return new APLIdent("no");
	}


	public Term moveStepToGoal(String agentname2, APLIdent color,
			APLNum ax, APLNum ay) {
		client.communication.sendMoveRequest(new RowCol(ax.toInt(),ay.toInt()));
		PhaseWaiter waiter = new PhaseWaiter(cgs.getPhases());
		waiter.doWait(3, 5);
		if (cgs.getMyPlayer().getPosition().equals(new RowCol(ax.toInt(),ay.toInt()))) {
			APLList uTD = new APLList(new APLNum(cgs.getMyPlayer().getPosition().row),new APLNum(cgs.getMyPlayer().getPosition().col));
			System.out.println(agentname2+"[CTAH] moveStepToGoal returns: " + uTD);
			return uTD; 
		}
		//nopoints.add(new RowCol(ax.toInt(),ay.toInt()));
		System.out.println(agentname2+"[CTAH] moveStepToGoal returns: no");
		return new APLIdent("no");
	}

	public void setGoal(String agentname, APLNum apl_id, APLNum xcoor,
			APLNum ycoor, APLNum gx, APLNum gy) {
		// TODO Auto-generated method stub

	}

	public void setGoal(String agentname, APLNum apl_id, APLIdent color,
			APLIdent type) {
		// TODO Auto-generated method stub

	}

	public Term sendResponse(String agentname2, APLNum msgID, APLIdent response) {
		BasicProposalDiscourseMessage proposal = (BasicProposalDiscourseMessage) messages.get(msgID.toInt());
		BasicProposalDiscussionDiscourseMessage responseMessage = new BasicProposalDiscussionDiscourseMessage(proposal);
		if( response.getName().equals("accept") ) {
			responseMessage.acceptOffer();
		} else {
			responseMessage.rejectOffer();
		}
		client.communication.sendDiscourseRequest(responseMessage);
		System.out.println(agentname2+"[CTAH] sent response: "+responseMessage);
		return new APLIdent("true");
	}

}
