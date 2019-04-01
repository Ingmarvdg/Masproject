/**
 * 
 */
package MAS_Agent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import MAS_Agent.help_components.CUHKAgent_Offering;
import MAS_Agent.help_components.Fawkes_Offering;
import MAS_Agent.help_components.HardHeaded_Offering;
import MAS_Agent.help_components.TheNegotiatorReloaded_Offering;
import genius.core.Bid;
import genius.core.bidding.BidDetails;
import genius.core.boaframework.BOAparameter;
import genius.core.boaframework.NegotiationSession;
import genius.core.boaframework.NoModel;
import genius.core.boaframework.OMStrategy;
import genius.core.boaframework.OfferingStrategy;
import genius.core.boaframework.OpponentModel;
import genius.core.boaframework.SortedOutcomeSpace;
import genius.core.issue.Issue;
import genius.core.issue.Value;

/**
 * @author Andrea Scorza, Diego Staphorst, Erik Lokhorst, Ingmar van der Geest
 *
 */
public class Group8_BS extends OfferingStrategy {

//	/**
//	 * k in [0, 1]. For k = 0 the agent starts with a bid of maximum utility
//	 */
	private double k;
	/** Maximum target utility */
	private double Pmax;
	/** Minimum target utility */
	private double Pmin;
	/** Concession factor */
	private double e;
	/** Outcome space */
	private SortedOutcomeSpace outcomespace;
	
	/** Bidding strategies */
	public OfferingStrategy[] bid_methods = new OfferingStrategy[3];
	public Double[] bid_weights = new Double[3];

//
	
	
//	/**
//	 * Method which initializes the agent by setting all parameters. The
//	 * parameter "e" is the only parameter which is required.
//	 */
	@Override
	public void init(NegotiationSession negoSession, OpponentModel model, OMStrategy oms,
			Map<String, Double> parameters) throws Exception {
		
		this.negotiationSession = negoSession;
		this.opponentModel 		= model;
		this.omStrategy 		= oms;

		// Initiate bidding strategies
		bid_methods[0] = new CUHKAgent_Offering(negoSession, model, oms);
		bid_weights[0] = 0.25;
		
		bid_methods[1] = new Fawkes_Offering(negoSession, model, oms, parameters);
		bid_weights[1] = 0.25;

//		bid_methods[2] = new HardHeaded_Offering(negoSession, model, oms, parameters);
//		bid_weights[2] = 0.25;

		bid_methods[2] = new TheNegotiatorReloaded_Offering(negoSession, model, oms);
		bid_weights[2] = 0.25;
		
		
						
		System.out.println("heeye");
		System.out.println("DOEEEI");
		this.e = parameters.get("e");

		if (parameters.get("k") != null)
			this.k = parameters.get("k");
		else
			this.k = 0;

		if (parameters.get("min") != null)
			this.Pmin = parameters.get("min");
		else
			this.Pmin = negoSession.getMinBidinDomain().getMyUndiscountedUtil();

		if (parameters.get("max") != null) {
			Pmax = parameters.get("max");
		} else {
			BidDetails maxBid = negoSession.getMaxBidinDomain();
			Pmax = maxBid.getMyUndiscountedUtil();
		}
		
		outcomespace = new SortedOutcomeSpace(negotiationSession.getUtilitySpace());
		negotiationSession.setOutcomeSpace(outcomespace);
		
		// PRINT AGENTS AND WEIGHTS
		for (OfferingStrategy offeringStrategy : bid_methods) {
            System.out.println(offeringStrategy); 

		}

		
	}

	@Override
	public BidDetails determineOpeningBid() {
		return determineNextBid();
	}

	/**
	 * Simple offering strategy which retrieves the target utility and looks for
	 * the nearest bid if no opponent model is specified. If an opponent model
	 * is specified, then the agent return a bid according to the opponent model
	 * strategy.
	 */
	@Override
	public BidDetails determineNextBid() {
		double time = negotiationSession.getTime();
		double utilityGoal;
		utilityGoal = p(time);

//		 System.out.println("[e=" + e + ", Pmin = " +
//		 BilateralAgent.round2(Pmin) + "] t = " + BilateralAgent.round2(time)
//		 + ". Aiming for " + utilityGoal);

		// if there is no opponent model available
		
//		HashMap<Issue, HashMap<Value, Double>> bids = new HashMap<Issue, HashMap<Value, Double>>();
		for (OfferingStrategy offeringStrategy : bid_methods) {
			
		}
		
		HashMap<Issue, HashMap<Value, Double>> bids = new HashMap<Issue, HashMap<Value, Double>>();
		HashMap<Value, Double> bid_with_weight = new HashMap<Value, Double>();
		for (int i = 0; i < bid_methods.length; i++) {
			OfferingStrategy agent_bs = bid_methods[i];
			Double agent_weight = bid_weights[i];
			for (Issue issue : negotiationSession.getIssues()) {
				Value value_for_issue = agent_bs.determineNextBid().getBid().getValue(issue);
				bid_with_weight.put(value_for_issue, agent_weight);
				bids.put(issue, bid_with_weight);
			}
		}
//		for (OfferingStrategy agent_bs : bid_methods) {
//			System.out.println(agent_bs);
//
//			for (Issue issue : negotiationSession.getIssues()) {
//				Double agent_weight = bid_methods.get(agent_bs);
//				Value value_for_issue = agent_bs.determineNextBid().getBid().getValue(issue);
//				bid_with_weight.put(value_for_issue, agent_weight);
//				bids.put(issue, bid_with_weight);
//			}
//		}
		
		for (Entry<Issue, HashMap<Value, Double>> issue : bids.entrySet()) {
			System.out.println(issue);
			for (Entry<Value, Double> values_in_issue : issue.getValue().entrySet()) {
				System.out.println(values_in_issue);
			}
		}

		System.out.println("TEST");
		
//		HashMap<String, Integer> test = new HashMap<String, Integer>();
		for (Issue issue : negotiationSession.getIssues()) {
			System.out.println(issue);
//			for (int i = 0; i < bids_by_methods.length; i++) {
//				System.out.println(bids_by_methods[i].getBid().getValue(issue));
//			}
		}
		
		if (opponentModel instanceof NoModel) {
			nextBid = negotiationSession.getOutcomeSpace().getBidNearUtility(utilityGoal);
		} else {
			nextBid = omStrategy.getBid(outcomespace, utilityGoal);
		}
		return nextBid;
	}

	/**
	 * From [1]:
	 * 
	 * A wide range of time dependent functions can be defined by varying the
	 * way in which f(t) is computed. However, functions must ensure that 0 <=
	 * f(t) <= 1, f(0) = k, and f(1) = 1.
	 * 
	 * That is, the offer will always be between the value range, at the
	 * beginning it will give the initial constant and when the deadline is
	 * reached, it will offer the reservation value.
	 * 
	 * For e = 0 (special case), it will behave as a Hardliner.
	 */
	public double f(double t) {
		if (e == 0)
			return k;
		double ft = k + (1 - k) * Math.pow(t, 1.0 / e);
		return ft;
	}

	/**
	 * Makes sure the target utility with in the acceptable range according to
	 * the domain. Goes from Pmax to Pmin!
	 * 
	 * @param t
	 * @return double
	 */
	public double p(double t) {
		return Pmin + (Pmax - Pmin) * (1 - f(t));
	}

	public NegotiationSession getNegotiationSession() {
		return negotiationSession;
	}

	@Override
	public Set<BOAparameter> getParameterSpec() {
		Set<BOAparameter> set = new HashSet<BOAparameter>();
		set.add(new BOAparameter("e", 1.0, "Concession rate"));
		set.add(new BOAparameter("k", 0.0, "Offset"));
		set.add(new BOAparameter("min", 0.0, "Minimum utility"));
		set.add(new BOAparameter("max", 0.99, "Maximum utility"));

		return set;
	}

	@Override
	public String getName() {
		return "Group8_BS";
	}
}