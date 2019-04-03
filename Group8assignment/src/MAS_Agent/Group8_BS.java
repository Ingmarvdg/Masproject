/**
 * 
 */
package MAS_Agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import MAS_Agent.help_components.Fawkes_Offering;
import MAS_Agent.help_components.TheNegotiatorReloaded_Offering;
import genius.core.Bid;
import genius.core.bidding.BidDetails;
import genius.core.boaframework.NegotiationSession;
import genius.core.boaframework.OMStrategy;
import genius.core.boaframework.OfferingStrategy;
import genius.core.boaframework.OpponentModel;
import genius.core.boaframework.SortedOutcomeSpace;
import genius.core.issue.Issue;
import genius.core.issue.Value;
import genius.core.utility.AbstractUtilitySpace;

/**
 * @author Andrea Scorza, Diego Staphorst, Erik Lokhorst, Ingmar van der Geest
 *
 */

public class Group8_BS extends OfferingStrategy {
//	/** Outcome space */
	private SortedOutcomeSpace outcomespace;
	
	/** Bidding strategies */
	public List<OfferingStrategy> bid_methods = new ArrayList<OfferingStrategy>();
	
	/** Bidding weights for current bid */
	public List<Double> bid_weights = new ArrayList<Double>();
	
	/** Bidding utility space of current negotiation */
	private AbstractUtilitySpace abstractUtilitySpace;
	

public Group8_BS() {
		// TODO Auto-generated constructor stub
	}

	/**
 	* Initiate agents, weights for the bidding strategy
 	*/
	@Override
	public void init(NegotiationSession negoSession, OpponentModel model, OMStrategy oms,
			Map<String, Double> parameters) throws Exception {
		
		this.negotiationSession = negoSession;
		this.opponentModel 		= model;
		this.omStrategy 		= oms;

		// Initiate bidding strategies		
//		bid_methods.add(new CUHKAgent_Offering(negoSession, model, oms));
		bid_methods.add(new Fawkes_Offering(negoSession, model, oms, parameters));
		bid_methods.add(new TheNegotiatorReloaded_Offering(negoSession, model, oms));

		// Initiate bidding weights, this needs to be replaced by TKI	
//		bid_weights.add(0.33);
		bid_weights.add(0.33);
		bid_weights.add(0.33);

		
		outcomespace = new SortedOutcomeSpace(negotiationSession.getUtilitySpace());
		negotiationSession.setOutcomeSpace(outcomespace);		
	}

	@Override
	public BidDetails determineOpeningBid() {
		return determineNextBid();
	}

	/**
	 * Offering strategy which retrieves the bids of multiple agents.
	 * The function loops over all the bids and determines the values with
	 * the highest weight.
	 */
	@Override
	public BidDetails determineNextBid() {
		// Pools all bids, separated in issues, with its value and determined
		// weight for this bid per agent in the variable bids
		HashMap<Issue, HashMap<Value, Double>> bids = new HashMap<Issue, HashMap<Value, Double>>();		
		HashMap<Value, Double> bid_with_weight;
		for (int i = 0; i < bid_methods.size(); i++) {
			OfferingStrategy agent_bs = bid_methods.get(i);
			Double agent_weight = bid_weights.get(i);
			Bid agent_bid = agent_bs.determineNextBid().getBid();
			for (Issue issue : negotiationSession.getIssues()) {
				if (i == 0)
					bid_with_weight = new HashMap<Value, Double>();
				else 
					bid_with_weight = bids.get(issue);
				bid_with_weight.merge(agent_bid.getValue(issue), agent_weight, Double::sum);
				bids.put(issue, bid_with_weight);	
			}			
		}

		
		// Get issues with highest overall weight
		// If multiple with same values randomly pick one
		int i = 1;
		HashMap<Integer, Value> bid_to_determine = new HashMap<>();
		for (Issue issue : negotiationSession.getIssues()) {
			Double max_weight = 0.0;
			Value value_with_heighest_weight = null;
			for (Entry<Value, Double> value_with_weight : bids.get(issue).entrySet()) {
				if (max_weight < value_with_weight.getValue()) {
					max_weight = value_with_weight.getValue();
					value_with_heighest_weight = value_with_weight.getKey();
				}
			}
			bid_to_determine.put(i, value_with_heighest_weight);
			i++;
		}
		// Our Acceptance strategy is based on the Utility we offer to the other agent
		double utility_bid =  this.abstractUtilitySpace.getUtility(new Bid(negotiationSession.getDomain(), bid_to_determine));		
		nextBid = new BidDetails(new Bid(negotiationSession.getDomain(), bid_to_determine), utility_bid);
		return nextBid;
	}


	/**
 	* To determine the utility for the offer we make
 	*/
	public void setAbstractUtilitySpace(AbstractUtilitySpace abstractUtilitySpace) {
		this.abstractUtilitySpace = abstractUtilitySpace;
	}

	public NegotiationSession getNegotiationSession() {
		return negotiationSession;
	}

	@Override
	public String getName() {
		return "Group8_BS";
	}
}