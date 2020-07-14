/**
 * 
 */
package sim.dto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import sim.algorithms.EEA;
import sim.exceptions.CsvException;
/**
 * @author Sadiya
 * 
 */
public class CsvData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3161483271845446435L;

	public static enum CsvType{
		HEADER,BODY;
	}
	
	
	
	private List<CsvRow> datas;
	private CsvRow header;
	
	public static void extractCsv() throws FileNotFoundException, CsvException, IOException {
	CsvData csvData=new Builder("C:\\Users\\Sadiya\\Desktop\\Coflow-data\\Data\\CPUmatrix.csv")
			.createHeaderRow().buildData().create();
	/*System.out.println(csvData.getHeader());*/
	//csvData.datas.forEach(System.out::println);
	
    /*System.out.println(csvData.getIndexForColumn("Efficiency"));*/
    System.out.println(csvData.getColumnValue("Efficiency"));
    //Change the getColumnValue each time to get different collumn
    EEA.intlist=csvData.getColumnValue("Efficiency").stream().map(Float::parseFloat).collect(Collectors.toList());
     
	}
	
	private CsvData(Builder builder)throws CsvException {
		this.datas=builder.datas;
		this.header=builder.header;
		EEA.intlist=new ArrayList<Float>();
	}

	
	public List<CsvRow> getDatas() {
		return datas;
	}

	public CsvRow getHeader() {
		return header;
	}

	public int getIndexForColumn(String columnName){
		if(!header.cols.contains(columnName)){
			throw new CsvException("ColumnName "+columnName+"not found");
		}
		return header.cols.indexOf(columnName);
	}
	
	public List<String> getColumnValue(String columnName){
		List<String> columns = new ArrayList<>();
		int columnIndex = getIndexForColumn(columnName);
		this.datas.forEach(data->{
			columns.add(data.cols.get(columnIndex));
		});
		return columns;
	}

	public static class Builder{
		private File csvFile;
		private List<CsvRow> datas;
		private CsvRow header;
		
		public Builder(String filename) throws CsvException{
			//Validating for String
			if(filename==null || filename.isEmpty()){
				throw new CsvException("Invalid File name");
			}
			csvFile=new File(filename);
			if(!(csvFile.exists() && csvFile.isFile()))
			{
				throw new CsvException("File not found");
			}
		}
		
		
		public Builder createHeaderRow() throws FileNotFoundException, IOException{
			try(BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), StandardCharsets.UTF_8))){
				String headerRow = br.readLine();
				header = new CsvRow(CsvType.HEADER,Arrays.asList(headerRow.split(",")));
			}
			return this;
		}
		
		public Builder buildData() throws IOException{
			boolean headerSkipped = false;
			try(BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), StandardCharsets.UTF_8))){
				datas = new ArrayList<>();
				String line;
				while(( line = br.readLine())!= null){
					if(header!=null && !headerSkipped){
						headerSkipped=true;
					}else{
						datas.add(new CsvRow(CsvType.BODY,Arrays.asList(line.split(","))));
					}
				}
			}
			return this;
		}
		
		
		public CsvData create(){
			return new CsvData(this);
		}
		
	}
	
	public static class CsvRow implements Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -4394175215508934045L;
		private CsvType type;
		private List<String> cols;
		
		public CsvRow(CsvType type, List<String> cols) {
			super();
			this.type = type;
			this.cols = cols;
		}

		public CsvType getType() {
			return type;
		}
		public List<String> getCols() {
			return cols;
		}

		@Override
		public String toString() {
			return "CsvRow [type=" + type + ", cols=" + cols + "]";
		}	
		
	}

	@Override
	public String toString() {
		return "CsvData [datas=" + datas + ", header=" + header + "]";
	}  
}
