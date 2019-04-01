package MAS_Agent;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.Test;

public class TKI {
	
	public static ArrayList<Double> opponentBid = new ArrayList<Double>();
	static double lastBidUtil;
	public static ArrayList<Double> coopList = new ArrayList<Double>();
	
	
	@Test
	public static void print() 
	  throws IOException {
	    String str = "Standard Deviation =" + standard_deviation() + "Average Cooperativness =" + average_cooperat();
	 
	    //Path path = Paths.get("/Users/andrea/eclipse-workspace/Group8assignment/src/MAS_Agent/print.txt");
	    //Path path = Paths.get("./print.txt");
	    //Path path = Paths.get(".", "./print.txt");
	    File file = new File("print.txt");
	    String path1 = file.getAbsolutePath();
	    Path path = Paths.get(path1);
	    //../myFile.txt

	    byte[] strToBytes = str.getBytes();
	 
	    Files.write(path, strToBytes);
	 
	    //String read = Files.readAllLines(path).get(0);
	    //assertEquals(str, read);
	}
	
	public static void populate(double lastOpponentBidUtil) {
		lastBidUtil = lastOpponentBidUtil;
		opponentBid.add(lastOpponentBidUtil);
		
		
		if (opponentBid.isEmpty() != true) {
			//populating the list of cooperativity value
			coopList.add(TKI.currentbidcooperativeness());
		}
	}
	
	public static void list_size(int x) {
		if (x == 1) 
			System.out.println("Opponent Bid Size = "+ opponentBid.size());
		else
			if (x == 2) 
				System.out.println("Cooperativness list size = "+ coopList.size());
			else
				System.out.println("Opponent Bid Size = "+ opponentBid.size() + "  Cooperativness list size = "+ coopList.size());	
	}
	
	public static double standard_deviation() {
		double avg = 0, sum = 0;
		for(int j = 0; j < opponentBid.size(); j++) {
			avg = avg + opponentBid.get(j);
        }
		
		avg = avg / opponentBid.size();
		for(int j = 0; j < opponentBid.size(); j++) {
			sum = sum + Math.pow((opponentBid.get(j) - avg), 2.0);
        }
		
		sum = sum / opponentBid.size();
		
		
		//System.out.println("variance" + sum);
		return  Math.pow(sum, (double)1/2) ;
	}
	
	
	public static double currentbidcooperativeness() {
		double x = 0;
		for(int j = 0; j < opponentBid.size(); j++) {
			x = x + opponentBid.get(j);
        }
		x = x / opponentBid.size();
		
		if (lastBidUtil > x)
			return (lastBidUtil - x);
		
		else if (lastBidUtil < x)
			return (-(x - lastBidUtil));
		
		return 0; 
	}
	
	
	public static double average_cooperat() {
		double avgCoop = 0;
		//if (opponentBid.isEmpty() != true) {
			//we put this value [-1,1] in this list
			//coopList.add(currentbidcooperativeness(lastBidUtil, opponentBid));
			
			//every time we calculate the average
			int j;
			for(j = 0; j < coopList.size(); j++) {
				avgCoop = avgCoop + coopList.get(j);
	        }
			//System.out.println("J = " + j);
			return(avgCoop / coopList.size());
		//}
		////	return 0;
	}

	
}
