package sim.csvs;

import sim.Reducer;
import sim.utils.Constants;

import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

public class FlowSizeCSV implements CsvContract {
    private static final DateTimeFormatter DATE_TIME_FORMATER = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);

    private String algo;
    private String flowsize;
    private String reducer;

    /**
     * @param algo
     * @param flowsize
     * @param Reducer
     */
    public FlowSizeCSV(String algo, String flowsize,String Reducer) {
        this.algo = algo;
        this.flowsize = flowsize;
        this.reducer = Reducer;
    }

    public FlowSizeCSV() {

    }

    public String getAlgo() {
        return algo;
    }

    public void setAlgo(String algo) {
        this.algo = algo;
    }

    public String getFlowsize() {
        return flowsize;
    }

    public void setFlowsize(String flowsize) { this.flowsize = flowsize; }

    public String getLength() {
        return reducer;
    }

    public void setLength(String length) {
        this.reducer = reducer;
    }

    @Override
    public String toString() {
        return "EnergyLogCSV{" +
                "algo='" + algo + '\'' +
                ", flowsize='" + flowsize + '\'' +
                ", reducer='" + reducer + '\'' +
                '}';
    }


    public String toCsvString() {
        StringJoiner stringJoiner = new StringJoiner(",");
        stringJoiner.add(algo);
        stringJoiner.add(flowsize);
        stringJoiner.add(reducer);
        return stringJoiner.toString();
    }
}
