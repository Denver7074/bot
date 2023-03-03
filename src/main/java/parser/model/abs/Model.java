package parser.model.abs;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class Model {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
}
