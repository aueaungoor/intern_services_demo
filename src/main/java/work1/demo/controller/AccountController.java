package work1.demo.controller;


import work1.demo.data.AccountCareerData;
import work1.demo.data.AccountList;
import work1.demo.data.Paging;
import work1.demo.model.Account;
import work1.demo.model.AccountCareer;
import work1.demo.model.AccountSport;
import work1.demo.service.AccountCareerService;
import work1.demo.service.AccountService;
import work1.demo.service.AccountsportService;
import work1.demo.service.CommonUtils;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.File;
import java.nio.file.*;


import work1.demo.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/accounts")
public class AccountController {

    
    @Value("")
    private String prefixPath;

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountsportService accountSportService;
    @Autowired
    private AccountCareerService accountCareerService;

    public static final String SUCCESS = "SUCCESS";
    public static final String ENTRIES = "entries";

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);


    
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

        return ResponseEntity.ok("สร้างบัญชีและอัปโหลดไฟล์สำเร็จ: " + fileName);
    } catch (IOException e) {
        return ResponseEntity.status(500).body("เกิดข้อผิดพลาด: " + e.getMessage());
    }
}


   @PutMapping("/editaccount/{id}")
   public ResponseEntity<String> update(
@PathVariable Long id , @RequestBody Account account )
    {
        log.info("account -> {}",account);
        try{
            accountService.updateAccount(account , id);
            return ResponseEntity.ok("แก้ไขบัญชีเรียบร้อย: " );
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body("เกิดข้อผิดพลาด: " + e.getMessage());
        }
       
   }

   @PutMapping("/editpic/{id}")
   @CrossOrigin(origins = "http://localhost:4200")
   public ResponseEntity<String> changePicture(
       @PathVariable Long id,
       @RequestParam("newfile") MultipartFile newfile  // ✅ แก้ตรงนี้
   ) {
       log.info("📥 อัปโหลดไฟล์: id={}, fileName={}, size={} bytes", id, newfile.getOriginalFilename(), newfile.getSize());
   
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
        Optional<Account> result = accountService.getAccountById(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", result);
        response.put("message", "ดึงข้อมูลสำเร็จ");

        return ResponseEntity.ok(response);
    } catch (Exception e) {
        Map<String, Object> error = new HashMap<>();
        error.put("message", "ไม่สามารถดึงข้อมูลอาชีพได้");

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
public ResponseEntity<Map<String , Object>> addSport(@RequestBody AccountSport newSport) {
    try{
AccountSport result = accountSportService.addSport(newSport);

Map<String , Object> response = new HashMap<>();
response.put("data" , result);
response.put("status","เพิ่มข้อมูลกีฬาเรัยบร้อย");

return ResponseEntity.ok(response);
    } catch(Exception e)
    {
        Map<String , Object> error = new HashMap<>();
        error.put("status","eไม่สามารถเพิ่มข้อมูลกีฬาได้");
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
        File imageFile = new File(filename);

        if (!imageFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        try {
            byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
            String base64String = Base64.getEncoder().encodeToString(imageBytes); // ✅ ใช้ของ java.util
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
public ResponseEntity<Map<String, Object>>  getaccountlist(@RequestBody Paging param)
{
    try{
         AccountList result = accountService.getAccountList(param);
         return new ResponseEntity<>(CommonUtils.response(result ,  SUCCESS), HttpStatus.OK);

    } catch(Exception e){
        return new ResponseEntity<>(CommonUtils.responseError(e.getMessage()), HttpStatus.OK);
    }
 
}



@DeleteMapping("/deleteSport/{id}")
public String addSport(@PathVariable Long id ){
   
    return accountSportService.deleteSport(id);
}

@DeleteMapping("/deleteCareer/{id}")
public String deleteCareer(@PathVariable Long id) {
    return accountCareerService.deleteCareer(id);
}

@PostMapping("/checkLogin")
public boolean checkLogin(@RequestBody Map<String, String> loginData) {
{
    String username = loginData.get("username");
    String password = loginData.get("password");

    boolean valid = accountService.checkLogin(username , password);

    if(valid)
    {
        return true;
    }
    else{
        return false;
    }
    
}
}


 @PostMapping("/search")
 public ResponseEntity <Map<String , Object>> search(@RequestBody Account key) 
 {
   try{
    List<Account> result = accountService.search(key);
     return new ResponseEntity<>(CommonUtils.response(result ,  SUCCESS), HttpStatus.OK);

   } catch(Exception e)
   {
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
