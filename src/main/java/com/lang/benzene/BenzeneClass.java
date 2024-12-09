package src.main.java.com.lang.benzene;

import java.util.List;
import java.util.Map;

public class BenzeneClass implements BenzeneCallable{
    final String name;
    private final Map<String, BenzeneFunction> methods;
    private final Map<String, BenzeneFunction> staticMethods;
    final BenzeneClass superClass;

    BenzeneClass(String name, BenzeneClass superClass, Map<String,  BenzeneFunction> methods, Map<String, BenzeneFunction> staticMethods){
        this.name = name;
        this.methods = methods;
        this.staticMethods = staticMethods;
        this.superClass = superClass;
    }

    BenzeneFunction findmethod(String name){
        if (methods.containsKey(name)){
            return methods.get(name);
        }

        if (superClass != null){
            return superClass.findmethod(name);
        }

        return null;
    }

    BenzeneFunction get(String name){
        if (staticMethods.containsKey(name)){
            return staticMethods.get(name);
        } 
        
        if (superClass != null){
            return superClass.get(name);
        }

        return null;
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
