package src.main.java.com.lang.benzene;

public class ContinueError extends RuntimeException {
    int line;

    ContinueError(int line, String message){
        super(message);
        this.line = line;
    }
}
