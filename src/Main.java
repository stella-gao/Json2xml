import org.apache.commons.io.FilenameUtils;
import org.json.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


public class Main {

	private static String START_DIRECTORY;

	public static void main(String[] args) throws IOException {

		START_DIRECTORY = "C:\\Users\\xigao\\Desktop\\test\\";

		long startTime = System.currentTimeMillis();

		try {

			System.out.println("starting walk of file tree...");

			Files.walk(Paths.get(START_DIRECTORY)).forEach(filePath -> {
				if (Files.isRegularFile(filePath)) {
					System.out.println(filePath);
					Path p = Paths.get(String.valueOf(filePath));
					//String sourceFileName = p.getFileName().toString();


					//Path pathToFile = (Path)filePath;
					if(filePath.toString().substring(filePath.toString().length() - 5, filePath.toString().length()).equals(".json")){
						//so we have encountered a file that is purportedly .json, we should attempt to produce an xml file from each of the json objects that it contains
						try{
							//split the json file with several json article objects inside into several json objects. One for each "article"
							ArrayList<JSONObject> JSONObjects = separateJSONObjectsInFile(filePath);
							//for each of these JSONObjects, create an individual JSON file for it, and then create an XML file of that.
							for(JSONObject each: JSONObjects){
								//create the XML file
								createXMLFileFromJSONObject(each, filePath);
							}
						}catch(IOException e){
							System.out.println("attempted to convert the following file to XML and incurred an io exception: " + filePath);
							e.printStackTrace();
						}
					}

				}
			});
		} catch (IOException e) {
			System.out.println("IO EXCEPTION INCURRED DURING THE VISITOR'S WALK");
			e.printStackTrace();
		}finally{
			System.out.println("finished walking file tree after approx " + ((System.currentTimeMillis() - startTime)/1000)  + " seconds : )" );
		}
	}



	/**
	 * takes a json file that has several json objects defined within and returns a list of those json objects
	 * @param pathToFile the path to the file that is to be separated into its constituent json objects
	 * @return ArrayList<JSONObject> a list of all of the JSONObjects that are defined in the supplied file
	 */
	private static ArrayList<JSONObject> separateJSONObjectsInFile(Path pathToFile) throws IOException {


		ArrayList<String> listOfJSONStrings = (ArrayList<String>) Files.readAllLines(pathToFile, Charset.availableCharsets().get("ISO-8859-1"));
		ArrayList<JSONObject> listOfJSONObjects = new ArrayList<>();
		for(String each: listOfJSONStrings){
			listOfJSONObjects.add(new JSONObject(each));
		}

		return listOfJSONObjects;
	}




	/**
	 * creates an XMLFile at the specified location
	 * @param JSONObj the JSONObject that we are "converting" into an xml file
	 * @param pathToOriginalFile the location that we are creating the file in
	 * @throws IOException
	 */
	private static void createXMLFileFromJSONObject(JSONObject JSONObj, Path pathToOriginalFile) throws IOException {


		String suffix = ".xml";
		String realFileName = pathToOriginalFile.getFileName().toString();
		String fileNameWithOutExt = FilenameUtils.removeExtension(realFileName);

		String outputPath = START_DIRECTORY + fileNameWithOutExt + suffix;
		File XMLFile = new File(outputPath);

		while(XMLFile.exists()){

			outputPath = START_DIRECTORY + fileNameWithOutExt + suffix;
			XMLFile = new File(outputPath);
		}


		java.io.FileWriter writer = new java.io.FileWriter(XMLFile);
		writer.write(XML.toString(JSONObj, "root"));
		//note: using "root" as the enclosing json tags. This seems conventional.
		writer.close();
	}



}
