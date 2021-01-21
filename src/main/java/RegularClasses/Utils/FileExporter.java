package RegularClasses.Utils;

import DButils.Tables.GaugeMeasurement;
import RegularClasses.Mediator.ControllerHolder;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class FileExporter {


    public void createWorkBook(List<GaugeMeasurement> spreadsheetData) {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet1 = workbook.createSheet("Dane Ca³oœæ");
        XSSFSheet sheet2 = workbook.createSheet("Dane Uporz¹dkowane");
        XSSFSheet sheet3 = workbook.createSheet("Dane Posortowane");

        Row header = sheet1.createRow(0);
        header.createCell(0).setCellValue("ID wodowskazu");
        header.createCell(1).setCellValue("Nazwa wodowskazu");
        header.createCell(2).setCellValue("Rzeka");
        header.createCell(3).setCellValue("Rok");
        header.createCell(4).setCellValue("Miesi¹c");
        header.createCell(5).setCellValue("Dzieñ");
        header.createCell(6).setCellValue("Dane 1");
        header.createCell(7).setCellValue("Dane 2");
        header.createCell(8).setCellValue("Dane 3");

        for (int i = 0; i < spreadsheetData.size(); i++) {
            Row row = sheet1.createRow(i+1);
            row.createCell(0).setCellValue(spreadsheetData.get(i).getGaugeID());
            row.createCell(1).setCellValue(spreadsheetData.get(i).getGaugeName());
            row.createCell(2).setCellValue(spreadsheetData.get(i).getRiverName());
            row.createCell(3).setCellValue(spreadsheetData.get(i).getMeasurementYear());
            row.createCell(4).setCellValue(spreadsheetData.get(i).getMeasurementMonth());
            row.createCell(5).setCellValue(spreadsheetData.get(i).getMeasurementDay());
            row.createCell(6).setCellValue(spreadsheetData.get(i).getData1());
            row.createCell(7).setCellValue(spreadsheetData.get(i).getData2());
            row.createCell(8).setCellValue(spreadsheetData.get(i).getData3());
        }

        try {
            FileOutputStream path = new FileOutputStream(ControllerHolder.getInstance().getTownName()+".xlsx");
            workbook.write(path);
            path.close();
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
