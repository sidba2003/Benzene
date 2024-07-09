package src.main.java.com.craftinginterpreters.lox;

import static src.main.java.com.craftinginterpreters.lox.TokenType.MINUS;
import static src.main.java.com.craftinginterpreters.lox.TokenType.PLUS;

public class AstPrinter implements Expr.Visitor<String> {
    public String generateTree(Expr expression){
        return expression.accept(this);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr){
        return printTree(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr){
        return printTree("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr){
        if (expr.value == null) return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr){
        return printTree(expr.operator.lexeme, expr.right);
    }

    private String printTree(String operator, Expr... exprs){
        StringBuilder treePrint = new StringBuilder();

        treePrint.append("(");
        treePrint.append(operator);

        for (Expr expr : exprs){
            treePrint.append(" ");
            treePrint.append(expr.accept(this));
        }

        treePrint.append(")");

        return treePrint.toString();
    }

    public static void main(String[] args){
        Expr expr = new Expr.Binary(
            new Expr.Binary(new Expr.Literal(21), new Token(PLUS, "+", null, 0), new Expr.Literal(2)),
            new Token(PLUS, "+", null, 0),
            new Expr.Binary(new Expr.Unary(new Token(MINUS, "-", null, 0), new Expr.Literal(2)), new Token(PLUS, "+", null, 0), new Expr.Literal(2))
        );

        System.out.println(new AstPrinter().generateTree(expr));
    }
}
