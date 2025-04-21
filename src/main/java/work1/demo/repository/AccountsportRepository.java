package work1.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import work1.demo.model.AccountSport;

import java.util.List;

public interface AccountsportRepository extends JpaRepository<AccountSport, Long> {

    @Query(nativeQuery = true, value = """
            select * from  account_career

            where idaccount = :idaccount
            """)
    List<AccountSport> findcarrerByIdaccount(@Param("idaccount") Long idaccount);

    


    
}
