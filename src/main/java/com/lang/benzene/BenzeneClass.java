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

    BenzeneFunction get(String name){
        if (staticMethods.containsKey(name)){
            return staticMethods.get(name);
        } else{
            throw new RuntimeError(null, "value for " + name + " not found in class");
        }
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> argumemts){
        BenzeneInstance instance = new BenzeneInstance(this);
        BenzeneFunction initializer = findmethod("init");
        if (initializer != null){
            initializer.bind(instance).call(interpreter, argumemts);
        }
        return instance;
    }

    @Override
    public int arity(){
        BenzeneFunction initializer = findmethod("init");
        if (initializer == null){
            return 0;
        }

        return initializer.arity();
    }

    @Override
    public String toString(){
        return "<user defined class: " + this.name + ">";
    }
    
}
