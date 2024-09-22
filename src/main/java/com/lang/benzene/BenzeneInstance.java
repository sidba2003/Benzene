package src.main.java.com.lang.benzene;

import java.util.Map;
import java.util.HashMap;

public class BenzeneInstance {
    private BenzeneClass klass;
    private final Map<String, Object> fields = new HashMap<>();

    BenzeneInstance(BenzeneClass klass){
        this.klass = klass;
    }

    @Override
    public String toString(){
        return this.klass.name + " instance";
    }

    Object get(Token name){
        if (fields.containsKey(name.lexeme)){
            return fields.get(name.lexeme);
        }
        
        throw new RuntimeError(name, "Undefined property " + name.lexeme);
    }
}
