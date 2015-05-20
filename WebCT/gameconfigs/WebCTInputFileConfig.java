

import edu.harvard.eecs.airg.coloredtrails.server.ServerPhases;
import edu.harvard.eecs.airg.coloredtrails.shared.Scoring;
import edu.harvard.eecs.airg.coloredtrails.shared.discourse.BasicProposalDiscourseMessage;
import edu.harvard.eecs.airg.coloredtrails.shared.discourse.BasicProposalDiscussionDiscourseMessage;
import edu.harvard.eecs.airg.coloredtrails.shared.discourse.ChipsRevelationDiscourseMessage;
import edu.harvard.eecs.airg.coloredtrails.shared.discourse.DiscourseMessage;
import edu.harvard.eecs.airg.coloredtrails.shared.discourse.NormColorDiscourseMessage;
import edu.harvard.eecs.airg.coloredtrails.shared.discourse.NormGoalDiscourseMessage;
import edu.harvard.eecs.airg.coloredtrails.shared.types.*;

import java.util.Date;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import mwspaces.CTsetup;



/**
 * This configuration implements much of the setup and functionality. Including
 * automatic exchange after accept an offer, and automatic movement.
 */
public class WebCTInputFileConfig extends GameConfigDetailsRunnable implements
		PhaseChangeHandler {

	/**
	 * The scoring function used for players in the game. 100 for a player
	 * reaching the goal, -10 per unit distance if the player does not reach the
	 * goal, 5 for each chip remaining after the player has reached the goal or
	 * cannot move any farther towards the goal.
	 */
	Scoring s = new Scoring(100, -10, 5);

	/**
	 * indicated the current file number that data is taken from
	 */
	int CurrentInputFileIndex = 0;


	//calculated automatically in run time
	int numberOfConfigFiles = 0;
	
	boolean automaticChipTransfer = true;
	boolean EnableChipsRevelation = true;
	
	
	CTsetup spaces;
	 
	/**
	 * Called by GameConfigDetailsRunnable methods when calculation and
	 * assignment of player scores is desired
	 */
	protected void assignScores() {
//		for (PlayerStatus ps : gs.getPlayers()) {
//			ps.setScore(getPlayerScore(ps));
//			System.out.println("Player: " + ps.getPin() + "  Score: "
//					+ ps.getScore());
//		}
	}


	
	
	/**
	 * Start a new game
	 */
	public void run() {
		initLog();
		spaces = new mwspaces.CTsetup(gs);
	    spaces.initializeGS();
	    
		System.out.println("Let the game begin...");

		System.out.println("game id= " + gs.getGameId());

		GamePalette gp = new GamePalette();
		gp.add("white");
		gp.add("brown");
		gp.add("blue");
		gp.add("yellow");
		gs.setGamePalette(gp);
		gs.setScoring(s);
				
		//count number of config files to read
		String pathToConfigFiles = "lib/adminconfig";
		File file = new File(pathToConfigFiles);
		if (file.isDirectory()) {
			File[] configFiles = file.listFiles();
			
			for (int i = 0; i < configFiles.length; i++) {
				if (configFiles[i].getName().contains("board_") &&
						configFiles[i].getName().endsWith(".txt")) {
					numberOfConfigFiles++;
				}
			}
		}		
		// set up phase sequence
		ServerPhases ph = new ServerPhases(this);
		for (int i = 0; i < 1; i++) {
			ph.addPhase("Norm Phase", 60);		
		}
		for (int i = 0; i < 1; i++) {
			ph.addPhase("Communication Phase", 60);		
		}
		for (int i = 0; i < 1; i++) {
			ph.addPhase("Movement Phase", 60);			
		}
		ph.addPhase("Feedback Phase", 1);
		ph.setLoop(false);
		gs.setPhases(ph);		
		
		gs.setInitialized(); // will generate GAME_INITIALIZED message		
	}

	/**
	 * Places random colors on specified board
	 */
	protected void setBoard(GamePalette gp, Scanner in) throws Exception {

		System.out.println("-------- Creating Board --------");
		int row = in.nextInt();
		int col = in.nextInt();
		in.nextLine(); // clears out any comments
		Board board = new Board(row, col);
		Square[][] squares = new Square[row][col];
		for (int r = 0; r < row; r++) {
			for (int c = 0; c < col; c++) {
				squares[r][c] = new Square();
				squares[r][c].setColor(gp.get(in.nextInt()));
				spaces.writeTile(r,c,squares[r][c].getColor());
			}
		}
		board.setSquares(squares);
		board.setGoal(getPosition(in), true); // goal location
		
		
		gs.setBoard(board);
		Goal g = board.getGoals().iterator().next();
		//g.setType(type);
		if (g != null) {
			spaces.writeGoal(g);
		}
		
	}

	protected ChipSet getChipSet(GamePalette gp, Scanner in) {
		ChipSet cs = new ChipSet();
		int counter = 0;
		while (in.hasNextInt()) {
			cs.add(gp.get(counter), in.nextInt());
			counter++;
		}
		in.nextLine();
		return cs;
	}

	protected RowCol getPosition(Scanner in) {
		int row = in.nextInt();
		int col = in.nextInt();
		in.nextLine();
		return new RowCol(row, col);
	}

	/**
	 * Generates chipset with zero values for revelation chips
	 */
	protected static ChipSet getZeroSumsChipSet(GamePalette gp) {
		ChipSet chipset = new ChipSet();

		for (String color : gp.getColors())
			chipset.set(color, 0);

		return chipset;
	}
	@Override
	public void beginPhase(String phasename) {
		System.out.println("A New Phase Began: " + phasename);
		if (phasename.equals("Norm Phase")) {		
			try {
				//TODO remove 0 hack
				FileReader fr = new FileReader("lib/adminconfig/board_"
						+ 0 + ".txt");
				Scanner in = new Scanner(fr);

				// assign game-board colors
				setBoard(gs.getGamePalette(), in);

				// for all the players
				for (int i = 0; i < gs.getPlayers().size();i++) {
					PlayerStatus player = gs.getPlayerByPerGameId(i);
					player.setScore(1000);
					//player.setTeamId(3); // set teams for players
					player.setChips(getChipSet(gs.getGamePalette(), in));
					int row = in.nextInt();
					int col = in.nextInt();
					player.setPosition(new RowCol(row, col));
					player.setRole(in.next("[a-z]+"));
					in.nextLine();
					player.setCommunicationAllowed(false);
					player.setTransfersAllowed(true);
					player.setMovesAllowed(false);
					
					ChipSet revelationChips = getZeroSumsChipSet(gs.getGamePalette());
					System.out.println("revelation chips: "
							+ revelationChips.toString());
					player.setRevelationChips(revelationChips);
				}
								
				

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (phasename.equals("Communication Phase")) {			

			try {
				// for all the players
				for (int i = 0; i < gs.getPlayers().size();i++) {
					PlayerStatus player = gs.getPlayerByPerGameId(i);
					//player.setTeamId(3); // set teams for players
					player.setCommunicationAllowed(true);
					player.setTransfersAllowed(true);
					player.setMovesAllowed(true);
				}
								
				

			} catch (Exception e) {
				e.printStackTrace();
			}

			

//			boolean ProposerCommunicationAllowed = true;
//			boolean ProposerTransfersAllowed = true;
//			boolean ProposerMovesAllowed = true;
//			boolean ResponderCommunicationAllowed = true;
//			boolean ResponderTransfersAllowed = true;
//			boolean ResponderMovesAllowed = true;
//
//		
//			//switch rolls on new boards
//			if (CurrentInputFileIndex > 0)
//				swapRoles();
//			
//			PlayerStatus Responder = gs.getPlayerByPerGameId(ResponderID);
//			PlayerStatus Proposer = gs.getPlayerByPerGameId(ProposerID);
//			
//			Responder.setCommunicationAllowed(ResponderCommunicationAllowed);
//			Responder.setTransfersAllowed(ResponderTransfersAllowed);
//			Responder.setMovesAllowed(ResponderMovesAllowed);
//
//			Proposer.setCommunicationAllowed(ProposerCommunicationAllowed);
//			Proposer.setTransfersAllowed(ProposerTransfersAllowed);
//			Proposer.setMovesAllowed(ProposerMovesAllowed);
		}
		if (phasename.equals("Movement Phase")) {	
			for (int i = 0; i < gs.getPlayers().size();i++) {
				PlayerStatus player = gs.getPlayerByPerGameId(i);
				player.setMovesAllowed(true);
			}
		}
	}

	@Override
	public void endPhase(String phaseName) {

		if (phaseName.equals("Feedback Phase")) {
			// end game
			gs.setEnded();
		} else
		if (phaseName.equals("Norm Phase")) {
			CurrentInputFileIndex++;
				//doAutomaticMovement(s);

				// calculate scores after all players have moved
				// (e.g, in case a player's score depends on others' locations)
				assignScores();
			}
	}
	
	// Override super method do discourse in order to make an automatic transfer
	// after accept a proposal
	public boolean doDiscourse(DiscourseMessage dm) {
		System.out.println("Received Discourse Message ");
		System.out.println("Class: " + dm.getClass());
		System.out.println("From: " + dm.getFromPerGameId());
		System.out.println("From: " + dm.getToPerGameId());

		
		// Sending the message to the client
		boolean result = gs.doDiscourse(dm);

		// ### automatic exchange of chips upon acceptance of a proposal ###
		if (dm instanceof BasicProposalDiscussionDiscourseMessage) {
			System.out
					.println("---- BasicProposalDiscussionDiscourseMessage ----");

			BasicProposalDiscussionDiscourseMessage bpddm = (BasicProposalDiscussionDiscourseMessage) dm;

			if (bpddm.accepted()) {		
				spaces.writeResponse(dm.getMessageId(),"accepted");
				
				// print the chips before the transfer
				System.out.println("We have an accepted offer, chips before: ");
				for (PlayerStatus player : gs.getPlayers()) {
					System.out.println("player pin: " + player.getPin());
					System.out.println("player chips: " + player.getChips());
				}
				// transfer the chips between the players
				doAutomaticChipTransfer(bpddm);

				// print the chips after the transfer
				System.out.println("chips after: ");
				for (PlayerStatus player : gs.getPlayers()) {
					System.out.println("player pin: " + player.getPin());
					System.out.println("player chips: " + player.getChips());
				}
			} else {
				spaces.writeResponse(dm.getMessageId(),"rejected");
				
				System.out.println("Offer rejected");
				//System.out.println("swaping roles for counter offer");
				//swapRoles();
			}
		}  else if (dm instanceof BasicProposalDiscourseMessage) {
			BasicProposalDiscourseMessage bpdm = (BasicProposalDiscourseMessage) dm;
			spaces.writeProposal(bpdm);
		} else if (dm instanceof NormColorDiscourseMessage) {
			System.out.println("---- NormColorDiscourseMessage ----");
			NormColorDiscourseMessage ncdm = (NormColorDiscourseMessage) dm;
			//String color = gs.getGamePalette().get(ncdm.getColor());
			spaces.writeNormColor(ncdm.getToPerGameId(),ncdm.getColor(),ncdm.isNorm(),ncdm.getSanction());	
		}  else if (dm instanceof NormGoalDiscourseMessage) {
			System.out.println("---- NormGoalDiscourseMessage ----");
			NormGoalDiscourseMessage ngdm = (NormGoalDiscourseMessage) dm;
			spaces.writeNormGoal(ngdm.getToPerGameId(),ngdm.getGoal(),ngdm.getOrigLoc(),ngdm.getSanction());	
		}
		return result;
	}
	 private void initLog() {
	    	try {
	        	File file = new File("./log/game"+ new Date(System.currentTimeMillis()) +".log");

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
	    }




	@Override
	public int getPlayerScore(PlayerStatus ps) {
		return ps.getScore();
	}


}