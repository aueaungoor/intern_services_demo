package work1.demo.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
public class AccountSport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idaccountSport;

    private String sportname;


    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "idaccount",referencedColumnName  = "idaccount", nullable = false)
    private Account account;
}
