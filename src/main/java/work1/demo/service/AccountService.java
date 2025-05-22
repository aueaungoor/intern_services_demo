package work1.demo.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import work1.demo.controller.AccountController;
import work1.demo.data.AccountList;
import work1.demo.data.Paging;
import work1.demo.model.Account;
import work1.demo.repository.AccountRepository;

@Slf4j

@Service
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    private static final String SUCCUESS = "success";

    private static final String ERROR = "error";

    private static final String WHERE = "where";

    @Value("${pathpicture}")
    private String prefixPath;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    public String createAccount(Account criteria) {
        try {

            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO account (");

            List<Object> params = new ArrayList<>();
            List<String> columns = new ArrayList<>();

            if (criteria.getUsername() != null) {
                columns.add("username");
                params.add(criteria.getUsername());
            }
            if (criteria.getPassword() != null) {
                columns.add("password");
                params.add(criteria.getPassword());
            }
            if (criteria.getFname() != null) {
                columns.add("fname");
                params.add(criteria.getFname());
            }
            if (criteria.getLname() != null) {
                columns.add("lname");
                params.add(criteria.getLname());
            }
            if (null != criteria.getDescription()) {
                sb.append("description,");
                params.add(criteria.getDescription());
            }
            if (null != criteria.getGender()) {
                sb.append("gender,");
                params.add(criteria.getGender());
            }
            if (null != criteria.getBirthday()) {
                sb.append("birthday,");
                params.add(criteria.getBirthday());
            }

            sb.append("pathpicture ");
            sb.append(String.join(", ", columns));
            sb.append(") VALUES (");
            sb.append(columns.stream().map(c -> "?").collect(Collectors.joining(", ")));
            sb.append(")");
            jdbcTemplate.update(sb.toString(), params.toArray());

            log.info("testtest");

            return SUCCUESS;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ERROR;
        }
    }

    public boolean updateAccount(Account updated, Long id) throws IOException {

        String sql = """
                    UPDATE account
                    SET username = ?, password = ?, fname = ?, lname = ?, gender = ?, birthday = ?,description = ?
                    WHERE idaccount = ?
                """;

        int rows = jdbcTemplate.update(
                sql,
                updated.getUsername(),
                updated.getPassword(),
                updated.getFname(),
                updated.getLname(),
                updated.getGender(),
                updated.getBirthday(),
                updated.getDescription(),
                id);

        if (rows > 0) {
            return true;
        } else {
            return false;
        }
    }

    public int deleteAccount(Long id) {
        String sql = "DELETE FROM account WHERE idaccount = ?";
        return jdbcTemplate.update(sql, id);
    }

    public boolean checkLogin(String username, String password) {

        String sql = "SELECT COUNT(*) FROM account WHERE username = ? AND password = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username, password);

        return count != null && count > 0;

    }

    public void editpicture(Long id, MultipartFile file) {
        try {
            // 1. เตรียม path
            Path uploadPath = Paths.get(prefixPath);
            log.info(prefixPath);

            log.info("uploadPath->{}", uploadPath);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 2. ตั้งชื่อไฟล์ใหม่กันชื่อซ้ำ

            String rawFileName = file.getOriginalFilename();
            String safeFileName = sanitizeFileName(rawFileName);

            String fileName = System.currentTimeMillis() + "_" + safeFileName;

            Path filePath = uploadPath.resolve(fileName);

            // 3. เซฟไฟล์ลง disk
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 4. โหลด Account จาก DB
            String sql = "SELECT * FROM account WHERE idaccount = ?";
            Account acc = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Account.class), id);

            log.info("uploadPath->{}", uploadPath);
            // 5. เซ็ต path แล้วอัปเดต
            acc.setPathpicture(uploadPath.toString() + "/" + fileName); // ❗ ใช้ path แบบ URL สำหรับ frontend

            // 6. อัปเดต DB
            String updateSql = "UPDATE account SET pathpicture = ? WHERE idaccount = ?";
            jdbcTemplate.update(updateSql, acc.getPathpicture(), id);

        } catch (IOException e) {
            throw new RuntimeException("❌ ไม่สามารถบันทึกรูปได้: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("❌ เกิดข้อผิดพลาดในระบบ: " + e.getMessage(), e);
        }
    }

    public String sanitizeFileName(String original) {
        return Paths.get(original).getFileName().toString();
    }

    public AccountList getAccountList(Paging param) {

        String sql = "SELECT * FROM account ORDER BY idaccount LIMIT ? OFFSET ?";
        String findtotal = "SELECT COUNT(*) FROM account";

        long total = jdbcTemplate.queryForObject(findtotal, Long.class);

        List<Account> accList = jdbcTemplate.query(
                sql,
                new BeanPropertyRowMapper<>(Account.class),
                param.getLimit(),
                (param.getIndex() - 1) * param.getLimit());

        AccountList response = new AccountList();
        response.setAccount(accList);
        response.setTotal(total);

        log.info("✅ accountlist: {}", response);

        return response;
    }

    public List<Account> search(Account param) {
        String like = "%" + param.getUsername() + "%";
        String sql = "SELECT * FROM account WHERE fname LIKE ? OR lname LIKE ?";

        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Account.class), like, like);

    }

    public ResponseEntity<Resource> zipAndReturn(String sourceFolder, String zipFileName) throws IOException {
        String zipPath = "/tmp/" + zipFileName + ".zip";

        // 1. สร้างไฟล์ zip
        zipFolder(sourceFolder, zipPath);

        // 2. เตรียม Resource สำหรับ ResponseEntity
        File zipFile = new File(zipPath);
        if (!zipFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(zipFile));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFile.getName() + "\"")
                .contentLength(zipFile.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    // เมธอดหลักในการ zip โฟลเดอร์
    private void zipFolder(String sourceFolderPath, String outputZipPath) throws IOException {
        FileOutputStream fos = new FileOutputStream(outputZipPath);
        ZipOutputStream zos = new ZipOutputStream(fos);

        File sourceFolder = new File(sourceFolderPath);
        zipFile(sourceFolder, sourceFolder.getName(), zos);

        zos.close();
        fos.close();
    }

    // recursive zip file
    private void zipFile(File fileToZip, String fileName, ZipOutputStream zos) throws IOException {
        // ป้องกัน Path Traversal โดยกำหนด path ปลอดภัย
        Path basePath = Paths.get("/home/aueaungoorn/uploads").toRealPath(); // 🔐 แก้ตรงนี้หากคุณใช้ path อื่น
        Path requestedPath = fileToZip.toPath().toRealPath();

        // ❗ หาก path ไม่ได้อยู่ภายใน base path ให้ block ทันที
        if (!requestedPath.startsWith(basePath)) {
            throw new SecurityException("❌ Path traversal detected: " + requestedPath);
        }

        // ข้ามไฟล์ซ่อน
        if (fileToZip.isHidden())
            return;

        // ถ้าเป็นโฟลเดอร์ → zip recursive
        if (fileToZip.isDirectory()) {
            if (!fileName.endsWith("/"))
                fileName += "/";
            zos.putNextEntry(new ZipEntry(fileName));
            zos.closeEntry();

            File[] children = fileToZip.listFiles();
            if (children != null) {
                for (File childFile : children) {
                    zipFile(childFile, fileName + childFile.getName(), zos); // 🔁 ทำซ้ำสำหรับไฟล์ลูก
                }
            }
            return;
        }

        // ถ้าเป็นไฟล์ปกติ → zip ไฟล์
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zos.putNextEntry(zipEntry);

        byte[] buffer = new byte[1024];
        int len;
        while ((len = fis.read(buffer)) > 0) {
            zos.write(buffer, 0, len);
        }

        fis.close();
    }

    public Account checkusername(Account criteria) {
        try {
            StringBuilder sb = new StringBuilder();
            List<Object> params = new ArrayList<>();

            sb.append("SELECT * FROM account WHERE ");

            if (criteria.getUsername() != null && !criteria.getUsername().isEmpty()) {
                sb.append("username = ?");
                sb.append("limit 1");
                params.add(criteria.getUsername());

                log.info("SQL: {}", sb.toString());

                return jdbcTemplate.queryForObject(
                        sb.toString(),
                        new BeanPropertyRowMapper<>(Account.class),
                        params.toArray());
            } else {
                log.info("Username is null or empty");
                return null;
            }

        } catch (EmptyResultDataAccessException e) {
            log.info("No account found for username: {}", criteria.getUsername());
            return null;
        } catch (Exception e) {
            log.error("Error in checkusername", e);
            return null;
        }
    }

}
