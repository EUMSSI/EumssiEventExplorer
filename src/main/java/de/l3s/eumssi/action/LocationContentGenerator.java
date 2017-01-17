package de.l3s.eumssi.action;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import de.l3s.eumssi.dao.MongoDBManager;

public class LocationContentGenerator extends ContentGenerator {
	//get the collection of all locations.
	public MongoDBManager mongo=MongoDBManager.getInstance();
    public DBCollection locationCollection=mongo.getCollection("allLocations_dbpedia");
	Map<String, String> locationMapQuestion = new HashMap<String, String>();
	Map<String, String> locationMapInfo = new HashMap<String, String>();
	
	ArrayList<String> questionableKeyList = new ArrayList<String>();
	ArrayList<String> infoableKeyList = new ArrayList<String>();
	//template for questions of locations



	public BasicDBObject locationObject=new BasicDBObject();
	  
  public LocationContentGenerator(BasicDBObject locationObjectConstructor){
		 locationObject= locationObjectConstructor;
	//	 mongo=mongoClient;
	//	 locationCollection = mongo.getCollection("allLocations");
	
		
		 locationMapQuestion.put("currency", "What is the name of the currency?");
		
		 locationMapQuestion.put("officialLanguage", "What is the official language spoken here?");
	//	 locationMapQuestion.put("languages", "Which language/languages are spoken?");
		
		 locationMapQuestion.put("neighbours", "Which countries are the neighbours?");
		
		 locationMapQuestion.put("timezone", "In which timezone is this city located?");
		
		 locationMapQuestion.put("capital", "What is the name of the capital?");
		
		 locationMapQuestion.put("country", "In which country is this city located?");
		 locationMapQuestion.put("adminArea", "In which region is this city located?");
		 locationMapQuestion.put("population", "Which is more populated?");
		 
		 locationMapQuestion.put("largestCity", "Which one is the largest city in this country?");
		 locationMapQuestion.put("drivesOn", "Which is the side for vehicle in this country?");
		 
		//template for info of locations
		locationMapInfo.put("currency", "The local currency is ");
		locationMapInfo.put("officialLanguage", "The language spoken is ");
		locationMapInfo.put("capital", "The capital is ");
		locationMapInfo.put("country", "This city is located in ");
		locationMapInfo.put("languages", "language spoken ");
		
}
	

	@Override
	public ArrayList<String> makeDicision() {
		
		
		boolean hasAbstract=false;
		
	

		for (Iterator iteratorForKeyIntersection = locationObject.keySet()
				.iterator(); iteratorForKeyIntersection.hasNext();) {
			String keyForIntersection = (String) iteratorForKeyIntersection.next();
			
			if (locationMapQuestion.containsKey(keyForIntersection)) {
				questionableKeyList.add(keyForIntersection);
			}
			
			
			if (locationMapInfo.containsKey(keyForIntersection)) {
				infoableKeyList.add(keyForIntersection);
			}
		
			if(keyForIntersection.equals("abstract"))
				hasAbstract=true;
			
		}
	
		ArrayList<String> dicisionList=new ArrayList<String>();
		//options for the dicision
		
	
		if(questionableKeyList.size()>0 && locationObject.containsKey("longitude"))
			dicisionList.add("question");
		
	
		if(infoableKeyList.size()>0)
			dicisionList.add("info");
		if(hasAbstract==true)
           dicisionList.add("abstract");
          
		dicisionList.add("map");
	//	dicisionList.add("wordGraph");
		
		// take dicision randomly
		
		
		return dicisionList;
	}

	
	@Override
	public JSONArray questionGenerator() {
		JSONArray questions=new JSONArray();
		String correctAns = null;
		String correctOrderAns;
		String type=(String) locationObject.get("type");
		JSONArray options;
		String question;
		String mainKeyValue=null;
	    
		for(int i=0;i<questionableKeyList.size();i++){
			JSONObject questionObj=new JSONObject();
		//if mainkey is population, then value is long 
			System.out.println("questionable key list: "+questionableKeyList);
		if(!questionableKeyList.get(i).equals("population")){
		 mainKeyValue = (String) locationObject.get(questionableKeyList.get(i));
		}
		if(questionableKeyList.get(i).equals("population") && type.equals("city")){
			String country =locationObject.getString("country");
			String cityName=(String) locationObject.get("name");
			Long population=Long.valueOf(locationObject.getString("population"));
			JSONArray comparedOption=CityCompareByPopulation(cityName, country, population);
			question=locationMapQuestion.get(questionableKeyList.get(i));
			questionObj.put("question",locationMapQuestion.get(questionableKeyList.get(i)));
			questionObj.put("options",comparedOption);
			questionObj.put("correct",comparedOption.get(2));
		    //questions array will go inside content array
			questions.add(questionObj);
		
		}
		else if(questionableKeyList.get(i).equals("drivesOn") && type.equals("country")){
			String drivesOn=(String) locationObject.get("drivesOn");
			JSONArray drivesOnOption=new JSONArray();
			drivesOnOption.add("Left");
			drivesOnOption.add("Right");
			Collections.shuffle(drivesOnOption);
			questionObj.put("question",locationMapQuestion.get(questionableKeyList.get(i)));
			questionObj.put("options",drivesOnOption);
			questionObj.put("correct",drivesOn);
			
			questions.add(questionObj);
			
		}
		else if(questionableKeyList.get(i).equals("largestCity") && type.equals("country")){
			String largestCity=(String) locationObject.get("largestCity");
			if(largestCity.equals("capital")){
				largestCity=(String) locationObject.get("capital");
			}
			JSONArray largestCityOption=new JSONArray();
			String countryName=(String) locationObject.get("name");
			Long population=(Long) locationObject.get("population");
			largestCityOption=LargestCityFalseAns(countryName,largestCity,population);
			question=locationMapQuestion.get(questionableKeyList.get(i));
			questionObj.put("question",question);
			questionObj.put("options",largestCityOption);
			questionObj.put("correct",largestCity);
			questions.add(questionObj);
			
		}
		
		else if(questionableKeyList.get(i).equals("population") && type.equals("country"))
			continue;
		
		
        //for locations
		else{
		correctAns = mainKeyValue;
		double longitude = Double.parseDouble((String) locationObject.get("longitude"));
		double latitude = Double.parseDouble((String) locationObject.get("latitude"));
		options = GetLocationFalseAns((String)questionableKeyList.get(i), correctAns.split("[,]"), longitude, latitude);
        
		// in the options the order of answer may change, which is
		// important for the logic of quize. The
		// CorrectAnsOrderFinder() will retrieve and return ordered
		// correct ans.
		
		correctOrderAns = CorrectAnsOrderFinder(correctAns.split("[,]"), options);
        questionObj.put("question",locationMapQuestion.get(questionableKeyList.get(i)));
        questionObj.put("options",options);
        questionObj.put("correct",correctOrderAns);
        questions.add(questionObj);
		
		

		
		}
	  } 
		return questions;
	}

	@Override
	public JSONArray infoGenerator() {
		JSONArray infos=new JSONArray();
		for(int i=0;i<infoableKeyList.size();i++){
		String info = (String) locationObject.get(infoableKeyList.get(i));
        info = locationMapInfo.get(infoableKeyList.get(i)) + info;
        infos.add(info);
		}
		return infos;
	}
	
	public String mapGenerator(String locationName){
		String mapIframe="<iframe width='600' height='300' frameborder='0' style='border:0'"
				+ " src='https://www.google.com/maps/embed/v1/place?q="+locationName+"&key=AIzaSyAJCit6uJoDOEdy-PPb0L5_3SydfJbJ3Vg'"
				+ " allowfullscreen></iframe>";
		return mapIframe;
	}
	
	   
	   public String abstractGenerator(){
		   if ((String) locationObject.get("abstract") == null)
				return null;
		   else{
		   String abs =(String) locationObject.get("abstract");
		 //  System.out.println((String) locationObject.get("abstract"));
			abs = abs.replaceAll("\\(.+?\\)\\s*", "");
			Charset.forName("iso-8859-1").encode(abs);
			System.out.println(abs);
		    return abs;
		   }
		   
		  
	   }
	   public String wordGraphGenerator(String entityName){
		   entityName=entityName.replaceAll("_", " ");
		   String entityNameForId=entityName.replace(" ", "-");
		   String wordgraph= "<div id='my-genericgraph-"+entityNameForId+"' class='genericgraph'><script type='text/javascript'>"
		   +"EUMSSI.Manager.addWidget(new AjaxSolr.GenericGraphWidget({id: 'my-genericgraph-"+entityNameForId+"\',target: '#my-genericgraph-"+entityNameForId+"'})); EUMSSI.Manager.doRequest(\'"+entityName+"\'); </script></div>" ;
		   return wordgraph;
	   }
	
	private JSONArray GetLocationFalseAns(String keyName, String[] keyValue, double longi, double lat) {
		ArrayList optionList = new ArrayList();
		JSONArray options=new JSONArray();
		DBCollection collectionName = null;
		int falseAnsNum = (4 - keyValue.length);
        String where;
        String projection;
        for(int i=0;i<keyValue.length;i++){
        if (keyValue[i].contains("language")) {
			// tempValue = tempValue.replace("language", "");
			String[] splitTempValue = keyValue[i].split("\\s+");
			keyValue[i]= splitTempValue[0];
		}
	}
        
		if(keyName.equals("neighbours")){
			where="type";
			projection="name";
		}
		else{
			where=keyName;
			projection=keyName;
		}
/*
		for (String keyvalue : keyValue)
			optionList.add(keyvalue);

		if (optionList.size() >= 4)
			optionList =  new ArrayList(optionList.subList(0, 3));
	*/
		optionList.add(keyValue[0]);
		
		ArrayList distances = new ArrayList();
		Map<Double, String> distanceToKeyValue = new HashMap<Double, String>();
		BasicDBObject whereQuery = new BasicDBObject();
        
		if(!keyName.equals("neighbours")){
		whereQuery.put(where, java.util.regex.Pattern.compile("."));
		}
		else{
			whereQuery.put(where, "country");
		}
		BasicDBObject projectionQuery = new BasicDBObject();
		projectionQuery.put("_id", 0);
		projectionQuery.put(projection, 1);
		projectionQuery.put("longitude", 1);
		projectionQuery.put("latitude", 1);
		DBCursor locationCursor = locationCollection.find(whereQuery, projectionQuery);

		while (locationCursor.hasNext()) {
			BasicDBObject entity = (BasicDBObject) locationCursor.next();
			if (entity.containsField("latitude") && entity.containsField("longitude")) {
				double falseLat = Double.parseDouble((String) entity.get("latitude"));
				double falseLong = Double.parseDouble((String) entity.get("longitude"));
				double distance = getDistance(falseLat, falseLong, lat, longi);
				distances.add(distance);
				String tempValue = (String) entity.get(projection);
				if (tempValue.contains("language")) {
					// tempValue = tempValue.replace("language", "");
					String[] splitTempValue = tempValue.split("\\s+");
					distanceToKeyValue.put(distance, splitTempValue[0]);
				} else
					distanceToKeyValue.put(distance, tempValue);
			} else
				continue;
		}
		Collections.sort(distances);

		// optionList.add(Arrays.asList(keyValue));
		ArrayList checkList;
		if (keyName.equals("neighbours")) {
			//get all the neihbours for checking
			checkList = new ArrayList(Arrays.asList(keyValue));
			distances.remove(0);

		} else
			checkList = optionList;

		for (int i = 0; i < distances.size(); i++) {

			if (!checkList.contains((String) distanceToKeyValue.get(distances.get(i)))) {
				String option=distanceToKeyValue.get(distances.get(i));
				if(optionList.contains(option.substring(0, 1).toUpperCase() + option.substring(1)) || checkList.contains(option.substring(0, 1).toUpperCase() + option.substring(1)))
						continue;
				if(optionList.contains(option.toLowerCase()) || checkList.contains(option.toLowerCase()))
					continue;
				if(optionList.contains(option.replaceAll("_", " ")) || checkList.contains(option.replaceAll("_", " ")))
					continue;
				optionList.add(distanceToKeyValue.get(distances.get(i)));
				if (optionList.size() == 4) {
					break;
				}
			}
		}
		Collections.shuffle(optionList);
		for(int i=0;i<optionList.size();i++){
			options.add(optionList.get(i));
		}
		return options;

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


   private JSONArray LargestCityFalseAns(String countryName,String largestCity,Long population){
	   
	   
	   
	   JSONArray options=new JSONArray();
	   BasicDBObject whereQuery = new BasicDBObject();
       whereQuery.put("country", countryName);
       BasicDBObject capitalWhereQuery = new BasicDBObject();
       capitalWhereQuery.put("$exists", 0);
       BasicDBObject nameWhereQuery = new BasicDBObject();
       nameWhereQuery.put("$exists", 1);
       nameWhereQuery.put("$ne", largestCity);
       whereQuery.put("capital", capitalWhereQuery);
       whereQuery.put("name", nameWhereQuery);
	   BasicDBObject projectionQuery = new BasicDBObject();
		projectionQuery.put("_id", 0);
		projectionQuery.put("name", 1);
		BasicDBObject sortQuery = new BasicDBObject();
		sortQuery.put("population", -1);
		DBCursor largestCityCursor = locationCollection.find(whereQuery, projectionQuery).sort(sortQuery).limit(3);

		while(largestCityCursor.hasNext()){
			BasicDBObject cityObject=(BasicDBObject) largestCityCursor.next();
			String name=cityObject.getString("name");
		   options.add(name);
		}
	   options.add(largestCity);
	   Collections.shuffle(options);
	   return options;
   }
	
	
	
	private JSONArray CityCompareByPopulation(String cityName, String countryName, Long population){
		JSONArray returnList=new JSONArray();
		int numberOfDigitOfCity=String.valueOf(population).length();
		BasicDBObject whereQuery = new BasicDBObject();

		whereQuery.put("country", countryName);
		BasicDBObject projectionQuery = new BasicDBObject();
		projectionQuery.put("_id", 0);
		projectionQuery.put("name", 1);
		projectionQuery.put("population", 1);
		DBCursor cityCursor = locationCollection.find(whereQuery, projectionQuery);
	//	mainloop:
	//	for(int i=0;i<numberOfDigitOfCity;i++){
		while(cityCursor.hasNext()){
			BasicDBObject cityObject=(BasicDBObject) cityCursor.next();
			String name=cityObject.getString("name");
			Long ComparingPopulation=Long.valueOf(cityObject.getString("population"));
			int numberOfDigitOfComparingCity=String.valueOf(ComparingPopulation).length();
			if(numberOfDigitOfCity==numberOfDigitOfComparingCity && !name.equals(cityName)){
			  	returnList.add(name);
			    returnList.add(cityName);
			    Collections.shuffle(returnList);
			    if(ComparingPopulation>population)
			    	returnList.add(name);
			    else if(ComparingPopulation<population)
			    	returnList.add(cityName);
			    break ;
			}
			
		}
	//	}
		
		return returnList;
	}

	}



