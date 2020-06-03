package sim;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFRow;

public class TimeCSV {

	TimeCSV csv;

	HSSFRow row ;

	public TimeCSV() {
		// TODO Auto-generated constructor stub
	}

	public void logtoCSV()
	{
		FileOutputStream fileOut  = null;
		try(
				BufferedReader br = new BufferedReader(new InputStreamReader(
						new DataInputStream(new FileInputStream("Timelog.log"))));){

			String strLine;

			ArrayList<String> Averagelist=new ArrayList<String>();

			ArrayList<String> Completionlist=new ArrayList<String>();

			//Extraction data from log file

			while ((strLine = br.readLine()) != null){
				if(strLine.contains("Average"))
				{
					Averagelist.add(strLine);
				}
				else if(strLine.contains("Completion")){
					Completionlist.add(strLine);
				}
			}		
			String AWT="", Algo="",comptime="";

			Iterator<String> itr;

			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("Excel Sheet");
			HSSFRow rowhead = sheet.createRow((short)0);
			rowhead.createCell((int) 0).setCellValue("Algorithm");
			rowhead.createCell((int) 1).setCellValue("AWT");
			rowhead.createCell((int)2).setCellValue("ACT");
			rowhead.createCell((int)3).setCellValue("Date");


			int index=1;

			//Extract from AverageList

			for (itr=Averagelist.iterator(); itr.hasNext(); ){
				String str=itr.next().toString();
				String [] splitSt =str.split(":");

				//System.out.println("length is "+splitSt.length+" value is "+str);

				AWT=splitSt[2];

				row = sheet.createRow((short)index);

				if(splitSt.length>1)
				{
					row.createCell((int)1).setCellValue(AWT);
				}

				index++;
			}		
			index=1;

			//Extract from CompletionList

			for (itr=Completionlist.iterator(); itr.hasNext(); ){
				String str=itr.next().toString();

				String [] splitspace=str.split(" ");

				//System.out.println("length is "+splitspace.length+" value is "+str);

				int algoindex= Arrays.asList(splitspace).indexOf("for");

				int timeindex=Arrays.asList(splitspace).indexOf(":");

				Algo=splitspace[algoindex+1];

				comptime=splitspace[timeindex+1];

				row=sheet.getRow((short)index);
				DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS");		
				if(splitspace.length>1)
				{
					row.createCell((int)0).setCellValue(Algo);
					row.createCell((int)2).setCellValue(comptime);
					row.createCell((int)3).setCellValue(dateTimeFormatter.format(LocalDateTime.now()));
				}

				index++;
			}


			fileOut= new FileOutputStream("C:\\Users\\Sadiya\\Desktop\\Coflow-data\\TimeCSV1.csv");
			//PrintStream fileOut = new PrintStream(new FileOutputStream("C:\\Users\\Sadiya\\Desktop\\Coflow-data\\TimeCSV.csv"),true, "UTF-8");
			//byte[] bom = {(byte)0xEF, (byte)0xBB, (byte)0xBF};
			//fileOut.write(bom);
			wb.write(fileOut);
			fileOut.close();
			System.out.println("Data is saved in excel file.");
		}
		catch(Exception e){System.out.println(e);}
		finally {
			if(fileOut != null){
				try {
					fileOut.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
