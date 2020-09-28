package sim.csvs;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.StringJoiner;

public interface CsvContractNoOfRuns extends CsvContract {
    String toCsvNoOfRunsString();

    static <T extends CsvContractNoOfRuns> void writeToCSV(File file, String fileName, boolean isAppendable, List<T> csvDatas, CsvHeaderType headerType){
        StringJoiner csvData = new StringJoiner(",\n");
        if(!isAppendable)
            csvData.add(CsvContract.getHeaderCSV(headerType));
        Path path = Paths.get(fileName.replace("\\", "/"));
        csvDatas.stream().map(CsvContractNoOfRuns::toCsvNoOfRunsString).forEach(csvData::add);
        try {
            Files.write(path, (csvData.toString()+System.lineSeparator()).getBytes(StandardCharsets.UTF_8), file.exists()? StandardOpenOption.APPEND : StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  //Append mode
    }
}
