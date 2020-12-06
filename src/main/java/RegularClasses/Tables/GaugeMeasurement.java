package RegularClasses.Tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "Wszystkie_Dane")
public class GaugeMeasurement {

    @DatabaseField(generatedId = true, columnName = "ID")
    private int id;

    @DatabaseField(index = true, columnName = "Nazwa_wodowskazu")
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

    public void tryGetGaugeMeasurement(String[] fileLine) throws NumberFormatException {
        this.setGaugeID(Integer.parseInt(fileLine[0].replace("\"", "").trim()));
        this.setGaugeName(fileLine[1].replace("\"", "").trim());
        this.setRiverName(fileLine[2].replace("\"", "").trim());
        this.setMeasurementYear(Short.parseShort(fileLine[3].replace("\"", "").trim()));
        this.setMeasurementMonth(Short.parseShort(fileLine[4].replace("\"", "").trim()));
        this.setMeasurementDay(Short.parseShort(fileLine[5].replace("\"", "").trim()));
        this.setData1(new BigDecimal(fileLine[6].replace("\"", "").trim()));
        this.setData2(new BigDecimal(fileLine[7].replace("\"", "").trim()));
        this.setData3(new BigDecimal(fileLine[8].replace("\"", "").trim()));
    }

}

