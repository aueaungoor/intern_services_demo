package work1.demo.data;

import java.util.List;

import lombok.Data;
import work1.demo.model.Account;

@Data
public class AccountCareerData {
    
    // main model
    private Long idaccountCareer;
    private Account account;
//    private Account account;



    // model exeternal
    private String careername;
   
}
