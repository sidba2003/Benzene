package src.main.java.com.lang.benzene;

import java.util.List;

class BenzeneFunction implements BenzeneCallable{
    private final Stmt.Function declaration;

    BenzeneFunction(Stmt.Function function){
        this.declaration = declaration;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments){
        Environment environment = new Environment(interpreter.globals);

        for (int i = 0; i < declaration.params.size(); i++){
            environment.define(declaration.params.get(i).lexeme, arguments.get(i));
        }

        interpreter.executeBlock(declaration.body, environment);
        return null;
    }

    @Override
    public int arity(){
        return declaration.params.size();
    }
}

