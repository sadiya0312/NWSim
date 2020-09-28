package sim.csvs;

import sim.algorithms.Algorithm;
import sim.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.StringJoiner;

public interface CsvContract extends Constants.Output, Serializable {

    enum CsvHeaderType{
        ENERGY,
        ENERGY_NO_OF_RUNS,
        PARAMETER_LOG,
        FLOWSIZE_LOG,
        TIME;

    }

    String toCsvString();
    static  String getHeaderCSV(CsvHeaderType type){
        switch (type){
            case TIME:
                return getTimeHeader();
            case ENERGY:
                return getEnergyHeader();
            case ENERGY_NO_OF_RUNS:
                return getEnergyNoOfRunsHeader();
            case PARAMETER_LOG:
                return getParameterLogHeader();
            case FLOWSIZE_LOG:
                return getFlowSizeHeader();
            default:
                return null;
        }
    }

     static String getParameterLogHeader() {
        StringJoiner stringJoiner = new StringJoiner(",");

        stringJoiner.add(ALGORITHM);
        stringJoiner.add(COFLOW);
        stringJoiner.add(LENGTH);
        stringJoiner.add(SIZE);
        stringJoiner.add(JOB);
        stringJoiner.add(COFLOW_RUNTIME);
        return stringJoiner.toString();
    }

    static String getEnergyNoOfRunsHeader() {
        StringJoiner stringJoiner = new StringJoiner(",");
        stringJoiner.add(TIMESTAMP);
        stringJoiner.add(ALGORITHM);
        stringJoiner.add(COFLOW);
        stringJoiner.add(FLOW);
        stringJoiner.add(ENERGY);
        stringJoiner.add(EFFICIENCY);
        stringJoiner.add(REDUCER);
        stringJoiner.add(NUMBER_OF_RUNS);
        stringJoiner.add(GIGAFLOPS);
        stringJoiner.add(WATTS);

        return stringJoiner.toString();
    }

    static String getTimeHeader() {
        StringJoiner stringJoiner = new StringJoiner(",");
        stringJoiner.add(Constants.Output.ALGORITHM);
        stringJoiner.add(Constants.Output.AWT);
        stringJoiner.add(Constants.Output.ACT);
        stringJoiner.add(Constants.Output.DATE_TIME);
        return stringJoiner.toString();
    }

    static String getEnergyHeader() {
        StringJoiner stringJoiner = new StringJoiner(",");
        stringJoiner.add(TIMESTAMP);
        stringJoiner.add(ALGORITHM);
        stringJoiner.add(COFLOW);
        stringJoiner.add(FLOW);
        stringJoiner.add(ENERGY);
        stringJoiner.add(EFFICIENCY);
        stringJoiner.add(REDUCER);
        return stringJoiner.toString();
    }

    static String getFlowSizeHeader(){
        StringJoiner stringJoiner = new StringJoiner(",");

        stringJoiner.add(ALGORITHM);
        stringJoiner.add(FLOW_SIZE);
        stringJoiner.add(REDUCER);
        return stringJoiner.toString();
    }



    static <T extends CsvContract> void writeToCSV(File file, String fileName,  List<T> csvDatas, CsvHeaderType headerType){
        StringJoiner csvData = new StringJoiner(",\n");
        if(!Constants.File.isFileExists(file))
            csvData.add(CsvContract.getHeaderCSV(headerType));
        Path path = Paths.get(fileName.replace("\\", "/"));
        csvDatas.stream().map(CsvContract::toCsvString).forEach(csvData::add);
        try {
            Files.write(path, (csvData.toString()+System.lineSeparator()).getBytes(StandardCharsets.UTF_8), file.exists()? StandardOpenOption.APPEND : StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  //Append mode
    }
}
