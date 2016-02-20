package de.l3s.eumssi.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.l3s.eumssi.dao.MongoDBManager;

public abstract class ContentGenerator {
	
 public	MongoDBManager mongo = new MongoDBManager();
    
   public abstract String makeDicision(); 
 
   public abstract String questionGenerator();
   
   public abstract String infoGenerator();
 
   public abstract String abstractGenerator();
   
   public String CorrectAnsOrderFinder(String[] correctAns, ArrayList options) {
		  for(int i=0;i<correctAns.length;i++){
		        if (correctAns[i].contains("language")) {
					// tempValue = tempValue.replace("language", "");
					String[] splitTempValue = correctAns[i].split("\\s+");
					correctAns[i]= splitTempValue[0];
				}
			}
		Map<Integer, String> indexToAns = new HashMap<Integer, String>();
		List<Integer> index = new<Integer> ArrayList();
		String correctOrder = null;
		
		System.out.println(options);
		try {
			for (String ans: correctAns) {
				System.out.println("Ans=" + ans);
				if (options.indexOf(ans) < 0)
					continue;
				index.add(options.indexOf(ans));
				indexToAns.put(options.indexOf(ans), ans);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(options);
		}
		Collections.sort(index);
		for (int i = 0; i < index.size(); i++) {
			if (correctOrder != null)
				correctOrder = correctOrder + "," + indexToAns.get(index.get(i));
			else
				correctOrder = indexToAns.get(index.get(i));
		}
		return correctOrder;

	}
   
	private double getDistance(double submittedCountryLat, double submittedCountryLong, double countryLat,
			double countryLong) {

		double distance = 0;
		double submittedCountryLatRadian = Math.toRadians(submittedCountryLat);
		double submittedCountryLongRadian = Math.toRadians(submittedCountryLong);
		double countryLatRadian = Math.toRadians(countryLat);
		double countryLongRadian = Math.toRadians(countryLong);

		double absoluteDistanceLat = submittedCountryLatRadian - countryLatRadian;
		double absoluteDistanceLong = submittedCountryLongRadian - countryLongRadian;

		distance = Math.sin(absoluteDistanceLat / 2) * Math.sin(absoluteDistanceLat / 2)
				+ Math.cos(submittedCountryLatRadian) * Math.cos(countryLatRadian) * Math.sin(absoluteDistanceLong / 2)
						* Math.sin(absoluteDistanceLong / 2);

		distance = 2 * Math.asin(Math.sqrt(distance));
		return distance * 6371;

	}
}
