package DButils.Tables;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@ToString
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Rzeki")
public class River {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "Nazwa_rzeki", unique = true)
    private String riverName;

//    @Override
//    public boolean equals(Object obj) {
//        if (obj == null) {
//            return false;
//        }
//        if (obj instanceof River) {
//            River river = (River) obj;
//            return riverName.equals(river.riverName);
//        }
//        return false;
//    }
//
//    @Override
//    public int hashCode() {
//        return 17 * riverName.hashCode();
//    }
}
