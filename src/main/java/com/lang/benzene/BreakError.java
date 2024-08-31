package src.main.java.com.lang.benzene;

public class BreakError extends RuntimeException {
    private int line;

    BreakError(int line, String message){
        super(message);
        this.line = line;
    }
}
