package sim;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Vector;

public class Mapper {

	Mapper mp;

	/*In this class properties of Mapper are defined
	 *length=size of its largest flow in bytes
	 *skew=coefficient of variation of its flows in terms of size
	 */

	public static PriorityQueue<Integer> Queue;	

	public Map<Integer,Float> Connection_map;	        //This stores Reducer id and available bandwidth for this mapper(linkedhashmap)
	public Map<Integer,Integer>Jobs_queue;	            //This stores number of current jobs in each queue of this mapper(width) 
	public Vector<PriorityQueue<Integer>>mapper_queue;
	public int Next_job;						        //This stores time left to schedule next job
	public int size;             			            //This stores Total number of jobs with the mapper(size),size=the sum of all flows in bytes
	public int width;							        //width=the number of parallel flows

	Mapper()
	{
		/*A mapper class should be called with the specification of number of jobs assigned to this mapper(Totaljobs_mapper)
		 */

		/*Date date= new Date();
		  	long time = date.getTime();*/
		/*Next_job = new Time(time);
			size=(int) (Math.random()*Framework.Queue_length*3);
			width=(int) Math.random();
		 */


		Connection_map=new HashMap<Integer,Float>();
		Jobs_queue=new HashMap<Integer,Integer>();	
		mapper_queue=new Vector<>();

		for(int i=0;i<Framework.queuesinmapper;i++)
		{
			Queue = new PriorityQueue<>(Framework.Queue_length);
			mapper_queue.add(Queue);
		}			
	}

	public void setband(int id,float band)
	{
		Connection_map.put(id, band);		

	}

	public void showdata(){

		System.out.println("Time left to schedule next job " + Next_job);
		System.out.println("Total number of jobs with mapper currently "+size);
		System.out.println("The number of paralel flows "+width);
	}

	public void Update_capacity(int reducer_id,float updated_band,int queue_num,int jobs_current,Time Next_job, int size,int width)
	{
		/*This function is called when job is scheduled and send to the reducer or its completed at the reducer.
			For example a job is released to the reducer , an update should be made at mapper that after releasing that job on
			which connection(reducer_id) bandwidth(update_band) has been decreased.The job has been released from which queue
			(queue_num)and now queue is left with how many jobs(jobs_current). 

			As soon as reducer receive the job again bandwidth(update_band) is changed on the same channel(reducer_id) with same
			queue(queue_num) and same jobs(jobs_current).This will act like an acknowledgement for the mapper.
		 * 
		 */


	}

	public void identifyvalues()
	{

	}

}
