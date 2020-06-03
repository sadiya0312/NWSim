package sim;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Map.Entry;

public class Algomapper {

	int job;
	ArrayList<Integer> incomp;

	public Algomapper() {
		// TODO Auto-generated constructor stub
		incomp=new ArrayList<Integer>();
	}

	public void Initial_Algo(Graph graph) throws IOException{

		System.out.println("\n-----------Round Robin entery of Coflows in mapper queues----------------\n");
		Set <Entry<Integer,Event>> entry=Engine.Lhm.entrySet();

		Iterator<Entry<Integer, Event>> itr=entry.iterator();

		//Enter values in queue in round robin fashion

		for(int i=0;i<Framework.Mapper;i++)   // Mapper loop
		{


			for(int j=0;j<Framework.queuesinmapper;j++)  // Queue loop
			{                 	

				if (Graph.all_mappers[i].mapper_queue.get(j).size()<Framework.Queue_length)
				{
					//System.out.println("Current Mapper Queue Size:"+graph.all_mappers[i].mapper_queue.get(j).size());
					//System.out.println("Framework.Queue_length:"+Framework.Queue_length);
					if(itr.hasNext()) //Pick event
					{
						Entry<Integer,Event> entryInLoop=(Entry<Integer, Event>)itr.next();  

						Graph.all_mappers[i].mapper_queue.get(j).add(entryInLoop.getKey());
						System.out.println("Entered event "+entryInLoop.getKey()+" in Queue"+j+" of mapper"+i);     					
					}
				}

			}

			if(Graph.all_mappers[(Framework.Mapper)-1].mapper_queue.get((Framework.queuesinmapper)-1).size()==Framework.Queue_length)
			{
				//Log all values that are not send to queue

				while(itr.hasNext())
				{
					Entry<Integer, Event> left =itr.next();
					GenerateLog log=new GenerateLog(left);	

					//GenerateLog log=new GenerateLog(itr.next());
					log.Enterlogs();
					System.out.println();	

					int key=left.getKey();
					job=(Framework.number_jobs)-1;

					while(job>=0){
						Engine.array_jobs[job].array_task_id.forEach((n)->{
							//Enter these job id in an array
							incomp.add(key);
							if((n)==key){ 
								Incompletejobs incomplete=new Incompletejobs(job);
								incomplete.Enterlogs();
							}		
						});
						job--;

					}

				}     
				break;

			}

			if(i>=Framework.Mapper-1 && itr.hasNext())
			{
				i=-1;
			}

		}	


		for(int k=0;k<Framework.Mapper;k++){

			Iterator<PriorityQueue<Integer>>itrm =Graph.all_mappers[k].mapper_queue.iterator();
			for(int j=0;j<Framework.queuesinmapper;j++)  // Queue loop
			{ 


				while (itrm.hasNext())
				{
					System.out.println("\nSize of queue is :"+Graph.all_mappers[k].mapper_queue.get(j).size() +" of mapper:"+k);
					System.out.println(itrm.next()+"\n");

				}
			}

		}

	}	

}
