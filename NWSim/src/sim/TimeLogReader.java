/**
 * 
 */
package sim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import sim.dto.TimeLogCSV;
import sim.dto.TimeLogType;
import sim.dto.TimelogDTO;
import sim.utils.Constants;

/**
 * @author Sadiya
 *
 */
public class TimeLogReader {
	private List<TimelogDTO> waitingList;
	private List<TimelogDTO> completionList;

	/**
	 * 
	 */
	public TimeLogReader() {
		// TODO Auto-generated constructor stub
		

	}

	/**
	 * @param args
	 */
	public void exportLog() {
		readLogFile("Timelog.log");
	  exportToCSV(processList(),
			 Constants.File.FILE_PATH+Constants.File.FILE_NAME+Constants.File.CSV);
	 
	}
		
	/**
	 * 1. Read a log file
	 * 2. Convert Stream to pojo
	 * 3. Pojo to csv string 
	 * 5. Finally write to csv file
	 * 	 a) check file content exists then append 
	 *   b) else create anew file
	 */
	private List<TimeLogCSV> processList(){
		System.out.println("============================== Process List ====================================");
		
	
		  List<TimeLogCSV> timeLogCSVs = new ArrayList<>();
		  for(int i = 0 ; i< completionList.size(); i++){
			  timeLogCSVs.add(new TimeLogCSV(waitingList.get(i).getValue(), completionList.get(i).getValue(), waitingList.get(i).getAlgo()));
		  }
		return timeLogCSVs;
	}
	
	private void exportToCSV(List<TimeLogCSV> csvs, String filePath){
		 StringJoiner stringJoiner = new StringJoiner(",\n");
		 File file = new File(filePath);
		  if(!file.exists()){
			  stringJoiner.add(TimeLogCSV.getHeaderCSV());
		  }
		  
		  Path path = Paths.get(filePath.replace("\\", "/"));
		  csvs.forEach(timeLog -> stringJoiner.add(timeLog.toCsvString()));
		  try {
			Files.write(path, (stringJoiner.toString()+System.lineSeparator()).getBytes(StandardCharsets.UTF_8), file.exists()?StandardOpenOption.APPEND : StandardOpenOption.CREATE_NEW);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  //Append mode
		  System.out.println("================================== Export Complete ======================================");
	}
	
	private void readLogFile(String fileName){
		waitingList = new ArrayList<>();
		completionList = new ArrayList<>();
		try( BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)))){
			String strLine;
			while((strLine = bufferedReader.readLine())!= null){
				
				if(strLine.contains(Constants.AVERAGE_WAITING_TIME)){
					waitingList.add(new TimelogDTO(getValue(strLine), getAlgoType(strLine), TimeLogType.WAITING_TIME));
				} else if(strLine.contains(Constants.COMPLETION_TIME)){
					completionList.add(new TimelogDTO(getValue(strLine), getAlgoType(strLine), TimeLogType.COMPLETION_TIME));
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private String getAlgoType(String datalog){
		if(datalog.contains(Constants.Algo.FIFO)){
			return Constants.Algo.FIFO;
		}else if(datalog.contains(Constants.Algo.SJF)){
			return Constants.Algo.SJF;
		}else if(datalog.contains(Constants.Algo.PFS)){
			return Constants.Algo.PFS;
		} 
		return null;
	}
	
	private String getValue(String datalog){
		String [] values = datalog.split(Constants.Algo.SEPARATOR);
		return values.length > 1 ? values[1] : "0";
	}

}
