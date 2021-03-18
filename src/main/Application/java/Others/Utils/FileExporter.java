package Others.Utils;

import DButils.TableDBActions.GaugeDBActions;
import DButils.Tables.GaugeMeasurement;
import Others.Mediator.ControllerHolder;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static Others.Mediator.ControllerHolder.getInstance;

public class FileExporter {


    public static final String DANE_CALOSC = "Dane Ca³oœæ";
    public static final String DANE_UPORZADKOWANE = "Dane Uporz¹dkowane";
    public static final String DANE_POSORTOWANE = "Dane Posortowane";
    public static final String XLSX = ".xlsx";
    public static final String PRZEPLYWY_PRAWDOPODOBNE = "Przep³ywy prawdopodobne";
    private final XSSFWorkbook workbook = new XSSFWorkbook ();
    ListsUtils ls = new ListsUtils (new GaugeDBActions ().getCorrectedDoubleMeasurementsList (
            getInstance ().getRiverName (),
            getInstance ().getTownName ()));

    public void createWorkBook (List<GaugeMeasurement> spreadsheetData, String absolutePath) {


        XSSFSheet sheet1 = workbook.createSheet (DANE_CALOSC);
        XSSFSheet sheet2 = workbook.createSheet (DANE_UPORZADKOWANE);
        XSSFSheet sheet3 = workbook.createSheet (DANE_POSORTOWANE);
        XSSFSheet sheet4 = workbook.createSheet (PRZEPLYWY_PRAWDOPODOBNE);
        Map<Short, List<Double>> dataMap = new HashMap<> ();
        CellStyle cellStyle = workbook.createCellStyle ();
        cellStyle.setAlignment (HorizontalAlignment.LEFT);
        createHeadersInSheet (sheet1);

//insert danych z bazy do pierwszego arkusza
        for (int i = 0; i < spreadsheetData.size (); i++) {
            Row row = sheet1.createRow (i + 1);
            row.createCell (0).setCellValue (spreadsheetData.get (i).getGaugeID ());
            row.getCell (0).setCellStyle (cellStyle);
            row.createCell (1).setCellValue (spreadsheetData.get (i).getGaugeName ());
            row.createCell (2).setCellValue (spreadsheetData.get (i).getRiverName ());
            row.createCell (3).setCellValue (spreadsheetData.get (i).getMeasurementYear ());
            row.createCell (4).setCellValue (spreadsheetData.get (i).getMeasurementMonth ());
            row.createCell (5).setCellValue (spreadsheetData.get (i).getMeasurementDay ());
            row.createCell (6).setCellValue (spreadsheetData.get (i).getData1 ());
            row.createCell (7).setCellValue (spreadsheetData.get (i).getData2 ());
            row.createCell (8).setCellValue (spreadsheetData.get (i).getData3 ());
        }
        sheet1.autoSizeColumn (0);
        sheet1.autoSizeColumn (1);
        sheet1.autoSizeColumn (2);


        //tworzenie wierszy w drugim arkuszu
        List<Row> rowListInSheet2 = getRowListInSheet (sheet2);


        List<Short> yearList = spreadsheetData.stream ()
                .map (GaugeMeasurement::getMeasurementYear)
                .distinct ()
                .collect (Collectors.toList ());

        yearList.forEach (aShort -> {
            List<Double> dataList = spreadsheetData.stream ()
                    .filter (gaugeMeasurement -> gaugeMeasurement.getMeasurementYear () == aShort)
                    .map (GaugeMeasurement::getData2)
                    .collect (Collectors.toList ());
            dataMap.put (aShort, dataList);
        });

        //uzupe³nianie danych w drugim arkuszu
        Row firstRowSheet2 = sheet2.createRow (0);
        for (int i = 0; i < yearList.size (); i++) {
            List<Double> dataByYear = dataMap.get (yearList.get (i));
            firstRowSheet2.createCell (i + 1).setCellValue (yearList.get (i));
            int dataSize = Math.min (dataByYear.size (), 366);
            for (int j = 0; j < dataSize; j++) {
                rowListInSheet2.get (j).createCell (i + 1).setCellValue (dataByYear.get (j));
            }
        }
//tworzenie sumy i œredniej dla danego roku w drugim arkuszu
        Row sumRow = sheet2.createRow (368);
        sumRow.createCell (0).setCellValue ("SUMA");
        Row averageRow = sheet2.createRow (369);
        averageRow.createCell (0).setCellValue ("ŒREDNIA");
        Row maxValueRow = sheet2.createRow (370);
        maxValueRow.createCell (0).setCellValue ("MAX");
        for (int i = 0; i < yearList.size (); i++) {
            List<Double> dataByYear = dataMap.get (yearList.get (i));
            sumRow.createCell (i + 1).setCellValue (dataByYear.stream ().mapToDouble (Double::doubleValue).sum ());
            //dodaæ jakiegoœ optionala?
            averageRow.createCell (i + 1).setCellValue (dataByYear.stream ().mapToDouble (Double::doubleValue).average ().orElse (0.0));
            maxValueRow.createCell (i + 1).setCellValue (dataByYear.stream ().mapToDouble (Double::doubleValue).max ().orElse (0.0));
        }

        //tworzenie wierszy w trzecim arkuszu
        List<Row> rowListInSheet3 = getRowListInSheet (sheet3);
        Row firstRowSheet3 = sheet3.createRow (0);
        for (int i = 0; i < yearList.size (); i++) {
            List<Double> dataByYear = dataMap.get (yearList.get (i));
            dataByYear.sort (Double::compareTo);
            Collections.reverse (dataByYear);
            firstRowSheet3.createCell (i + 1).setCellValue (yearList.get (i));
            int dataSize = Math.min (dataByYear.size (), 366);
            for (int j = 0; j < dataSize; j++) {
                rowListInSheet3.get (j).createCell (i + 1).setCellValue (dataByYear.get (j));
            }
        }

        for (Row row : rowListInSheet3) {
            Row row2 = rowListInSheet3.get (0);
            Cell cell = row.createCell (yearList.size () + 1);
            String address = getCellAddress (row2, yearList.size ()).split ("[0-9]")[0];
            cell.setCellFormula (new StringBuilder ().append ("ROUND(AVERAGE(B")
                                         .append (row.getRowNum () + 1)
                                         .append (":")
                                         .append (address)
                                         .append (row.getRowNum () + 1)
                                         .append ("),2)").toString ());
        }


        createHeadersInSheet4 (sheet4);


        if (ls.getInputList ().size () > 0) {
            CellRangeAddress yValues = new CellRangeAddress (1, ls.getInputList ().size (), 1, 1);
            CellRangeAddress xValues = new CellRangeAddress (1, ls.getInputList ().size (), 3, 3);

            final int rowNum = ls.getInputList ().size () + 1;
            Row decilesRowHeader = sheet4.createRow (rowNum);
            decilesRowHeader.createCell (5).setCellValue ("Decyl");
            decilesRowHeader.createCell (6).setCellValue ("Wartoœæ");
            decilesRowHeader.createCell (8).setCellValue ("kwantylowy wspó³czynnik cv");
            Row decileValues1 = sheet4.createRow (rowNum + 1);
            Row decileValues2 = sheet4.createRow (rowNum + 2);
            Row decileValues3 = sheet4.createRow (rowNum + 3);
            Row decileValues4 = sheet4.createRow (rowNum + 4);
            Row logTrendFunctionInfo = sheet4.createRow (rowNum + 6);
            Row logTrendValueOfA = sheet4.createRow (rowNum + 7);
            Row logTrendValueOfB = sheet4.createRow (rowNum + 8);

            overallActionsInSheet4 (yValues, xValues, decilesRowHeader, decileValues1, decileValues2, decileValues3,
                                    decileValues4
                    , logTrendFunctionInfo, logTrendValueOfA, logTrendValueOfB);

            getSheet4TableValues (sheet4, logTrendValueOfA, logTrendValueOfB);

            interpolationActions (decilesRowHeader, decileValues1, decileValues2, decileValues3);

        }

        sheet4.autoSizeColumn (1);
        sheet4.autoSizeColumn (2);
        sheet4.autoSizeColumn (8);


        //zapis do pliku
        try {
            File file = new File (new StringBuilder ().append (absolutePath)
                                          .append (System.getProperty ("file.separator"))
                                          .append (ControllerHolder.getInstance ().getTownName ())
                                          .append (XLSX).toString ());
            FileOutputStream path = new FileOutputStream (file);
            workbook.write (path);
            Platform.runLater (() -> {
                Alert alert = new Alert (Alert.AlertType.CONFIRMATION);
                alert.setTitle ("Zakoñczono");
                alert.setContentText (new StringBuilder ().append ("Wyeksportowano do pliku: ")
                                              .append (ControllerHolder.getInstance ().getTownName ())
                                              .append (".xlsx").toString ());
                alert.setHeaderText ("zadanie wykonano");
                alert.showAndWait ();
            });
        } catch (IOException e) {

            Platform.runLater (() -> {
                Alert alert = new Alert (Alert.AlertType.ERROR);
                alert.setContentText (e.getMessage ());
                alert.showAndWait ();
            });
        }

    }

    private void overallActionsInSheet4 (CellRangeAddress yValues, CellRangeAddress xValues, Row decilesRowHeader,
                                         Row decileValues1, Row decileValues2, Row decileValues3, Row decileValues4,
                                         Row logTrendFunctionInfo, Row logTrendValueOfA, Row logTrendValueOfB) {
        logTrendValueOfA.createCell (6).setCellFormula (new StringBuilder ().append ("INDEX(LINEST(")
                                                                .append (yValues.formatAsString ())
                                                                .append (",")
                                                                .append (xValues.formatAsString ())
                                                                .append (",),1)").toString ());
        logTrendValueOfB.createCell (6).setCellFormula (new StringBuilder ().append ("INDEX(LINEST(")
                                                                .append (yValues.formatAsString ())
                                                                .append (",")
                                                                .append (xValues.formatAsString ())
                                                                .append (",),1,2)").toString ());
        decileValues1.createCell (5).setCellValue ("Q10%");
        decileValues1.createCell (6).setCellFormula (getDecile (logTrendValueOfA, logTrendValueOfB, 10));
        decileValues2.createCell (5).setCellValue ("Q50%");
        decileValues2.createCell (6).setCellFormula (getDecile (logTrendValueOfA, logTrendValueOfB, 50));
        decileValues3.createCell (5).setCellValue ("Q90%");
        decileValues3.createCell (6).setCellFormula (getDecile (logTrendValueOfA, logTrendValueOfB, 90));
        decileValues4.createCell (5).setCellValue ("Q100%");
        decileValues4.createCell (6).setCellFormula (getDecile (logTrendValueOfA, logTrendValueOfB, 100));
        decilesRowHeader.createCell (9).setCellFormula (new StringBuilder ().append ("ROUND((")
                                                                .append (getCellAddress (decileValues1, 6))
                                                                .append ("-")
                                                                .append (getCellAddress (decileValues3, 6))
                                                                .append (")/(2*")
                                                                .append (getCellAddress (decileValues2, 6))
                                                                .append ("),2)").toString ());
        decileValues1.createCell (8).setCellValue ("wielkoœæ pomocnicza b");
        decileValues1.createCell (9).setCellFormula (new StringBuilder ().append ("ROUND((")
                                                             .append (getCellAddress (decilesRowHeader, 9))
                                                             .append ("*")
                                                             .append (getCellAddress (decileValues2, 6))
                                                             .append (")/(").append (getCellAddress (decileValues2, 6))
                                                             .append ("-")
                                                             .append (getCellAddress (decileValues4, 6))
                                                             .append ("),2)").toString ());

        logTrendFunctionInfo.createCell (5).setCellValue ("wzór: y = (a * LN(x)) + b");
        logTrendValueOfA.createCell (5).setCellValue ("a=");
        logTrendValueOfB.createCell (5).setCellValue ("b=");
    }

    private void interpolationActions (Row decilesRowHeader, Row decileValues1, Row decileValues2, Row decileValues3) {
        double helper = decileValues1.getCell (9).getNumericCellValue ();
        double lowerBound = Utils.getLowerBoundsForInterpolationB (helper);
        double upperBound = Utils.getLowerBoundsForInterpolationB (lowerBound);
        double lowerSkew = Utils.getLowerSkewCoefficientBound (lowerBound);
        double upperSkew = Utils.getUpperSkewCoefficientBound (upperBound);
        String interpolationStep = getInterpolationModifier (lowerBound,upperBound);
        decilesRowHeader.createCell (13).setCellValue ("Interpolacja");
        decileValues1.createCell (13).setCellValue (lowerBound);
        decileValues3.createCell (13).setCellValue (upperBound);
        decileValues2.createCell (13).setCellValue (getInterpolationModifier(decileValues1.getCell (13).getNumericCellValue (),decileValues3.getCell (13).getNumericCellValue ()));
        decileValues2.createCell (13).setCellFormula (new StringBuilder ().append ("(")
                                                              .append (decileValues3.getCell (13).getAddress ().formatAsString ())
                                                              .append ("-")
                                                              .append (decileValues1.getCell (13).getAddress ().formatAsString ()).append (")")
                                                              .append ("/").append (interpolationStep).toString ());
    }

    private void getSheet4TableValues (XSSFSheet sheet4, Row logTrendValueOfA, Row logTrendValueOfB) {
        for (int i = 1; i <= ls.getInputList ().size (); i++) {
            Row row = sheet4.createRow (i);
            row.createCell (0).setCellValue (i);
            row.createCell (1).setCellValue (ls.getInputList ().get (i - 1));
            row.createCell (2).setCellFormula (new StringBuilder ().append ("100*(")
                                                       .append (i)
                                                       .append ("/(")
                                                       .append (ls.getInputList ().size ())
                                                       .append ("+ 1))").toString ());
            row.createCell (3).setCellFormula (new StringBuilder ().append ("ROUND(")
                                                       .append ("LN(")
                                                       .append (ls.getX (i))
                                                       .append (")")
                                                       .append (",2)").toString ());
            row.createCell (4).setCellFormula (new StringBuilder ().append ("ROUND(")
                                                       .append (getCellAddress (logTrendValueOfA, 6))
                                                       .append ("*")
                                                       .append (getCellAddress (row, 3))
                                                       .append ("+")
                                                       .append (getCellAddress (logTrendValueOfB, 6))
                                                       .append (",2)").toString ());
        }
    }

    private String getInterpolationModifier (double lower, double bigger) {
        if (bigger - lower <= 1) {
            return "0.01";
        } else if (bigger - lower <= 10) {
            return "1";
        } else {
            return "2";
        }
    }


    private String getCellAddress (Row decileValues1, int i) {
        return decileValues1.getCell (i).getAddress ().formatAsString ();
    }

    private String getDecile (Row logTrendValueOfA, Row logTrendValueOfB, int x) {
        return new StringBuilder ().append (getCellAddress (logTrendValueOfA, 6))
                .append ("*").append ("LN(").append (x).append (")").append ("+")
                .append (getCellAddress (logTrendValueOfB, 6)).toString ();
    }

    private List<Row> getRowListInSheet (XSSFSheet sheet) {
        List<Row> rowListInSheet2 = new ArrayList<> ();
        for (short i = 0; i < 366; i++) {
            Row row = sheet.createRow (i + 1);
            row.createCell (0).setCellValue (i + 1);
            rowListInSheet2.add (row);
        }
        return rowListInSheet2;
    }

    private void createHeadersInSheet (XSSFSheet sheet1) {
        Row header = sheet1.createRow (0);
        header.createCell (0).setCellValue ("ID wodowskazu");
        header.createCell (1).setCellValue ("Nazwa wodowskazu");
        header.createCell (2).setCellValue ("Rzeka");
        header.createCell (3).setCellValue ("Rok");
        header.createCell (4).setCellValue ("Miesi¹c");
        header.createCell (5).setCellValue ("Dzieñ");
        header.createCell (6).setCellValue ("Dane 1");
        header.createCell (7).setCellValue ("Dane 2");
        header.createCell (8).setCellValue ("Dane 3");
    }

    private void createHeadersInSheet4 (XSSFSheet sheet4) {
        CellStyle cellWithMod = workbook.createCellStyle ();
        cellWithMod.setAlignment (HorizontalAlignment.CENTER);
        cellWithMod.setWrapText (true);
        Row header = sheet4.createRow (0);
        header.createCell (0).setCellValue ("LP");
        header.createCell (1).setCellValue ("(y) przep³yw maksymalny roczny");
        header.getCell (1).setCellStyle (cellWithMod);
        header.createCell (2).setCellValue ("(x) prawodpodobieñstwo empiryczne");
        header.getCell (2).setCellStyle (cellWithMod);
        header.createCell (3).setCellValue ("ln(x)");
        header.getCell (3).setCellStyle (cellWithMod);
        header.createCell (4).setCellValue ("trend (y)");
        header.getCell (4).setCellStyle (cellWithMod);

    }


}
