package sim;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class GenerateLog {

	Entry<Integer, Event> coflow;
	public GenerateLog( Entry<Integer, Event> entry) {
		// TODO Auto-generated constructor stub
		this.coflow=entry;
	}
	public void Enterlogs(){
		FileHandler handler;
		try {
			handler = new FileHandler("LogFile.log", true);

			SimpleFormatter formatter = new SimpleFormatter();  
			handler.setFormatter(formatter);  
			Logger logger = Logger.getLogger(GenerateLog.class.getName());
			logger.addHandler(handler);


			logger.info("Queues are full for coflow: "+coflow+"\n");

			logger.config("config message");

			logger.fine("fine message");

			logger.finer("finer message");

			logger.finest("finest message");


		} catch (SecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

