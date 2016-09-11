package singh.com.sixthsense.model;

import java.util.ArrayList;

/**
 * Created by kthethi on 11/09/16.
 */
public class DestinationResponse {

    EstimoteBeacon source;
    ArrayList<EstimoteBeacon> destinations;
    String status;
    String code;
    String message;

    public EstimoteBeacon getSource() {
        return source;
    }

    public void setSource(EstimoteBeacon source) {
        this.source = source;
    }

    public ArrayList<EstimoteBeacon> getDestinations() {
        return destinations;
    }

    public void setDestinations(ArrayList<EstimoteBeacon> destinations) {
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
