package RegularClasses.Tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;

@ToString
@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "Rzeki")
public class River {

    @DatabaseField(generatedId = true, columnName = "ID")
    private int id;

    @DatabaseField(columnName = "Nazwa_rzeki")
    private String riverName;

    @ForeignCollectionField(eager = true, columnName = "lista_nazw_punktow")
    private Collection<Town> measurementPoints;
}
