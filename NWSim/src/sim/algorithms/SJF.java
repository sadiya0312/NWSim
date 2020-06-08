package sim.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import sim.Engine;
import sim.Event;
import sim.Framework;
import sim.Graph;
import sim.Reducer;
import sim.Timelog;

public class SJF implements Algorithm {


	long time=System.nanoTime();
	long timejob,AvgSJF=0;
	String algo;
	SJF sjf;
	Event flowprop;
	int last_element,coflowid,i;
	int rd=1,core=1,map=-1;
	long average_process,np,job_length=0;
	long sum_flows=0;
	static ArrayList<Integer> Job_len_arr =new ArrayList<Integer>() ;

	public SJF() {

	}
	
	public void runAlgo() throws InterruptedException {
		/*For each coflow in job
		 * {
		 * pick coflowid and find in Lhm
		 * pick no. of flows for that coflowid
		 * average_processing=(Sum(all_reducers.Reducer.processingtime)/Framework.Reducer)
		 * np=(no. of flow * average_processing)
		 * job_length=job_length+np
		 * }
		 * Compare job lengths
		 * Assign flows of coflows of shortest job to the reducer
		 */

		//Find job length for each job

		System.out.println("\n---------------Shortest Job First-----------------\n");
		for(int job = 0;job<(Framework.number_jobs);job++)
		{
			job_length=0;
			Engine.array_jobs[job].array_task_id.forEach((n)->{

				//System.out.println("Initial "+job_length);

				flowprop=Engine.Lhm.get(n);
				average_process=Reducer.process_time; 							//All reducers have same processing time(can change)
				np=((flowprop.n_flows)*(average_process));
				job_length=job_length+np;

				//System.out.println("\nJob length:"+np);

			});
			Job_len_arr.add((int) job_length);
			System.out.println("\nJob "+job+" length:"+job_length+"\n");

		}

		//Find min job length from arraylist of job length

		int job=Framework.number_jobs;
		while(job>0){
			timejob=0;
			timejob=System.nanoTime();
			int minindex=Job_len_arr.indexOf(Collections.min(Job_len_arr));	
			System.out.println("\nShortest Job id is: "+ (minindex));
			Engine.array_jobs[minindex].array_task_id.forEach((n)->{
				coflowid=(n);
				flowprop=Engine.Lhm.get(n);
				last_element=(flowprop.n_flows);
				sum_flows=sum_flows+last_element;
				int ratio=(last_element/((Reducer.cores)*(Framework.Reducer)));

				map=(-1);

				System.out.println("Coflowid "+coflowid+" and number of flows "+ last_element);

				//Iterate through mapper queues to find a coflowid

				for(i=0;i<(Framework.Mapper);i++){ 	

					Graph.all_mappers[i].mapper_queue.forEach((nn)->{

						Iterator<Integer> value = nn.iterator();

						while (value.hasNext()) { 

							if((int)(value.next())==coflowid){
								map=i;
								System.out.println("Coflow: "+coflowid+" is found at Mapper "+map);
							}
						}
					});

				}


				//Store it in reducer

				while(ratio!=0){
					rd=1;
					core=1;
					System.out.println("\n--------Another Round--------");

					//Update the reducer here

					for(int assign=1;assign<=((Reducer.cores)*(Framework.Reducer));assign++){

						if((core<=Reducer.cores) && (map!=-1)){
							Graph.all_reducers[rd].Connection_Reduce.put(map,coflowid);
							System.out.println("\n-----Coflow "+coflowid+" and flow "+assign+" is assigned to core "+core+" of reducer "+rd+" by mapper "+ map+"------\n");
							core++;
							if(core>4){
								core=1;
								rd++;
							}
						}	
					}
					ratio--;	
					try {
						Thread.sleep(Reducer.process_time);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				rd=1;
				System.out.println("\n--------Another Round--------");
				for(int assign_last=1;assign_last<=(last_element%((Reducer.cores)*(Framework.Reducer)));assign_last++){

					if((core<=Reducer.cores) && (map!=-1)){
						Graph.all_reducers[rd].Connection_Reduce.put(map,coflowid);
						System.out.println("\n-----Coflow "+coflowid+" and flow "+assign_last+" is assigned to core "+core+" of reducer "+rd+" by mapper "+ map+"------\n");
						core++;
						if(core>4){
							core=1;
							rd++;
						}
					}

				}		

			});

			Job_len_arr.set(minindex,((Collections.max(Job_len_arr))+1));
			timejob=((System.nanoTime()-timejob)/1000000);
			AvgSJF=AvgSJF+timejob;
			//System.out.println("Time it took to assign "+minindex+" is "+timejob);
			algo=Integer.toString(minindex);
			Timelog tjob=new Timelog(timejob,algo);
			tjob.Enterjoblogs();
			job--;	
		}
		time=((System.nanoTime()-time)/1000000);
		//System.out.println("Time to run SJF in milliseconds "+time);
		algo="SJF";

		AvgSJF=((AvgSJF-timejob)/Framework.number_jobs);

		Timelog avg=new Timelog(AvgSJF,algo);
		avg.Enteraverage();

		Timelog tlog=new Timelog(time,algo);
		tlog.Enterlogs();


	}
}


