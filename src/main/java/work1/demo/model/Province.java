package work1.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Province {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idprovince;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcountry", referencedColumnName = "idcountry", nullable = false)
    private Country country;

}
