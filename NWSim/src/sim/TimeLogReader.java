/**
 * 
 */
package sim;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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
	  exportToCSV(processList(readLogFile("Timelog.log")),
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
	private List<TimeLogCSV> processList(List<TimelogDTO> timelogDTOs){
		  Map<TimeLogType,List<TimelogDTO>> groupedObject = timelogDTOs.stream().collect(Collectors.groupingBy(TimelogDTO::getLogtype));
		  List<TimelogDTO> compeletionList = groupedObject.get(TimeLogType.COMPLETION_TIME);
		  List<TimelogDTO> waitingList = groupedObject.get(TimeLogType.WAITING_TIME);
		  List<TimeLogCSV> timeLogCSVs = new ArrayList<>();
		  for(int i = 0 ; i< compeletionList.size(); i++){
			  timeLogCSVs.add(new TimeLogCSV(waitingList.get(i).getValue(), compeletionList.get(i).getValue(), compeletionList.get(i).getAlgo()));
		  }
		return timeLogCSVs;
	}
	
	private void exportToCSV(List<TimeLogCSV> csvs, String filePath){
		 StringJoiner stringJoiner = new StringJoiner(",\n");
		 File file = new File(filePath);
		  if(!file.exists())
			  stringJoiner.add(TimeLogCSV.getHeaderCSV());
		  
		  Path path = Paths.get(filePath.replace("\\", "/"));
		  csvs.forEach(timeLog -> stringJoiner.add(timeLog.toCsvString()));
		  try {
			Files.write(path, stringJoiner.toString().getBytes(), file.exists()?StandardOpenOption.APPEND : StandardOpenOption.CREATE_NEW);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  //Append mode
	}
	
	private List<TimelogDTO> readLogFile(String fileName){
		List<TimelogDTO> timelogs = new ArrayList<>();
		try( BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)))){
			String strLine;
			while((strLine = bufferedReader.readLine())!= null){
				
				if(strLine.contains(Constants.AVERAGE_WAITING_TIME)){
					timelogs.add(new TimelogDTO(getValue(strLine), getAlgoType(strLine), TimeLogType.WAITING_TIME));
				} else if(strLine.contains(Constants.COMPLETION_TIME)){
					timelogs.add(new TimelogDTO(getValue(strLine), getAlgoType(strLine), TimeLogType.COMPLETION_TIME));
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return timelogs;
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
