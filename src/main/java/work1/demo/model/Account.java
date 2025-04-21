package work1.demo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Data
public class Account {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idaccount;

   
        public String username;
        public String password;
        public String fname;
        public String lname;
        public String description;
        public String gender;
       

        @JsonFormat(pattern = "yyyy-MM-dd")
private Date birthday;

        public String pathpicture;
   
    

   
}
