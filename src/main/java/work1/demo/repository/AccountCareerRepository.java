package work1.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import work1.demo.model.AccountCareer;

public interface AccountCareerRepository extends JpaRepository<AccountCareer, Long> {

}
