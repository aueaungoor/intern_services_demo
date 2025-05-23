package work1.demo.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Account {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long idaccount;

        private String username;
        private String password;
        private String fname;
        private String lname;
        private String description;
        private String gender;
        private Boolean isAdmin;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private Date birthday;

        private String pathpicture;

}
