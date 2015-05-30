package edu.harvard.eecs.airg.coloredtrails.shared.discourse;

import edu.harvard.eecs.airg.coloredtrails.shared.types.ChipSet;



/**
 * Discourse Message for chip revelation. 
 * used for adding uncertanity to the game. 
 * each player can choose how many of his chips the other players will see
 * @author Hen Barshak 
 */
public class NormGroupMessage extends DiscourseMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	/**
	 * 
	 */
	//private static final long serialVersionUID = 8732569987805134283L;
	String norm;
	int sanction;
	
	public int getSanction() {
		return sanction;
	}

	public void setSanction(int sanction) {
		this.sanction = sanction;
	}

	public NormGroupMessage(int senderID, int receiverID, int messageId, String norm, int sanction) {
		super(senderID, receiverID, "normgroup", messageId);

        this.norm = norm;
        this.sanction = sanction;
	}

	public String isNorm() {
		return norm;
	}

	public void setNorm(String norm) {
		this.norm = norm;
	}
	
	
}
