package src.main.java.com.lang.benzene;

import static src.main.java.com.lang.benzene.TokenType.SLASH;

import java.util.List;;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    private Environment environment = new Environment();

    void interpret(List<Stmt> statements){
        try {
            for (Stmt stmt : statements){
                execute(stmt);
            }
        } catch (RuntimeError error){
            Benzene.runtimeError(error);
        }
    }

    public Void visitExpressionStmt(Stmt.Expression statement){
        evaluate(statement.expression);
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
    public Object visitAssignExpr(Expr.Assign expr){
        Object value = evaluate(expr);
        environment.assign(expr.name, value);

        return value;
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr){
        return expr.value;
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
                throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                return (double)left / (double)right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
        }

        return null;
    }
}
