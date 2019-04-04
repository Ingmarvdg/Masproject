package MAS_Agent;

import java.util.List;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import MAS_Agent.help_components.ExtensionUncertain;
import genius.core.Bid;
import genius.core.boaframework.AcceptanceStrategy;
import genius.core.boaframework.BoaParty;
import genius.core.boaframework.OMStrategy;
import genius.core.boaframework.OfferingStrategy;
import genius.core.boaframework.OpponentModel;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.ValueDiscrete;
import genius.core.parties.NegotiationInfo;
import genius.core.uncertainty.AdditiveUtilitySpaceFactory;
import genius.core.utility.AbstractUtilitySpace;

/**
 * This example shows how BOA components can be made into an independent
 * negotiation party and which can handle preference uncertainty.
 * 
 * For the course Multi-Agent system
 */
@SuppressWarnings("serial")
public class Group8_BoaParty extends BoaParty 
{
	@Override
	public void init(NegotiationInfo info) 
	{
		// The choice for each component is made here
		AcceptanceStrategy 	ac  = new Group8_AS();
		OfferingStrategy 	os  = new Group8_BS();
		OpponentModel 		om  = new Group8_OM();		
		OMStrategy			oms = new Group8_OMS();
		
		// All component parameters can be set below.
		Map<String, Double> noparams = Collections.emptyMap();
		Map<String, Double> osParams = new HashMap<String, Double>();
		// Set the concession parameter "e" for the offering strategy to yield Boulware-like behavior
		osParams.put("e", 0.2);
		
		// Initialize all the components of this party to the choices defined above
		configure(ac, noparams, 
				os,	osParams, 
				om, noparams,
				oms, noparams);
		super.init(info);
		((Group8_BS) os).setAbstractUtilitySpace(super.utilitySpace);
	}



	/**
	 * Specific functionality, such as the estimate of the utility space in the
	 * face of preference uncertainty, can be specified by overriding the
	 * default behavior.
	 * 
	 * This example estimator sets all weights and all evaluator values randomly.
	 */
	@Override
	public AbstractUtilitySpace estimateUtilitySpace() 
	{
		// make use of extended class ExtensionUncertain to access weights
				ExtensionUncertain additiveUtilitySpaceFactory = new ExtensionUncertain(getDomain());
				List<IssueDiscrete> issues = additiveUtilitySpaceFactory.getIssues();
				List<Bid> bidRanking = userModel.getBidRanking().getBidOrder();
				double highUtil = userModel.getBidRanking().getHighUtility();
				double lowUtil = userModel.getBidRanking().getLowUtility();
				double nonZeroCounter = 0;
				double LowCounter = 0;
				double minWeight = 0.01;
				Bid bestBid = userModel.getBidRanking().getMaximalBid();
				Bid worstBid = userModel.getBidRanking().getMinimalBid();
				for(int p = 0; p < bidRanking.size() - 1; p++)
					System.out.println(bidRanking.get(p));
				System.out.println(highUtil);
				System.out.println(lowUtil);
				
				for (IssueDiscrete i : issues)
				{
					if (bestBid.getValue(i) == worstBid.getValue(i)) //compare bestBid to worstBid
						System.out.println(bestBid.getValue(i));
						additiveUtilitySpaceFactory.setWeight(i, minWeight);  //if values are equal (between best and worst), set weight to 0.01
					if (additiveUtilitySpaceFactory.getWeight(i) != minWeight)
						nonZeroCounter++; // count the issues for which values weren't the same
					if (additiveUtilitySpaceFactory.getWeight(i) == minWeight)
						LowCounter += minWeight; // count issues for which values were the same
					System.out.println(nonZeroCounter);
				}
				
				// compute how much weight has been left to divide and distribute equally to remaining issues
				double nonZeroWeight = (1 - LowCounter) / nonZeroCounter; 
				
				for (IssueDiscrete i : issues)
				{
					if (additiveUtilitySpaceFactory.getWeight(i) != minWeight)
						additiveUtilitySpaceFactory.setWeight(i, nonZeroWeight);
					for (ValueDiscrete v : i.getValues())
						if (bestBid.containsValue(i, v))
							additiveUtilitySpaceFactory.setUtility(i, v, 10); //if the value is in the bestBid, give it 10 (highest value)
						else if (worstBid.containsValue(i, v))
							additiveUtilitySpaceFactory.setUtility(i, v, 1); // if the value is in the worstBid, give it 1 (lowest value)
						else {
							additiveUtilitySpaceFactory.setUtility(i, v, 5); // if the value is in neither the best or worst bid, give it an average value
						}
						
				}
				
				// Normalize the weights, since we picked them randomly in [0, 1]
				additiveUtilitySpaceFactory.normalizeWeights();
				
				// The factory is done with setting all parameters, now return the estimated utility space
				return additiveUtilitySpaceFactory.getUtilitySpace();
	}
	
	@Override
	public String getDescription() 
	{
		return "Thotiana";
	}

	// All the rest of the agent functionality is defined by the components selected above, using the BOA framework
}
