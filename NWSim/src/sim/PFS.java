package sim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class PFS {

	Jobs[] Jobarray=Engine.array_jobs.clone();
	long time=System.nanoTime();
	long AvgPFS=0;
	String algo;
	ArrayList<Long> timejob=new ArrayList<Long>(Collections.nCopies((Framework.number_jobs),0L));
	PFS pfs;
	int flag=0;
	int p,coflow,coflow1,coflow2,Mapper,location,Reducer_location,send_reducer;
	ArrayList<Integer> history_mapper;
	ArrayList<Integer> p_value;
	ArrayList<Integer> s_coflow;
	ArrayList<Integer> check;
	LinkedHashMap<Integer,Integer>weight;
	ArrayList<Integer> reducer_location;
	public PFS() {
		history_mapper=new ArrayList<>(Collections.nCopies((Framework.Mapper), 0));
		weight=new LinkedHashMap<Integer,Integer>();
		p_value=new ArrayList<>(Collections.nCopies((Framework.Mapper-1), 0));
		s_coflow=new ArrayList<>(Collections.nCopies((Framework.Mapper-1), 0));
		check=new ArrayList<>(Collections.nCopies((Framework.Mapper-1), 0));
		reducer_location=new ArrayList<Integer>();

	}

	@SuppressWarnings("unchecked")
	public void PFS_red(){


		//Weight is based on the location of the reducer
		//if reducers in a coflow not = 1 then 
		//Find which mapper coflow belong
		//find the smallest reducer location (subtract all reducer locations with the mapper location)
		//Increament history_mapper as per the mapper

		//P=Weight/(history_mapper)
		// Find P for two mappers (1&2) 
		// if P1>P2 then send P1 to reducer


		//Use Loop here 	
		for(int run=0;run<=((Framework.Mapper)*(Framework.queuesinmapper)*(Framework.Queue_length));run++){

			coflow_mapper();
			if(run==0){
				s_coflow.remove(s_coflow.size()-1);
				p_value.remove(p_value.size()-1);
			}
			System.out.println(s_coflow);
			int smallest_coflow=Collections.min(s_coflow);
			send_reducer=Collections.max(p_value);

			System.out.println("P values "+p_value);

			int index=(s_coflow.indexOf(smallest_coflow));

			if(smallest_coflow==0){
				System.out.println("Smallest coflow is "+0);

				//s_coflow.add(index,(int) Math.pow(10000,1000));	
				s_coflow.remove(index);
				s_coflow.add(index,(int) Math.pow(10000,1000));
				smallest_coflow=Collections.min(s_coflow);
				index=(s_coflow.indexOf(smallest_coflow));
			}

			System.out.println("Check "+ check);

			if(check.stream().distinct().limit(2).count() <= 1)
			{
				//Find time taken by each job and fill up the log book.
				jobincomplete();

				//System.out.println("Time to run PFS in milliseconds "+time);
				algo="PFS";	   

				AvgPFS=((timejob.stream().mapToInt(Long::intValue).sum())/Framework.number_jobs);

				Timelog avg1=new Timelog(AvgPFS,algo);
				avg1.Enteraverage();

				time=((System.nanoTime()-time)/1000000);
				Timelog tlog=new Timelog(time,algo);
				tlog.Enterlogs();

				return;

			}

			history_mapper.add(index, history_mapper.get(index)+1);

			//Sending coflow to reducer
			//Pick the coflow from Lhm
			//pick number of flows in it and reducer location
			//send number of flows=cores to each reducer

			System.out.println("\nSending "+Collections.min(s_coflow)+ " coflow "+"of mapper "+index+ " with p value "+send_reducer);

			removejobelement(Collections.min(s_coflow));

			for (Entry<Integer, Event> val2 : Engine.Lhm.entrySet()) {

				if(smallest_coflow==val2.getKey()){

					int n_flows=val2.getValue().n_flows;


					reducer_location=(ArrayList<Integer>) (val2.getValue().reducers_location.clone());

					int Reducer_send=reducer_location.get(0);
					reducer_location.remove(0);

					for(int assign=1;assign<=n_flows;)
					{   int same=1;
					while(same<=Reducer.cores && assign<=n_flows){
						System.out.println("Send "+assign +" flow to reducer "+Reducer_send);
						//reducer_location.remove(0);
						same++;
						assign++;
					}
					if(reducer_location.isEmpty()){
						break;
					}
					else{

						Reducer_send=reducer_location.get(0);
						reducer_location.remove(0);
					}
					}
				}
			}


			//Delete this coflow from the mapper queue
			for(int queue=0;queue<Framework.queuesinmapper;queue++)//Queue loop
			{   
				if(Graph.all_mappers[index].mapper_queue.get(queue).isEmpty()){}
				else{
					if(Graph.all_mappers[index].mapper_queue.get(queue).peek()==smallest_coflow){
						Graph.all_mappers[index].mapper_queue.get(queue).remove();
						s_coflow.clear();
						check.clear();
						System.out.println("I have deleted coflow "+smallest_coflow);
					}
				}
			}

		}

	}

	public void coflow_mapper(){

		for(int map=0;map<(Framework.Mapper);map++) //Mapper loop
		{   
			//Smallest coflow in a mapper
			Mapper=coflow_queue(map);
			s_coflow.add(map,Mapper);
			check.add(map,Mapper);
			//Assign weight of this mapper by finding smallest reducer location
			Reducer_location=find_reducerlocation(Mapper);
			weight.put(map,Reducer_location); 
			//find p value of each mapper and store in p_value
			int p=(int) find_p(weight.get(map),history_mapper.get(map));
			System.out.println("\nWeight "+weight.get(map)+" and history "+history_mapper.get(map));
			if(p_value.get(map)==0){
				p_value.add(map, p);	
			}
			else{
				p_value.remove(map);
				p_value.add(map, p);
			}
		}

	}


	public int find_reducerlocation(int findcoflow){

		for (Entry<Integer, Event> val : Engine.Lhm.entrySet()) {

			if(findcoflow==val.getKey()){

				System.out.print("\n" + val.getKey()
				+ "			" + val.getValue() + "		"+ (val.getValue())); 
				location=Collections.min(val.getValue().reducers_location);
			}
		}

		return location;
	}


	public int coflow_queue(int mapper)
	{
		for(int queue=0;queue<Framework.queuesinmapper;queue++)//Queue loop
		{   
			if(queue==0){
				//Queue 1
				if(Graph.all_mappers[mapper].mapper_queue.get(queue).isEmpty()){
					coflow1=0;
				}
				else{
					coflow1=Graph.all_mappers[mapper].mapper_queue.get(queue).peek();
				}

				//Queue 2   
				if((queue+1)<Framework.queuesinmapper){
					if(Graph.all_mappers[mapper].mapper_queue.get(queue+1).isEmpty()){
						coflow2=0;
					}
					else{
						coflow2=Graph.all_mappers[mapper].mapper_queue.get(queue+1).peek();
					}

					//compare
					//coflow1=Engine.Lhm.get(coflow1).n_flows;
					//coflow2=Engine.Lhm.get(coflow2).n_flows;
					if(coflow1>coflow2/*reducers coflow1>reducers coflow2*/)
					{
						coflow=coflow1;
					}
					else{coflow=coflow2;}
					//Search coflow1 and coflow2 and return coflow with the least reducers
				}	
			}
			else{
				if((queue+1)<Framework.queuesinmapper){
					if(Graph.all_mappers[mapper].mapper_queue.get(queue+1).isEmpty()){
						coflow2=0;
					}
					else{
						coflow2=Graph.all_mappers[mapper].mapper_queue.get(queue+1).peek();
					}

					//compare

					if(coflow2>coflow/*reducers coflow1>reducers coflow2*/)
					{
						coflow=coflow2;
					}
					else{coflow=coflow;}
					//Search coflow1 and coflow2 and return coflow with the least reducers
				}	 
			}

		}
		return coflow;

	}

	public float find_p(int weight,int count){
		if(count!=0){
			p=weight/count;
		}
		else{p=weight;}
		return p;

	}

	public void removejobelement(int coflowremove){

		for(int find=0;find<Framework.number_jobs;find++){

			if(/*Jobarray[find].array_task_id.size()!=0 &&*/Jobarray[find].array_task_id!=null && Jobarray[find].array_task_id.contains(coflowremove)){			   

				if(timejob.get(find)==0){
					timejob.remove(find);
					timejob.add(find,((System.nanoTime())/1000000));
					System.out.println(coflowremove +" coflow is my first coflow. I am job "+find+". Updated Job time is "+timejob);
				}
				Jobarray[find].array_task_id.remove(new Integer(coflowremove));
				System.out.println("I have deleted coflow "+coflowremove +" from job "+find);
				flag++;

				if(flag==Framework.id || flag==Framework.Mapper*Framework.queuesinmapper*Framework.Queue_length)
				{
					System.out.println("Its a national flag");
					removejobelement(coflowremove);
				}

			}
			else{
				if(Jobarray[find].array_task_id!=null && Jobarray[find].array_task_id.isEmpty() && timejob.get(find)!=0){
					//Stop time	  
					long totExecTime=(System.nanoTime()/1000000)-timejob.get(find);
					timejob.remove(find);
					timejob.add(find,((totExecTime)));
					System.out.println("System Time: "+(System.nanoTime()/1000000));
					System.out.println("Execution time: "+totExecTime+" of job "+find);
					Jobarray[find].array_task_id=null;

				}
				else if(Jobarray[find].array_task_id!=null && Jobarray[find].array_task_id.size()!=0){
					System.out.println(find+" I neither have the coflow to be removed nor I am completely empty. Job time currently for all is : "+timejob);
				}
				else{
					System.out.println("I have been made null and my size is 0. I am "+find +" with jobtime "+timejob);
				}

			}
		}  
	}

	public void jobincomplete(){
		/**
		 * 
		 */
		for(int find=0;find<Framework.number_jobs;find++){
			if((Jobarray[find].array_task_id==null || Jobarray[find].array_task_id.isEmpty())){
				System.out.println(find+ " Job is complete");
				//log time of job completion
				Timelog tjob=new Timelog(timejob.get(find),Integer.toString(find));
				tjob.Enterjoblogs();
			}

			else{
				System.out.println(find+" Job is incomplete");
			}
		}

	}

} 
