package DButils.Tables;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

import static java.lang.Double.parseDouble;


@ToString
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Wszystkie_Dane", indexes = @Index(columnList = "Nazwa_wodowskazu", name = "GaugeIndex"))
public class GaugeMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "Nazwa_wodowskazu")
    private String gaugeName;

    @Column(name = "Numer_wodowskazu")
    private int gaugeID;

    @Column(name = "Nazwa_rzeki")
    private String riverName;

    @Column(name = "Rok_pomiaru")
    private short measurementYear;


    @Column(name = "Miesiac_pomiaru")
    private short measurementMonth;

    @Column(name = "Dzien_pomiaru")
    private short measurementDay;

    @Column(name = "Dane1")
    private double data1;

    @Column(name = "Dane2")
    private double data2;

    @Column(name = "Dane3")
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
