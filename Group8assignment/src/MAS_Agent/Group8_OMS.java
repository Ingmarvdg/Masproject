/**
 * 
 */
package MAS_Agent;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import MAS_Agent.help_components.TheFawkes.TheFawkes_OMS;

import genius.core.bidding.BidDetails;
import genius.core.boaframework.NegotiationSession;
import genius.core.boaframework.OMStrategy;
import genius.core.boaframework.OpponentModel;

/**
 * @author Andrea Scorza, Diego Staphorst, Erik Lokhorst, Ingmar van der Geest
 *
 */
public class Group8_OMS extends TheFawkes_OMS
{
	  private ArrayDeque<Double> lastTen;
	  private int secondBestCounter = 1;
	  
	  public Group8_OMS() {}
	  
	  public void init(NegotiationSession paramNegotiationSession, OpponentModel paramOpponentModel, Map<String, Double> paramMap)
	  {
	    initializeAgent(paramNegotiationSession, paramOpponentModel);
	  }
	  
	  private void initializeAgent(NegotiationSession paramNegotiationSession, OpponentModel paramOpponentModel)
	  {
	    super.init(paramNegotiationSession, paramOpponentModel, new HashMap());
	    lastTen = new ArrayDeque(11);
	  }
	  
	  public BidDetails getBid(List<BidDetails> paramList)
	  {
	    return super.getBid(paramList);
	  }
	  
	  public int getSecondBestCount()
	  {
	    return secondBestCounter;
	  }
	  
	  public boolean canUpdateOM()
	  {
	    return true;
	  }
	  
	  public String getName()
	  {
	    return "Group8_OMS";
	  }
	  
	  public static final class Comparing
	    implements Comparator<BidDetails>
	  {
	    private final OpponentModel model;
	    
	    protected Comparing(OpponentModel paramOpponentModel)
	    {
	      model = paramOpponentModel;
	    }
	    
	    public int compare(BidDetails paramBidDetails1, BidDetails paramBidDetails2)
	    {
	      double d1 = model.getBidEvaluation(paramBidDetails1.getBid());
	      double d2 = model.getBidEvaluation(paramBidDetails2.getBid());
	      if (d1 < d2) {
	        return 1;
	      }
	      if (d1 > d2) {
	        return -1;
	      }
	      return 0;
	    }
	  }
	}