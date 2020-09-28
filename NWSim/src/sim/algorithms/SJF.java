package sim.algorithms;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import sim.*;
import sim.csvs.*;
import sim.utils.Constants;

public class SJF implements Algorithm {


	long time=System.nanoTime();
	long coflowexecute;
	long timejob,AvgSJF=0;
	String algo="SJF";
	SJF sjf;
	Event flowprop;
	int last_element,coflowid,i,jobid,jobfind;
	int rd=1,core=1,map=-1;
	long average_process,np,job_length=0;
	long sum_flows=0;
	static ArrayList<Integer> Job_len_arr =new ArrayList<Integer>() ;
	Jobs[] jobarray=Engine.array_jobs.clone();

	Map<Integer,ArrayList<Float>> csvReducerMap;
	Map<Integer,Float> csvEfficiency;
	ArrayList<Float> efficiency;
	int key;
	float maxcombination;
	int flow;

	public SJF() {

		csvReducerMap = new HashMap<>();
		csvEfficiency = new HashMap<>();
		efficiency=new ArrayList<>();

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

		energyLogs.add(new EnergyLogCSV(reducerprop,Constants.Algo.SJF,String.valueOf(1) ,String.valueOf(coflow_max),String.valueOf(flow_max),
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
        AddEfficiency();
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
					coflowexecute=System.nanoTime();
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
				List<FlowSizeCSV> flowsizeCSVS  = new ArrayList<>();
				algo="SJF";
				while(ratio!=0){

					rd=1;
					core=1;
					System.out.println("\n--------Another Round--------");

					//Update the reducer here

					for(int assign=1;assign<=((Reducer.cores)*(Framework.Reducer));assign++){

						if((core<=Reducer.cores) && (map!=-1)){
							Graph.all_reducers[rd].Connection_Reduce.put(map,coflowid);
							System.out.println("\n-----Coflow "+coflowid+" and flow "+assign+" is assigned to core "+core+" of reducer "+rd+" by mapper "+ map+"------\n");
							int sizeofflow=Engine.flowsize.get(coflowid).get(assign);
							flowsizeCSVS.add(new FlowSizeCSV(algo,String.valueOf(sizeofflow),String.valueOf(rd)));
							String fileName = Constants.File.getFileName(Constants.File.FILE_LINE_FLOWSIZE);
							File file = Constants.File.getFile(Constants.File.FILE_LINE_FLOWSIZE);
							CsvContract.writeToCSV(file,fileName,flowsizeCSVS,CsvContract.CsvHeaderType.FLOWSIZE_LOG);
							core++;

							maxcombination=efficiency.get(rd-1);
							flow=Engine.flowsize.get(coflowid).get(assign);
							WriteEnergyCSV(0,maxcombination,coflowid,flow,1);


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

						maxcombination=efficiency.get(rd-1);
						flow=Engine.flowsize.get(coflowid).get(assign_last-1);
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

				List<ParameterLogCSV> parameterLogCSVS  = new ArrayList<>();
				coflowexecute=((System.nanoTime()-coflowexecute)/1000000);


				int coflowlength= Collections.max(Engine.flowsize.get(coflowid));
				int size=Engine.flowsize.get(coflowid).size();

				/*
				 * Find job id
				 */
				for(jobfind = 0;jobfind<(Framework.number_jobs);jobfind++)
				{
					jobarray[jobfind].array_task_id.forEach((nfind)-> {

						if (coflowid == (nfind)) {
							jobid=jobfind;
						}
					});
				}
				algo="SJF";
				//Store Algo,coflow,length,size,job,job_runtime
				parameterLogCSVS.add(new ParameterLogCSV(algo,String.valueOf(coflowid),String.valueOf(coflowlength),String.valueOf(size),String.valueOf(jobid),String.valueOf(coflowexecute)));
				String fileName = Constants.File.getFileName(Constants.File.FILE_NAME_PARAMETER);
				File file = Constants.File.getFile(Constants.File.FILE_NAME_PARAMETER);
				CsvContract.writeToCSV(file,fileName,parameterLogCSVS,CsvContract.CsvHeaderType.PARAMETER_LOG);

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


