package sim.utils;

public interface Constants {

	String COMPLETION_TIME = "Completion time";
	String AVERAGE_WAITING_TIME = "Average waiting time";
	String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss.SS";
	
	public interface Algo {
		String FIFO = "FIFO";
		String SJF = "SJF";
		String PFS ="PFS";
		String EEA="EEA";
		String SEPARATOR = " : ";
		
	}
	
	public interface File{
		String FILE_PATH = "C:\\Users\\Sadiya\\Desktop\\Coflow-data\\";
		String FILE_NAME = "TimeLogCSV";
		String FILE_NAME_ENERGY="EnergyLogCSV";
		String CSV = ".csv";
		
		static String getFileName(String fileName){
			return FILE_PATH+fileName+CSV;
		}
		
		static java.io.File getFile(String fileName){
			return new java.io.File(getFileName(fileName));
		}
		
		static boolean isFileExists(String file){
			return isFileExists(new java.io.File(file));
		}
		
		static boolean isFileExists(java.io.File file){
			return file.exists() && file.isFile();
		}
	}
	
	public interface Output{
		String ALGORITHM = "Algorithm";
		String ACT = "ACT";
		String AWT ="AWT";
		String DATE_TIME = "DateTime";
		String COFLOW="Coflow";
		String ENERGY="Energy";
		
	}

 }
