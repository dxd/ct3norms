package edu.harvard.eecs.airg.coloredtrails.shared.discourse;

import edu.harvard.eecs.airg.coloredtrails.shared.types.ChipSet;



/**
 * Discourse Message for chip revelation. 
 * used for adding uncertanity to the game. 
 * each player can choose how many of his chips the other players will see
 * @author Hen Barshak 
 */
public class NormColorDiscourseMessage extends DiscourseMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8732569987805134283L;
	Integer color;
	boolean norm;
	
	public NormColorDiscourseMessage(int senderID, int receiverID, int messageId, Integer color, boolean norm) {
		super(senderID, receiverID, "normcolor", messageId);

        this.norm = norm;
        this.color = color;
	}

	public Integer getColor() {
		return color;
	}

	public void setColor(Integer color) {
		this.color = color;
	}

	public boolean isNorm() {
		return norm;
	}

	public void setNorm(boolean norm) {
		this.norm = norm;
	}
	
	
}
