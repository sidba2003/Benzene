package src.main.java.com.lang.benzene;

import java.util.List;

public class BenzeneClass implements BenzeneCallable{
    final String name;

    BenzeneClass(String name){
        this.name = name;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> argumemts){
        BenzeneInstance instance = new BenzeneInstance(this);
        return instance;
    }

    @Override
    public int arity(){
        return 0;
    }

    @Override
    public String toString(){
        return "<user defined class: " + this.name + ">";
    }
    
}
