package mas2019.group8.help_components;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


public class TKI {
	
	public static ArrayList<Double> opponentBid = new ArrayList<Double>(); //list of opponent bid
	static double lastBidUtil; //this variable is referred to the last bid utility made by the opponent
	static double myBidUtil; //this is referred to out last bid utility
	public static ArrayList<Double> coopList = new ArrayList<Double>();
	
	
	//This method used to take the results from a previous negotiation stored in 
	//a temporary file and copy them inside the final file
	
	/*@Test
	public static void saveToFile() 
	  throws IOException {
	   
	    File file1 = new File("print.txt");
	    String path1 = file1.getAbsolutePath();
	    Path path1bis = Paths.get(path1);
	    
	    File file2 = new File("final.txt");
	    String path2 = file2.getAbsolutePath();
	    Path path2bis = Paths.get(path2);
	    	    

	    boolean empty2 = file2.exists() && file2.length() == 0;
	    boolean empty1 = file1.exists() && file1.length() == 0;
	    
	    if(empty1 != true) {
	    	String read1 = Files.readAllLines(path1bis).get(0);
		    if (empty2 == true) {
		    	byte[] strToBytes = read1.getBytes();
			    Files.write(path2bis, strToBytes);
		    }
		    else {
		    	BufferedReader reader = new BufferedReader(new FileReader("final.txt"));
			    int lines = 0;
			    while (reader.readLine() != null) lines++;
			    reader.close();
			    for (int x = 0; lines > x; x++) {
			    	read1 = read1 + '\n' + Files.readAllLines(path2bis).get(x);
			    }
			    byte[] strToBytes = read1.getBytes();
			    Files.write(path2bis, strToBytes);
		    }
	    }
	    
	}*/
	
	// this method was used to print the information about the negotiation inside the temporary file
	// these information were then used inside to arrange the weight 
	
	/*@Test 
	public static void print(int z) 
	  throws IOException {
		String str;
		if (z == 0) { //this is the case we accept his bid
		    str = "Last Bid Utility = " + lastBidUtil + "---Standard Deviation = " + standard_deviation() + "---Average Cooperativness = " + average_cooperat();

		}
		else { //here is the case he accepts our bid
			str = "Last Bid Utility = " + myBidUtil + "---Standard Deviation = " + standard_deviation() + "---Average Cooperativness = " + average_cooperat();
		}
		
	    File file = new File("print.txt");
	    String path1 = file.getAbsolutePath();
	    Path path = Paths.get(path1);

	    byte[] strToBytes = str.getBytes();
	 
	    Files.write(path, strToBytes);
	
	}*/
	
	// this method called inside the Acceptance Strategy is used to populate the list inside the TKI, 
	//and store values useful for the later calculations
	
	public static void populate(double lastOpponentBidUtil, double nextMyBidUtil) {
		lastBidUtil = lastOpponentBidUtil;
		myBidUtil = nextMyBidUtil;
		opponentBid.add(lastOpponentBidUtil);
		
		//if we have some bid, the cooperativity list will be populated
		
		if (opponentBid.isEmpty() != true) {

			coopList.add(TKI.currentbidcooperativeness());
		}
	}
	
	//This method was used to check the dimension of the two lists in order to check for errors
	
	/*public static void list_size(int x) {
		if (x == 1) 
			System.out.println("Opponent Bid Size = "+ opponentBid.size());
		else
			if (x == 2) 
				System.out.println("Cooperativness list size = "+ coopList.size());
			else
				System.out.println("Opponent Bid Size = "+ opponentBid.size() + "  Cooperativness list size = "+ coopList.size());	
	}*/
	
	//This method calculate the standar deviation based on the opponent bid
	
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
		return  Math.pow(sum, (double)1/2) ;
	}
	
	// This method calculates the cooperativness of the opponent most recent bid and it will normalize it from -1 to 1
	
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
	
	// This method returns the average cooperativness of the opponent based on the list of the opponent stored data about cooperativity
	
	public static double average_cooperat() {
		double avgCoop = 0;
			
			//every time we calculate the average
			int j;
			for(j = 0; j < coopList.size(); j++) {
				avgCoop = avgCoop + coopList.get(j);
	        }
			return(avgCoop / coopList.size());
	}



	
}
