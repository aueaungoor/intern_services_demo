package work1.demo;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import work1.demo.data.AccountCareerData;
import work1.demo.service.AccountCareerService;
import work1.demo.model.Account;
import work1.demo.service.AccountService;
import work1.demo.service.CommonUtils;
import work1.demo.controller.AccountController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class DemoApplicationTests {

	public static final String SUCCESS = "SUCCESS";
    public static final String ENTRIES = "entries";

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private AccountCareerService accountCareerService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private AccountController accountController;

    @Autowired
    private ObjectMapper objectMapper; // 



	@Test
	void contextLoads() {
	}

	@Test
	void findAccountByKey() {
		log.info("find account by key");
		Account testaccount = new Account();
		testaccount.setUsername("a");

		
		
		List<Account> list = accountService.search(testaccount);

		




		

		log.info("รายการ Account ทั้งหมด service:");
    for (Account acc : list) {
        log.info(" - id: {}, username: {}, fname: {}", acc.getIdaccount(), acc.getUsername(), acc.getFname());

    }
	}

   @Test
void testSearchAccountsAndLoopResult() throws Exception {
    
    Account searchRequest = new Account();
    searchRequest.setUsername("a");

    // 🔥 1. รัน request และเก็บ response เป็น string
    MvcResult result = mockMvc.perform(post("/accounts/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(searchRequest)))
            .andExpect(status().isOk())
            .andReturn();

    String json = result.getResponse().getContentAsString();
    log.info("RAW JSON: {}", json); // ✅ ลองดู JSON เต็มก่อน

    // 🔥 2. แปลง JSON → Map
    Map<String, Object> map = objectMapper.readValue(json, new TypeReference<>() {});
    List<Map<String, Object>> dataList = (List<Map<String, Object>>) map.get("data");

    // 🔥 3. Loop ข้อมูลออกมาแสดง
    log.info("รายการที่ค้นพบ: controller");
    for (Map<String, Object> acc : dataList) {
        log.info(" - id: {}, username: {}, fname: {}", acc.get("idaccount"), acc.get("username"), acc.get("fname"));
    }

    // (Optional) ตรวจสอบว่าข้อมูลไม่ว่าง
    Assertions.assertFalse(dataList.isEmpty());
}

}
