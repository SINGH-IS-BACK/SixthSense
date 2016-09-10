package singh.com.sixthsense.model;

import java.util.ArrayList;

/**
 * Created by kthethi on 11/09/16.
 */
public class DestinationResponse {

    Beacon source;
    ArrayList<Beacon> destinations;
    String status;
    String code;
    String message;

    public Beacon getSource() {
        return source;
    }

    public void setSource(Beacon source) {
        this.source = source;
    }

    public ArrayList<Beacon> getDestinations() {
        return destinations;
    }

    public void setDestinations(ArrayList<Beacon> destinations) {
        this.destinations = destinations;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
