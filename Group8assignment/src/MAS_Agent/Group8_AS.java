/**
 * 
 */
package MAS_Agent;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import genius.core.boaframework.AcceptanceStrategy;
import genius.core.boaframework.Actions;
import genius.core.boaframework.BOAparameter;
import genius.core.boaframework.NegotiationSession;
import genius.core.boaframework.OfferingStrategy;
import genius.core.boaframework.OpponentModel;

/**
 * IN THIS VERSION I JUST COPIED AC_Next.java
 * @author Andrea Scorza, Diego Staphorst, Erik Lokhorst, Ingmar van der Geest
 *
 */
public class Group8_AS extends AcceptanceStrategy {
	private double a;
	private double b;
	
	TKI tki = new TKI();
	
	/**
	 * Empty constructor for the BOA framework.
	 */
	public Group8_AS() {
	}

	public Group8_AS(NegotiationSession negoSession, OfferingStrategy strat,
			double alpha, double beta) {
		this.negotiationSession = negoSession;
		this.offeringStrategy = strat;
		this.a = alpha;
		this.b = beta;
	}

	@Override
	public void init(NegotiationSession negoSession, OfferingStrategy strat,
			OpponentModel opponentModel, Map<String, Double> parameters)
			throws Exception {
		this.negotiationSession = negoSession;
		this.offeringStrategy = strat;

		if (parameters.get("a") != null || parameters.get("b") != null) {
			a = parameters.get("a");
			b = parameters.get("b");
		} else {
			a = 1;
			b = 0;
		}
	}

	@Override
	public String printParameters() {
		String str = "[a: " + a + " b: " + b + "]";
		return str;
	}

	@Override
	public Actions determineAcceptability() {
			
		
		double nextMyBidUtil = offeringStrategy.getNextBid()
				.getMyUndiscountedUtil();
		double lastOpponentBidUtil = negotiationSession.getOpponentBidHistory()
				.getLastBidDetails().getMyUndiscountedUtil();
		
		
		TKI.populate(lastOpponentBidUtil);
		try {
			tki.print();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("inside the catch");
		}

		if (a * lastOpponentBidUtil + b >= nextMyBidUtil) {
			tki.list_size(0);
			return Actions.Accept;
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
