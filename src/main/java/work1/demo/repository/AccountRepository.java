package work1.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import work1.demo.model.Account;
import work1.demo.model.AccountSport;



public interface AccountRepository extends JpaRepository<Account, Long> {
	
    // สามารถเพิ่มเมธอด custom ได้ เช่น findByUsername()
    Account findByUsername(String username);

    @Query(nativeQuery = true, value = """
            select * from  account

            where idaccount = :idaccount
            """)
    List<AccountSport> findByAccountIdaccount(@Param("idaccount") Long idaccount);
   
    
}
