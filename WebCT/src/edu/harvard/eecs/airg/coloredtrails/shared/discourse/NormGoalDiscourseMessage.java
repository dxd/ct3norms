package edu.harvard.eecs.airg.coloredtrails.shared.discourse;

import edu.harvard.eecs.airg.coloredtrails.shared.types.ChipSet;
import edu.harvard.eecs.airg.coloredtrails.shared.types.RowCol;



/**
 * Discourse Message for chip revelation. 
 * used for adding uncertanity to the game. 
 * each player can choose how many of his chips the other players will see
 * @author Hen Barshak 
 */
public class NormGoalDiscourseMessage extends DiscourseMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8732569987805134283L;
	RowCol goal;
	RowCol origLoc;
	
	public NormGoalDiscourseMessage(int senderID, int receiverID, int messageId, RowCol goal, RowCol orig, boolean norm) {
		super(senderID, receiverID, "normgoal", messageId);

        this.goal = goal;
        this.origLoc = orig;
	}

	public void setOrigLoc(RowCol origLoc) {
		this.origLoc = origLoc;
	}

	public RowCol getGoal() {
		return goal;
	}

	public void setGoal(RowCol goal) {
		this.goal = goal;
	}

	public RowCol getOrigLoc() {
		return origLoc;
	}


	
	
}
