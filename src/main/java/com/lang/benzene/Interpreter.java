package src.main.java.com.lang.benzene;


import static src.main.java.com.lang.benzene.TokenType.SLASH;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    final Environment globals = new Environment();
    private Environment environment = globals;

    Interpreter(){
        globals.define("clock", new BenzeneCallable() {
            @Override
            public int arity(){return 0;}

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments){
                return (double)System.currentTimeMillis() / 1000.0;
            }

            @Override
            public String toString(){
                return "<native fn>";
            }
        });
    }

    void interpret(List<Stmt> statements){
        try {
            for (Stmt stmt : statements){
                execute(stmt);
            }
        } catch (RuntimeError error){
            Benzene.runtimeError(error);
        }
    }

    @Override
    public Object visitAnonymousFunction(Expr.AnonymousFunction expr){
        return new BenzeneAnonymousFunction(expr, environment);
    }
    
    @Override
    public Void visitExpressionStmt(Stmt.Expression statement){
        System.out.println(evaluate(statement.expression));
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt){
        BenzeneFunction function = new BenzeneFunction(stmt, environment);
        environment.define(stmt.name.lexeme, function);
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt){
        Object value = null;

        if (stmt.value != null){
            value = evaluate(stmt.value);
        }

        throw new ReturnError(value);
    }

    public Void visitIfStmt(Stmt.If stmt){
        if (isTruthy(evaluate(stmt.condition))){
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null){
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print statement){
        Object value = evaluate(statement.expression);
        System.out.println(stringify(value));
        return null;

    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt){
        Object value = null;
        if (stmt.initializer != null){
            value = evaluate(stmt.initializer);
        }

        environment.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt){
        while (isTruthy(evaluate(stmt.condition))){
            try{
                execute(stmt.body);
            } catch (RuntimeException error){
                if (error instanceof ContinueError) continue;
                else if (error instanceof BreakError) break;
                else throw error;
            }
        }

        return null;
    }

    @Override
    public Void visitBreakStmt(Stmt.Break stmt){
        int breakLine = stmt.token.line;

        throw new BreakError(breakLine, "Encountered a break statement on line " + breakLine + ".");
    }

    @Override
    public Void visitContinueStmt(Stmt.Continue stmt){
        int continueLine = stmt.token.line;

        throw new ContinueError(stmt.token.line, "Encountered a break statement on line " + continueLine + ".");
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr){
        Object value = evaluate(expr.value);
        environment.assign(expr.name, value);

        return value;
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr){
        return expr.value;
    } 

    @Override
    public Object visitLogicalExpr(Expr.Logical expr){
        Object left = evaluate(expr.left);

        if (expr.operator.type == TokenType.OR){
            if (isTruthy(left)) return left;
        } else{
            if (!isTruthy(left)) return left;
        }

        return isTruthy(expr.right);
    }

    @Override
    public Object visitSetExpr(Expr.Set expr){
        Object object = evaluate(expr.object);

        if(!(object instanceof BenzeneInstance)){
            throw new RuntimeError(expr.name, "Only instances have fields.");
        }

        Object value = evaluate(expr.value);
        ((BenzeneInstance) object).set(expr.name, value);

        return null;
    }

    @Override
    public Object visitThisExpr(Expr.This expr){
        return environment.get(expr.keyword);
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr){
        return evaluate(expr.expression);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr){
        Object right = evaluate(expr.right);

        switch (expr.operator.type){
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double)right;
        }

        return null;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr){
        return environment.get(expr.name);
    }

    private void checkNumberOperand(Token operator, Object operand){
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right){
        if (left instanceof Double && right instanceof Double) {
            if (operator.type == SLASH && (Double)right == 0){
                throw new RuntimeError(operator, "Division by zero.");
            }
            return;
        }
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private boolean isTruthy(Object object){
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;

        return true;
    }

    private boolean isEqual(Object a, Object b){
        if (a == null && b == null) return true;
        if (a == null) return false;

        return a.equals(b); 
    }

    private String stringify(Object object){
        if (object == null) return "nil";

        if (object instanceof Double){
            String text = object.toString();

            if (text.endsWith(".0")){
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }

    private void execute(Stmt statement){
        statement.accept(this);
    }

    void executeBlock(List<Stmt> statements, Environment environment){
        Environment previous = this.environment;
        try {
            this.environment = environment;

            for (Stmt statement : statements){
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    @Override 
    public Void visitBlockStmt(Stmt.Block stmt){
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt){
        environment.define(stmt.name.lexeme, null);

        Map<String, BenzeneFunction> methods = new HashMap<>();
        for (Stmt.Function method : stmt.methods){
            BenzeneFunction function = new BenzeneFunction(method, environment);
            methods.put(method.name.lexeme, function);
        }

        Map<String, BenzeneFunction> staticMethods = new HashMap<>();
        for (Stmt.Function staticMethod : stmt.staticMethods){
            BenzeneFunction function = new BenzeneFunction(staticMethod, environment);
            staticMethods.put(staticMethod.name.lexeme, function);
        }

        BenzeneClass klass = new BenzeneClass(stmt.name.lexeme, methods, staticMethods);
        environment.assign(stmt.name, klass);

        return null;
    }
    
    private Object evaluate(Expr expression){
        return expression.accept(this);
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr){
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type){
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left <= (double)right;
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left - (double)right;
            case PLUS:
                if (left instanceof Double && right instanceof Double){
                    return (double)left + (double)right;
                }
                if (left instanceof String && right instanceof String){
                    return (String)left + (String)right;
                }

                if (left instanceof Double && right instanceof String){
                    return stringify(left) + right;
                }

                if (left instanceof String && right instanceof Double){
                    return left + stringify(right);
                }

                throw new RuntimeError(expr.operator, "Each operand must be a string or a number.");
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                return (double)left / (double)right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
        }

        return null;
    }

    @Override
    public Object visitCallExpr(Expr.Call expr){
        Object callee = evaluate(expr.callee);

        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments){
            Object evaluatedArgument = argument;

            evaluatedArgument = evaluate(argument);

            arguments.add(evaluatedArgument);
        }

        if (!(callee instanceof BenzeneCallable)){
            throw new RuntimeError(expr.paren, "Can only call function and classes.");
        }

        BenzeneCallable function = (BenzeneCallable)callee;

        if (arguments.size() != function.arity()){
            throw new RuntimeError(expr.paren, "Expected" + function.arity() + "arguments. But got" + arguments.size());
        }
        return function.call(this, arguments);
    }

    @Override
    public Object visitGetExpr(Expr.Get expr){
        Object object = evaluate(expr.object);

        if (object instanceof BenzeneClass){
            BenzeneFunction staticFunction = ((BenzeneClass)object).findStaticMethod(expr.name.lexeme);
            return staticFunction;
        }

        if (object instanceof BenzeneInstance){
            return ((BenzeneInstance) object).get(expr.name);
        }

        throw new RuntimeError(expr.name, "Only instances hasve properties.");
    }
}
