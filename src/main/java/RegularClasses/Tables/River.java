package RegularClasses.Tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "Rzeki_wodowskazy")
public class River {

    @DatabaseField(generatedId = true, columnName = "ID")
    private int id;

    @DatabaseField(columnName = "Rzeka")
    private String riverName;

    @ForeignCollectionField(eager = false)
    @DatabaseField(columnName = "Lista_punktow")
    private List<Town> measurementPoints;
}
