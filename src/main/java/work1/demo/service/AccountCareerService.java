package work1.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import work1.demo.data.AccountCareerData;
import work1.demo.model.Account;
import work1.demo.model.AccountCareer;
import work1.demo.repository.AccountCareerRepository;

@Slf4j
@Service
public class AccountCareerService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AccountCareerRepository accountCareerRepository;

    public static final String SUCCESS = "SUCCESS";
    public static final String ENTRIES = "entries";

    public AccountCareer addCareer(AccountCareer newcareer) {

        log.info("newcareer = {}", newcareer);
        return accountCareerRepository.save(newcareer);

    }

    public List<AccountCareer> getAllCareer() {
        return accountCareerRepository.findAll();
    }

    public Map<String, Object> getAccountCareerListByCondition(Account criteria) {
        Map<String, Object> result = new HashMap<>();

        String sql = """
                    SELECT ac.idaccount_career AS idaccountCareer,
                           ac.careername AS careername,
                           a.idaccount AS account_idaccount,
                           a.username AS account_username,
                        a.password AS account_password,
                           a.fname AS account_fname,
                           a.lname AS account_lname,
                            a.birthday AS account_birthday,
                             a.description AS account_description,
                             a.gender as account_gender
                    FROM account_career ac
                    JOIN account a ON ac.idaccount = a.idaccount
                    WHERE 1=1
                """;

        List<Object> params = new ArrayList<>();

        if (criteria.getIdaccount() != null && criteria.getIdaccount() != null) {
            sql += " AND ac.idaccount = ?";
            params.add(criteria.getIdaccount());
        }

        // ✅ ใช้ custom RowMapper เพื่อ map ข้อมูล account ลงใน object
        List<AccountCareerData> listCareer = jdbcTemplate.query(sql, params.toArray(), (rs, rowNum) -> {
            AccountCareerData data = new AccountCareerData();
            data.setIdaccountCareer(rs.getLong("idaccountCareer"));
            data.setCareername(rs.getString("careername"));

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

        log.info("listCareer -> {}", listCareer);

        result.put("careerList", listCareer);
        return result;
    }

    public String deleteCareer(Long id) {

        List<AccountCareer> listcareer = getAllCareer();

        for (AccountCareer career : listcareer) {
            if (career.getIdaccountCareer().equals(id)) {
                String sql = "Delete from account_career where idaccount_career = ?";
                jdbcTemplate.update(sql, id);
                return "ลบข้อมูล Career เรียบร้อย";
            }
        }
        return "ลบข้อมูง Career ไม่ได้";
    }
}
