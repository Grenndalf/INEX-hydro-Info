package RegularClasses;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.*;

@ToString
@Getter
@Setter
@NoArgsConstructor
//@DatabaseTable(tableName = "Wszystkie_Dane")
public class GaugeMeasurement {

    @DatabaseField(generatedId = true, columnName = "ID")
    private int id;
    @DatabaseField(columnName = "Nazwa_wodowskazu")
    private String gaugeName;

    @DatabaseField(columnName = "Rok_pomiaru")
    private int measurementYear;

    @DatabaseField(columnName = "Miesiac_pomiaru")
    private int measurementMonth;

    @DatabaseField(columnName = "Dzien_pomiaru")
    private int measurementDay;

    @DatabaseField(columnName = "Dane1")
    private byte data1;
    @DatabaseField(columnName = "Dane2")
    private byte data2;
    @DatabaseField(columnName = "Dane3")
    private byte data3;


}

