package DButils.Tables;


import com.j256.ormlite.field.DatabaseField;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;

@ToString
@Getter
@Setter
@NoArgsConstructor
@Entity(name = "Wodowskazy")
public class Town {

    @DatabaseField(generatedId = true, columnName = "ID")
    private int id;

    @DatabaseField(columnName = "Wodowskaz" )
    private String townName;
}

