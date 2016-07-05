package de.l3s.eumssi.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import de.l3s.eumssi.dao.MongoDBManager;

public class LocationContentGenerator extends ContentGenerator {
	//get the collection of all locations.
	public MongoDBManager mongo=MongoDBManager.getInstance();
    public DBCollection locationCollection=mongo.getCollection("allLocations");
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
		 locationMapQuestion.put("population", "Which is the most populated city in this country?");
		 
		//template for info of locations
		locationMapInfo.put("currency", "The local currency is ");
		locationMapInfo.put("officialLanguage", "The language spoken is ");
		locationMapInfo.put("capital", "The capital is ");
		locationMapInfo.put("country", "This city is located in ");
		locationMapInfo.put("languages", "language spoken ");
		
}
	

	@Override
	public String makeDicision(String infoOrQues) {
		
		String dicision;
		boolean hasAbstract=false;
		
	

		for (Iterator iteratorForKeyIntersection = locationObject.keySet()
				.iterator(); iteratorForKeyIntersection.hasNext();) {
			String keyForIntersection = (String) iteratorForKeyIntersection.next();
			if(infoOrQues.equals("question") || infoOrQues.equals("both")){
			if (locationMapQuestion.containsKey(keyForIntersection)) {
				questionableKeyList.add(keyForIntersection);
			}
			}
			if(infoOrQues.equals("info") || infoOrQues.equals("both")){
			if (locationMapInfo.containsKey(keyForIntersection)) {
				infoableKeyList.add(keyForIntersection);
			}
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
		dicisionList.add("wordGraph");
		
		// take dicision randomly
		if(dicisionList.size()==0)
		   return null;
		Random ran = new Random();
	
		int x = ran.nextInt(dicisionList.size());
		dicision=dicisionList.get(x);
		
		return dicision;
	}

	
	@Override
	public String questionGenerator() {
		
		String correctAns = null;
		String correctOrderAns;
		String type=(String) locationObject.get("type");
		ArrayList options;
		String question ;
		Random ran = new Random();
		int questionSelectorNumber = ran.nextInt(questionableKeyList.size());
		String mainKeyForQuestion = questionableKeyList.get(questionSelectorNumber);
		String mainKeyValue = (String) locationObject.get(mainKeyForQuestion);
		if(mainKeyForQuestion.equals("population") && type.equals("city")){
			String country =locationObject.getString("country");
			String cityName=(String) locationObject.get("name");
			Long population=Long.valueOf(locationObject.getString("population"));
			ArrayList comparedOption=CityCompareByPopulation(cityName, country, population);
			 question = "<div><img src=Images" + "//" + "quiz.png><strong>" + cityName + "</strong><br>"
						+ locationMapQuestion.get(mainKeyForQuestion) + "<br><input type='radio' name=\'"
						+ comparedOption.get(2) + "\' value=\'" + comparedOption.get(0) + "\'>" + comparedOption.get(0)
						+ "<br><input type='radio' name=\'" + comparedOption.get(2) + "\' value=\'" + comparedOption.get(1) + "\'>"
						+ comparedOption.get(1) 
						+ "<br><input type='button' class='btn btn-primary' value='check'></div>";
		
		return question;
		}
		else if(mainKeyForQuestion.equals("population") && type.equals("country"))
			return null;
		
		
        //for locations
		else{
		correctAns = mainKeyValue;
		double longitude = Double.parseDouble((String) locationObject.get("longitude"));
		double latitude = Double.parseDouble((String) locationObject.get("latitude"));
		options = GetLocationFalseAns(mainKeyForQuestion, correctAns.split("[,]"), longitude, latitude);
        
		// in the options the order of answer may change, which is
		// important for the logic of quize. The
		// CorrectAnsOrderFinder() will retrieve and return ordered
		// correct ans.
		
		correctOrderAns = CorrectAnsOrderFinder(correctAns.split("[,]"), options);
        
		
		
		 question = "<div><img src=Images" + "//" + "quiz.png><strong>" + (String)locationObject.get("name")
				+ "</strong><br>" + locationMapQuestion.get(mainKeyForQuestion)
				+ "<input type='hidden' value=\'" + correctOrderAns + "\'>"
				+ "<br><input type='checkbox' name=\'" + correctOrderAns + "\' id=\'" + options.get(0)
				+ "\'>" + options.get(0) + "<br><input type='checkbox' name=\'" + correctOrderAns
				+ "\' id=\'" + options.get(1) + "\'>" + options.get(1)
				+ "<br><input type='checkbox' name=\'" + correctOrderAns + "\' id=\'" + options.get(2)
				+ "\'>" + options.get(2) + "<br><input type='checkbox' name=\'" + correctOrderAns
				+ "\' id=\'" + options.get(3) + "\'>" + options.get(3)
				+ "<br><input type='button' class='btn btn-primary' id='check'  value='check'></div>";

		return question;
		}
	}

	@Override
	public String infoGenerator() {
		Random ran = new Random();
		int infoSelectorNumber = ran.nextInt(infoableKeyList.size());
		String mainKeyForInfo = infoableKeyList.get(infoSelectorNumber);
		String mainKeyValue = (String) locationObject.get(mainKeyForInfo);
		String info;
        info = "<img src=Images" + "/" + "Info.png><strong>" + locationObject.getString("name") + "</strong>" + "<br>"
				+ locationMapInfo.get(mainKeyForInfo) + mainKeyValue;
		
		return info;
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
		   String abs = "<strong>" + locationObject.getString("name") + "</strong>" + "<br>" + (String) locationObject.get("abstract");
			abs = abs.replaceAll("\\(.+?\\)\\s*", "");
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
	
	private ArrayList GetLocationFalseAns(String keyName, String[] keyValue, double longi, double lat) {
		ArrayList optionList = new ArrayList();
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
			where="capital";
			projection="name";
		}
		else{
			where=keyName;
			projection=keyName;
		}
		for (String keyvalue : keyValue)
			optionList.add(keyvalue);

		if (optionList.size() >= 4)
			optionList = new ArrayList(optionList.subList(0, 3));
	
		ArrayList distances = new ArrayList();
		Map<Double, String> distanceToKeyValue = new HashMap<Double, String>();
		BasicDBObject whereQuery = new BasicDBObject();

		whereQuery.put(where, java.util.regex.Pattern.compile("."));
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
			checkList = new ArrayList(Arrays.asList(keyValue));
			distances.remove(0);

		} else
			checkList = optionList;

		for (int i = 0; i < distances.size(); i++) {

			if (!checkList.contains((String) distanceToKeyValue.get(distances.get(i)))) {
				optionList.add(distanceToKeyValue.get(distances.get(i)));
				if (optionList.size() == 4) {
					break;
				}
			}
		}
		Collections.shuffle(optionList);
		return optionList;

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


	
	private ArrayList CityCompareByPopulation(String cityName, String countryName, Long population){
		ArrayList returnList=new ArrayList();
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



