package DButils.Tables;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Rzeki")
public class River {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_rzeki")
    private int id;

    @Column(name = "Nazwa_rzeki")
    private String riverName;

    @Override
    public String toString() {
        return riverName;
    }
}
