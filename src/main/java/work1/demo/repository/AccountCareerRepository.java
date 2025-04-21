package work1.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import work1.demo.model.AccountCareer;
import work1.demo.model.AccountSport;



public interface AccountCareerRepository extends JpaRepository<AccountCareer, Long> {

}
