package sim.csvs;

import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

import sim.utils.Constants;

public class ParameterLogCSV implements CsvContract{

    private static final DateTimeFormatter DATE_TIME_FORMATER = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);

    private String algo;
    private String coflow;
    private String length;
    private String size;
    private String job;
    private String coflowexecute;


    /**
     * @param algo
     * @param coflow
     * @param length
     * @param size
     * @param Job
     * @param coflowexecute
     */
    public ParameterLogCSV(String algo, String coflow,String length, String size, String Job, String coflowexecute) {
        this.algo = algo;
        this.coflow = coflow;
        this.length=length;
        this.size = size;
        this.job = Job;
        this.coflowexecute = coflowexecute;
    }

    public ParameterLogCSV() {

    }

    public String getAlgo() {
        return algo;
    }

    public void setAlgo(String algo) {
        this.algo = algo;
    }

    public String getCoflow() {
        return coflow;
    }

    public void setCoflow(String coflow) {
        this.coflow = coflow;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getJobRuntime() {
        return coflowexecute;
    }

    public void setJobRuntime(String jobRuntime) {
        this.coflowexecute = coflowexecute;
    }

    @Override
    public String toString() {
        return "EnergyLogCSV{" +
                "algo='" + algo + '\'' +
                ", coflow_max='" + coflow + '\'' +
                ", flow_max='" + length + '\'' +
                ", avg_energyusage='" + size + '\'' +
                ", efficiency='" + job + '\'' +
                ", reducer='" + coflowexecute + '\'' +
                '}';
    }

    @Override
    public String toCsvString() {
        StringJoiner stringJoiner = new StringJoiner(",");
        stringJoiner.add(algo);
        stringJoiner.add(coflow);
        stringJoiner.add(length);
        stringJoiner.add(size);
        stringJoiner.add(job);
        stringJoiner.add(coflowexecute);
        return stringJoiner.toString();
    }
}
