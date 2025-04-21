package work1.demo.service;

import java.util.HashMap;
import java.util.Map;

public class CommonUtils {

    public static Map<String, Object> response(Object data, String status) {
        Map<String, Object> result = new HashMap<>();
        result.put("status", status);
        result.put("data", data);
        
        return result;
    }

    public static Map<String, Object> responseError(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "ERROR");
        result.put("message", message);
        return result;
    }

   

}
