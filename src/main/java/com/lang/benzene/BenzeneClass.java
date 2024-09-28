package src.main.java.com.lang.benzene;

import java.util.List;
import java.util.Map;

public class BenzeneClass implements BenzeneCallable{
    final String name;
    private final Map<String, BenzeneFunction> methods;
    private final Map<String, BenzeneFunction> staticMethods;

    BenzeneClass(String name, Map<String, BenzeneFunction> methods, Map<String, BenzeneFunction> staticMethods){
        this.name = name;
        this.methods = methods;
        this.staticMethods = staticMethods;
    }

    BenzeneFunction findmethod(String name){
        if (methods.containsKey(name)){
            return methods.get(name);
        }

        return null;
    }

    BenzeneFunction findStaticMethod(String name){
        if (staticMethods.containsKey(name)){
            return staticMethods.get(name);
        }

        return null;
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
