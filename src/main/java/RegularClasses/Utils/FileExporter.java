package RegularClasses.Utils;

import DButils.TableDBActions.GaugeDBActions;
import DButils.Tables.GaugeMeasurement;
import RegularClasses.Mediator.ControllerHolder;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileExporter {
    private GaugeDBActions gg = new GaugeDBActions();

    public void createWorkBook(List<GaugeMeasurement> spreadsheetData) {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet1 = workbook.createSheet("Dane Ca³oœæ");
        XSSFSheet sheet2 = workbook.createSheet("Dane Uporz¹dkowane");
        XSSFSheet sheet3 = workbook.createSheet("Dane Posortowane");

        createHeadersInSheet(sheet1);

        for (int i = 0; i < spreadsheetData.size(); i++) {
            Row row = sheet1.createRow(i + 1);
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


        //tworzenie wierszy w drugim arkuszu
        List<Row> rowListInSheet2 = new ArrayList<>();
        for (short i = 0; i < 366; i++) {
            Row row = sheet2.createRow(i + 1);
            row.createCell(0).setCellValue(i + 1);
            rowListInSheet2.add(row);
        }

        Map<Short, List<Double>> dataMap = new HashMap<>();
        List<GaugeMeasurement> totalData = gg.queryForDataOfSelectedTown(ControllerHolder.getInstance().getTownName());
        List<Short> yearList = totalData.stream().map(GaugeMeasurement::getMeasurementYear).distinct().collect(Collectors.toList());
        yearList.forEach(aShort -> {
            List<Double> dataList = totalData.stream().filter(gaugeMeasurement -> gaugeMeasurement.getMeasurementYear() == aShort).map(GaugeMeasurement::getData2).collect(Collectors.toList());
            dataMap.put(aShort, dataList);
        });

        Row row = sheet2.createRow(0);
        for (int i = 0; i < yearList.size(); i++) {
            List<Double> dataByYear = dataMap.get(yearList.get(i));
            row.createCell(i+1).setCellValue(yearList.get(i));
            int dataSize = Math.min(dataByYear.size(), 366);
            for (int j = 0; j < dataSize; j++) {
                rowListInSheet2.get(j).createCell(i + 1).setCellValue(dataByYear.get(j));
            }
        }

        try {
            FileOutputStream path = new FileOutputStream(ControllerHolder.getInstance().getTownName() + ".xlsx");
            workbook.write(path);
            path.close();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void createHeadersInSheet(XSSFSheet sheet1) {
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
    }
}
