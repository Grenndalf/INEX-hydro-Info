package Others.Multihreading;

import DButils.Tables.GaugeMeasurement;
import Others.Mediator.ControllerHolder;
import Others.Utils.Calculations;
import Others.Utils.Utils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.XDDFColor;
import org.apache.poi.xddf.usermodel.XDDFLineProperties;
import org.apache.poi.xddf.usermodel.XDDFShapeProperties;
import org.apache.poi.xddf.usermodel.XDDFSolidFillProperties;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class FileExporter extends Task {

    public static final String DANE_CALOSC = "Dane Ca³oœæ";
    public static final String DANE_UPORZADKOWANE = "Dane Uporz¹dkowane";
    public static final String DANE_POSORTOWANE = "Dane Posortowane";
    public static final String XLSX = ".xlsx";
    public static final String PRZEPLYWY_PRAWDOPODOBNE = "Przep³ywy prawdopodobne";
    public static final String CREATOR = "INEX GREEN";

    private final XSSFWorkbook workbook = new XSSFWorkbook ();
    List<GaugeMeasurement> gaugeMeasurements;
    Calculations calculations;
    String absolutePath;
    List<Row> tableRowList = new ArrayList<> ();

    public FileExporter (List<GaugeMeasurement> gaugeMeasurements, Calculations calculations, String absolutePath) {
        this.gaugeMeasurements = gaugeMeasurements;
        this.calculations = calculations;
        this.absolutePath = absolutePath;
    }

    @Override
    protected Object call () {
        createWorkBook ();
        return null;
    }

    public void createWorkBook () {

        XSSFSheet sheet1 = workbook.createSheet (DANE_CALOSC);
        XSSFSheet sheet2 = workbook.createSheet (DANE_UPORZADKOWANE);
        XSSFSheet sheet3 = workbook.createSheet (DANE_POSORTOWANE);
        XSSFSheet sheet4 = workbook.createSheet (PRZEPLYWY_PRAWDOPODOBNE);
        Map<Short, List<Double>> dataMap = new HashMap<> ();
        List<Short> yearList = getYearList ();

        setAuthor ();
        fillSheet1 (sheet1);
        fillDataMap (dataMap, yearList);
        fillSheet2 (sheet2, dataMap, yearList);
        fillSheet3 (sheet3, dataMap, yearList);
        fillSheet4 (sheet4);
        saveToFile ();
    }

    private void fillSheet4 (XSSFSheet sheet4) {
        createHeadersInSheet4 (sheet4);

        if (calculations.getInputList ().size () > 0) {
            CellRangeAddress yValues = new CellRangeAddress (1, calculations.getInputList ().size (), 1, 1);
            CellRangeAddress xValues = new CellRangeAddress (1, calculations.getInputList ().size (), 3, 3);
            final int rowNum = Math.max (Utils.interpolationMap ().size (), calculations.getInputList ().size ()) + 2;

            Row decilesRowHeader = sheet4.createRow (rowNum);
            Row decileValues1 = sheet4.createRow (rowNum + 1);
            Row decileValues2 = sheet4.createRow (rowNum + 2);
            Row decileValues3 = sheet4.createRow (rowNum + 3);
            Row decileValues4 = sheet4.createRow (rowNum + 4);
            Row logTrendFunctionInfo = sheet4.createRow (rowNum + 6);
            Row logTrendValueOfA = sheet4.createRow (rowNum + 7);
            Row logTrendValueOfB = sheet4.createRow (rowNum + 8);
            decilesRowHeader.createCell (5).setCellValue ("Decyl");
            decilesRowHeader.createCell (6).setCellValue ("Wartoœæ");
            decilesRowHeader.createCell (8).setCellValue ("kwantylowy wspó³czynnik cv");
            overallActionsInSheet4 (yValues, xValues, decilesRowHeader, decileValues1, decileValues2, decileValues3,
                                    decileValues4, logTrendFunctionInfo, logTrendValueOfA, logTrendValueOfB);
            getSheet4TableValues (sheet4, logTrendValueOfA, logTrendValueOfB);
            interpolationActions (decilesRowHeader, decileValues1, decileValues2, decileValues3);
            fillSheet4withMaps ();
            createInterpolationSkewTable (sheet4, rowNum,
                                          calculations.getModifiedInterpolationSkewMap (calculations.getSkewCoefficient ().doubleValue ()));
            addBorders (sheet4, CellRangeAddress.valueOf ("G1:H36"));
            addBorders (sheet4, new CellRangeAddress (decilesRowHeader.getRowNum (), decileValues4.getRowNum (), 5, 6));
            addBorders (sheet4, CellRangeAddress.valueOf ("J1:AB22"));
        }
        sheet4.autoSizeColumn (1);
        sheet4.autoSizeColumn (2);
        sheet4.autoSizeColumn (8);
    }

    private void saveToFile () {
        try {
            File file = new File (new StringBuilder ().append (absolutePath)
                                          .append (System.getProperty ("file.separator"))
                                          .append (ControllerHolder.getInstance ().getTownName ())
                                          .append (XLSX).toString ());
            FileOutputStream path = new FileOutputStream (file);
            workbook.write (path);
            path.close ();
            workbook.close ();
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
    private List<Short> getYearList () {
        return gaugeMeasurements.stream ()
                .map (GaugeMeasurement::getMeasurementYear)
                .distinct ()
                .collect (Collectors.toList ());
    }
    private void fillDataMap (Map<Short, List<Double>> dataMap, List<Short> yearList) {
        yearList.forEach (aShort -> {
            List<Double> dataList = gaugeMeasurements.stream ()
                    .filter (gaugeMeasurement -> gaugeMeasurement.getMeasurementYear () == aShort)
                    .map (GaugeMeasurement::getData2)
                    .collect (Collectors.toList ());
            dataMap.put (aShort, dataList);
        });
    }
    private void fillSheet2 (XSSFSheet sheet2, Map<Short, List<Double>> dataMap,
                             List<Short> yearList) {

        //uzupe³nianie danych w drugim arkuszu
        List<Row> rowListInSheet2 = getRowListInSheet (sheet2);
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
            averageRow.createCell (i + 1).setCellValue (dataByYear.stream ().mapToDouble (Double::doubleValue)
                                                                .average ().orElse (0.0));
            maxValueRow.createCell (i + 1).setCellValue (dataByYear.stream ().mapToDouble (Double::doubleValue).max
                    ().orElse (0.0));
        }
    }
    private void fillSheet1 (XSSFSheet sheet1) {
        CellStyle cellStyle = workbook.createCellStyle ();
        cellStyle.setAlignment (HorizontalAlignment.LEFT);
        createHeadersInSheet (sheet1);

//insert danych z bazy do pierwszego arkusza
        for (int i = 0; i < gaugeMeasurements.size (); i++) {
            Row row = sheet1.createRow (i + 1);
            row.createCell (0).setCellValue (gaugeMeasurements.get (i).getGaugeID ());
            row.getCell (0).setCellStyle (cellStyle);
            row.createCell (1).setCellValue (gaugeMeasurements.get (i).getGaugeName ());
            row.createCell (2).setCellValue (gaugeMeasurements.get (i).getRiverName ());
            row.createCell (3).setCellValue (gaugeMeasurements.get (i).getMeasurementYear ());
            row.createCell (4).setCellValue (gaugeMeasurements.get (i).getMeasurementMonth ());
            row.createCell (5).setCellValue (gaugeMeasurements.get (i).getMeasurementDay ());
            row.createCell (6).setCellValue (gaugeMeasurements.get (i).getData1 ());
            row.createCell (7).setCellValue (gaugeMeasurements.get (i).getData2 ());
            row.createCell (8).setCellValue (gaugeMeasurements.get (i).getData3 ());
        }
        sheet1.autoSizeColumn (0);
        sheet1.autoSizeColumn (1);
        sheet1.autoSizeColumn (2);
    }
    private void setAuthor () {
        POIXMLProperties xmlProps = workbook.getProperties ();
        POIXMLProperties.CoreProperties coreProps = xmlProps.getCoreProperties ();
        coreProps.setCreator (CREATOR);
    }
    private void fillSheet3 (XSSFSheet sheet3, Map<Short, List<Double>> dataMap, List<Short> yearList) {
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
    }
    private void addBorders (XSSFSheet sheet4, CellRangeAddress rangeAddress) {
        final XSSFCellStyle style = workbook.createCellStyle ();
        style.setBorderBottom (BorderStyle.THIN);
        style.setBorderLeft (BorderStyle.THIN);
        style.setBorderRight (BorderStyle.THIN);
        style.setBorderTop (BorderStyle.THIN);
        style.setBottomBorderColor (IndexedColors.BLACK.getIndex ());
        for (int i = rangeAddress.getFirstRow (); i <= rangeAddress.getLastRow (); i++) {
            for (int j = rangeAddress.getFirstColumn (); j <= rangeAddress.getLastColumn (); j++) {
                Cell cell = sheet4.getRow (i).getCell (j);
                cell.setCellStyle (style);
            }
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
        decilesRowHeader.createCell (9).setCellFormula (new StringBuilder ().append ("ROUND(")
                                                                .append ("(")
                                                                .append (getCellAddress (decileValues1, 6))
                                                                .append ("-")
                                                                .append (getCellAddress (decileValues3, 6))
                                                                .append (")/(2*")
                                                                .append (getCellAddress (decileValues2, 6))
                                                                .append (")")
                                                                .append (",2)").toString ());
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
        double helper = calculations.getHelperValue ().doubleValue ();
        double lowerBound = calculations.getLowerBoundsForInterpolationB (helper);
        double upperBound = calculations.getUpperBoundsForInterpolationB (helper);
        double lowerSkew = calculations.getLowerSkewCoefficientBound (lowerBound);
        double upperSkew = calculations.getUpperSkewCoefficientBound (upperBound);
        if (lowerBound != upperBound) {
            decilesRowHeader.createCell (11).setCellValue ("Interpolacja");
            decileValues1.createCell (11).setCellValue (lowerBound);
            decileValues3.createCell (11).setCellValue (upperBound);
            decileValues2.createCell (11).setCellValue (0.01);
            decileValues1.createCell (12).setCellValue (lowerSkew);
            decileValues3.createCell (12).setCellValue (upperSkew);
            decileValues2.createCell (12).setCellFormula (new StringBuilder ().append ("(")
                                                                  .append (getCellAddress (decileValues3, 12))
                                                                  .append ("-")
                                                                  .append (getCellAddress (decileValues1, 12))
                                                                  .append (")/((")
                                                                  .append (getCellAddress (decileValues3, 11))
                                                                  .append ("-")
                                                                  .append (getCellAddress (decileValues1, 11))
                                                                  .append (")/")
                                                                  .append (decileValues2.getCell (11).getAddress ())
                                                                  .append (")").toString ());
            decileValues3.createCell (8).setCellValue ("kwantylowy wspó³czynnik skoœnoœci");
            decileValues3.createCell (9).setCellFormula (new StringBuilder ()
                                                                 .append ("ROUND(((((")
                                                                 .append (decileValues1.getCell (9).getAddress ())
                                                                 .append ("-")
                                                                 .append (decileValues1.getCell (11).getAddress ())
                                                                 .append (")/")
                                                                 .append (decileValues2.getCell (11).getAddress ())
                                                                 .append (")*")
                                                                 .append (decileValues2.getCell (12).getAddress ())
                                                                 .append (")+")
                                                                 .append (decileValues1.getCell (12).getAddress ())
                                                                 .append ("),2)").toString ());
        } else {
            decileValues3.createCell (8).setCellValue ("kwantylowy wspó³czynnik skoœnoœci");
            decileValues3.createCell (9).setCellValue (calculations.getQuantile (helper));
        }
    }
    private void getSheet4TableValues (XSSFSheet sheet4, Row row1, Row row2) {
        if (calculations.getInputList ().size () >= Utils.interpolationMap ().size ()) {
            for (int i = 1; i <= calculations.getInputList ().size (); i++) {
                tableRowList.add (sheet4.createRow (i));
            }
        } else {
            for (int i = 1; i <= Utils.interpolationMap ().size (); i++) {
                tableRowList.add (sheet4.createRow (i));
            }
        }

        for (int i = 0; i < calculations.getInputList ().size (); i++) {
            Row row = tableRowList.get (i);
            row.createCell (0).setCellValue (i + 1);
            row.createCell (1).setCellValue (calculations.getInputList ().get (i));
            row.createCell (2).setCellFormula (new StringBuilder ().append ("100*(")
                                                       .append (i + 1)
                                                       .append ("/(")
                                                       .append (calculations.getInputList ().size ())
                                                       .append ("+ 1))").toString ());
            row.createCell (3).setCellFormula (new StringBuilder ().append ("ROUND(")
                                                       .append ("LN(")
                                                       .append (calculations.getX (i + 1))
                                                       .append (")")
                                                       .append (",2)").toString ());
            row.createCell (4).setCellFormula (new StringBuilder ().append ("ROUND(")
                                                       .append (row1.getCell (6).getAddress ())
                                                       .append ("*")
                                                       .append (getCellAddress (row, 3))
                                                       .append ("+")
                                                       .append (row2.getCell (6).getAddress ())
                                                       .append (",2)").toString ());
        }
    }
    private void fillSheet4withMaps () {
        List<Double> keySet1 = new ArrayList<> (Utils.interpolationMap ().keySet ());
        List<Double> keySet2 = new ArrayList<> (Utils.interpolationSkewMap ().keySet ());
        for (int i = 0; i < keySet1.size (); i++) {
            Row row = tableRowList.get (i);
            row.createCell (6).setCellValue (keySet1.get (i));
            row.createCell (7).setCellValue (Utils.interpolationMap ().get (keySet1.get (i)));
        }
        for (int i = 0; i < keySet2.size (); i++) {
            Row row = tableRowList.get (i);
            row.createCell (9).setCellValue (keySet2.get (i));
            for (int j = 0; j < Utils.interpolationSkewMap ().get (keySet2.get (i)).size (); j++) {
                row.createCell (10 + j).setCellValue (Utils.interpolationSkewMap ().get (keySet2.get (i)).get (j));
            }
        }
    }
    private String getCellAddress (Row rowName, int i) {
        return rowName.getCell (i).getAddress ().formatAsString ();
    }
    private String getDecile (Row logTrendValueOfA, Row logTrendValueOfB, int x) {
        return new StringBuilder ().append ("ROUND(").append (getCellAddress (logTrendValueOfA, 6))
                .append ("*").append ("LN(").append (x).append (")").append ("+")
                .append (getCellAddress (logTrendValueOfB, 6)).append (",2)").toString ();
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
        header.createCell (1).setCellValue ("(y) przep³yw" + '\n' + "maksymalny roczny");
        header.getCell (1).setCellStyle (cellWithMod);
        header.createCell (2).setCellValue ("(x) prawodpodobieñstwo" + '\n' + "empiryczne");
        header.getCell (2).setCellStyle (cellWithMod);
        header.createCell (3).setCellValue ("ln(x)");
        header.getCell (3).setCellStyle (cellWithMod);
        header.createCell (4).setCellValue ("trend (y)");
        header.getCell (4).setCellStyle (cellWithMod);
        header.createCell (6).setCellValue ("Wartoœæ b");
        header.createCell (7).setCellValue ("Wspó³czynnik " + '\n' + "skoœnoœci s");
        header.getCell (7).setCellStyle (cellWithMod);
        header.createCell (9).setCellValue ("Wartoœci s/p%");
        header.getCell (9).setCellStyle (cellWithMod);
        header.createCell (10).setCellValue (100);
        header.createCell (11).setCellValue (99);
        header.createCell (12).setCellValue (95);
        header.createCell (13).setCellValue (90);
        header.createCell (14).setCellValue (80);
        header.createCell (15).setCellValue (70);
        header.createCell (16).setCellValue (50);
        header.createCell (17).setCellValue (30);
        header.createCell (18).setCellValue (20);
        header.createCell (19).setCellValue (10);
        header.createCell (20).setCellValue (5);
        header.createCell (21).setCellValue (3);
        header.createCell (22).setCellValue (2);
        header.createCell (23).setCellValue (1);
        header.createCell (24).setCellValue (0.5);
        header.createCell (25).setCellValue (0.2);
        header.createCell (26).setCellValue (0.1);
        header.createCell (27).setCellValue (0.01);
    }
    private void createInterpolationSkewTable (XSSFSheet sheet4, int rowNum, Map<Double, LinkedList<Double>> valueMap) {
        Row header = sheet4.getRow (rowNum + 6);
        final LinkedList<Double> list = Utils.percentValueList ();
        for (int i = 0; i < list.size (); i++) {
            header.createCell (9 + i).setCellValue (list.get (list.size () - 1 - i));
        }
        if (valueMap.size () > 1) {
            Row data1 = sheet4.getRow (rowNum + 7);
            final double lowerBound =
                    calculations.getLowerBoundsForSkewInterpolation (calculations.getSkewCoefficient ().doubleValue ());
            data1.createCell (8).setCellValue (lowerBound);
            for (int j = 0; j < valueMap.get (lowerBound).size (); j++) {
                data1.createCell (9 + j).setCellValue (valueMap.get (lowerBound).get (j));
            }
            Row data2 = sheet4.getRow (rowNum + 8);
            Row interpoler = sheet4.createRow (rowNum + 9);
            final double upperBound =
                    calculations.getUpperBoundsForSkewInterpolation (calculations.getSkewCoefficient ().doubleValue ());
            data2.createCell (8).setCellValue (upperBound);
            interpoler.createCell (8).setCellValue (calculations.getSkewCoefficient ().doubleValue ());
            createFinalTableHeader (sheet4, rowNum);
            for (int k = 0; k < valueMap.get (upperBound).size (); k++) {
                data2.createCell (9 + k).setCellValue (valueMap.get (upperBound).get (k));
                interpoler.createCell (9 + k).setCellFormula (new StringBuilder ().append ("ROUND(((")
                                                                      .append (interpoler.getCell (8).getAddress ())
                                                                      .append ("-")
                                                                      .append (data1.getCell (8).getAddress ())
                                                                      .append (")/(")
                                                                      .append (data2.getCell (8).getAddress ())
                                                                      .append ("-")
                                                                      .append (data1.getCell (8).getAddress ())
                                                                      .append ("))*(")
                                                                      .append (data2.getCell (9 + k).getAddress ())
                                                                      .append ("-")
                                                                      .append (data1.getCell (9 + k).getAddress ())
                                                                      .append (")+")
                                                                      .append (data1.getCell (9 + k).getAddress ())
                                                                      .append (",2)")
                                                                      .toString ());
                Row row = sheet4.createRow (rowNum + 12 + k);
                row.createCell (5).setCellValue (header.getCell (9 + k).getNumericCellValue ());
                row.createCell (6).setCellValue (calculations.getFinalResults ().get (k));
            }
        } else {
            Row data1 = sheet4.getRow (rowNum + 7);
            createFinalTableHeader (sheet4, rowNum);
            data1.createCell (8).setCellValue (calculations.getSkewCoefficient ().doubleValue ());
            for (int i = 0; i < valueMap.get (calculations.getSkewCoefficient ().doubleValue ()).size (); i++) {
                data1.createCell (7 + i).setCellValue (valueMap.get (calculations.getSkewCoefficient ().doubleValue ()).get (i));
                Row row = sheet4.createRow (12 + i);
                row.createCell (5).setCellValue (header.getCell (9 + i).getNumericCellValue ());
                row.createCell (6).setCellValue (calculations.getFinalResults ().get (i));
            }
        }
        createChart (sheet4, rowNum);
    }
    private void createChart (XSSFSheet sheet4, int rowNum) {
        try {
            XSSFDrawing drawing = sheet4.createDrawingPatriarch ();
            XSSFClientAnchor anchor = drawing.createAnchor (0, 0, 0, 0, 8,
                                                            Math.max (Utils.interpolationMap ().size (),
                                                                      calculations.getInputList ().size ()) + 13,
                                                            23, Math.max (Utils.interpolationMap ().size (),
                                                                          calculations.getInputList ().size ()) + 43);
            XSSFChart chart = drawing.createChart (anchor);
            XDDFValueAxis bottomAxis = chart.createValueAxis (AxisPosition.BOTTOM);
            bottomAxis.setTitle ("prawdopodobieñstwo przewy¿szenia p [%]");
            XDDFValueAxis leftAxis = chart.createValueAxis (AxisPosition.RIGHT);
            leftAxis.getOrAddMajorGridProperties ();
            leftAxis.getOrAddMinorGridProperties ();
            bottomAxis.getOrAddMajorGridProperties ();
            bottomAxis.getOrAddMinorGridProperties ();
            bottomAxis.setMinimum (0.0);
            bottomAxis.setOrientation (AxisOrientation.MAX_MIN);
            leftAxis.setTitle ("Przep³yw [m3/s]");
            leftAxis.setCrosses (AxisCrosses.AUTO_ZERO);
            CellRangeAddress cellAddresses = new CellRangeAddress (rowNum + 12,
                                                                   rowNum + 11 + Utils.percentValueList ().size (),
                                                                   5,
                                                                   5);
            final CellRangeAddress cellRangeAddress = new CellRangeAddress (rowNum + 12,
                                                                            rowNum + 11 + Utils.percentValueList ().size (),
                                                                            6,
                                                                            6);
            XDDFDataSource<Double> xs = XDDFDataSourcesFactory.fromNumericCellRange (sheet4, cellAddresses);
            XDDFNumericalDataSource<Double> ys2 = XDDFDataSourcesFactory.fromNumericCellRange (sheet4,
                                                                                               cellRangeAddress);
            XDDFChartData data = chart.createData (ChartTypes.SCATTER, bottomAxis, leftAxis);
            XDDFChartData.Series series2 = data.addSeries (xs, ys2);
            series2.setTitle ("Dane P% / Q(P%)", null);
            chart.plot (data);
            String color = "#248f24";
            lineSeriesColor (series2, XDDFColor.from (hex2Rgb (color)));
            for (int i = 0; i < Utils.percentValueList ().size (); i++) {
                Row interpoler = sheet4.getRow (rowNum + 9);
                if (sheet4.getRow (rowNum + 9) == null) {
                    interpoler = sheet4.getRow (rowNum + 7);
                }
                Row row = sheet4.getRow (rowNum + 12 + i);
                row.createCell (6).setCellFormula (new StringBuilder ().append (sheet4.getRow (rowNum + 2).getCell (6).getAddress ())
                                                           .append ("*(1+")
                                                           .append (sheet4.getRow (rowNum).getCell (9).getAddress ())
                                                           .append ("*")
                                                           .append (interpoler.getCell (9 + i).getAddress ())
                                                           .append (")").toString ());
            }
            CellRangeAddress rangeAddress = new CellRangeAddress (rowNum + 11, rowNum + Utils.percentValueList ().size () + 11, 5, 6);
            addBorders (sheet4, rangeAddress);

        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
    private void lineSeriesColor (XDDFChartData.Series series, XDDFColor color) {
        XDDFSolidFillProperties fill = new XDDFSolidFillProperties (color);
        XDDFLineProperties line = new XDDFLineProperties ();
        line.setFillProperties (fill);
        XDDFShapeProperties properties = series.getShapeProperties ();
        if (properties == null) {
            properties = new XDDFShapeProperties ();
        }
        properties.setLineProperties (line);
        series.setShapeProperties (properties);
    }
    private byte[] hex2Rgb (String colorStr) {
        int r = Integer.valueOf (colorStr.substring (1, 3), 16);
        int g = Integer.valueOf (colorStr.substring (3, 5), 16);
        int b = Integer.valueOf (colorStr.substring (5, 7), 16);
        return new byte[]{(byte) r, (byte) g, (byte) b};
    }
    private void createFinalTableHeader (XSSFSheet sheet4, int rowNum) {
        Row pqHeader = sheet4.createRow (rowNum + 11);
        pqHeader.createCell (5).setCellValue ("P%");
        pqHeader.createCell (6).setCellValue ("Q(P%)");
    }
}
