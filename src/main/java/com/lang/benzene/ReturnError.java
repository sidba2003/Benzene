package src.main.java.com.lang.benzene;

public class ReturnError extends RuntimeException{
    Object returnValue;

    ReturnError(Object value){
        super(value.toString());

        this.returnValue = value;
    }
}
