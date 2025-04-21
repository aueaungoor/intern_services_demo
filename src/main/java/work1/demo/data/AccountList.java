package work1.demo.data;

import jakarta.persistence.*;
import lombok.Data;
import work1.demo.model.Account;

import java.util.Date;
import java.util.List;



@Data
public class AccountList {

   private List<Account> account;
        private Long total;
        
   
    

   
}
