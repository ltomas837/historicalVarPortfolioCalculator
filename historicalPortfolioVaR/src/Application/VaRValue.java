package Application;


/**
 * The VaRValue class is used to stock the VaR value an the error message if
 * something wrong happen (incorrect inputs).
 */
public class VaRValue {

    private String errorMessage = null;
    private Double value = null;

    public VaRValue(){}

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

}
