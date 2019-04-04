/**
 * 
 */
package MAS_Agent;

import java.io.IOException;
import java.util.HashSet;

import java.util.Map;
import java.util.Set;

//import genius.core.Bid;
//import genius.core.issue.Issue;
//import genius.core.issue.IssueDiscrete;
//import genius.core.issue.IssueReal;
import genius.core.boaframework.AcceptanceStrategy;
import genius.core.boaframework.Actions;
import genius.core.boaframework.BOAparameter;
import genius.core.boaframework.NegotiationSession;
import genius.core.boaframework.OfferingStrategy;
import genius.core.boaframework.OpponentModel;
//import genius.core.utility.UtilitySpace;
//import genius.core.utility.AdditiveUtilitySpace;

/**
 * This Acceptance Condition will accept an opponent bid if the utility is
 * higher than the bid the agent is ready to present.
 * 
 * Decoupling Negotiating Agents to Explore the Space of Negotiation Strategies
 * T. Baarslag, K. Hindriks, M. Hendrikx, A. Dirkzwager, C.M. Jonker
 * 
 */
public class Group8_AS extends AcceptanceStrategy {

	TKI tki = new TKI();

	private double t;

	/**
	 * Empty constructor for the BOA framework.
	 */
	public Group8_AS() {
	}

	public Group8_AS(NegotiationSession negoSession, OfferingStrategy strat, double time) {
		this.negotiationSession = negoSession;
		this.offeringStrategy = strat;
		this.t = time;
	}

	@Override
	public void init(NegotiationSession negoSession, OfferingStrategy strat, OpponentModel opponentModel,
			Map<String, Double> parameters) throws Exception {
		this.negotiationSession = negoSession;
		this.offeringStrategy = strat;
		double discount = 1.0;

		if (negoSession.getDiscountFactor() <= 1D && negoSession.getDiscountFactor() > 0D)
			discount = negoSession.getDiscountFactor();

		if (parameters.get("t") != null) {
			t = parameters.get("t");
		} else {
			t = .98;
		}
	}

	@Override
	public String printParameters() {
		String str = "[t: " + t + "]";
		return str;
	}

	@Override
	public Actions determineAcceptability() {
		double currentNegoTime = negotiationSession.getTime();
		double timeWindow = 1 - currentNegoTime;
		double t = 0.995;
		double nextMyBidUtil = offeringStrategy.getNextBid().getMyUndiscountedUtil();
		double lastOpponentBidUtil = negotiationSession.getOpponentBidHistory().getLastBidDetails()
				.getMyUndiscountedUtil();

		// System.out.println("TIME1 = " + timeWindow);

		TKI.populate(lastOpponentBidUtil, nextMyBidUtil);

		try {
			tki.print(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("inside the catch");
		}

		if (lastOpponentBidUtil >= nextMyBidUtil) {
			try {
				tki.print(0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("inside the catch");
			}
			System.out.println("ACCEPT 1");
			return Actions.Accept;
		} else {
			if (negotiationSession.getOpponentBidHistory()
					.filterBetweenTime(currentNegoTime - timeWindow, currentNegoTime).getBestBidDetails() != null) {
				double bestBidLastWindow = negotiationSession.getOpponentBidHistory()
						.filterBetweenTime(currentNegoTime - timeWindow, currentNegoTime).getBestBidDetails()
						.getMyUndiscountedUtil();
				if (currentNegoTime > t && lastOpponentBidUtil >= bestBidLastWindow) {
					try {
						tki.print(0);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("inside the catch");
					}

					return Actions.Accept;
				}
			} 
//			else
//				return Actions.Accept;
		}
		return Actions.Reject;
	}

	@Override
	public Set<BOAparameter> getParameterSpec() {

		Set<BOAparameter> set = new HashSet<BOAparameter>();
		set.add(new BOAparameter("a", 1.0,
				"Accept when the opponent's utility * a + b is greater than the utility of our current bid"));
		set.add(new BOAparameter("b", 0.0,
				"Accept when the opponent's utility * a + b is greater than the utility of our current bid"));

		return set;
	}

	@Override
	public String getName() {
		return "Group8_AS";
	}
}