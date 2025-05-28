package work1.demo.data;

import lombok.Data;
import work1.demo.model.Account;

@Data
public class AccountSportData {

    // main model
    private Long idaccountSport;
    private Account account;
    // private Account account;

    // model exeternal
    private String sportname;

}
