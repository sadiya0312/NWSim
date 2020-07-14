package sim;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CallPython {

	public CallPython() {
		
	}
	public static void main(String[] args) throws Exception {
		ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	    ProcessBuilder processBuilder = new ProcessBuilder();
	    processBuilder.command("python","C:\\Users\\Sadiya\\Desktop\\Coflow-data\\Data\\hello.py");
	    Process process = processBuilder.inheritIO().start();
	    try(BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))){
	    	ProcessTask task = new ProcessTask(br);
	    	Future<List<String>> future = executorService.submit(task);
	    	future.get(5, TimeUnit.SECONDS).forEach(System.out::println);
	    }finally{
	    	executorService.shutdown();
	    }
	}
	
	private static class ProcessTask implements Callable<List<String>>{
		private BufferedReader bufferedReader;

		/**
		 * @param bufferedReader
		 */
		public ProcessTask(BufferedReader bufferedReader) {
			this.bufferedReader = bufferedReader;
		}

		@Override
		public List<String> call() throws Exception {
			return bufferedReader.lines().collect(Collectors.toList());
		}	
	}
}
