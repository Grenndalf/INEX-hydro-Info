package DButils.Tables;


import com.j256.ormlite.field.DatabaseField;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import static java.lang.Double.parseDouble;

@ToString
@Getter
@Setter
@NoArgsConstructor
@Entity(name = "Wszystkie_Dane")
public class GaugeMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DatabaseField(columnName = "ID", generatedId = true)
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
    private double data1;

    @DatabaseField(columnName = "Dane2")
    private double data2;

    @DatabaseField(columnName = "Dane3")
    private double data3;

    public void tryGetGaugeMeasurement(String[] fileLine) throws NumberFormatException {
        this.setGaugeID(Integer.parseInt(fileLine[0].replace("\"", "").trim()));
        this.setGaugeName(fileLine[1].replace("\"", "").trim());
        this.setRiverName(fileLine[2].replace("\"", "").trim());
        this.setMeasurementYear(Short.parseShort(fileLine[3].replace("\"", "").trim()));
        this.setMeasurementMonth(Short.parseShort(fileLine[4].replace("\"", "").trim()));
        this.setMeasurementDay(Short.parseShort(fileLine[5].replace("\"", "").trim()));
        this.setData1(parseDouble(fileLine[6].replace("\"", "").trim()));
        this.setData2(parseDouble(fileLine[7].replace("\"", "").trim()));
        this.setData3(parseDouble(fileLine[8].replace("\"", "").trim()));
    }

}

