# Context-Free Grammar (CFG) for the Benzene

This document describes the grammar rules used for the language.

## Program Structure
program ::= declaration* EOF

## Declarations
declaration ::= varDecl | funDecl | classDecl | statement

varDecl ::= "var" IDENTIFIER ( "=" expression )? ";"

funDecl ::= "fun" function

function ::= IDENTIFIER "(" parameters? ")" block

parameters ::= IDENTIFIER ( "," IDENTIFIER )*

classDecl ::= "class" IDENTIFIER ( "<" IDENTIFIER )? "{" function* "}"

## Statements
statement ::= exprStmt | printStmt | block | ifStmt | whileStmt | returnStmt

exprStmt ::= expression ";"

printStmt ::= "print" expression ";"

block ::= "{" declaration* "}"

ifStmt ::= "if" "(" expression ")" statement ( "else" statement )?

whileStmt ::= "while" "(" expression ")" statement

returnStmt ::= "return" expression? ";"

## Expressions
expression ::= assignment

## Logical and Comparison Operations
assignment ::= IDENTIFIER "=" assignment | logic_or

logic_or ::= logic_and ( "or" logic_and )*

logic_and ::= equality ( "and" equality )*

equality ::= comparison ( ( "!=" | "==" ) comparison )*

comparison ::= term ( ( ">" | ">=" | "<" | "<=" ) term )*

## Arithmetic Operations

term ::= factor ( ( "-" | "+" ) factor )*

factor ::= unary ( ( "/" | "" ) unary )

## Unary and Function Calls

unary ::= ( "!" | "-" ) unary | call

call ::= primary ( "(" arguments? ")" | "." IDENTIFIER )*

arguments ::= expression ( "," expression )*

## Primary Expressions

primary ::= "true" | "false" | "nil" | "this" | NUMBER | STRING | IDENTIFIER | "(" expression ")"

### Explanation
- **Program**: The entry point consists of multiple declarations followed by the `EOF`.
- **Declarations**: These include variable, function, and class declarations, as well as statements.
- **Statements**: Supported statements include expressions, print statements, blocks, if/else, while, and return.
- **Expressions**: Built around assignments, logical operations, comparisons, and arithmetic operations.
- **Primary Expressions**: Simple expressions like literals (`true`, `false`, `nil`), identifiers, and grouped expressions.
