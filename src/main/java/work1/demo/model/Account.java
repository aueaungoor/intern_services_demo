package work1.demo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

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

        @JsonFormat(pattern = "yyyy-MM-dd")
        private Date birthday;

        private String pathpicture;

}
