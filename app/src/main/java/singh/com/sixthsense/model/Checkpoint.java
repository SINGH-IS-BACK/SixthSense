package singh.com.sixthsense.model;

/**
 * Created by kthethi on 11/09/16.
 */
public class Checkpoint {

    EstimoteBeacon source;
    EstimoteBeacon destination;
    int stepNumber;
    String voiceText;

    public EstimoteBeacon getSource() {
        return source;
    }

    public void setSource(EstimoteBeacon source) {
        this.source = source;
    }

    public EstimoteBeacon getDestination() {
        return destination;
    }

    public void setDestination(EstimoteBeacon destination) {
        this.destination = destination;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int step) {
        this.stepNumber = step;
    }

    public String getVoiceText() {
        return voiceText;
    }

    public void setVoiceText(String voiceText) {
        this.voiceText = voiceText;
    }
}
