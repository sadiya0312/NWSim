package sim.csvs;

import sim.Framework;
import sim.algorithms.EEA;
import sim.csvs.CsvData;
import sim.utils.Constants;

import java.io.IOException;
import java.util.stream.Collectors;

public class CsvHelper {
    public enum CSVFile{
        CPU_SPECS,
        ENERGY_LOGS
    }

    public static CsvData initData(CSVFile csvFile) throws IOException {
        CsvData csvData =null;
        switch (csvFile){
            case CPU_SPECS:
                csvData = extractCPUSpecs();
                break;
            case ENERGY_LOGS:
                csvData = extractEnergyLogs();
                break;

        }
        return csvData;
    }

    private static CsvData extractCPUSpecs() throws IOException {
        CsvData csvData=new CsvData.Builder("C:\\Users\\Sadiya\\Desktop\\Coflow-data\\Data\\cpu_specs.csv")
                .createHeaderRow().buildData().create();
        /*System.out.println(csvData.getHeader());*/
        //csvData.datas.forEach(System.out::println);

        /*System.out.println(csvData.getIndexForColumn("Efficiency"));*/
        System.out.println(csvData.getColumnValue("Efficiency"));
        //Change the getColumnValue each time to get different collumn
        EEA.intlist=csvData.getColumnValue("Efficiency").stream().map(Float::parseFloat).collect(Collectors.toList());

        Framework.gigaflops=csvData.getColumnValue("Efficiency").stream().map(Float::parseFloat).collect(Collectors.toList());

        Framework.intlist=csvData.getColumnValue("Gigaflops").stream().map(Float::parseFloat).collect(Collectors.toList());

        Framework.watts=csvData.getColumnValue("Watts").stream().map(Float::parseFloat).collect(Collectors.toList());


        System.out.println("Int List "+Framework.intlist);
        return csvData;
    }

    private static CsvData extractEnergyLogs() throws IOException {
        CsvData csvData=new CsvData.Builder("C:\\Users\\Sadiya\\Desktop\\Coflow-data\\EnergyLogCSV.csv")
                .createHeaderRow().buildData().create();
        System.out.println(csvData.getColumnValue(Constants.Output.NUMBER_OF_RUNS).stream().map(Integer::parseInt).collect(Collectors.toList()));
        return csvData;
    }

    public static void main(String[] args) throws IOException {
        extractEnergyLogs();
    }
}
