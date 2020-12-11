package RegularClasses.Tables;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "Wodowskazy")
public class Town {

    @DatabaseField(generatedId = true, columnName = "ID")
    private int id;

    @DatabaseField(columnName = "Wodowskaz")
    private String townName;

}

