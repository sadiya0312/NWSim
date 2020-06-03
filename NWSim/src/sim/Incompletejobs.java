package sim;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Incompletejobs {
	int incomplete;
	public Incompletejobs(int incomplete) {
		this.incomplete=incomplete;
	}
	public void Enterlogs(){
		FileHandler handler;
		try {
			handler = new FileHandler("Incomplete.log", true);

			SimpleFormatter formatter = new SimpleFormatter();  
			handler.setFormatter(formatter);  
			Logger logger = Logger.getLogger(Incompletejobs.class.getName());
			logger.addHandler(handler);


			logger.info("Incomplete job: "+incomplete+"\n");

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
