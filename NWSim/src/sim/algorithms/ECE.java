package sim.algorithms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Clock;
import java.util.*;

import sim.*;
import sim.csvs.CsvContract;
import sim.csvs.CsvData;
import sim.csvs.CsvHelper;
import sim.csvs.EnergyLogCSV;
import sim.exceptions.CsvException;
import sim.utils.Constants;
import sim.utils.Utils;

public class ECE implements Algorithm,Constants.File{

    ECE ECE;

    LinkedHashMap<Integer,Long> avgwaiting = new LinkedHashMap<>();
    long prevtime=0L;
    long time=System.nanoTime();
    long AvgECE=0;
    String algo;
    /*
     * Take the highest coflow from all mappers
     * Assign it to the highest efficiency
     */
    long jobsum=0L;
    int ncoflow=1;
    LinkedHashMap<Integer, Long> jobavg=new LinkedHashMap<Integer,Long>();

    LinkedHashMap<Integer, ArrayList<Integer>> flowsize;
    ArrayList<Integer> flowsizesum;
    ArrayList<Integer>coflowsize;
    int coflow_max, temp, n, r,reducer, flow_max,key;
    float energyusage, Avg_energyusage;
    Map<Integer,ArrayList<Float>> csvReducerMap;
    Map<Integer,Float> csvEfficiency;
    ArrayList<Float>efficiency;
    ArrayList<Integer> mapper_max;
    ArrayList<Integer> exchange;
    ArrayList<Integer> check;


    ArrayList<Integer> queue_max;
    List<Float> efficiencylist;
    Reducer[] inputreducer;
    Float maxcombination;
    List<Integer> index;
    Mapper[] mappers;
    Float lt, ut;


    public ECE() throws FileNotFoundException, CsvException, IOException {
        //Constructor
        efficiency=new ArrayList<Float>();
        exchange=new ArrayList<Integer>();
        check=new ArrayList<Integer>();
        //efficiency.add((float) 44.15584416);
        //efficiency.add((float) 49.76190476);
        index = new ArrayList<Integer>();
        csvReducerMap = new HashMap<>();
        csvEfficiency = new HashMap<>();
        flowsizesum=new ArrayList<Integer>();
        coflowsize=new ArrayList<Integer>(Framework.id);
        CloneAll();
    }

    @Override
    public void runAlgo() throws InterruptedException, IOException {


        ArrayList<Long> timejob=new ArrayList<Long>(Collections.nCopies((Framework.number_jobs),0L));
        AddEfficiency();
        TaskAssignment();
        String algo;
        algo="ECE";
        Timelog avg1=new Timelog(AvgECE,algo);
        avg1.Enteraverage();
        time=((System.nanoTime()-time)/1000000);
        Timelog tlog=new Timelog(time,algo);
        tlog.Enterlogs();

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
        }
    }

    @SuppressWarnings("unchecked")
    public void CloneAll()
    {
        /*
         * Clone mapper array and flowsize
         */
        mappers = Graph.all_mappers.clone();
        inputreducer = Graph.all_reducers.clone();
        flowsize = (LinkedHashMap<Integer, ArrayList<Integer>>) Engine.flowsize.clone();
        flowsizesum.add(0,0);
        flowsize.forEach((key,value)->{
            flowsizesum.add(key,value.stream().mapToInt(Integer::intValue).sum());
        });
        System.out.println("Flowsizesum "+flowsizesum);
        System.out.println("This is the print "+Collections.min(flowsize.get(1)));
    }

    public void CalculateArray(){
        /*
         * Calculate and store maximum of all mappers in an array
         */
        queue_max = new ArrayList<>();
        mapper_max = new ArrayList<>();
        coflowsize.add(0,0);

        /*
         * Create an array that contains every single co flow of each mapper
         * Find the greatest from that
         */


        for (int map = 0; map < mappers.length; map++) { // loop for mapper
            if(mappers[map]!=null){
                for (int queue = 0; queue < mappers[map].mapper_queue.size(); queue++) { // loop for queue          
                    PriorityQueue<Integer> completequeue = mappers[map].mapper_queue.get(queue);
                    if(Collections.disjoint(check, completequeue)) {
                        check.addAll(completequeue);
                        exchange.addAll(completequeue);
                    }
                    if(completequeue.size() !=0) {
                        /*
                         * Create map <coflow,size>
                         * Pick the each coflow from completequeue and find it in flowsize and sum up all flowsize
                         * store coflow and flowsize sum in <coflow,size>
                         * Pick the maximum size
                         */
                        queue_max.add(Collections.max(completequeue));

                    }
                }

                if(queue_max.size() !=0)
                {
                    //mapper_max.add(Collections.max(queue_max));
                    //queue_max.remove(Collections.max(queue_max));

                    //coflowsize.set(coflowsize.indexOf(Collections.max(coflowsize)),0);
                }
            }
            //System.out.println("These are mappers highest coflows " + mapper_max);
        }
        System.out.println("Exchange "+ exchange);
        if(!exchange.isEmpty()) {

            /*
             * Get the coflow with greatest size
             * sum the all the flowsizes of each coflow
             */
            int highest=Collections.max(flowsizesum);
            mapper_max.add(flowsizesum.indexOf(highest));
            flowsizesum.set(flowsizesum.indexOf(highest),0);
            //flowsizesum.remove(flowsizesum.indexOf(highest));
            exchange.remove(exchange);
        }
        else{
            System.exit(0);
        }

    }

    public void RemoveFlowsize(int flow_max, int coflow_max){
        /*
         * Remove elements from Flowsize
         */

        flowsize.get(coflow_max).remove((Integer) flow_max);

    }

    public void RemoveCoflowMappers(int coflow_max){

        /*
         * Remove coflow from the mapper array
         */
        if(mapper_max.indexOf(coflow_max)>=0){
            mappers[mapper_max.indexOf(coflow_max)].mapper_queue.stream().filter(q -> q.contains(coflow_max))
                    .findAny().ifPresent(q -> {
                q.remove(coflow_max);
            });
        }
    }

    public void RemoveMapper(){
        /*
         * Find all queues of mapper are empty and then remove mapper from mappers
         */

        for(n=0;n<mappers.length;n++){
            if(mappers[n]!=null){
                for(r=0;r<mappers[n].mapper_queue.size();r++){
                    if(mappers[n].mapper_queue.get(r).isEmpty()){
                        mappers[n].mapper_queue.remove(r);
                    }
                    else{


                    }
                }
                if(mappers[n].mapper_queue.isEmpty()){
                    mappers[n]=null;
                }
            }

        }
    }

    public void SetMapperMax(int coflow_max){
        /*
         * Set the value of empty mapper as 0 in mapper max
         */
        int in = mapper_max.indexOf(coflow_max);
        if(in>=0){
            mapper_max.set(in, 0);
        }
    }

    public void WriteEnergyCSV(float Avg_energyusage,Float maxcombination,int coflow_max,int flow_max, int trackingNumber)  {

        /*
         * Write in the CSV file energy usage, average energy, coflow and flows used
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

        energyLogs.add(new EnergyLogCSV(reducerprop,Constants.Algo.ECE,String.valueOf(trackingNumber) ,String.valueOf(coflow_max),String.valueOf(flow_max),
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

    public void TaskAssignment() throws InterruptedException{
        int trackingNumber = 1;
        try {
            File file = Constants.File.getFile(Constants.File.FILE_NAME_ENERGY);
            if(file.exists()) {
                CsvData energyLogCSVData = CsvHelper.initData(CsvHelper.CSVFile.ENERGY_LOGS);
                trackingNumber = Integer.parseInt(energyLogCSVData.getColumnValue(Constants.Output.NUMBER_OF_RUNS).stream()
                        .reduce((first, second) -> second).orElse("0")) + 1;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        CalculateArray();
        while(!isAllCflowEmpty()) {
            while (efficiency.size() != 0 && mapper_max.size() != 0) {
                maxcombination = Collections.max(efficiency);
                coflow_max = Collections.max(mapper_max);

                for (int assign = 0; assign < Reducer.cores; assign++) {
                    if (isAllCflowEmpty()) {
                        System.out.println("Breaking the loop");
                        break;
                    }//endif(isAllcflowempty)
                    if(mapper_max.get(0)==0){
                        System.exit(0);
                    }//end if (mapper_max.get(0))
                     if (flowsize.get(coflow_max).isEmpty()) {

                        RemoveCoflowMappers(coflow_max);
                        SetMapperMax(coflow_max);
                        if (Collections.frequency(mapper_max, 0) == mapper_max.size() && !isAllCflowEmpty()) {
                            CalculateArray();
                        }//endif Collections.frequency(mapper_max, 0)
                        if (mapper_max.size() != 0) {
                            System.out.println("mapper_max " + mapper_max);
                            coflow_max = Collections.max(mapper_max);
                        }//endif(mapper_max.size() != 0)
                    }//endif(isempty)
                    if (mapper_max.size() != 0 && flowsize.get(coflow_max) != null && !flowsize.get(coflow_max).isEmpty()) {
                        System.out.println("coflow "+coflow_max);
                        System.out.println(flowsize.get(coflow_max));
                        flow_max = Collections.max(flowsize.get(coflow_max));

                        System.out.println(coflow_max + " has " + flow_max + " which is assigned to " + maxcombination);

                        /*
                         * Calculate energy usage and average energy
                         * maxcombination is efficiency
                         * flow max is the flow
                         */

                        energyusage = (maxcombination) * (flow_max);
                        Avg_energyusage = Avg_energyusage + energyusage;

                        WriteEnergyCSV(Avg_energyusage, maxcombination, coflow_max, flow_max, trackingNumber);

                        RemoveFlowsize(flow_max, coflow_max);
                    }//end if(mapper_max.size)
                }//endfor(assign)

                avgwaiting.put(coflow_max, (prevtime));
                prevtime = (System.nanoTime() - prevtime)/1000000;

                AvgwaitingTime();

                if (flowsize.get(coflow_max) != null && flowsize.get(coflow_max).isEmpty() && mapper_max.size() != 0) {

                    RemoveCoflowMappers(coflow_max);
                    SetMapperMax(coflow_max);
                    coflow_max = Collections.max(mapper_max);

                }//endif(flowsize.get(coflow_max))


                // System.out.println(Collections.frequency(mapper_max, 0));
                // System.out.println(mapper_max.size());

                if (Collections.frequency(mapper_max, 0) == mapper_max.size()) {
                    CalculateArray();

                }//endif(collections.frequency)
                if (mapper_max.size() != 0) {
                    System.out.println("Last bit " + mapper_max);
                    efficiency.remove(efficiency.indexOf(maxcombination));
                }//endif(mapper_max.size)

            }//endwhile(efficiency.size)

            Thread.sleep(100);

            AddEfficiency();

        } //EndWhile (AllCflow)
        System.out.println("Finshed");
    }

    public void AvgwaitingTime(){
        /*
         * Calculate waiting time of each coflow
         */

        long sum=0L;
        for (Long val : avgwaiting.values()){
            sum += val;
        }

        AvgECE=(sum/1000000)/avgwaiting.size();
    }

    public boolean isAllCflowEmpty()
    {
        boolean result=true;
        for (int map = 0; map < mappers.length; map++) { // loop for mapper

            for (int queue = 0; queue < mappers[map].mapper_queue.size(); queue++) { // loop for queue          
                if(! mappers[map].mapper_queue.get(queue).isEmpty())
                {
                    result=false;
                    break;

                }
            }

        }
        if(result)
            System.out.println("All cflow Is Empty");
        return result;


    }

}


