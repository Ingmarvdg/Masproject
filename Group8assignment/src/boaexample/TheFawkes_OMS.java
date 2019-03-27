package boaexample;

import genius.core.bidding.BidDetails;
import genius.core.boaframework.NegotiationSession;
import genius.core.boaframework.OMStrategy;
import genius.core.boaframework.OpponentModel;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class TheFawkes_OMS
  extends OMStrategy
{
  private ArrayDeque<Double> lastTen;
  private int secondBestCounter = 1;
  
  public TheFawkes_OMS() {}
  
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
    Collections.sort(paramList, new Comparing(model));
    BidDetails localBidDetails = (BidDetails)paramList.get(0);
    int i = 1;
    Iterator localIterator = lastTen.iterator();
    while (localIterator.hasNext())
    {
      double d = ((Double)localIterator.next()).doubleValue();
      if (d != localBidDetails.getMyUndiscountedUtil()) {
        i = 0;
      }
    }
    if (i != 0)
    {
      secondBestCounter += 1;
      if (paramList.size() > 1) {
        localBidDetails = (BidDetails)paramList.get(1);
      }
    }
    lastTen.addLast(Double.valueOf(localBidDetails.getMyUndiscountedUtil()));
    if (lastTen.size() > 10) {
      lastTen.removeFirst();
    }
    return localBidDetails;
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
    return "TheFawkes";
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
