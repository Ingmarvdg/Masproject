package boaexample;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.bidding.BidDetails;
import genius.core.boaframework.NegotiationSession;
import genius.core.boaframework.OfferingStrategy;
import genius.core.utility.AbstractUtilitySpace;

public class Random_Offering
  extends OfferingStrategy
{
  public Random_Offering() {}
  
  public BidDetails determineNextBid()
  {
    Bid localBid = negotiationSession.getUtilitySpace().getDomain().getRandomBid(null);
    try
    {
      nextBid = new BidDetails(localBid, negotiationSession.getUtilitySpace().getUtility(localBid), negotiationSession.getTime());
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return nextBid;
  }
  
  public BidDetails determineOpeningBid()
  {
    return determineNextBid();
  }
  
  public String getName()
  {
    return "Other - Random Offering";
  }
}
