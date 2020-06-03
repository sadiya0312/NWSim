package sim;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Seed {
	Seed seedclass;

	HSSFRow row;

	HSSFCell cell;

	int i=0;

	public Seed() {
		// TODO Auto-generated constructor stub
	}

	public void writeDataLineByLine() throws InvalidFormatException 
	{ 
		// first create file object for file placed at location 
		// specified by filepath 
		//File file = new File("C:\\Users\\Sadiya\\Desktop\\Coflow-data\\Seed.xlsx"); 
		try { 
			// create FileWriter object with file as parameter 
			//FileOutputStream out = new FileOutputStream(file);
			//FileInputStream input = new FileInputStream(file); 

			/*FileOutputStream fileOut = new FileOutputStream("C:\\Users\\Sadiya\\Desktop\\Coflow-data\\Seed.xls");
	        FileOutputStream fileOut2 = new FileOutputStream("C:\\Users\\Sadiya\\Desktop\\Coflow-data\\Seed.xls");
	        HSSFWorkbook wb2 = new HSSFWorkbook();
	        HSSFSheet sheet2 = wb2.createSheet("Seeds");
	        HSSFRow rowhead2 = sheet2.createRow((short)0);
			rowhead2.createCell((int) 0).setCellValue("Seeds");
			wb2.write(fileOut2);*/

			FileInputStream input = new FileInputStream("C:\\Users\\Sadiya\\Desktop\\Coflow-data\\Seed.xls");


			HSSFWorkbook wb = new HSSFWorkbook(input);
			HSSFSheet sheet = wb.getSheetAt(0);
			if (sheet.getLastRowNum() == 0 && sheet.getRow(1) == null) {
				FileOutputStream fileOut3 = new FileOutputStream("C:\\Users\\Sadiya\\Desktop\\Coflow-data\\Seed.xls");
				for(int i=1;i<=50;i++){ 
					row = sheet.createRow((short)i);
					row.createCell((int)0).setCellValue((int)(Math.random()*Framework.number_jobs));
					System.out.println("Yes its input data");
				}
				wb.write(fileOut3);
			}
			else{ 


				Iterator<Row> rowIterator = sheet.rowIterator();
				rowIterator.next();
				while (rowIterator.hasNext()) {             
					Row seedrow = (Row) rowIterator.next();
					cell = (HSSFCell) seedrow.getCell(0);

					if(cell!=null){
						if(cell.getCellType()==XSSFCell.CELL_TYPE_NUMERIC)
						{

							System.out.println("check type");

							System.out.println("Cell value "+cell.getNumericCellValue());
							Framework.seed=(cell.getNumericCellValue());
							System.out.println("Seed is "+ Framework.seed);
							seedrow.getCell(0).setCellValue(" ");
							break;


						}
					}

				}


				FileOutputStream fileOut = new FileOutputStream("C:\\Users\\Sadiya\\Desktop\\Coflow-data\\Seed.xls");
				wb.write(fileOut);
			}

			//fileOut.close();
			input.close();
		} 
		catch (IOException e) { 
			// TODO Auto-generated catch block 
			e.printStackTrace(); 
		} 
	} 

}
