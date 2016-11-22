package de.l3s.eumssi.action;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;


import de.l3s.eumssi.dao.MongoDBManager;


/**
 * The Class to store the company json object into mongoDB
 */
public class StoreCompaniesIntoMongo {
	private static Set<String> listOfAsianCountries = new HashSet<String>();
	
	/**
	 * Method to store the companies into mongoDB  and strictly filter the Asian companies
	 * its use companies as mongoDB collection name in database.
	 *
	 * @param companies the array of famous company names
	 * @param listOfFamousCompaniesWithJsonData the list of famous companies in json data. These json data got from DBPedia
	 * @return true, if operation successful
	 * @throws Exception the exception
	 */
	public static boolean insertCompanies(String companies[], HashMap<String, JSONObject> listOfFamousCompaniesWithJsonData) throws Exception {
		MongoDBManager mongo=MongoDBManager.getInstance(); 
		DBCollection collection=mongo.getCollection("companies");
		getAsianCountries();
		
		/*getting company names from array and storing into mongoDB*/ 
		for(String company : companies) {
			JSONObject innerJsonObject=new JSONObject();
			
		   	Object ob;
		   	JSONObject job1;
		   	JSONObject job2;
		   	JSONArray jarray;
		   	
		   	/*getting the company object*/
		   	JSONObject jsonObject = listOfFamousCompaniesWithJsonData.get(company);
		    
		   	/*iterate over the company object*/
		    for(Iterator<?> iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
	   		    String key = (String) iterator.next();
	
	   	        ob= jsonObject.get(key);
	   		    job1 = (JSONObject) ob;
	   		  
	   		 /*cleaning, selecting and pre-processing the predicates from the json object*/
	   		 for(Iterator<?> iterator1 = job1.keySet().iterator(); iterator1.hasNext();){
	   			String key1 = (String) iterator1.next();
			    if(job1.get(key1) instanceof JSONArray){
			    	String mainValue=null;
		    		jarray= (JSONArray)job1.get(key1);
		    		String mainKey=null;
		    		
		    		
		    		String[] splitKey1=(String[])key1.split("/");
				    mainKey=splitKey1[splitKey1.length-1].toString();
				    
				    /*List of chosen predicates to store into mongoDB
				     * other predicates will be simply skip*/
				    if(		mainKey.equals("location") ||
				    		mainKey.equals("abstract") ||
				    		mainKey.equals("locationCountry") ||
				    		mainKey.equals("locationCity") || 
				    		mainKey.equals("products")  || 
				    		mainKey.equals("assets")  || 
		    			   mainKey.equals("netIncome")  ||
		    			   mainKey.equals("numberOfEmployees")  ||				    		
				    		mainKey.equals("industry") ||
				    		mainKey.equals("slogan") ||
				    		mainKey.equals("thumbnail") ||
				    		mainKey.equals("revenue")){
				    	
				    	for( int i=0;i<jarray.size(); i++){
				    		job2=(JSONObject)(jarray.get(i));
				    		
				    		
		    			     if(mainKey.equals("abstract")){
		    					if(job2.get("lang").equals("en")){
		    						   String abs=job2.get("value").toString();
		    						   List<String> sentenceList=new ArrayList<String>();
		    						Pattern re = Pattern.compile("[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)", Pattern.MULTILINE | Pattern.COMMENTS);
		    					    Matcher reMatcher = re.matcher(abs);
		    					    while (reMatcher.find()) {
		    					        sentenceList.add(reMatcher.group());
		    					    }
		    					    if(!sentenceList.isEmpty()){
		    					    
		    					    	if(sentenceList.size()>1)
		    			 			mainValue=(String)sentenceList.get(0)+(String)sentenceList.get(1);
		    					    	else
		    					    		mainValue=(String)sentenceList.get(0);
		    					    }

		    					   }
		    				}
		    			     
		    			    /*getting from uri*/
				    		if(mainKey.equals("location") ||
				    		   mainKey.equals("products") || 
			    			   mainKey.equals("product")  ||  
			    			   mainKey.equals("netIncome")  ||
			    			   mainKey.equals("numberOfEmployees")  ||
				    		   mainKey.equals("locationCountry") ||
			    			   mainKey.equals("industry") ||				    		   
				    		   mainKey.equals("locationCity")){
				    			String tempValue=job2.get("value").toString();
				    			String[] splitKey2=(String[])tempValue.split("/");
   		    					if(mainValue==null){   		    						   		    					    
   			    			        mainValue=splitKey2[splitKey2.length-1].toString();   			    			           		    					
   		    					} else{   		    						   		    					    
   			    			        mainValue=mainValue+","+splitKey2[splitKey2.length-1].toString();
   		    					}   		    					
   		    					mainValue=mainValue.replaceAll("[_]", " ");
   		    					
				    		}
				    		
				    		else if (mainKey.equals("slogan") ||
				    				mainKey.equals("thumbnail") ||
				    				mainKey.equals("netIncome")  ||
				    			   	mainKey.equals("assets")  ||
				    			   	mainKey.equals("numberOfEmployees")  ||				    				
				    				mainKey.equals("revenue")) {
   		    						mainValue=job2.get("value").toString();
   		    				}   		    						    		
				    	}
				    	innerJsonObject.put(mainKey, mainValue);
				    }
			    }
	   		 }
		    }
		    
		    /*making mongoDB object*/
		   	innerJsonObject.put("name", company);
		   	innerJsonObject.put("type", "company");
		   	
		   	/*strictly filter the Asian Companies*/
		   	if (isAsianCompany(innerJsonObject)) {
		   		System.out.println("Asian company name : "+company);
			    if (asianCompanyFilter(innerJsonObject)) {
			    	//
					continue;
				}			   
			}
		   	
		    
		   	/*Acctual insert into mongoDB companies collection*/
		    collection.insert(new BasicDBObject(innerJsonObject));
		}
	    
		return true;
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Filter to Asian company. (Reason: some big Asian companies are not interestingto the normal users because they are dealing with B2B- business to business)
	 *
	 * @param companyJson the json object that hold a company
	 * @return true, if successful
	 */
	private static boolean asianCompanyFilter(JSONObject companyJson) {
		int count = 0;
		
		String revenue;
		if ((revenue = (String) companyJson.get("revenue")) != null) {
			int rev = Character.getNumericValue(revenue.charAt(0));		
			if (rev >= 4) { 
				count++;//return true; 
				}
		}
		
		String netIncome;
		if ((netIncome = (String) companyJson.get("netIncome")) != null) {
			if (netIncome.contains("$") && String.valueOf(netIncome.charAt(0)).equals("$")) {
				netIncome = netIncome.substring(1);
			}
			int ni = Character.getNumericValue(netIncome.charAt(0));						
			if (ni >= 4) { 
				count++;//return true; 
				}
		}
		
		String assets;
		if ((assets = (String) companyJson.get("assets")) != null) {
			if (assets.contains("$") && String.valueOf(assets.charAt(0)).equals("$")) {
				assets = assets.substring(1);
			}
			int a = Character.getNumericValue(assets.charAt(0));
						
			if (a >= 5) { 
				count ++;;//return true; 
				}
		}
		
		String numberOfEmp;
		if ((numberOfEmp = (String) companyJson.get("numberOfEmployees")) != null) {
			int numberOfEmployees = Integer.parseInt((String) companyJson.get("numberOfEmployees").toString());
			if (numberOfEmployees >= 150000) {
				count += 2;
			}else  if (numberOfEmployees >= 60000) {
				count++;
			}
		}
		
		if (count >= 2) { return false;	} else { return true; }
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Checks if is an Asian company.
	 *
	 * @param companyJson the json object that hold the company detail
	 * @return true, if is an Asian company
	 */
	private static boolean isAsianCompany(JSONObject companyJson) {
		List<String> locationList = new ArrayList<String>();
		String spliter = ",";
		List<String> tempList;
		
		/*check with Asian countries*/
		String locationCountry;
		if ((locationCountry = (String) companyJson.get("locationCountry")) != null) {
			locationList.addAll(splitString(spliter, locationCountry));
			return isAsianCountries(locationList);
		}				
		
		/*Check with Asian cities*/
		String location;
		if ((location = (String) companyJson.get("location")) != null) {
			tempList = new ArrayList<String>(splitString(spliter, location));
			locationList.addAll(tempList);
		}		
		/*Check with Asian cities*/		
		String locationCity;
		if ((locationCity = (String) companyJson.get("locationCity")) != null) {
			tempList = new ArrayList<String>(splitString(spliter, locationCity));
			locationList.addAll(tempList);
		}	
				
		return IsAsianCity(locationList);
	}
	
	
	
	
	private static boolean IsAsianCity(List<String> locationList) {
		for(String location : locationList){
			String url = "http://dbpedia.org/data/"+location+".json";
			
			String cityJsonString;			
			if ((cityJsonString = CompanyInMongo.readUrl(url)) == null) {
				continue;
			}
			for(String country : listOfAsianCountries){
				String countryString = "http://dbpedia.org/ontology/country\" : [ { \"type\" : \"uri\", \"value\" : \"http://dbpedia.org/resource/"+country;				
				if (cityJsonString.contains(countryString)) {
					//System.out.println("Location City Returning "+location+" Loaction-list is : "+locationList.toString() +" country : "+country);
					return true;
				}
			}
		}
		return false;
	}
	
	
	
	
	private static boolean isAsianCountries(List<String> locationList) {
		//System.out.println("isAsianCountries Dis-joint"+locationList.toString());
		return !Collections.disjoint(listOfAsianCountries, locationList);
	}
	
	
	
	
	
	
	
	
	
	private static List<String> splitString(String spliter, String location) {
		List<String> locationList = Arrays.asList(location.split(spliter));
		return locationList;
	}
	
	
	
	
	
	
	private static void getAsianCountries() throws ParseException {
		String url = "http://dbpedia.org/data/Category:East_Asian_countries.json";
		String asianCountriesJosnString = CompanyInMongo.readUrl(url);
		
		JSONParser parser = new JSONParser();
	   	Object obj = parser.parse(asianCountriesJosnString);
	    JSONObject jsonObject = (JSONObject) obj;
	    
	    /*getting all available predicates from the company object. Which is got from DBPedia*/
	    Iterator<?> iterator = jsonObject.keySet().iterator();
	    /*iterate over the predicates*/
	    while(iterator.hasNext()){
	    	String key = (String) iterator.next();
	    	    	 			   			  
	    	Object ob= jsonObject.get(key);
		   	JSONObject job1 = (JSONObject) ob;
    		String[] splitKey1 = (String[])key.split("/");
		    String asianCountry = splitKey1[splitKey1.length-1].toString();
		    if (!asianCountry.contains("Category:") && !asianCountry.equals(null)) {
			    listOfAsianCountries.add(asianCountry);
	   		    //System.out.println("Json Key "+asianCountry);				
			}
 		 
	    }
	    listOfAsianCountries.add("India");
	}
}
