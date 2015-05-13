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
	String color;
	boolean norm;
	int sanction;
	
	public int getSanction() {
		return sanction;
	}

	public void setSanction(int sanction) {
		this.sanction = sanction;
	}

	public NormColorDiscourseMessage(int senderID, int receiverID, int messageId, String color, String norm, int sanction) {
		super(senderID, receiverID, "normcolor", messageId);

        this.norm = norm == "yes" ? true : false;
        this.color = color;
        this.sanction = sanction;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public boolean isNorm() {
		return norm;
	}

	public void setNorm(boolean norm) {
		this.norm = norm;
	}
	
	
}
