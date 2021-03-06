package sim.algorithms;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import sim.Engine;
import sim.Event;
import sim.Framework;
import sim.Graph;
import sim.Jobs;
import sim.Reducer;
import sim.Timelog;
import sim.csvs.*;
import sim.utils.Constants;

public class PFS implements Algorithm {

	Jobs[] Jobarray=Engine.array_jobs.clone();
	int job,jobid;
	@SuppressWarnings("unchecked")
	LinkedHashMap<Integer,Event> coflowLhm= (LinkedHashMap<Integer, Event>) Engine.Lhm.clone();
	sim.Mapper[] mappers = Graph.all_mappers.clone();
	long time=System.nanoTime();
	long coflowexecute;
	long AvgPFS=0;
	String algo;
	ArrayList<Long> timejob=new ArrayList<Long>(Collections.nCopies((Framework.number_jobs),0L));
	PFS pfs;
	int flag=0;
	int p,coflow,coflow1,coflow2,Mapper,location,Reducer_location,send_reducer;
	ArrayList<Integer> history_mapper;
	ArrayList<Integer> p_value;
	ArrayList<Integer> s_coflow;
	ArrayList<Integer> s_reducer;
	ArrayList<Integer> check;
	LinkedHashMap<Integer,Integer>weight;
	ArrayList<Integer> reducer_location;

	ArrayList<Float> efficiency;

	Map<Integer,Float> csvReducerMap;
	Jobs[] jobarray=Engine.array_jobs.clone();

	int key;

	public PFS() {
		history_mapper=new ArrayList<>(Collections.nCopies((Framework.Mapper), 0));
		weight=new LinkedHashMap<Integer,Integer>();
		p_value=new ArrayList<>(Collections.nCopies((Framework.Mapper-1), 0));
		s_coflow=new ArrayList<>(Collections.nCopies((Framework.Mapper-1), 0));    //smallest reducer location of smallest coflow    
		s_reducer=new ArrayList<>(Collections.nCopies((Framework.Mapper-1), 0));     
		check=new ArrayList<>(Collections.nCopies((Framework.Mapper-1), 0));         
		reducer_location=new ArrayList<Integer>();                                  

	}

	public void AddEfficiency(){
		try {
			CsvHelper.initData(CsvHelper.CSVFile.CPU_SPECS);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(int reducer=0;reducer<((Framework.Reducer));reducer++)
		{
			//efficiency.add(Framework.intlist.get(reducer));
			//csvReducerMap.put(reducer, Framework.intlist.get(reducer));
		}
	}

	public void WriteEnergyCSV(float Avg_energyusage,Float maxcombination,int coflow_max,int flow_max, int trackingNumber)  {

		/*
		 * Write in the CSV file energy usage, average energy, coflow and flows used
		 */

		csvReducerMap.forEach((k, v) -> {
			if(v==maxcombination){
				key=k;
				System.out.println(" Index is "+key);
			}
		});

		List<EnergyLogCSV> energyLogs = new ArrayList<>();

		//energyLogs.add(new EnergyLogCSV(Constants.Algo.PFS,String.valueOf(trackingNumber) ,String.valueOf(coflow_max),String.valueOf(flow_max),
				//String.valueOf(Avg_energyusage),String.valueOf(maxcombination),String.valueOf(key)));


		if (!energyLogs.isEmpty()) {
			String fileName = Constants.File.getFileName(Constants.File.FILE_NAME_ENERGY);
			File file = Constants.File.getFile(Constants.File.FILE_NAME_ENERGY);
			boolean append = Constants.File.isFileExists(file);
			StringJoiner csvData = new StringJoiner(",\n");
			if(!append)
				csvData.add(EnergyLogCSV.getHeaderCSVWithNoOfRuns());
			Path path = Paths.get(fileName.replace("\\", "/"));
			energyLogs.stream().map(el -> el.toCsvStringWithNoOfRuns()).forEach(csvData::add);
			try {
				Files.write(path, (csvData.toString()+System.lineSeparator()).getBytes(StandardCharsets.UTF_8), file.exists()? StandardOpenOption.APPEND : StandardOpenOption.CREATE_NEW);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  //Append mode

		}

	}

	@SuppressWarnings("unchecked")
	public void runAlgo() throws InterruptedException {
		//Weight is based on the location of the reducer
		//if reducers in a coflow not = 1 then 
		//Find which mapper coflow belong
		//find the smallest reducer location (subtract all reducer locations with the mapper location)
		//Increament history_mapper as per the mapper

		//P=Weight/(history_mapper)
		// Find P for two mappers (1&2) 
		// if P1>P2 then send P1 to reducer

        AddEfficiency();

		//Use Loop here 	
		for(int run=0;run<=((Framework.Mapper)*(Framework.queuesinmapper)*(Framework.Queue_length));run++){
			coflowexecute=System.nanoTime();
			coflow_mapper();
			if(run==0){
				s_coflow.remove(s_coflow.size()-1);
				s_reducer.remove(s_reducer.size()-1);
				p_value.remove(p_value.size()-1);
			}
			System.out.println(s_coflow);

			//s_reducer.removeIf(Objects::isNull);

			s_reducer.replaceAll(t -> Objects.isNull(t) ? 0 : t);

			int smallest_coflow=Collections.min(s_reducer);
			send_reducer=Collections.max(p_value);

			System.out.println("P values "+p_value);

			int index=(s_reducer.indexOf(smallest_coflow));
			
			//int indexp=(p_value.indexOf(send_reducer));
			//if(index!=indexp){index=indexp;}
			
			
			if(smallest_coflow==0){
				System.out.println("Smallest coflow is "+0);

				//s_coflow.add(index,(int) Math.pow(10000,1000));	
				s_coflow.remove(index);
				s_reducer.remove(index);
				
				s_coflow.add(index,(int) Math.pow(10000,1000));
				s_reducer.add(index,(int) Math.pow(10000,1000));
				
				smallest_coflow=Collections.min(s_reducer);
				index=(s_reducer.indexOf(smallest_coflow));
			}
            
			int coflow=(s_coflow.get(index));

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

			System.out.println("\nSending "+coflow+ " coflow "+"of mapper "+index+ " with p value "+send_reducer+" and history: "/*+leastusedmapper*/);

			removejobelement(coflow);

			for (Entry<Integer, Event> val2 : coflowLhm.entrySet()) {

				if(coflow==val2.getKey()){

					int n_flows=val2.getValue().n_flows;

					reducer_location=(ArrayList<Integer>) (val2.getValue().reducers_location.clone());

					int Reducer_send=reducer_location.get(0);
					reducer_location.remove(0);
					List<FlowSizeCSV> flowsizeCSVS  = new ArrayList<>();
					for(int assign=1;assign<=n_flows;)
					{   int same=1;
					while(same<=Reducer.cores && assign<=n_flows){
						System.out.println("Send "+assign +" flow to reducer "+Reducer_send);



						if(Engine.flowsize.get(coflow).size()>assign) {
							int sizeofflow = Engine.flowsize.get(coflow).get(assign);
							flowsizeCSVS.add(new FlowSizeCSV(algo, String.valueOf(sizeofflow), String.valueOf(Reducer_send)));
							String fileName = Constants.File.getFileName(Constants.File.FILE_LINE_FLOWSIZE);
							File file = Constants.File.getFile(Constants.File.FILE_LINE_FLOWSIZE);
							CsvContract.writeToCSV(file, fileName, flowsizeCSVS, CsvContract.CsvHeaderType.FLOWSIZE_LOG);
						}

						//WriteEnergyCSV(float Avg_energyusage,Float maxcombination,coflow,assign, int trackingNumber);
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

			List<ParameterLogCSV> parameterLogCSVS  = new ArrayList<>();

			coflowexecute=((System.nanoTime()-coflowexecute)/1000000);


			int coflowlength= Collections.max(Engine.flowsize.get(coflow));
			int size=Engine.flowsize.get(coflow).size();

			/*
			 * Find job id
			 */
			for(job = 0;job<(Framework.number_jobs);job++)
			{
				if(jobarray[job].array_task_id!=null) {
					jobarray[job].array_task_id.forEach((n) -> {

						if (coflow == (n)) {
							jobid = job;
						}
					});
				}
			}

			String algo="PFS";
			//Store Algo,coflow,length,size,job,job_runtime
			parameterLogCSVS.add(new ParameterLogCSV(algo,String.valueOf(coflow),String.valueOf(coflowlength),String.valueOf(size),String.valueOf(jobid),String.valueOf(coflowexecute)));
			String fileName = Constants.File.getFileName(Constants.File.FILE_NAME_PARAMETER);
			File file = Constants.File.getFile(Constants.File.FILE_NAME_PARAMETER);
			CsvContract.writeToCSV(file,fileName,parameterLogCSVS,CsvContract.CsvHeaderType.PARAMETER_LOG);


			System.out.println("Coflow execution time:"+coflowexecute);


			//Delete this coflow from the mapper queue
			for(int queue=0;queue<Framework.queuesinmapper;queue++)//Queue loop
			{   
				if(mappers[index].mapper_queue.get(queue).isEmpty()){}
				else{
					if(mappers[index].mapper_queue.get(queue).peek()==coflow){
						mappers[index].mapper_queue.get(queue).remove();
						s_coflow.clear();
						s_reducer.clear();
						check.clear();
						System.out.println("I have deleted coflow "+coflow);
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
			System.out.println("S Reducer "+ s_reducer);
			System.out.println("Coflow Lhm " +coflowLhm);
			System.out.println("Mapper "+ Mapper);
			System.out.println("CoflowLhm object "+coflowLhm.get(Mapper));
			if(Mapper!=0){
				s_reducer.add(map,coflowLhm.get(Mapper).reducers_req);
			}
			else{
				s_reducer.add(map,null);
			}
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

		for (Entry<Integer, Event> val : coflowLhm.entrySet()) {

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
		ArrayList<Integer> req_red=new ArrayList<>();
		coflowLhm.forEach((k,v)->{req_red.add(v.reducers_req);});
		for(int queue=0;queue<Framework.queuesinmapper;queue++)//Queue loop
		{   
			if(queue==0){
				//Queue 1
				if(mappers[mapper].mapper_queue.get(queue).isEmpty()){
					coflow1=0; // QUeue is empty coflow is zero
				}
				else{
					coflow1=mappers[mapper].mapper_queue.get(queue).peek(); // Queue isnot empty getting mapper which cannot be zero
				}

				//Queue 2   
				if((queue+1)<Framework.queuesinmapper){
					if(mappers[mapper].mapper_queue.get(queue+1).isEmpty()){
						coflow2=0;
					}
					else{
						coflow2=mappers[mapper].mapper_queue.get(queue+1).peek();
					}

					//compare

					int coflow1_flow=Collections.max(req_red)+1;
					if(coflow1!=0){
						coflow1_flow=coflowLhm.get(coflow1).reducers_req;	
					}
					int coflow2_flow=Collections.max(req_red)+1;;
					if(coflow2!=0){
						coflow2_flow=coflowLhm.get(coflow2).reducers_req;
					}
					if(coflow1_flow<coflow2_flow/*reducers coflow1<reducers coflow2*/)
					{
						coflow=coflow1;
					}
					else{coflow=coflow2;}
					//Search coflow1 and coflow2 and return coflow with the least reducers
				}	
			}
			else{
				if((queue+1)<Framework.queuesinmapper){
					if(mappers[mapper].mapper_queue.get(queue+1).isEmpty()){
						coflow2=0;
					}
					else{
						coflow2=mappers[mapper].mapper_queue.get(queue+1).peek();
					}

					//compare
					int coflow1_flow=Collections.max(req_red)+1;
					if(coflow1!=0){
						coflow1_flow=coflowLhm.get(coflow).reducers_req;
					}
					int coflow2_flow=Collections.max(req_red)+1;
					if(coflow2!=0){
						coflow2_flow=coflowLhm.get(coflow2).reducers_req;
					}
					if(coflow1_flow<coflow2_flow/*reducers coflow1<reducers coflow2*/)
					{
						// coflow=coflow;
					}
					else{coflow=coflow2;}
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
