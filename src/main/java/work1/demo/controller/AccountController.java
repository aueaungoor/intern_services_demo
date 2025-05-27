package work1.demo.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import work1.demo.data.AccountList;
import work1.demo.data.Paging;
import work1.demo.model.Account;
import work1.demo.model.AccountCareer;
import work1.demo.model.AccountSport;
import work1.demo.service.AccountCareerService;
import work1.demo.service.AccountService;
import work1.demo.service.AccountsportService;
import work1.demo.service.CommonUtils;

@RestController
@CrossOrigin(origins = "http://localhost:4200")

@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Value("")
    private String prefixPath;

    @Autowired
    private AccountsportService accountSportService;
    @Autowired
    private AccountCareerService accountCareerService;

    public static final String SUCCESS = "SUCCESS";
    public static final String ENTRIES = "ENTRIES";
    public static final String ERROR = "ERROR";

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    @PostMapping("/creat-account")
    public ResponseEntity<Object> CreatAccount(@RequestBody Account data) {
        Map<String, Object> response = new HashMap<>();
        String result = accountService.createAccount(data);

        try {
            if ("success".equalsIgnoreCase(result)) {

                response.put("message", "สร้างบัญชีสำเร็จ");
                response.put("data", data);

                return ResponseEntity.ok(response);
            } else {
                response.put("message", "เกิดข้อผิดพลาดในการสร้างบัญชี");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(response);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response.put("message", "เกิดข้อผิดพลาดในระบบ");
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    @PostMapping("/check-username")
    public ResponseEntity<Map<String, Object>> checkUsername(@RequestBody Account data) {
        Map<String, Object> response = new HashMap<>();
        try {
            Account result = accountService.checkusername(data);

            if (result == null) {
                // username ยังไม่ถูกใช้
                response.put("available", true);
                response.put("message", "สามารถใช้ได้");
                return ResponseEntity.ok(response);
            } else {
                // username มีอยู่แล้ว
                response.put("available", false);
                response.put("message", "มีผู้ใช้นี้แล้ว");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response.put("available", false);
            response.put("message", "เกิดข้อผิดพลาด กรุณาลองใหม่ภายหลัง");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> create(
            @RequestPart("account") Account account,
            @RequestPart("file") MultipartFile file) {

        log.info("account -> {}", account);

        try {
            Path uploadPath = Paths.get(prefixPath);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String rawFileName = file.getOriginalFilename();
            String safeFileName = Paths.get(rawFileName).getFileName().toString(); // << ตัด path ออก

            String fileName = System.currentTimeMillis() + "_" + safeFileName;

            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // ✅ เก็บ path ลง Account ก่อน save
            account.setPathpicture(filePath.toString());

            log.info("account", account);

            accountService.createAccount(account);
            String escapedFileName = StringEscapeUtils.escapeHtml4(fileName);
            return ResponseEntity.ok("สร้างบัญชีและอัปโหลดไฟล์สำเร็จ: " + escapedFileName);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("เกิดข้อผิดพลาด: " + e.getMessage());
        }
    }

    @PutMapping("/editaccount/{id}")
    public ResponseEntity<String> update(
            @PathVariable Long id, @RequestBody Account account) {
        log.info("account -> {}", account);
        try {
            accountService.updateAccount(account, id);
            return ResponseEntity.ok("แก้ไขบัญชีเรียบร้อย: ");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("เกิดข้อผิดพลาด: " + e.getMessage());
        }

    }

    @PutMapping("/editpic/{id}")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<String> changePicture(
            @PathVariable Long id,
            @RequestParam("newfile") MultipartFile newfile // ✅ แก้ตรงนี้
    ) {
        log.info("📥 อัปโหลดไฟล์: id={}, fileName={}, size={} bytes", id, newfile.getOriginalFilename(),
                newfile.getSize());

        try {
            accountService.editpicture(id, newfile);
            return ResponseEntity.ok("แก้ไขรูปเรียบร้อย");
        } catch (Exception e) {
            log.error("❌ เกิดข้อผิดพลาดขณะอัปโหลด:", e);
            return ResponseEntity.status(500).body("เกิดข้อผิดพลาด: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getAccount(@PathVariable Long id) {
        try {
            Account result = accountService.getAccountById(id);

            Map<String, Object> response = new HashMap<>();
            response.put("data", result);
            response.put("message", "ดึงข้อมูลสำเร็จ");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            log.info(e.toString());
            error.put("message", "ดึงข้อมูลไม่สำเร็จ");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long id) {
        int rows = accountService.deleteAccount(id);

        if (rows > 0) {
            return ResponseEntity.ok("✅ ลบ Account ID " + id + " สำเร็จ");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("❌ ไม่พบ Account ID " + id + " สำหรับลบ");
        }
    }

    @PostMapping("/addCareer")
    public ResponseEntity<Map<String, Object>> addCareer(@RequestBody AccountCareer newCareer) {
        try {
            AccountCareer result = accountCareerService.addCareer(newCareer);

            Map<String, Object> response = new HashMap<>();
            response.put("data", result);
            response.put("message", "เพิ่มข้อมูลอาชีพสำเร็จ");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "ไม่สามารถดึงข้อมูลอาชีพได้");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @PostMapping("/addSport")
    public ResponseEntity<Map<String, Object>> addSport(@RequestBody AccountSport newSport) {
        try {
            AccountSport result = accountSportService.addSport(newSport);

            Map<String, Object> response = new HashMap<>();
            response.put("data", result);
            response.put("status", "เพิ่มข้อมูลกีฬาเรัยบร้อย");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "eไม่สามารถเพิ่มข้อมูลกีฬาได้");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

    }

    @PostMapping("/getCareer")
    public ResponseEntity<Map<String, Object>> getCareer(HttpServletRequest request, HttpServletResponse response,
            @RequestBody Account data) {
        try {

            log.info(ENTRIES);

            Map<String, Object> result = accountCareerService.getAccountCareerListByCondition(data);

            return new ResponseEntity<>(CommonUtils.response(result.get("careerList"), SUCCESS), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(CommonUtils.responseError(e.getMessage()), HttpStatus.OK);
        }
    }

    @GetMapping("/base64")
    public ResponseEntity<String> getImageAsBase64(@RequestParam String filename) {
        // 🔐 1. โฟลเดอร์ปลอดภัย
        Path targetPath = Paths.get(filename).normalize(); // เปลี่ยน path ให้ตรงระบบของคุณ

        log.info("pathpic" + targetPath);

        // ✅ 4. ตรวจสอบและอ่านไฟล์
        File imageFile = targetPath.toFile();
        if (!imageFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        try {
            byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
            String base64String = Base64.getEncoder().encodeToString(imageBytes);
            log.info("real path pic" + base64String);

            return ResponseEntity.ok(base64String);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }

    }

    @PostMapping("/getSport")
    public ResponseEntity<Map<String, Object>> getSport(HttpServletRequest request, HttpServletResponse response,
            @RequestBody Account data) {
        try {

            log.info(ENTRIES);

            Map<String, Object> result = accountSportService.getAccountSportListByCondition(data);

            return new ResponseEntity<>(CommonUtils.response(result.get("listSport"), SUCCESS), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(CommonUtils.responseError(e.getMessage()), HttpStatus.OK);
        }
    }

    @PostMapping("/accountpaging")
    public ResponseEntity<Map<String, Object>> getaccountlist(@RequestBody Paging param) {
        try {
            AccountList result = accountService.getAccountList(param);
            return new ResponseEntity<>(CommonUtils.response(result, SUCCESS), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(CommonUtils.responseError(e.getMessage()), HttpStatus.OK);
        }

    }

    @DeleteMapping("/deleteSport/{id}")
    public String addSport(@PathVariable Long id) {

        return accountSportService.deleteSport(id);
    }

    @DeleteMapping("/deleteCareer/{id}")
    public String deleteCareer(@PathVariable Long id) {
        return accountCareerService.deleteCareer(id);
    }

    @PostMapping("/checkLogin")
    public ResponseEntity<Map<String, Object>> checkLogin(@RequestBody Account loginData) {

        Account result = accountService.checkLogin(loginData);

        try {
            if (result != null) {
                Map<String, Object> response = new HashMap<>();

                response.put("data", result);
                response.put("message", "ดึงข้อมูลสำเร็จ");

                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();

                response.put("data", null);
                response.put("message", "ไม่เจอข้อมูลในระบบ");
                return ResponseEntity.ok(response);

            }

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "เกิดข้อผิดพลาด");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

        }

    }

    @PostMapping("/search")
    public ResponseEntity<Map<String, Object>> search(@RequestBody Account key) {
        try {

            List<Account> result = accountService.search(key);
            return new ResponseEntity<>(CommonUtils.response(result, SUCCESS), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(CommonUtils.responseError(e.getMessage()), HttpStatus.OK);
        }

    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadZip(@RequestParam String sourceFolder,
            @RequestParam String zipName) {
        try {
            return accountService.zipAndReturn(sourceFolder, zipName);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

}
