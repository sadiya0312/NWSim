package sim.algorithms;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Clock;
import java.util.*;
import java.util.Map.Entry;

import sim.Engine;
import sim.Event;
import sim.Framework;
import sim.Graph;
import sim.Jobs;
import sim.Mapper;
import sim.Reducer;
import sim.Timelog;
import sim.csvs.*;
import sim.utils.Constants;


public class FIFO implements Algorithm{

	FIFO fifo;

	HashMap<Integer,Long> jobExecutionTime;

	Stack<Integer> prevjob= new Stack<Integer>();

	long[] time_wait;
	long[] time_comp;

	long firstassign,cflowTime,coflowexecute;

	long time=System.nanoTime(),AvgFIFO=0;
	String algo="FIFO";

	int rd=1,core=1,job;
	int coflowid;
	int last_element;
	long sum_flows=0;

	int jobid;

	Map<Integer,ArrayList<Float>> csvReducerMap;
	Map<Integer,Float> csvEfficiency;
	ArrayList<Float> efficiency;
	int key;

	public FIFO() {

		time_comp=null;
		time_wait=null;
		// TODO Auto-generated constructor stub
		jobExecutionTime=new HashMap<>();

		csvReducerMap = new HashMap<>();
		csvEfficiency = new HashMap<>();
		efficiency=new ArrayList<>();
		//AddEfficiency();

	}

	public void AddEfficiency(){
		try {
			CsvHelper.initData(CsvHelper.CSVFile.CPU_SPECS);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(int reducer=0;reducer<((Framework.Reducer));reducer++)
		{
			efficiency.add(Framework.intlist.get(reducer));
			csvEfficiency.put(reducer,Framework.intlist.get(reducer));
			ArrayList<Float> prop=new ArrayList<>();
			prop.add(Framework.gigaflops.get(reducer));
			prop.add(Framework.watts.get(reducer));
			csvReducerMap.put(reducer, prop);
			System.out.println(csvReducerMap);

		}
	}

	public void WriteEnergyCSV(float Avg_energyusage,Float maxcombination,int coflow_max,int flow_max, int trackingNumber)  {

		/*
		 * Write in the CSV file energy usage, average energy, coflow and flows used
		 * maxcombination is reducer number
		 */


		csvEfficiency.forEach((k, v) -> {
			if(v==maxcombination){
				key=k;
				System.out.println(" Index is "+key);
			}
		});

		List<EnergyLogCSV> energyLogs = new ArrayList<>();

		ArrayList<Float> reducerprop=new ArrayList<>();
		reducerprop.addAll(csvReducerMap.get(key));

		energyLogs.add(new EnergyLogCSV(reducerprop,Constants.Algo.FIFO,String.valueOf(1) ,String.valueOf(coflow_max),String.valueOf(flow_max),
				String.valueOf(Avg_energyusage),String.valueOf(maxcombination),String.valueOf(key)));


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


		System.out.println("Coflow max is " + coflow_max);
		System.out.println("Flow max is " + flow_max);
		System.out.println("Reducer is " + maxcombination);


	}





	@Override
	public void runAlgo() throws InterruptedException {
		/*pick first element from the first queue of the first mapper
		 *Find that picked id in Lhm 
		 *From Lhm you will get the properties of that coflow, extract last_element of that coflow
		 *Loop untill all mapper queues are empty
		 *Loop untill Framework.reducer
		 *last_element=last_element-(Reducer.cores*Framework.reducer)
		 *reducer stores coflow id;
		 *if last_element = 0, delete coflow from mapper queue and move to queue 2 of mapper 1 and so on
		 *After loop, Delay untill Reducer.process_time, then start the same loop from queue and mapper where we left untill all mappers are empty
		 */

		AddEfficiency();

		Mapper[] all_mapper=Graph.all_mappers.clone();
		Jobs[] jobarray=Engine.array_jobs.clone();
		
		System.out.println("\n-----------------FIFO Algo----------------\n");


		for(int map=0;map<Framework.Mapper;map++){
			for(int queue=0;queue<Framework.queuesinmapper;queue++){

				int qsize=(all_mapper[map].mapper_queue.size());          	

				for(int length=0;length<=(qsize+1);length++){

					if((all_mapper[map].mapper_queue.get(queue).peek())!=null){
						coflowid=all_mapper[map].mapper_queue.get(queue).poll();

						for (Entry<Integer, Event> val : Engine.Lhm.entrySet()) { 

							if(val.getKey()==coflowid){
								coflowexecute=System.nanoTime();
								cflowTime=(System.nanoTime()/1000000);
								System.out.println("\n"+coflowid+" coflow id is found");
								Event flowprop=val.getValue();
								last_element=flowprop.n_flows;
								//Send coflowid to fuction f if coflowsize of FIFO coflow to log size and ACT and AWT in FIFO log file.
								sum_flows=sum_flows+last_element;
								System.out.println("Total flows:"+last_element);

								int ratio=(last_element/((Reducer.cores)*(Framework.Reducer)));

								System.out.println("Ratio: "+ratio);
								List<FlowSizeCSV> flowsizeCSVS  = new ArrayList<>();
								algo="FIFO";
								while(ratio!=0){
									rd=1;
									core=1;
									System.out.println("\n--------Another Round--------");

									//Update the reducer here

									for(int assign=1;assign<=((Reducer.cores)*(Framework.Reducer));assign++){
										if(core<=Reducer.cores){
											Graph.all_reducers[rd].Connection_Reduce.put(map,coflowid);

											System.out.println("\n-----Coflow "+coflowid+" and flow "+assign+" is assigned to core "+core+" of reducer "+rd+" by mapper "+ map+"------\n");
											int sizeofflow=Engine.flowsize.get(coflowid).get(assign);
											flowsizeCSVS.add(new FlowSizeCSV(algo,String.valueOf(sizeofflow),String.valueOf(rd)));
											String fileName = Constants.File.getFileName(Constants.File.FILE_LINE_FLOWSIZE);
											File file = Constants.File.getFile(Constants.File.FILE_LINE_FLOWSIZE);
											CsvContract.writeToCSV(file,fileName,flowsizeCSVS,CsvContract.CsvHeaderType.FLOWSIZE_LOG);

											float maxcombination=efficiency.get(rd-1);
											int flow=Engine.flowsize.get(coflowid).get(assign);
											WriteEnergyCSV(0,maxcombination,coflowid,flow,1);

											core++;
											if(core>4){
												core=1;
												rd++;
											}
										}

									}
									ratio--;
									Thread.sleep(Reducer.process_time);

								}
								rd=1;
								System.out.println("\n--------Another Round--------");
								List<ParameterLogCSV> parameterLogCSVS  = new ArrayList<>();



								for(int assign_last=1;assign_last<=(last_element%((Reducer.cores)*(Framework.Reducer)));assign_last++){
									if(core<=Reducer.cores){
										Graph.all_reducers[rd].Connection_Reduce.put(map,coflowid);
										System.out.println("\n-----Coflow "+coflowid+" and flow "+assign_last+" is assigned to core "+core+" of reducer "+rd+" by mapper "+ map+"------\n");
										ArrayList<Integer> flows = Engine.flowsize.get(coflowid);

										float maxcombination=efficiency.get(rd-1);
										int flow=Engine.flowsize.get(coflowid).get(assign_last-1);
										WriteEnergyCSV(0,maxcombination,coflowid,flow,1);

										if(Engine.flowsize.get(coflowid).size()>assign_last) {
											int sizeofflow = Engine.flowsize.get(coflowid).get(assign_last);
											flowsizeCSVS.add(new FlowSizeCSV(algo, String.valueOf(sizeofflow), String.valueOf(rd)));
											String fileName = Constants.File.getFileName(Constants.File.FILE_LINE_FLOWSIZE);
											File file = Constants.File.getFile(Constants.File.FILE_LINE_FLOWSIZE);
											CsvContract.writeToCSV(file, fileName, flowsizeCSVS, CsvContract.CsvHeaderType.FLOWSIZE_LOG);


										}
										core++;
										if(core>4){
											core=1;
											rd++;
										}
									}

								}

								coflowexecute=((System.nanoTime()-coflowexecute)/1000000);


								int coflowlength= Collections.max(Engine.flowsize.get(coflowid));
								int size=Engine.flowsize.get(coflowid).size();

								/*
								 * Find job id
								 */
								for(job = 0;job<(Framework.number_jobs);job++)
								{
									jobarray[job].array_task_id.forEach((n)-> {

										if (coflowid == (n)) {
											jobid=job;
										}
									});
								}
								//Store Algo,coflow,length,size,job,job_runtime
								parameterLogCSVS.add(new ParameterLogCSV(algo,String.valueOf(coflowid),String.valueOf(coflowlength),String.valueOf(size),String.valueOf(jobid),String.valueOf(coflowexecute)));
								String fileName = Constants.File.getFileName(Constants.File.FILE_NAME_PARAMETER);
								File file = Constants.File.getFile(Constants.File.FILE_NAME_PARAMETER);
								CsvContract.writeToCSV(file,fileName,parameterLogCSVS,CsvContract.CsvHeaderType.PARAMETER_LOG);


								System.out.println("Coflow execution time:"+coflowexecute);



								/*Find coflow in job
								 * In first occurance place coflow execution time in hashmap jobExecutionTime as per job
								 * In other occurances add coflow execution time to value in hahsmap jobExceutionTime as per job
								 */

								for(job = 0;job<(Framework.number_jobs);job++)
								{
									jobarray[job].array_task_id.forEach((n)->{

										if(coflowid==(n))
										{
											if(time_wait==null){
												//First time ever coflow assigned

												System.out.println("I m in if");

												time_wait=new long[Framework.number_jobs];
												firstassign=cflowTime;

												time_wait[job]=0;
											}

											if(jobExecutionTime.get(job)==null)
											{   
												//First time coflow of this job assigned

												jobExecutionTime.put(job, coflowexecute);
												time_wait[job]=cflowTime-firstassign;

											}
											else
											{   
												jobExecutionTime.put(job,coflowexecute+jobExecutionTime.get(job));
												if(prevjob.peek()!=job){
													for(int add=prevjob.peek();add>=0;add--){
														time_wait[prevjob.get(add)]+=coflowexecute;  
													}
												}
												else{
													prevjob.pop();
												}

											}


										}
									});
									prevjob.push(job);
								}



							}




							/* while(last_element>0){

							if(((Reducer.cores)*(Framework.Reducer))<last_element){
							last_element=last_element-((Reducer.cores)*(Framework.Reducer));
							System.out.println("Flows assign :"+((Reducer.cores)*(Framework.Reducer)));
							System.out.println("Flows left to assign :"+last_element);
							}
							else{

								System.out.println("Flows assign :"+(last_element));
								last_element=0;
								System.out.println("Flows left to assign :"+last_element);
							}


							 */
						}


					}
				}

			}
		}

		time=((System.nanoTime()-time)/1000000);

		System.out.println("Waitiing time\n"+Arrays.toString(time_wait)+"\n");

		jobExecutionTime.forEach((K,V) -> 
		{
			System.out.println("Job "+K + ", Completion time : " + V);
			Timelog tjob=new Timelog(V,String.valueOf(K));
			tjob.Enterjoblogs();
		});
		AvgFIFO=((Arrays.stream(time_wait).sum())/Framework.number_jobs);

		System.out.println("Average waiting time :"+AvgFIFO);
		System.out.println("Completion time for FIFO :"+time);

		Timelog avg=new Timelog(AvgFIFO,algo);
		avg.Enteraverage();


		Timelog tlog=new Timelog(time,algo);
		tlog.Enterlogs();


		return;
		
	}

	public void EnterCSV(){

	}

}
