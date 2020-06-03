package sim;
import java.util.*;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.*;
import java.security.Timestamp;

/**
 * @author Sadiya
 *
 */
public class Framework {

	/**
	 * Define all the variables in this class 
	 * Variables are M number of Mappers , R number of Reducers
	 * B as bandwidth, L -length of the job, W-width of the job, S- Skew of the job
	 */


	public static int Mapper;
	static int Length_max;
	static int Reducer;
	static int Width_max;
	static int Skew_max;
	static Timestamp Arrivaltime;
	static int Queue_length;
	static int Bandwidth;
	static double seed;
	static List<Integer> list=new ArrayList<Integer>();
	static int id,size;
	static int queuesinmapper;
	static int number_jobs;
	static int count=0;



	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @throws InvalidFormatException 
	 */

	public void CallFunctions(){

	}

	public static void main(String[] args) throws IOException, InterruptedException, InvalidFormatException {



		//Loading config.properties file.
		Properties prop=new Properties();
		FileInputStream ip=new FileInputStream("config.properties");
		prop.load(ip);

		Mapper= Integer.parseInt(prop.getProperty("Mappers"));
		Reducer=Integer.parseInt(prop.getProperty("Reducers"));
		Length_max= Integer.parseInt(prop.getProperty("Coflow_Length_max"));
		Width_max = Integer.parseInt(prop.getProperty("Coflow_Width_max"));
		Skew_max=Integer.parseInt(prop.getProperty("Coflow_Skew_max"));
		Queue_length=Integer.parseInt(prop.getProperty("Queue_length"));	
		Bandwidth=Integer.parseInt(prop.getProperty("Bandwidth"));
		//seed = Integer.parseInt(prop.getProperty("Seed"));
		id = Integer.parseInt(prop.getProperty("Events"));
		queuesinmapper=Integer.parseInt(prop.getProperty("Queueinmapper"));
		number_jobs= Integer.parseInt(prop.getProperty("numberjobs"));


		Server sr=new Server();
		Client cl=new Client();

		Thread t1 = new Thread(new Runnable() {
			public void run() {
				sr.serverstart();

			}
		});


		Thread t2 = new Thread(new Runnable() {
			public void run() {
				cl.clientstart();

			}
		});

		t1.start();
		t2.start();  

		Thread.sleep(400);


		//Collecting seed

		Seed seed=new Seed();
		seed.writeDataLineByLine();

		Collectdata cd=new Collectdata();
		cd.main(args);


		//Calling on function in Graph class
		Graph g=new Graph();
		g.createGraph(g);	
		//Calling on function in Engine class
		Engine en=new Engine();
		en.Create_Event(en);
		en.Create_jobs(en, g);
		en.send_mapper(en,g);
		FIFO fifo=new FIFO();
		fifo.FIFO_red();

		en.send_mapper(en,g);
		SJF sjf=new SJF();
		sjf.SJF_red();

		PFS pfs=new PFS();
		pfs.PFS_red();

		TimeLogReader csv=new TimeLogReader();
		csv.exportLog();
		System.exit(0);
	}
}
