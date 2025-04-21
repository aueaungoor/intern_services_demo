package work1.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import work1.demo.data.AccountCareerData;
import work1.demo.data.AccountSportData;
import work1.demo.model.AccountSport;
import work1.demo.model.Account;
import work1.demo.repository.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class AccountsportService {

    @Autowired
    private AccountsportRepository accountSportRepository;

    @Autowired
private EntityManager entityManager;

@Autowired
    private JdbcTemplate jdbcTemplate;


   public List<AccountSport> getAllAccounts_sport() {
       return accountSportRepository.findAll();
   }
//
//    public Optional<AccountSport> getAccount_sportById(Long id) {
//        return accountSportRepository.findById(id);
//    }
//
//    public AccountSport createAccount_sport(AccountSport accountSport) {
//        return accountSportRepository.save(accountSport);
//    }
//
   public void deleteAccount_sport(Long id) {
       accountSportRepository.deleteById(id);
   }

   
    
   public List<AccountSport> getSport(Long idaccount) {
    StringBuilder sql = new StringBuilder();
    sql.append("SELECT * FROM account_sport WHERE idaccount = :idaccount");

    List<AccountSport> listcareer = entityManager
            .createNativeQuery(sql.toString(), AccountSport.class)
            .setParameter("idaccount", idaccount)
            .getResultList();

    if (listcareer == null || listcareer.isEmpty()) {
        System.out.println("ไม่พบข้อมูลกีฬาใน account id = " + idaccount);
        return new ArrayList<>();
    }

    System.out.println("พบกีฬา: " + listcareer.size() + " รายการ");
    for (AccountSport sport : listcareer) {
        System.out.println("- " + sport.getSportname());
    }

    return listcareer;
}

public AccountSport addSport(AccountSport newsport) {
    log.info("newsport = {}",newsport);
    return accountSportRepository.save(newsport);
    
}


public Map<String, Object> getAccountSportListByCondition(Account criteria) {
        Map<String, Object> result = new HashMap<>();
    
        String sql = """
            SELECT ac.idaccount_sport AS idaccountSport,
                   ac.sportname AS sportname,
                   a.idaccount AS account_idaccount,
                   a.username AS account_username,
                a.password AS account_password,
                   a.fname AS account_fname,
                   a.lname AS account_lname,
                    a.birthday AS account_birthday,
                     a.description AS account_description,
                     a.gender as account_gender
            FROM account_sport ac
            JOIN account a ON ac.idaccount = a.idaccount
            WHERE 1=1
        """;
    
        List<Object> params = new ArrayList<>();
    
        if (criteria.getIdaccount() != null && criteria.getIdaccount() != null) {
            sql += " AND ac.idaccount = ?";
            params.add(criteria.getIdaccount());
        }
    
        // ✅ ใช้ custom RowMapper เพื่อ map ข้อมูล account ลงใน object
        List<AccountSportData> listSport = jdbcTemplate.query(sql, params.toArray(), (rs, rowNum) -> {
            AccountSportData data = new AccountSportData();
            data.setIdaccountSport(rs.getLong("idaccountSport"));
            data.setSportname(rs.getString("sportname"));
    
            Account acc = new Account();
            acc.setIdaccount(rs.getLong("account_idaccount"));
            acc.setUsername(rs.getString("account_username"));
            acc.setPassword(rs.getString("account_password"));
            acc.setFname(rs.getString("account_fname"));
            acc.setLname(rs.getString("account_lname"));
            acc.setBirthday(rs.getDate("account_birthday"));
            acc.setDescription(rs.getString("account_description"));
            acc.setGender(rs.getString("account_gender"));
    
            data.setAccount(acc);
            return data;
        });
    
        log.info("listSport -> {}", listSport);
    
        result.put("listSport", listSport);
        return result;
    }

    public String deleteSport(Long id)
    {

        List<AccountSport> listsport = getAllAccounts_sport();
        log.info("listSPort -> {}",listsport);

        for(AccountSport sport : listsport)
        {
            
            if(sport.getIdaccountSport().equals(id)){
                String sql = "DELETE From account_sport where idaccount_sport = ?";
                jdbcTemplate.update(sql, id);
                return "ลบข้อมูล Sport เรียบร้อย " ;
            }
        }

        return "id ผิด" + id;
    }


    
    
}
