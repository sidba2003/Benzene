package src.main.java.com.lang.benzene;

import java.util.List;

class BenzeneFunction implements BenzeneCallable{
    private final Stmt.Function declaration;
    private final Environment closure;

    BenzeneFunction(Stmt.Function declaration, Environment closure){
        this.closure = closure;
        this.declaration = declaration;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments){
        Environment environment = new Environment(closure);

        for (int i = 0; i < declaration.params.size(); i++){
            Object argument = arguments.get(i);

            if (argument instanceof Stmt.Function){
                BenzeneFunction function = new BenzeneFunction((Stmt.Function) argument, environment);
                environment.define(declaration.params.get(i).lexeme, function);
                
                continue;
            }

            environment.define(declaration.params.get(i).lexeme, argument);
        }

        try{
            interpreter.executeBlock(declaration.body, environment);
        } catch (ReturnError returnStmt){
            return returnStmt.returnValue; 
        }

        return null;
    }

    @Override
    public int arity(){
        return declaration.params.size();
    }

    @Override
    public String toString(){
        return "<user defined function: " + this.declaration.name.lexeme + ">";
    }
}

