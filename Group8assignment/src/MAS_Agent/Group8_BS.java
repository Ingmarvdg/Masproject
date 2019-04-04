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
import MAS_Agent.help_components.Gahboninho_Offering;
import MAS_Agent.help_components.NiceTitForTat_Offering;
import MAS_Agent.help_components.TKI;
import MAS_Agent.help_components.TheNegotiatorReloaded_Offering;
import MAS_Agent.help_components.Yushu_Offering;
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
	TKI tki = new TKI();

	/** Bidding strategies */
	public List<OfferingStrategy> bid_methods = new ArrayList<OfferingStrategy>();

	/** Bidding weights for current bid */
	public List<Double> bid_weights = new ArrayList<Double>();


	/** Bidding utility space of current negotiation */
	private AbstractUtilitySpace abstractUtilitySpace;

	// counter
	int counter = 0;


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
		bid_methods.add(new Fawkes_Offering(negoSession, model, oms, parameters));
		bid_methods.add(new TheNegotiatorReloaded_Offering(negoSession, model, oms));
		bid_methods.add(new Gahboninho_Offering(negoSession, model, oms, parameters));
		bid_methods.add(new Yushu_Offering(negoSession, model, oms, parameters));
		bid_methods.add(new NiceTitForTat_Offering(negoSession, model, oms, parameters));

		// Initiate bidding weights, those weight are the static basic weight and were calculated outside genius
		//Explanation in the report
		
		bid_weights.add(0.2);         //FAWKES
		bid_weights.add(0.190416149); //NEGO
		bid_weights.add(0.197129227); //GAHBO
		bid_weights.add(0.242762364); //YUSHU
		bid_weights.add(0.169692259); //NICE


		outcomespace = new SortedOutcomeSpace(negotiationSession.getUtilitySpace());
		negotiationSession.setOutcomeSpace(outcomespace);		
	}
	
	//This method uses the TKI values, and the coefficient calculated before to 
	//redistribute the weight of our panel member based on the opponent
	//We decided to call this method after five rounds in order to have a minimum sufficient amount of data
	
	public void Weight() {

		//calculation of the weights based on opponent
		double[] sd_coeff = new double[4];
		double[] coop_coeff = new double[4];
		double[] sd_cut = new double[4];
		double[] coop_cut = new double[4];
		//bid_weights.set(0, 0.3);
		
		//Those are the coefficients calculated with the training data
		//More info in the report
		
		sd_coeff[0] = 0.742159262; //NEGO
		sd_coeff[1] = 1.105698694; //GABO
		sd_coeff[2] = 1.46240313;  //YUSHU
		sd_coeff[3] = -0.96443325; //NICE

		coop_coeff[0] = -0.343431038;
		coop_coeff[1] = 0.859597362;
		coop_coeff[2] = 1.29910522;
		coop_coeff[3] = -3.074196406;

		sd_cut[0] = 0.832969372;
		sd_cut[1] = 0.819401409;
		sd_cut[2] = 0.689557158;
		sd_cut[3] = 0.814648053;

		coop_cut[0] = 0.884449006;
		coop_cut[1] = 1.105698694;
		coop_cut[2] = 0.757832995;
		coop_cut[3] = 0.775181624;

		double[] x = new double[4];
		double stand_dev = TKI.standard_deviation(); //implementation of the value collected by the TKI
		double avg_coop = TKI.average_cooperat();
		
		
		for (int i = 0; i < 4 ; i++) {
			x[i] = (sd_coeff[i] * stand_dev) + sd_cut[i];
			double y = (coop_coeff[i] * avg_coop) + coop_cut[i];
			x[i] = (x[i] + y) / 2;
		}
		
		//Normalization of the weights
		double Faw_avg = (x[0] + x[1] + x[2] + x[3]) / 4;
		double sum = x[0] + x[1] + x[2] + x[3] + Faw_avg;
		double coeff = 1 / sum;
		bid_weights.set(0, coeff * Faw_avg);
		bid_weights.set(1, coeff * x[0]);
		bid_weights.set(2, coeff * x[1]);
		bid_weights.set(3, coeff * x[2]);
		bid_weights.set(4, coeff * x[3]);

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
		counter++;
		if (counter > 4)
			this.Weight();
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