package work1.demo.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class AccountCareer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idaccountCareer;

 

   private String careername;

   @ManyToOne(fetch=FetchType.LAZY)
   @JoinColumn(name = "idaccount",referencedColumnName  = "idaccount", nullable = false)
   private Account account;

   
}
