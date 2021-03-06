package negotiator.boaframework.opponentmodel;

import java.util.Map;

import agents.bayesianopponentmodel.OpponentModelUtilSpace;
import genius.core.Bid;
import genius.core.boaframework.NegotiationSession;
import genius.core.boaframework.OpponentModel;
import genius.core.issue.Issue;
import genius.core.utility.AbstractUtilitySpace;
import negotiator.boaframework.opponentmodel.agentsmith.SmithModel;

/**
 * Adapter for the Frequency Model for the BOA framework.
 *
 * Tim Baarslag, Koen Hindriks, Mark Hendrikx, Alex Dirkzwager and Catholijn M.
 * Jonker. Decoupling Negotiating Agents to Explore the Space of Negotiation
 * Strategies
 *
 * @author Mark Hendrikx
 */
public class SmithFrequencyModel extends OpponentModel {

	private SmithModel model;
	private int round = 0;

	@Override
	public void init(NegotiationSession negotiationSession, Map<String, Double> parameters) {
		model = new SmithModel(negotiationSession.getUtilitySpace());
		this.negotiationSession = negotiationSession;
	}

	@Override
	public void updateModel(Bid opponentBid, double time) {
		round++;
		model.addBid(opponentBid);
	}

	@Override
	public double getBidEvaluation(Bid bid) {
		if (round > 0) {
			return model.getNormalizedUtility(bid);
		} else {
			return 0;
		}
	}

	@Override
	public AbstractUtilitySpace getOpponentUtilitySpace() {
		if (round > 0) {
			return new OpponentModelUtilSpace(model);
		} else {
			return negotiationSession.getUtilitySpace();
		}
	}

	public double getWeight(Issue issue) {
		return model.getWeight(issue.getNumber());
	}

	@Override
	public String getName() {
		return "Smith Frequency Model";
	}

	public void cleanUp() {
		super.cleanUp();
		model = null;
	}
}