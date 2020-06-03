package sim;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Timelog {

	long time;
	String algo;
	public Timelog(long time,String algo) {
		this.time=time;
		this.algo=algo;
	}

	public void Enterlogs(){
		FileHandler handler;
		try {
			handler = new FileHandler("Timelog.log", true);

			SimpleFormatter formatter = new SimpleFormatter();  
			handler.setFormatter(formatter);  
			Logger logger = Logger.getLogger(Timelog.class.getName());
			logger.addHandler(handler);


			logger.info("Completion time for "+algo+" in milliseconds : "+time+"\n");

			logger.config("config message");

			logger.fine("fine message");

			logger.finer("finer message");

			logger.finest("finest message");

		} catch (SecurityException | IOException e) {

			e.printStackTrace();
		}
	}
	public void Enterjoblogs(){
		FileHandler handler;
		try {
			handler = new FileHandler("Timelog.log", true);

			SimpleFormatter formatter = new SimpleFormatter();  
			handler.setFormatter(formatter);  
			Logger logger = Logger.getLogger(Timelog.class.getName());
			logger.addHandler(handler);


			logger.info("--------------------------------Time to run job "+algo+" in milliseconds : "+time+"\n");

			logger.config("config message");

			logger.fine("fine message");

			logger.finer("finer message");

			logger.finest("finest message");


		} catch (SecurityException | IOException e) {

			e.printStackTrace();
		}
	}
	public void Enteraverage(){
		FileHandler handler;
		try {
			handler = new FileHandler("Timelog.log", true);

			SimpleFormatter formatter = new SimpleFormatter();  
			handler.setFormatter(formatter);  
			Logger logger = Logger.getLogger(Timelog.class.getName());
			logger.addHandler(handler);


			logger.info("-------------Average waiting time for "+algo+" in milliseconds : "+time+"\n");

			logger.config("config message");

			logger.fine("fine message");

			logger.finer("finer message");

			logger.finest("finest message");

		} catch (SecurityException | IOException e) {

			e.printStackTrace();
		}
	}

}
