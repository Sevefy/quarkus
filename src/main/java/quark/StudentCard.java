package quark;

import jakarta.persistence.*;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Entity
@Table(name="students")
public class StudentCard extends PanacheEntity{
    @Column(name="name")
    private String name;
    @Column(name="surname")
    private String surname;

    public StudentCard() {

    }
}
