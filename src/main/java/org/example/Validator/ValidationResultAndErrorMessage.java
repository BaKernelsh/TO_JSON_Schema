package org.example.Validator;

public class ValidationResultAndErrorMessage {

    private boolean valid = false;
    private String message = "";


    public ValidationResultAndErrorMessage(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public ValidationResultAndErrorMessage(boolean valid) {
        this.valid = valid;
    }

    public ValidationResultAndErrorMessage() {
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static ValidationResultAndErrorMessage newInstanceValid(){
        return new ValidationResultAndErrorMessage(true);
    }

}
