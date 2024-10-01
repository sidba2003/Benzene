package src.main.java.com.lang.benzene;

import static src.main.java.com.lang.benzene.TokenType.*;

import java.util.ArrayList;
import java.util.List;

class Parser {
    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    List<Stmt> parse(){
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()){
            statements.add(declaration());
        }

        return statements;
    }

    private Stmt declaration(){
        try {
            if (match(CLASS)) return classDeclaration();
            if (match(FUN)) return function("function");
            if (match(VAR)) return varDeclaration();
            return statement();
        } catch (ParseError error){
            synchronize();
            return null;
        }
    }

    private Stmt classDeclaration(){
        Token name = consume(IDENTIFIER, "Expect class name after 'class' keyword");
        consume(LEFT_BRACE, "Expect '{' after class name.");
        
        List<Stmt.Function> body = new ArrayList<>();
        List<Stmt.Function> staticBody = new ArrayList<>();

        while (!check(RIGHT_BRACE) && !isAtEnd()){
            if (match(STATIC)){
                staticBody.add(function("static method"));
                continue;
            }
            body.add(function("methods"));
        }
        
        consume(RIGHT_BRACE, "Expect '}' after class body.");

        return new Stmt.Class(name, body, staticBody);
    }

    private Stmt varDeclaration(){
        Token name = consume(IDENTIFIER, "Expect variable name.");

        Expr initializer = null;
        if (match(EQUAL)){
            initializer = expression();
        }

        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Var(name, initializer);
    }

    private Stmt whileStatement(){
        consume(LEFT_PAREN, "Expect '(' after 'while'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after 'while'.");

        Stmt statements = statement();

        return new Stmt.While(condition, statements);
    }

    private Stmt statement(){
        if (match(BREAK)) return breakStatement();
        if (match(CONTINUE)) return continueStatement();
        if (match(IF)) return ifStatement();
        if (match(PRINT)) return printStatement();
        if (match(RETURN)) return returnStatement();
        if (match(WHILE)) return whileStatement();
        if (match(LEFT_BRACE)) return new Stmt.Block(block());

        return expressionStatement();
    }

    private Stmt continueStatement(){
        return new Stmt.Continue(previous());
    }

    private Stmt breakStatement(){
        Token breakToken = previous();
        consume(SEMICOLON, "Expected ';' after 'break' keyword.");

        return new Stmt.Break(breakToken);
    }

    private Stmt ifStatement(){
        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after if condition.");

        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(ELSE)){
            elseBranch = statement();
        }

        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    private Stmt printStatement(){
        Expr value = expression();
        consume(SEMICOLON, "Expect ; after value.");
        return new Stmt.Print(value);
    }

    private Stmt returnStatement(){
        Token keyword = previous();
        Expr value = null;

        if (!check(SEMICOLON)){
            value = expression();
        }
        
        consume(SEMICOLON, "Expect ':' after return value.");

        return new Stmt.Return(keyword, value);
    }

    private Stmt expressionStatement(){
        Expr expr = expression();
        consume(SEMICOLON, "Expect ; after value.");
        return new Stmt.Expression(expr);
    }

    private Stmt.Function function(String kind){
        Token name = consume(IDENTIFIER, "Expect " + kind + " name.");
        
        consume(LEFT_PAREN, "Expect '(' after function name.");
        List<Token> parameters = new ArrayList<>();
        if (!check(RIGHT_PAREN)){
            do{
                if (parameters.size() >= 255){
                    error(peek(), "Can't have more than 255 parameters.");
                }

                parameters.add(
                    consume(IDENTIFIER, "Expect parameter name.")
                );
            } while (match(COMMA));
        }

        consume(RIGHT_PAREN, "Expect ')' after parameters.");

        consume(LEFT_BRACE, "Expect '{' before " + kind + " body.");
        List<Stmt> body  = block();
        
        return new Stmt.Function(name, parameters, body);
    }

    private List<Stmt> block(){
        List<Stmt> statements = new ArrayList<>();

        while (!check(RIGHT_BRACE) && !isAtEnd()){
            statements.add(declaration());
        }

        consume(RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    private Expr assignment(){
        Expr expr = or();

        if (match(EQUAL)){
            Token equals = previous();
            Expr value = assignment();

            if (expr instanceof Expr.Variable){
                Token name = ((Expr.Variable)expr).name;
                return new Expr.Assign(name, value);
            } else if(expr instanceof Expr.Get){
                Expr.Get get = (Expr.Get) expr;
                return new Expr.Set(get.object, get.name, value);
            }

            error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    private Expr or(){
        Expr expr = and();

        while (match(OR)){
            Token operator = previous();
            Expr right = and();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr and(){
        Expr expr = equality();

        while (match(AND)){
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr expression(){
        return assignment();
    }

    private Expr equality(){
        Expr expr = comparision();

        while (match(BANG_EQUAL, EQUAL_EQUAL)){
            Token operator = previous();
            Expr right = comparision();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr comparision(){
        Expr expr = term();

        while (match(LESS, LESS_EQUAL, GREATER, GREATER_EQUAL)){
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr term(){
        Expr expr = factor();

        while (match(PLUS, MINUS)){
            Token operator = previous();
            Expr right = factor();

            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr factor(){
        Expr expr = unary();

        while (match(SLASH, STAR)){
            Token operator = previous();
            Expr right = unary();

            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary(){
        if (match(BANG, MINUS)){
            Token operator = previous();
            Expr right = unary();

            return new Expr.Unary(operator, right);
        }

        return call();
    }

    private Expr call(){
        Expr expr = primary();

        while (true){
            if (match(LEFT_PAREN)){
                expr = finishCall(expr);
            } else if (match(DOT)){
                Token name = consume(IDENTIFIER, "Expect property name after '.'");
                expr = new Expr.Get(expr, name);
            } else {
                break;
            }
        }

        return expr;
    }

    private Expr finishCall(Expr callee){
        List<Object> arguments = new ArrayList<>();

        if (!check(RIGHT_PAREN)){
            do {
                if (arguments.size() > 255){
                    error(peek(), "can't have more than 255 arguments.");
                }
                
                if (match(FUN)){
                    arguments.add(function("function"));
                } else{
                    arguments.add(expression());
                }

            } while (match(COMMA));
        }

        Token paren = consume(RIGHT_PAREN, "Expect ')' after arguments.");
        return new Expr.Call(callee, paren, arguments);
    }

    private Expr primary(){
        if (match(FALSE)) return new Expr.Literal(false);
        if (match(TRUE)) return new Expr.Literal(true);
        if (match(NIL)) return new Expr.Literal(null);

        if (match(NUMBER, STRING)){
            return new Expr.Literal(previous().literal);
        }

        if (match(THIS)) return new Expr.This(previous());

        if (match(IDENTIFIER)) return new Expr.Variable(previous());

        if (match(LEFT_PAREN)){
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expect expression.");
    }

    private boolean match(TokenType... types){
        for (TokenType type : types){
            if (check(type)){
                advance();
                return true;
            }
        }

        return false;
    }

    private Token consume(TokenType type, String message){
        if (check(type)) return advance();

        throw error(peek(), message);
    }

    private boolean check(TokenType type){
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance(){
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd(){
        return peek().type == EOF;
    }

    private Token peek(){
        return tokens.get(current);
    }

    private Token previous(){
        return tokens.get(current - 1);
    }

    private ParseError error(Token token, String message){
        Benzene.error(token, message);
        return new ParseError();
    }

    private void synchronize(){
        advance();

        while (!isAtEnd()){
            if (previous().type == SEMICOLON) return;

            switch (peek().type){
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }
        }
    }
}
