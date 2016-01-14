/*
 *Composed by Mainul Quraishi
 * 
 */
package de.l3s.eumssi.action;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.opensymphony.xwork2.Action;
import de.l3s.eumssi.dao.MongoDBManager;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class textsyncAction implements Action, ServletRequestAware {

	MongoDBManager mongo = new MongoDBManager();
	//get the collection of all person
	DBCollection personCollection = mongo.getCollection("person");
	//get the collection of all locations. 
	DBCollection locationCollection = mongo.getCollection("allLocations");

	private String myparam;
	private String videoUrl;
	private String url;
	public String jsonFileName;
	public String entities[];
	public int hour = 00;
	public int min = 00;
	public int sec = 00;
	public String dataContent = null;
	public String content1 = "{\"localisation\": [{ \"sublocalisations\": { \"localisation\": [";
	public String content8 = "]},\"type\": \"text\",\"tcin\": \"00:00:00.0000\",\"tcout\": \"02:00:00.0000\",\"tclevel\": 0}],\"id\": \"text-amalia01\",\"type\": \"text\",\"algorithm\": \"demo-video-generator\",\"processor\": \"Ina Research Department - N. HERVE\", \"processed\": 1421141589288, \"version\": 1}";
	// Hold the List of all the details of all entities extracted from mongoDB
	public ArrayList<BasicDBObject> mainArrayList = new ArrayList<BasicDBObject>();
	public ArrayList<String> thumbNailsList = new ArrayList<String>();
	HttpServletRequest request;

	// function for making data(by inserting image and time) to write in json file for amalia player
	//input is data and the image that should be shown 
	private void makeData(String data, int dataCounter, String thumbNailUrl) {
		if(thumbNailUrl==null){
			thumbNailUrl="Images/blank_image.png";
		}
		String content2 = "{\"data\": {\"text\": [";
		String content3 = "]},";
		String content4 = "\"tcin\":";
		String content5 = ",\"tcout\":";

		String content6 = ",\"tclevel\": 1}";
		String content7 = ",";
		String dataContentDemo = null;
		String tcin;
		String tcout;

		if (sec == 60) {
			sec = 00;
			sec = sec + 10;
			min = min + 1;
		} else
			sec = sec + 10;
		if (min == 60) {
			sec = 00;
			min = 00;
			hour = hour + 1;
		}
		tcin = new DecimalFormat("00").format(hour) + ":" + new DecimalFormat("00").format(min) + ":"
				+ new DecimalFormat("00").format(sec) + "." + "0000";
		// tcin=hour+":"+min+":"+sec+"."+"0000";
		if (sec < 60)
			sec = sec + 10;
		else {
			sec = 00;
			min = min + 1;
		}
		tcout = new DecimalFormat("00").format(hour) + ":" + new DecimalFormat("00").format(min) + ":"
				+ new DecimalFormat("00").format(sec) + "." + "0000";
		// System.out.println(data);

		if (dataContent != null) {
			dataContentDemo = content7 + content2 + "\"" + data + "\"" + content3 + content4 + "\"" + tcin + "\""
					+ content5 + "\"" + tcout + "\"" + ",\"thumb\"" + ":" + "\"" + thumbNailUrl + "\"" + content6;
			dataContent = dataContent + dataContentDemo;
		} else {
			dataContentDemo = content2 + "\"" + data + "\"" + content3 + content4 + "\"" + tcin + "\"" + content5 + "\""
					+ tcout + "\"" + ",\"thumb\"" + ":" + "\"" + thumbNailUrl + "\"" + content6;
			dataContent = dataContentDemo;
		}

		System.out.println(dataContent);
	}
	
    /* function responsible for finding which question should be asked or which info should be shown according to defined templates. Inputs
    * is the main ArrayList which holds the all the details of the matched entities. Decisions(whether questions or info or abstract
    * and which question or info) are made randomly.    
    */
	
	private void jsonWriter(ArrayList<BasicDBObject> mainArrayList) throws IOException {
		String mainContent;
		Map<String, String> locationMapQuestion = new HashMap<String, String>();
		Map<String, String> personMapQuestion = new HashMap<String, String>();

		Map<String, String> locationMapInfo = new HashMap<String, String>();
		Map<String, String> personMapInfo = new HashMap<String, String>();
        
		//template for questions of locations
	//	locationMapQuestion.put("currency", "What is the name of the currency?");
	//	locationMapQuestion.put("officialLanguage", "What language is spoken here?");
	//	locationMapQuestion.put("languages", "Which language/languages are spoken?");
	//	locationMapQuestion.put("neighbours", "Which countries are the neighbours?");
	//	locationMapQuestion.put("timezone", "which is the correct timezone for this city?");
	//	locationMapQuestion.put("capital", "What is the name of the capital?");
	//	locationMapQuestion.put("country", "In what country is it located?");
	//	locationMapQuestion.put("adminArea", "Under which this city located?");
		locationMapQuestion.put("population", "Which is the most populated city?");
		//template for questions of persons
		personMapQuestion.put("birthPlace", "Where was this person born?");
		personMapQuestion.put("almaMater", "Which university or college did this person attend??");
		//template for info of locations
		locationMapInfo.put("currency", "The local currency is ");
		locationMapInfo.put("officialLanguage", "The language spoken is ");
		locationMapInfo.put("capital", "The capital is ");
		locationMapInfo.put("country", "This city is located in ");
		locationMapInfo.put("languages", "language spoken ");
		//template for info of locations
		personMapInfo.put("birthPlace", "City of birth: ");
		personMapInfo.put("almaMater", "College attended: ");
		personMapInfo.put("birthdate", "Date of birth: ");
		personMapInfo.put("spouse", "Spouse: ");

		int dataCounter = 0;
		for (int i = 0; i < mainArrayList.size(); i++) {

			BasicDBObject entity = mainArrayList.get(i);
			String dicision;
			String type = null;
			boolean hasAbstract=false;
			ArrayList<String> questionableKeyList = new ArrayList<String>();
			ArrayList<String> infoableKeyList = new ArrayList<String>();
			
			for (Iterator iteratorForKeyIntersection = mainArrayList.get(i).keySet()
					.iterator(); iteratorForKeyIntersection.hasNext();) {
				String keyForIntersection = (String) iteratorForKeyIntersection.next();
				if (locationMapQuestion.containsKey(keyForIntersection)) {
					questionableKeyList.add(keyForIntersection);
				}
				else if (personMapQuestion.containsKey(keyForIntersection)) {
					questionableKeyList.add(keyForIntersection);
				}
				if (locationMapInfo.containsKey(keyForIntersection)) {
					infoableKeyList.add(keyForIntersection);
				}
				else if (personMapInfo.containsKey(keyForIntersection)) {
					infoableKeyList.add(keyForIntersection);
				}
				
				if(keyForIntersection.equals("abstract"))
					hasAbstract=true;
				
			}
			ArrayList<String> dicisionList=new ArrayList<String>();
			//options for the dicision
			
			if(entity.get("type").equals("location") || entity.get("type").equals("city") || entity.get("type").equals("country")){
			if(questionableKeyList.size()>0 && entity.containsKey("longitude"))
				dicisionList.add("question");
			}
			else{
				if(questionableKeyList.size()>0)
					dicisionList.add("question");
			}
			if(infoableKeyList.size()>0)
				dicisionList.add("info");
			if(hasAbstract==true)
               dicisionList.add("abstract");			
			// take dicision randomly
			Random ran = new Random();
			int x = ran.nextInt(dicisionList.size());
			dicision=dicisionList.get(0);

			type=entity.getString("type");
			// questions and info about location
		
				String entityName = (String) entity.get("name");
				entityName = entityName.replaceAll("[_]", " ");
				if (dicision.equals("question")) {
					String correctAns = null;
					String correctOrderAns;
					ArrayList options;
					String question ;
					int questionSelectorNumber = ran.nextInt(questionableKeyList.size());
					String mainKeyForQuestion = questionableKeyList.get(questionSelectorNumber);
					String mainKeyValue = (String) entity.get(mainKeyForQuestion);
					if(mainKeyForQuestion.equals("population") && type.equals("city")){
						String country =entity.getString("country");
						String cityName=entityName;
						Long population=Long.valueOf(entity.getString("population"));
						ArrayList comparedOption=CityCompareByPopulation(cityName, country, population);
						 question = "<div><img src=Images" + "//" + "quiz.png><strong>" + entityName + "</strong><br>"
									+ locationMapQuestion.get(mainKeyForQuestion) + "<br><input type='radio' name=\'"
									+ comparedOption.get(2) + "\' value=\'" + comparedOption.get(0) + "\'>" + comparedOption.get(0)
									+ "<br><input type='radio' name=\'" + comparedOption.get(2) + "\' value=\'" + comparedOption.get(1) + "\'>"
									+ comparedOption.get(1) 
									+ "<br><input type='button'  value='check'></div>";
						 String thumbnail=(String) entity.get("thumbnail");
							makeData(question, i + 1, thumbnail);
							continue;
					}
					correctAns = mainKeyValue;
			        //for locations
					if(type.equals("city") || type.equals("country") || type.equals("location")){
					double longitude = Double.parseDouble((String) entity.get("longitude"));
					double latitude = Double.parseDouble((String) entity.get("latitude"));
					options = GetLocationFalseAns(mainKeyForQuestion, correctAns.split("[,]"), longitude, latitude);
                    
					// in the options the order of answer may change, which is
					// important for the logic of quize. The
					// CorrectAnsOrderFinder() will retrieve and return ordered
					// correct ans.
					
					correctOrderAns = CorrectAnsOrderFinder(correctAns.split("[,]"), options);
			        
					
					
					 question = "<div><img src=Images" + "//" + "quiz.png><strong>" + entityName
							+ "</strong><br>" + locationMapQuestion.get(mainKeyForQuestion)
							+ "<input type='hidden' value=\'" + correctOrderAns + "\'>"
							+ "<br><input type='checkbox' name=\'" + correctOrderAns + "\' id=\'" + options.get(0)
							+ "\'>" + options.get(0) + "<br><input type='checkbox' name=\'" + correctOrderAns
							+ "\' id=\'" + options.get(1) + "\'>" + options.get(1)
							+ "<br><input type='checkbox' name=\'" + correctOrderAns + "\' id=\'" + options.get(2)
							+ "\'>" + options.get(2) + "<br><input type='checkbox' name=\'" + correctOrderAns
							+ "\' id=\'" + options.get(3) + "\'>" + options.get(3)
							+ "<br><input type='button'  value='check'></div>";

					
					}else{
					 options = GetPersonFalseAns(mainKeyForQuestion, mainKeyValue);

					 question = "<div><img src=Images" + "//" + "quiz.png><strong>" + entityName + "</strong><br>"
							+ personMapQuestion.get(mainKeyForQuestion) + "<br><input type='radio' name=\'"
							+ mainKeyValue + "\' value=\'" + options.get(0) + "\'>" + options.get(0)
							+ "<br><input type='radio' name=\'" + mainKeyValue + "\' value=\'" + options.get(1) + "\'>"
							+ options.get(1) + "<br><input type='radio' name=\'" + mainKeyValue + "\' value=\'"
							+ options.get(2) + "\'>" + options.get(2) + "<br><input type='radio' name=\'" + mainKeyValue
							+ "\' value=\'" + options.get(3) + "\'>" + options.get(3)
							+ "<br><input type='button'  value='check'></div>";

					}
					String thumbnail=(String) entity.get("thumbnail");
					makeData(question, i + 1, thumbnail);
				}
				if (dicision.equals("info")) {
				 	int infoSelectorNumber = ran.nextInt(infoableKeyList.size());
					String mainKeyForInfo = infoableKeyList.get(infoSelectorNumber);
					String mainKeyValue = (String) entity.get(mainKeyForInfo);
					String info;
					if(type.equals("city")|| type.equals("country") || type.equals("location"))
				      info = "<img src=Images" + "/" + "Info.png><strong>" + entityName + "</strong>" + "<br>"
							+ locationMapInfo.get(mainKeyForInfo) + mainKeyValue;
					else
					  info = "<img src=Images" + "/" + "Info.png><strong>" + entityName + "</strong>" + "<br>"
								+ locationMapInfo.get(mainKeyForInfo) + mainKeyValue;
					String thumbnail=(String) entity.get("thumbnail");
					makeData(info, i + 1, thumbnail);

				}
				if (dicision.equals("abstract")) {
					if ((String) entity.get("abstract") == null)
						continue;
					String abs = "<strong>" + entityName + "</strong>" + "<br>" + (String) entity.get("abstract");
					abs = abs.replaceAll("\\(.+?\\)\\s*", "");
					System.out.println(abs);
					makeData(abs, i + 1, (String) entity.get("thumbnail"));
				}

			} /*
			else if (type.equals("other")) {
				String otherEntityName = (String) entity.get("name");
				otherEntityName = otherEntityName.replaceAll("[_]", " ");
				String entityAbstract = "<strong>" + otherEntityName + "</strong><br>"
						+ (String) entity.get("abstract");
				entityAbstract = entityAbstract.replaceAll("[\"]", "");
				entityAbstract = entityAbstract.replaceAll("\\(.+?\\)\\s*", "");
				makeData(entityAbstract, i + 1, (String) entity.get("thumbnail"));
			}
			*/

		

		mainContent = content1 + dataContent + content8;
		// File file = new
		// File("G:\\workspace\\eventsense\\WebContent\\scripts\\"+jsonFileName+".json");

		ServletContext context = request.getServletContext();
		String path = context.getRealPath("/");
		// System.out.println(path);

		File file = new File(path + File.separator + "scripts" + File.separator + jsonFileName + ".json");

		System.out.println("Local filename to write: " + file.getAbsolutePath());
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(mainContent);
		bw.close();

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

	private String CorrectAnsOrderFinder(String[] correctAns, ArrayList options) {
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
		for (String ans : correctAns) {
			if (options.indexOf(ans) < 0)
				continue;
			index.add(options.indexOf(ans));
			indexToAns.put(options.indexOf(ans), ans);
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
/*
	// find language name by language code from mongodb
	private String LanguageNameFinder(String languagecode) {
		String language;
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("$or", Arrays.asList(new BasicDBObject("iso-639-3", languagecode),
				new BasicDBObject("iso-639-2", languagecode), new BasicDBObject("iso-639-1", languagecode)));
		BasicDBObject projectionQuery = new BasicDBObject();
		projectionQuery.put("_id", 0);
		projectionQuery.put("language", 1);
		DBCursor languageCursor = languagecodesCollection.find(whereQuery, projectionQuery);
		if (languageCursor.hasNext()) {
			BasicDBObject languageObject = (BasicDBObject) languageCursor.next();
			language = languageObject.getString("language");
		} else
			language = "not found";
		return language;
	}

	private String CountryNameFinder(String countrycode) {
		String country;
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("$or",
				Arrays.asList(new BasicDBObject("ISO", countrycode), new BasicDBObject("ISO3", countrycode)));
		BasicDBObject projectionQuery = new BasicDBObject();
		projectionQuery.put("_id", 0);
		projectionQuery.put("country", 1);
		DBCursor countryCursor = countryInfoCollection.find(whereQuery, projectionQuery);
		if (countryCursor.hasNext()) {
			BasicDBObject countryObject = (BasicDBObject) countryCursor.next();
			country = countryObject.getString("country");
		} else
			country = "not found";
		return country;
	}
*/
	private ArrayList GetPersonFalseAns(String keyName, String keyValue) {
		ArrayList<String> options = new ArrayList<String>();
		BasicDBObject whereQuery = new BasicDBObject();

		whereQuery.put(keyName, java.util.regex.Pattern.compile("."));
		BasicDBObject projectionQuery = new BasicDBObject();
		projectionQuery.put("_id", 0);
		projectionQuery.put(keyName, 1);

		Random ran = new Random();
		int x = ran.nextInt(440) + 1;
		DBCursor randomPersonCursor = personCollection.find(whereQuery, projectionQuery).limit(3).skip(x);

		// System.out.println("location cursor size:"+locationCursor);
		while (randomPersonCursor.hasNext()) {
			BasicDBObject entity = (BasicDBObject) randomPersonCursor.next();
			String tempValue = (String) entity.get(keyName);
			String[] value = (String[]) tempValue.split("[,]");

			options.add(value[0]);
		}
		options.add(keyValue);
		Collections.shuffle(options);
		return options;
	}

	@SuppressWarnings("unchecked")
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
		System.out.println(distanceToKeyValue);
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
/*
	@SuppressWarnings("unchecked")
	private ArrayList timezoneFinder(String realTimeZone) {
		BasicDBObject whereQuery = new BasicDBObject();

		whereQuery.put("timezone", java.util.regex.Pattern.compile("."));
		BasicDBObject projectionQuery = new BasicDBObject();
		projectionQuery.put("_id", 0);
		projectionQuery.put("timezone", 1);

		Random ran = new Random();
		int x = ran.nextInt(412);
		DBCursor timezoneCursor = timezoneCollection.find(whereQuery, projectionQuery).limit(4).skip(x);
		ArrayList timezones = new ArrayList();
		ArrayList subTimeZones = null;
		while (timezoneCursor.hasNext()) {
			DBObject a = timezoneCursor.next();
			String b = (String) a.get("timezone");
			timezones.add(b);
		}
		if (timezones.contains(realTimeZone))
			return timezones;
		else {
			subTimeZones = new ArrayList(timezones.subList(0, 3));
			subTimeZones.add(realTimeZone);
			Collections.shuffle(subTimeZones);
			return subTimeZones;
		}
	}
*/

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

	public String execute() throws FileNotFoundException, IOException, ParseException {
		System.out.println(videoUrl);

		ArrayList<String> subSubArray;
		ArrayList<ArrayList<String>> subArray;
		entities = myparam.split(",");

		for (int entityCounter = 1; entityCounter <= entities.length; entityCounter++) {
			if (jsonFileName == null)
				jsonFileName = entities[entityCounter - 1];
			else
				jsonFileName = jsonFileName + entities[entityCounter - 1];
		}

		JSONParser parser = new JSONParser();
		try {
			for (int entityCounter = 1; entityCounter < entities.length; entityCounter++) {
				BasicDBObject whereQuery = new BasicDBObject();
				whereQuery.put("name", entities[entityCounter]);
				BasicDBObject projectionQuery = new BasicDBObject();
				projectionQuery.put("_id", 0);
				DBCursor locationCursor = locationCollection.find(whereQuery, projectionQuery);
				DBCursor personCursor = personCollection.find(whereQuery, projectionQuery);

				while (locationCursor.hasNext()) {

					BasicDBObject entity = (BasicDBObject) locationCursor.next();


					mainArrayList.add(entity);

				}
				while (personCursor.hasNext()) {

					BasicDBObject entity = (BasicDBObject) personCursor.next();

					mainArrayList.add(entity);
				}
				if (locationCursor.count() == 0 && personCursor.count() == 0) {
					BasicDBObject entityOther = abstractFinder((String) entities[entityCounter]);
					entityOther.put("type", "other");
					entityOther.put("name", (String) entities[entityCounter]);
					mainArrayList.add(entityOther);

				}

			}
			// reArrangeMainArrayList(mainArrayList);
			// System.out.println(mainArrayList);
			jsonWriter(mainArrayList);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "test";
	}

	// Find the abstract of entity which is not found in mongodb
	private static BasicDBObject abstractFinder(String entityName) throws Exception {
		String outputString = readUrl("http://dbpedia.org/data/" + entityName + ".json");
		Object ob;
		JSONObject job1;
		JSONObject job2;
		JSONArray jarray;
		BasicDBObject mainValue = new BasicDBObject();
		JSONParser parser = new JSONParser();
		Object obj = new Object();
		try {
			obj = parser.parse(outputString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject jsonObject = (JSONObject) obj;
		for (Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();

			ob = jsonObject.get(key);
			job1 = (JSONObject) ob;
			int indexOfmainArrayList = 0;
			for (Iterator iterator1 = job1.keySet().iterator(); iterator1.hasNext();) {
				String key1 = (String) iterator1.next();
				if (job1.get(key1) instanceof JSONArray) {

					jarray = (JSONArray) job1.get(key1);
					String mainKey = null;
					String demoMainValue;
					String[] splitKey1 = (String[]) key1.split("/");
					mainKey = splitKey1[splitKey1.length - 1].toString();
					if (mainKey.equals("abstract") || mainKey.equals("thumbnail")) {
						for (int i = 0; i < jarray.size(); i++) {
							job2 = (JSONObject) (jarray.get(i));
							if (mainKey.equals("abstract")) {
								if (job2.get("lang").equals("en")) {
									String abs = job2.get("value").toString();
									List sentenceList = new ArrayList();
									Pattern re = Pattern.compile(
											"[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)",
											Pattern.MULTILINE | Pattern.COMMENTS);
									Matcher reMatcher = re.matcher(abs);
									while (reMatcher.find()) {
										sentenceList.add(reMatcher.group());
									}
									if (!sentenceList.isEmpty()) {

										if (sentenceList.size() > 1)
											mainValue.put("abstract",
													(String) sentenceList.get(0) + (String) sentenceList.get(1));

										else
											mainValue.put("abstract", (String) sentenceList.get(0));
									}

								}
							} else if (mainKey.equals("thumbnail")) {

								mainValue.put("thumbnail", job2.get("value").toString());
							}
						}
					}

				}
			}
		}
		return mainValue;
	}

	private static String readUrl(String urlString) throws Exception {
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
		} finally {
			if (reader != null)
				reader.close();
		}
	}

	public String getMyparam() {
		return myparam;
	}

	public void setMyparam(String myparam) {
		this.myparam = myparam;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

}
