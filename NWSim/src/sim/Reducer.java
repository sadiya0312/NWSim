package sim;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;;

public class Reducer {

	//In this class properties of Reducer are defined
	Reducer rd;
	public Map<Integer,Integer>Connection_Reduce;		//This stores Mapper id and coflow id
	public long cpu_pw = 0;                          //This stores time taken by this reducer to complete 1 megabyte of job
	public static int cores;                               //This stores number of cores in a reducer
	public long free_mem;                           //This stores free memory of the reducer
	public long max_mem;                            //This stores maximum memory of the reducer
	public long use_mem;                            //This stores memory in use of the reducer
	public static long process_time;

	Reducer()
	{
		Connection_Reduce=new HashMap<Integer,Integer>();

	}
	public void Reducerdetails() throws IOException
	{

		System.out.println();
		cores= Runtime.getRuntime().availableProcessors();  
		System.out.println("Available processors (cores): " + 
				cores);

		free_mem= Runtime.getRuntime().freeMemory();
		System.out.println("Free memory (bytes): " + 
				free_mem);


		max_mem = Runtime.getRuntime().maxMemory();
		/* Maximum amount of memory */
		System.out.println("Maximum memory (bytes): " + 
				(max_mem == Long.MAX_VALUE ? "no limit" : max_mem));


		use_mem=Runtime.getRuntime().totalMemory();
		System.out.println("Total memory (bytes) in use: " + 
				use_mem);
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

		cpu_pw += threadMXBean.getThreadCpuTime(1);
		System.out.println("Processing time in milliseconds "+(cpu_pw/1000000));

		process_time=(cpu_pw + Server.traveltime)/1000000; 
		System.out.println("Total Processing time for each flow: "+(process_time));

	}
}
