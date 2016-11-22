package de.l3s.eumssi.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.opensymphony.xwork2.Action;

public class CompanyInMongo implements Action, ServletRequestAware {
	HttpServletRequest request;
	
	/** Hashset to add the list of famous companies.. */
	private HashSet<String> listOfFamousCompanies									 = new HashSet<String>();
		
	/** Hashset to add the list of all companies. */
	private static HashSet<String> listOfCompanies											 = new HashSet<String>();
	
	/** HashMap to store the list of urls and count for check the predicates */
	private static 	HashMap<String, Integer> 	listOfUrlsWithPredicatesCount		 = new HashMap<String, Integer>();
	private static 	HashMap<String, JSONObject> listOfFamousCompaniesWithJsonData = new HashMap<String, JSONObject>();
	private static 	String currectUrlKey;


	
	/**
	 * This function do the following 
	 *  1. Grep list of companies from DBPedia
	 *  2. pre-process the dbpedia data 
	 *  3. call another function to check the given company interesting or not to the user
	 *
	 * @throws Exception the exception
	 */
	private void grepListOfCompanies() throws Exception {
		listOfUrlsWithPredicatesCount.put("Category:Car_manufacturers_of_Germany", 2);
		listOfUrlsWithPredicatesCount.put("Category:Car_manufacturers_of_France", 3);
		listOfUrlsWithPredicatesCount.put("Category:Car_manufacturers_of_Italy", 3);
		listOfUrlsWithPredicatesCount.put("Category:Car_manufacturers_of_Japan", 3);
		listOfUrlsWithPredicatesCount.put("Category:Car_manufacturers_of_Sweden", 3);
		listOfUrlsWithPredicatesCount.put("Category:Car_manufacturers_of_the_United_Kingdom", 3);
		listOfUrlsWithPredicatesCount.put("Category:Car_manufacturers_of_the_United_States", 4);
		listOfUrlsWithPredicatesCount.put("Consumer_electronics", 6);
		listOfUrlsWithPredicatesCount.put("Smartphone", 3);
		listOfUrlsWithPredicatesCount.put("Computer", 6);
		listOfUrlsWithPredicatesCount.put("Software", 6);
		listOfUrlsWithPredicatesCount.put("Internet", 5);
		listOfUrlsWithPredicatesCount.put("Computer_hardware", 3);
		listOfUrlsWithPredicatesCount.put("Category:Clothing_brands", 3);
		listOfUrlsWithPredicatesCount.put("Category:Soft_drinks", 3);
		
		/*Iterating through the url and read the list of companies from data*/		
		for(Iterator<?> listOfUrlsIterator = listOfUrlsWithPredicatesCount.keySet().iterator(); listOfUrlsIterator.hasNext();) {
			currectUrlKey = (String) listOfUrlsIterator.next();
			String url = "http://dbpedia.org/data/"+currectUrlKey+".json";
			/*Reading the list of companies for the given group category*/
			String outputString = readUrl(url);
			
		   	JSONParser parser = new JSONParser();
		   	Object obj = parser.parse(outputString);
		    JSONObject jsonObject = (JSONObject) obj;
		    
		    Iterator<?> iterator = jsonObject.keySet().iterator();
		    	    
		    /* iterate over the list of companies*/
		    while(iterator.hasNext()){
		    	String key = (String) iterator.next();		    		   		    	
	   		 	System.out.println("Key is : "+key);
	    		String[] splitKey = (String[])key.split("/");
	    		String carCompanyWith_ = splitKey[splitKey.length-1].toString();
	    		
	    		List<String> splitKey1 = new ArrayList<String>();
	    		splitKey1.addAll(Arrays.asList((String[])carCompanyWith_.split("_")));
	    		if (splitKey1.size() > 1) {					
	    			splitKey1.add(carCompanyWith_);
	    		}
	    		
	    		for(String carCompany : splitKey1){
		    		if(carCompany.contains("Category:")){
		    			carCompany = carCompany.replace("Category:","");
		    		}
		    		
		    		String dbpediaCompanyUrl = "http://dbpedia.org/data/"+carCompany+".json";
		    		/*the following condition check a company interesting or not to the user */
		    		if (isInterestingCompany(dbpediaCompanyUrl, carCompany)) {
		    			listOfFamousCompanies.add(carCompany);
					}
		    		listOfCompanies.add(carCompany);
			    }
		    }
		}
		
	    String[] companies = listOfFamousCompanies.toArray(new String[listOfFamousCompanies.size()]);
	    /*calling function to store the interesting companies into mongoDB*/
	    StoreCompaniesIntoMongo.insertCompanies(companies, listOfFamousCompaniesWithJsonData);	
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Checks if a company interesting or not to user. 
	 * its making decision based on 
	 * 	1. the predicates present in the company object
	 * 		for example: if a company contains revenue, assets, equity, products, then return true
	 *  2. check conditions for some predicates 
	 *      check the number of employee grater than a particular number
	 * 	 
	 * @param url the url of the company. It will use to read the company object from DBPedia
	 * @param companyName the name of the company
	 * @return true if the given company is interested to the user
	 * @throws Exception the exception
	 */
	private static Boolean isInterestingCompany(String url, String companyName) throws Exception {
		String outputString = readUrl(url);
		int count = 0;
		
		if (outputString == null) {
			return false;
		}
		
		JSONParser parser = new JSONParser();
	   	Object obj = parser.parse(outputString);
	    JSONObject jsonObject = (JSONObject) obj;
	    
	    /*getting all available predicates from the company object. Which is got from DBPedia*/
	    Iterator<?> iterator = jsonObject.keySet().iterator();
	    
	    /*iterate over the predicates*/
	    while(iterator.hasNext()){
	    	String key = (String) iterator.next();
	    	
	    	Object ob;
		   	JSONObject job1;
		   	JSONArray jarray;		   	
		   	
		   	ob= jsonObject.get(key);
   		    job1 = (JSONObject) ob;
   		    
   		 for(Iterator<?> iterator1 = job1.keySet().iterator(); iterator1.hasNext();){
   			String key1 = (String) iterator1.next();
		    if(job1.get(key1) instanceof JSONArray){
	    		jarray= (JSONArray)job1.get(key1);
	    		String mainKey=null;
	    		
	    		String[] splitKey1=(String[])key1.split("/");
			    mainKey=splitKey1[splitKey1.length-1].toString();
			    
	    		if (mainKey.equals("numberOfEmployees")	||
	    		    mainKey.equals("homepage") ||
	    		    mainKey.equals("netIncome") ||
	    		    mainKey.equals("equity") ||	 
	    		    mainKey.equals("assets") ||	
	    		    mainKey.equals("products") ||		    
	    		    mainKey.equals("production") ||		    		    
	    		    mainKey.equals("revenue")
	    		   ) {
	    			/*making decision based on the predicates*/
	    			if (checkPredicates(jarray, mainKey)) {
						count ++;
					}
	    			
	    			/*down the score to the companies, which have'nt important predicate but satisfied the condition*/
	    		    if(scoreDown(jarray, mainKey)){
	    		    	count -=3;
	    		    }
				}	    		
		    }
   		 }
	    }
	    
	    int counterCheck = listOfUrlsWithPredicatesCount.get(currectUrlKey);	    

	    /*decide a company interesting or not based on the counter.*/
	    if (count >= counterCheck) { 
	    	/*this hashmap storing the company-jsonobject to store into mongoDB*/
	    	listOfFamousCompaniesWithJsonData.put(companyName, jsonObject);
	    	return true; 
	    } else {
	    	return false;
	    }
	}
	
	
	
	
	
	
	/**
	 * down the score to the companies, which have'nt important predicate but satisfied the condition
	 *
	 * @param jarray a company in the JSON Array
	 * @param predicate the predicate to check
	 * @return the boolean, which uses to score down or not
	 */
	private static Boolean scoreDown(JSONArray jarray, String predicate){
		JSONObject jb2 = (JSONObject) jarray.get(0);
		String value = jb2.get("value").toString();
		
		switch (predicate) {
		case "numberOfEmployees": /*this is one of the important predicate to check*/
			int numberOfEmployees = Integer.parseInt(value);							 	        							    		    
			if (numberOfEmployees <= 4000) { return true; } else {return false;}
			
		case "netIncome": /*this is one of the important predicate to check*/
			String netIncome = value;
			if ((netIncome.contains("$") && String.valueOf(netIncome.charAt(0)).equals("$"))|| (netIncome.contains("€") && String.valueOf(netIncome.charAt(0)).equals("€"))) {
				netIncome = netIncome.substring(1);
			}
			int ni = Character.getNumericValue(netIncome.charAt(0));				
			if (ni < 4) { 
				return true; 
				} else {return false;}
			
		case "revenue": /*this is one of the important predicate to check*/
			String revenue = value;
			if ((revenue.contains("$") && String.valueOf(revenue.charAt(0)).equals("$"))|| (revenue.contains("€") && String.valueOf(revenue.charAt(0)).equals("€"))) {
				revenue = revenue.substring(1);
			}
			int rev = Character.getNumericValue(revenue.charAt(0));						
			if (rev < 4) { 
				return true; 
				} else {return false;}			
			
			
		default:
			return false;
		}	
	}
	
	
	
	
	

	
	
	
	/**
	 * Check predicate to make decision(is it interestion company or not).
	 *
	 * @param jarray a company in the JSON Array
	 * @param predicate the predicate to check
	 * @return the boolean, which uses to score down or not
	 */
	private static Boolean checkPredicates(JSONArray jarray, String predicate) {
		JSONObject jb2 = (JSONObject) jarray.get(0);
		String value = jb2.get("value").toString();
				
		switch (predicate) {
		case "numberOfEmployees":
			int numberOfEmployees = Integer.parseInt(value);							 	        							    		    
			if (numberOfEmployees >= 10000) { return true; } else {return false;}
			
		case "homepage":
			return true;

		case "netIncome":
			String netIncome = value;
			if ((netIncome.contains("$") && String.valueOf(netIncome.charAt(0)).equals("$"))|| (netIncome.contains("€") && String.valueOf(netIncome.charAt(0)).equals("€"))) {
				netIncome = netIncome.substring(1);
			}
			int ni = Character.getNumericValue(netIncome.charAt(0));			
			if (ni < 4) { 
				return true; 
				} else {return false;}
			
		case "revenue":
			String revenue = value;
			if ((revenue.contains("$") && String.valueOf(revenue.charAt(0)).equals("$"))|| (revenue.contains("€") && String.valueOf(revenue.charAt(0)).equals("€"))) {
				revenue = revenue.substring(1);
			}
			int rev = Character.getNumericValue(revenue.charAt(0));						
			if (rev < 4) { 
				return true; 
				} else {return false;}	
		
		case "equity":
			return true;
			

		case "production":
			return true;	
			
		case "assets":
			return true;			
			
		default:
			return false;
		}		
	}
	
	
	
	
	
	@Override
	public String execute() throws Exception {
		grepListOfCompanies();
		return null;
	}
	
	
	
	
	
	
	public void setServletRequest(HttpServletRequest request) {
		System.out.println("Getting data from DB-Pedia ");
		System.out.println("Please wait a moment...");
		this.request = request;		
	}
	
	
	
	
	
	
	
	/**
	 * function to grep the data from third-party server
	 *
	 * @param urlString the url to get the data
	 * @return the data got from third-party server
	 */
	public static String readUrl(String urlString)  {
	    BufferedReader reader = null;
	    try {
	        URL url = new URL(urlString);
	        reader = new BufferedReader(new InputStreamReader(url.openStream()));
	        StringBuffer buffer = new StringBuffer();
	        int read;
	        char[] chars = new char[1024];
	        while ((read = reader.read(chars)) != -1) {
	            buffer.append(chars, 0, read);
	        }
	        return buffer.toString();
	    } catch(Exception e) {
	    	return null;
	    }
	    finally {
	        if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    }
	}
}
