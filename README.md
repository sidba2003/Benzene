The syntax formalisation for Benzene is as follows:

program     ::= declaration* EOF ;

declaration ::= varDecl
              | funDecl
              | classDecl
              | statement ;

varDecl     ::= "var" IDENTIFIER ( "=" expression )? ";" ;

funDecl     ::= "fun" function ;

function    ::= IDENTIFIER "(" parameters? ")" block ;

parameters  ::= IDENTIFIER ( "," IDENTIFIER )* ;

classDecl   ::= "class" IDENTIFIER ( "<" IDENTIFIER )? "{"
                function* 
                "}" ;

statement   ::= exprStmt
              | printStmt
              | block
              | ifStmt
              | whileStmt
              | returnStmt ;

exprStmt    ::= expression ";" ;

printStmt   ::= "print" expression ";" ;

block       ::= "{" declaration* "}" ;

ifStmt      ::= "if" "(" expression ")" statement ( "else" statement )? ;

whileStmt   ::= "while" "(" expression ")" statement ;

returnStmt  ::= "return" expression? ";" ;

expression  ::= assignment ;

assignment  ::= IDENTIFIER "=" assignment
              | logic_or ;

logic_or    ::= logic_and ( "or" logic_and )* ;

logic_and   ::= equality ( "and" equality )* ;

equality    ::= comparison ( ( "!=" | "==" ) comparison )* ;

comparison  ::= term ( ( ">" | ">=" | "<" | "<=" ) term )* ;

term        ::= factor ( ( "-" | "+" ) factor )* ;

factor      ::= unary ( ( "/" | "*" ) unary )* ;

unary       ::= ( "!" | "-" ) unary
              | call ;

call        ::= primary ( "(" arguments? ")" | "." IDENTIFIER )* ;

arguments   ::= expression ( "," expression )* ;

primary     ::= "true"
              | "false"
              | "nil"
              | "this"
              | NUMBER
              | STRING
              | IDENTIFIER
              | "(" expression ")" ;

