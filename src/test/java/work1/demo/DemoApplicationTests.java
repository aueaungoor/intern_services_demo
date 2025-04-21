package work1.demo;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import work1.demo.data.AccountCareerData;
import work1.demo.service.AccountCareerService;
import work1.demo.model.Account;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class DemoApplicationTests {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private AccountCareerService accountCareerService;

	@Test
	void contextLoads() {
	}

	@Test
	void xxxx() {
		log.info("xxxxxxxxxxxxx");
		Account criteria = new Account();
		Account testaccount = new Account();
		testaccount.setIdaccount(6l);
		
		List<AccountCareerData> list = (List<AccountCareerData>) accountCareerService.getAccountCareerListByCondition(criteria);
		log.info("list -> {}", list);
	}
}
