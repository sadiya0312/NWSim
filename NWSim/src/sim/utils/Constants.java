package sim.utils;

public interface Constants {

	String COMPLETION_TIME = "Completion time";
	String AVERAGE_WAITING_TIME = "Average waiting time";
	String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss.SS";
	String JOB = "job";
	
	public interface Algo {
		String FIFO = "FIFO";
		String SJF = "SJF";
		String PFS ="PFS";
		String EEA="EEA";
		String SEPARATOR = " : ";
		String EAA = "EAA";
        String ECE ="ECE" ;
    }
	
	interface File{
		String FILE_PATH = "C:\\Users\\Sadiya\\Desktop\\Coflow-data\\";
		String FILE_NAME = "TimeLogCSV";
		String FILE_NAME_ENERGY="EnergyLogCSV";
		String FILE_NAME_PARAMETER = "ParameterLog";
		String FILE_LINE_FLOWSIZE = "Flowsize";
		String CSV = ".csv";

		enum FileExtension{
			CSV(".csv"),
			TSV(".tsv"),
			JSON(".json"),
			TXT(".txt");

			private final String ext;
			FileExtension(String ext) {
				this.ext = ext;
			}

			public String getExt() {
				return ext;
			}
		}
		
		static String getFileName(String fileName){
			return FILE_PATH+fileName+FileExtension.CSV.ext;
		}
		static String getFileName(String fileName, FileExtension extension){
			return FILE_PATH+fileName+extension.ext;
		}

		static java.io.File getFile(String fileName){
			return new java.io.File(getFileName(fileName));
		}
		static java.io.File getFile(String fileName, FileExtension extension){
			return new java.io.File(getFileName(fileName, extension));
		}

		static boolean isFileExists(String file){
			return isFileExists(new java.io.File(file));
		}

		static boolean isFileExists(java.io.File file){
			return file.exists() && file.isFile();
		}
	}
	
	interface Output{
		String ALGORITHM = "Algorithm";
		String FLOW_SIZE = "Flow size";
		String ACT = "ACT";
		String AWT ="AWT";
		String DATE_TIME = "DateTime";
		String COFLOW="Coflow";
		String FLOW="Flow";
		String SIZE="Size";
		String ENERGY="Energy";
		String TIMESTAMP = "TimeStamp";
		String EFFICIENCY ="Efficiency";
		String REDUCER ="Reducer";
		String NUMBER_OF_RUNS = "NumberOfRuns";
		String LENGTH = "Length";
		String JOB = "Job";
		String COFLOW_RUNTIME = "Coflow Runtime";
		String GIGAFLOPS = "GigaFlops";
		String WATTS = "Watts";
	}

 }
