package parser.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.envers.Audited;
import parser.model.abs.Model;


@Entity
@Getter
@Setter
@Audited
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Mailing extends Model {

    String email;
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    User user;
    @Override
    public String toString() {
        return email + '\n';
    }
}
