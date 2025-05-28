package work1.demo.data;

import java.util.List;

import lombok.Data;
import work1.demo.model.Account;

@Data
public class AccountList {

        private List<Account> account;
        private Long total;

}
