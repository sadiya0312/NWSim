package sim.utils;

public interface Constants {

	String COMPLETION_TIME = "Completion time";
	String AVERAGE_WAITING_TIME = "Average waiting time";
	String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss.SS";
	
	public interface Algo {
		String FIFO = "FIFO";
		String SJF = "SJF";
		String PFS ="PFS";
		String SEPARATOR = " : ";
	}
	
	public interface File{
		String FILE_PATH = "C:\\Users\\Sadiya\\Desktop\\Coflow-data\\";
		String FILE_NAME = "TimeLogCSV";
		String CSV = ".csv";
	}
	
	public interface Output{
		String ALGORITHM = "Algorithm";
		String ACT = "ACT";
		String AWT ="AWT";
		String DATE_TIME = "DateTime";
	}
 }
