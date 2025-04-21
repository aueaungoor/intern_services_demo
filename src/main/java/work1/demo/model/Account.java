package work1.demo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

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
        @Temporal(TemporalType.DATE)
        public Date birthday;

        public String pathpicture;
   
    

   
}
