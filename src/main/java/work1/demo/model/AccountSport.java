package work1.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class AccountSport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idaccountSport;

    private String sportname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idaccount", referencedColumnName = "idaccount", nullable = false)
    private Account account;
}
