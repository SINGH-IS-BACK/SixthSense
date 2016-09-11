package singh.com.sixthsense.model;

import java.util.ArrayList;

/**
 * Created by kthethi on 10/09/16.
 */
public class ServerResponse {

    ArrayList<Checkpoint> route;
    String status;
    String code;
    String message;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Checkpoint> getRoute() {
        return route;
    }

    public void setRoute(ArrayList<Checkpoint> route) {
        this.route = route;
    }
}
