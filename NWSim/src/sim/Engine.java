package sim;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;



public class Engine extends Mapper
{	
	//In this class properties of each Coflow are analyzed and placed in appropriate Mapper queue 

	Engine en;
	int reduce;
	public static LinkedHashMap<Integer, Event> Lhm;
	public static LinkedHashMap<Integer,ArrayList<Integer>> flowsize;        //Stores event(coflow) ID and flowsize of each flow in it.
	Set<Integer> set; 
	int input;

	public static Jobs[] array_jobs=new Jobs[Framework.number_jobs];

	void addVertex(int key,int arrival_time,int mappers_req,int mappers_location,int reducers_req,ArrayList<Integer> reducer_loc,int link_capacity,float demand_capacity,int deadline,int job_id,int n_flows) {

		Lhm.put(key, new Event(arrival_time,mappers_req,mappers_location,reducers_req,reducer_loc,link_capacity,demand_capacity,deadline,job_id,n_flows));

	}


	public Engine()
	{

		//create events and fill queue of events
		//create mapper and reducer network

		Lhm=new LinkedHashMap<Integer, Event>(Framework.id);
		flowsize=new LinkedHashMap<Integer,ArrayList<Integer>>();
		set = new HashSet<Integer>(Framework.Reducer);
		System.out.println("");   
	}


	
	public void Create_Event(Engine en)

	{    //Use Collectdata.Ratio and Collectdata.flow, Framework.id as number of events

		System.out.println("\n-------------------------Actual Coflows-------------------------------------------\n");
		int j=1,i,e=1;

		Random r = new Random();
		r.setSeed((long) Framework.seed);

		set=Collectdata.Ratio_location.keySet();
		int min=Collections.min(set);
		int max= Collections.max(set);
		Random rand = new Random();
		ArrayList<Integer> arr=new ArrayList<>(Framework.Reducer);
		for(int input=1;input<=Framework.Reducer;input++){
			arr.add((int) ((Math.random() * ((max - min) + 1)) + max));
		}

		for (Entry<Integer, Float> val2 : Collectdata.Ratio.entrySet())

		{	

			if(j<=Framework.id)
			{
				if(val2.getKey()!=1)
				{

					float val= val2.getValue();
					int flow=val2.getKey();
					float flowinput =(((Framework.id)*val)/100)*Framework.id;
					//System.out.println("I am inside " + flowinput);
					reduce=(int) (Math.ceil((float)flow/(float)Reducer.cores));
					ArrayList<Integer> reducer_loc=new ArrayList<Integer>();
					//(flow/Reducer.cores);
					//Iterator<Entry<Integer, Float>> val3 = Collectdata.Ratio_location.entrySet().iterator();
					//for(int add=1;add<=reduce;add++){

					Collectdata.Ratio_location.forEach((k,v)->{
						if(reduce!=0){
							System.out.println("key "+k+" Value "+v);
							//reducer_loc.add(val3.next().getKey());
							//pick location randomly in key range



							int picklocation=arr.get(rand.nextInt(arr.size()));
							reducer_loc.add(picklocation);
							reduce--;


							System.out.println("Reducer Locations: "+reducer_loc);
						}});


					//}

					//}


					for(i=1;i<=(int)flowinput;++i)
					{    if(e<=Framework.id)
					{
						en.addVertex(e,e,e,e,(int)(Math.ceil((float)flow/(float)Reducer.cores)),reducer_loc,e,e,e,((int)(r.nextInt(((Framework.number_jobs - 0) + 1) + 0))/*(Framework.number_jobs)*/),flow);
						Findflowsize(e,flow);
						//System.out.println("Number of flows:"+flow+" and "+flowinput+" and event "+i);
						e++;
					}

					}
					j=j+i-1;
					//System.out.println("This is j "+j);

				}

			}
		}
		System.out.println();

		System.out.println("Requested Events are created: \n");

		Set <Entry<Integer,Event>> entry=Lhm.entrySet();

		Iterator<Entry<Integer, Event>> itr=entry.iterator();
		while(itr.hasNext())
		{
			Entry<Integer,Event> entryInLoop=(Entry<Integer, Event>) itr.next();
			System.out.println("Event:"+entryInLoop.getKey()+" And parameters:"+entryInLoop.getValue().toString());
		}

	}

	public void Create_jobs(Engine en, Graph graph)
	{
		System.out.println("\n");

		for(int job = 0;job<(Framework.number_jobs);job++)
		{
			array_jobs[job]=new Jobs(job);					//creating these jobs storing their correspondng task.
			array_jobs[job].identifytask();	
		}

	}

	public void Findflowsize(int e, int n_flow){
		ArrayList<Integer> size= new ArrayList<Integer>(n_flow);
		for(int input=1;input<=n_flow;input++){
			size.add((int) ((Math.random() * ((30000 - 2) + 1)) + 2));
		}
		flowsize.put(e,size);

	}

	public void send_mapper(Engine en,Graph graph) throws IOException
	{

		/* 
		 * Now algorithm is applied to place the events in correct mapper.(analyze elements of mapper class)
		 * Analysis criteria - 
		 * 		If width of mapper < width of other mappers
		 * 		If size of mapper < size of other mappers
		 * 		If deadline > Next_job	
		 * As we place a certain event in a certain mapper, the mapper class is called to define mapper properties.
		 * Theses properties are updated with each assignment of event to the mapper.
		 */
		Algomapper algomp=new Algomapper();
		algomp.Initial_Algo(graph);

	}

	public void send_reducer()
	{
		/*
		 * Now the algorithm is applied to send the events from mapper to reducer
		 * With release of each event mapper class Update_capacity function is called
		 * And reducer class Update_capacity function is called
		 */
	}
}



