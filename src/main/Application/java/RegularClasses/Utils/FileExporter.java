package RegularClasses.Utils;

import DButils.Tables.GaugeMeasurement;
import RegularClasses.Mediator.ControllerHolder;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileExporter {


    public static final String DANE_CA£OSC = "Dane Ca³oœæ";
    public static final String DANE_UPORZADKOWANE = "Dane Uporz¹dkowane";
    public static final String DANE_POSORTOWANE = "Dane Posortowane";
    public static final String XLSX = ".xlsx";

    public void createWorkBook(List<GaugeMeasurement> spreadsheetData, String absolutePath) {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet1 = workbook.createSheet(DANE_CA£OSC);
        XSSFSheet sheet2 = workbook.createSheet(DANE_UPORZADKOWANE);
        XSSFSheet sheet3 = workbook.createSheet(DANE_POSORTOWANE);
        Map<Short, List<Double>> dataMap = new HashMap<>();

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
        List<Row> rowListInSheet2 = getRowListInSheet(sheet2);



        List<Short> yearList = spreadsheetData.stream()
                .map(GaugeMeasurement::getMeasurementYear)
                .distinct()
                .collect(Collectors.toList());

        yearList.forEach(aShort -> {
            List<Double> dataList = spreadsheetData.stream()
                    .filter(gaugeMeasurement -> gaugeMeasurement.getMeasurementYear() == aShort)
                    .map(GaugeMeasurement::getData2)
                    .collect(Collectors.toList());
            dataMap.put(aShort, dataList);
        });

        //uzupe³nianie danych w drugim arkuszu
        Row firstRowSheet2 = sheet2.createRow(0);
        for (int i = 0; i < yearList.size(); i++) {
            List<Double> dataByYear = dataMap.get(yearList.get(i));
            firstRowSheet2.createCell(i + 1).setCellValue(yearList.get(i));
            int dataSize = Math.min(dataByYear.size(), 366);
            for (int j = 0; j < dataSize; j++) {
                rowListInSheet2.get(j).createCell(i + 1).setCellValue(dataByYear.get(j));
            }
        }

        //tworzenie wierszy w trzecim arkuszu
        List<Row> rowListInSheet3 = getRowListInSheet(sheet3);
        Row firstRowSheet3 = sheet3.createRow(0);
        for (int i = 0; i < yearList.size(); i++) {
            List<Double> dataByYear = dataMap.get(yearList.get(i));
            dataByYear.sort(Double::compareTo);
            firstRowSheet3.createCell(i + 1).setCellValue(yearList.get(i));
            int dataSize = Math.min(dataByYear.size(), 366);
            for (int j = 0; j < dataSize; j++) {
                rowListInSheet3.get(j).createCell(i + 1).setCellValue(dataByYear.get(j));
            }
        }

        //zapis do pliku
        try {
            File file = new File(absolutePath + "\\" + ControllerHolder.getInstance().getTownName() + XLSX);
            FileOutputStream path = new FileOutputStream(file);
            workbook.write(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private List<Row> getRowListInSheet(XSSFSheet sheet) {
        List<Row> rowListInSheet2 = new ArrayList<>();
        for (short i = 0; i < 366; i++) {
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(i + 1);
            rowListInSheet2.add(row);
        }
        return rowListInSheet2;
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
