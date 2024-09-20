package src.main.java.com.lang.benzene;

import java.util.List;


public interface BenzeneCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
    String toString();
}
