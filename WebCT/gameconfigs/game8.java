

import edu.harvard.eecs.airg.coloredtrails.server.ServerPhases;
import edu.harvard.eecs.airg.coloredtrails.shared.Scoring;
import edu.harvard.eecs.airg.coloredtrails.shared.discourse.BasicProposalDiscourseMessage;
import edu.harvard.eecs.airg.coloredtrails.shared.discourse.BasicProposalDiscussionDiscourseMessage;
import edu.harvard.eecs.airg.coloredtrails.shared.discourse.ChipsRevelationDiscourseMessage;
import edu.harvard.eecs.airg.coloredtrails.shared.discourse.DiscourseMessage;
import edu.harvard.eecs.airg.coloredtrails.shared.discourse.NormColorDiscourseMessage;
import edu.harvard.eecs.airg.coloredtrails.shared.discourse.NormGoalDiscourseMessage;
import edu.harvard.eecs.airg.coloredtrails.shared.discourse.NormGroupMessage;
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
public class game8 extends GameConfigDetailsRunnable implements
		PhaseChangeHandler {

	/**
	 * The scoring function used for players in the game. 100 for a player
	 * reaching the goal, -10 per unit distance if the player does not reach the
	 * goal, 5 for each chip remaining after the player has reached the goal or
	 * cannot move any farther towards the goal.
	 */
	Scoring s = new Scoring(100, -10, 5);
	private static Random localrand = new Random();
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
		
		 if(gs.getDataFromController() != null)
		        System.out.println("we have data!!!!!!! " + (String) gs.getDataFromController());
				
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
		ph.addPhase("Setup Phase", 3);	
		for (int i = 0; i < 1; i++) {
			ph.addPhase("Norm Phase", 100);		
		}
		for (int i = 0; i < 1; i++) {
			ph.addPhase("Movement Phase", 100);			
		}
		ph.addPhase("Feedback Phase", 10);
		ph.setLoop(false);
		gs.setPhases(ph);		
		
		gs.setInitialized(); // will generate GAME_INITIALIZED message		
	}

	/**
	 * Places random colors on specified board
	 */
	protected void setBoard(GamePalette gp) throws Exception {

//		System.out.println("-------- Creating Board --------");
		int row = 6;
		int col = 6;
//		in.nextLine(); // clears out any comments
		Board board = new Board(row, col);
		Square[][] squares = new Square[row][col];
		for (int r = 0; r < row; r++) {
			for (int c = 0; c < col; c++) {
				squares[r][c] = new Square();
//				squares[r][c].setColor(gp.get(in.nextInt()));
				//overwriting to random colors
				squares[r][c].setColor(gp.getRandomColor()); 
				spaces.writeTile(r,c,squares[r][c].getColor());
			}
		}
		board.setSquares(squares);
		gs.setBoard(board);
		
		gs.getBoard().setGoal(getPosition(), true); // goal location
		gs.getBoard().getGoals().iterator().next().setType(-1);
		
		
		Goal g = gs.getBoard().getGoals().iterator().next();
		if (g != null) {
			spaces.writeGoal(g);
		}
		
	}

	protected ChipSet getChipSet(GamePalette gp) {
		ChipSet cs = new ChipSet();
//		int counter = 0;
//		while (in.hasNextInt()) {
//			cs.add(gp.get(counter), in.nextInt());
//			counter++;
//		}
//		in.nextLine();
		
		return getRandomChipSet(gp);
	}

	protected RowCol getPosition() {
//		int row = in.nextInt();
		int row = localrand.nextInt(gs.getBoard().getRows()-3) +2;
//		int col = in.nextInt();
		int col = localrand.nextInt(gs.getBoard().getCols()-3) +2;
//		in.nextLine();
		return new RowCol(row, col);
	}

	/**
	 * Generates chipset with zero values for revelation chips
	 */
	protected static ChipSet getRandomChipSet(GamePalette gp) {
		ChipSet chipset = new ChipSet();
		
		for (String color : gp.getColors())
			chipset.set(color, localrand.nextInt(5));
		return chipset;
	}
	/**
	Places random colors on specified board
	*/
	protected void setRandBoard(GamePalette gp) {
		Board board = new Board(7,7);
		Square[][] squares = new Square[board.getRows()][board.getCols()];

		for (int i = 0; i < board.getRows(); i++) {
			for (int j = 0; j < board.getCols(); j++) {
		      squares[i][j] = new Square();
		      squares[i][j].setColor(gp.getRandomColor());
			}
		}
		board.setSquares(squares);
		board.setGoal(new RowCol(2, 3), true);             // goal location player 0
  //    board.setGoal(new RowCol(2, 3), true,1);             // goal location player 1
		gs.setBoard(board);
	}
	
	@Override
	public void beginPhase(String phasename) {
		System.out.println("A New Phase Began: " + phasename);
		if (phasename.equals("Setup Phase")) {		
			try {
				//TODO remove 0 hack
//				FileReader fr = new FileReader("lib/adminconfig/board_"					+ 0 + ".txt");
//				Scanner in = new Scanner(fr);

				// assign game-board colors
				setBoard(gs.getGamePalette());


				String role = "raaa";
				// for all the players
				for (int i = 0; i < gs.getPlayers().size();i++) {
					PlayerStatus player = gs.getPlayerByPerGameId(i);
					player.setScore(1000);
					//player.setTeamId(3); // set teams for players
					player.setChips(getChipSet(gs.getGamePalette()));
//					int row = in.nextInt();
					int row = localrand.nextInt(gs.getBoard().getRows());
//					int col = in.nextInt();
					int col = localrand.nextInt(gs.getBoard().getCols());
					player.setPosition(new RowCol(row,col));
					spaces.writePosition(i, new RowCol(row,col));
//					player.setRole(in.next("[a-z]+"));
//					if (player.getRole().contains("ra"))
					if (i == 3) {
						player.setRole(role);
//						spaces.writeGroup(i,role);
					} else {
						player.setRole("none");
					}
//					in.nextLine();
					player.setCommunicationAllowed(false);
					player.setTransfersAllowed(false);
					player.setMovesAllowed(false);
				}								
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else	if (phasename.equals("Norm Phase")) {	
			for (int i = 0; i < gs.getPlayers().size();i++) {
				PlayerStatus player = gs.getPlayerByPerGameId(i);
				if (!player.getRole().equals("none"))
					spaces.writeGroup(i,player.getRole());
			}
		}
		else	if (phasename.equals("Movement Phase")) {	
			for (int i = 0; i < gs.getPlayers().size();i++) {
				PlayerStatus player = gs.getPlayerByPerGameId(i);
				player.setMovesAllowed(true);
				player.setCommunicationAllowed(true);
				player.setTransfersAllowed(true);
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
				//assignScores();
			}
	}
	
	// Override super method do discourse in order to make an automatic transfer
	// after accept a proposal
	public boolean doDiscourse(DiscourseMessage dm) {
		System.out.println("Received Discourse Message ");
		System.out.println("Class: " + dm.getClass());
		System.out.println("From: " + dm.getFromPerGameId());
		System.out.println("To: " + dm.getToPerGameId());

		
		// Sending the message to the client
		boolean result = gs.doDiscourse(dm);

		// ### automatic exchange of chips upon acceptance of a proposal ###
		if (dm instanceof BasicProposalDiscussionDiscourseMessage) {
			

			BasicProposalDiscussionDiscourseMessage bpddm = (BasicProposalDiscussionDiscourseMessage) dm;
			System.out.println("---- BasicProposalDiscussionDiscourseMessage ----" + bpddm);
			
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
		}  else if (dm instanceof NormGroupMessage) {
			System.out.println("---- NormGroupMessage ----" + dm);
			NormGroupMessage ngdm = (NormGroupMessage) dm;
			spaces.writeNormGroup(ngdm.isNorm(),ngdm.getSanction());	
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
	
	@Override
	 public boolean doMove(int perGameId, RowCol newpos) {
		spaces.writePosition(perGameId, newpos);
	    return gs.doMove(perGameId, newpos);
	}


}