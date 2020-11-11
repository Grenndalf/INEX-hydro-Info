package RegularClasses;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.*;

import java.math.BigDecimal;

@ToString
@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "Wszystkie_Dane")
public class GaugeMeasurement {

    @DatabaseField(generatedId = true, columnName = "ID")
    private int id;

    @DatabaseField(columnName = "Nazwa_wodowskazu")
    private String gaugeName;

    @DatabaseField(columnName = "Numer_wodowskazu")
    private int gaugeID;

    @DatabaseField(columnName = "Nazwa_rzeki")
    private String riverName;

    @DatabaseField(columnName = "Rok_pomiaru")
    private short measurementYear;

    @DatabaseField(columnName = "Miesiac_pomiaru")
    private short measurementMonth;

    @DatabaseField(columnName = "Dzien_pomiaru")
    private short measurementDay;

    @DatabaseField(columnName = "Dane1")
    private BigDecimal data1;

    @DatabaseField(columnName = "Dane2")
    private BigDecimal data2;

    @DatabaseField(columnName = "Dane3")
    private BigDecimal data3;

    public GaugeMeasurement tryGetGaugeMeasurement(String[] fileLine) throws NumberFormatException{
        GaugeMeasurement gaugeMeasurement;
            gaugeMeasurement = new GaugeMeasurement();
            gaugeMeasurement.setGaugeID(Integer.parseInt(fileLine[0].replace("\"", "").trim()));
            gaugeMeasurement.setGaugeName(fileLine[1].replace("\"", "").trim());
            gaugeMeasurement.setRiverName(fileLine[2].replace("\"", "").trim());
            gaugeMeasurement.setMeasurementYear(Short.parseShort(fileLine[3].replace("\"", "").trim()));
            gaugeMeasurement.setMeasurementMonth(Short.parseShort(fileLine[4].replace("\"", "").trim()));
            gaugeMeasurement.setMeasurementDay(Short.parseShort(fileLine[5].replace("\"", "").trim()));
            gaugeMeasurement.setData1(new BigDecimal(fileLine[6].replace("\"", "").trim()));
            gaugeMeasurement.setData2(new BigDecimal(fileLine[7].replace("\"", "").trim()));
            gaugeMeasurement.setData3(new BigDecimal(fileLine[8].replace("\"", "").trim()));
        return gaugeMeasurement;
    }

}

