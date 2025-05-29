package work1.demo.data;

import lombok.Data;
import work1.demo.model.Country;
import work1.demo.model.District;
import work1.demo.model.Province;

@Data
public class Location {

    private Country country;

    private Province province;

    private District district;

    private Long idaccount;

}
