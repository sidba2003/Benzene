This interpreted implementation of Benzene uses a Top-Down parser to translate Benzene source code into an Abstract Syntax Tree which is then interpreted by the interpreter.

The interpreter uses the Visitor Design Pattern as it allowed me to easily add support for new tree nodes.


# Usage

## Variable Declarations and Types
Variables ared ecalred by adding the `var` keyword before an identifier. The variables do not need to be intialised during decalration. For example, 
```
  var x;
  var x = 24;
```
Are both examples of valid Benzene code.


Benzene supports the basic types of numbers (which represent Java's int and double), string, boolean, and null.
```
  var x = false;
  var x = true;
  var x = "Hello, world!";
  var x = null;
```
Since Benzene is a dynamically typed language, the types do not need to be specified.


## Operations

Benzene supprots various types of operations, which include, `+`, `-`, `/`, `*` and the following comparator operators, `==`, `<=`, `<`, `>=`, `>`, `!=`.
Furthermore, numbers are implicitly converted to string when adding the two.

For examples,
```
  var x = "Hello";
  var y = "world";
  print x + y; // this is valid code (outputs, Helloworld)

  var z = 1;
  print x + z; // this is valid code too (outputs, Hello1)
```

## Functions and Statements
Single line comments are applied using the `//`, and multi line comments are applied this way `/* ...this is a multi-line comment...**/`

Print statement is a native statement, and is used by writing the `print` keyword followed by the experession whose value needs to be printed.

Functions are defined using the fun keyword, without the need to explicitly specify the types and the values are returned using the `return` keyword,
```
  fun main(a, b){
    print a + b;
    return 1;
  }
```
Furthermore, Benzene supports first class functions,
```
  // this is valid
  newFunction = main
  newFunction(1, 2);
```
Benzene also supports Anonymous functions, which are implemented using the `anonymous` keyword followed by the function signature,
```
  var function = anonymous(a){print a};
  function("Hello, world!"): // this is valid

  var firstClassFunction = function;
  firstClassFunction("Hello!!!"); // this is valid too
  
```

while and if statements follow the same syntax as in Java,
```
  var x = 1;
  while (x < 5){
    if (x > 2){
      print x;
    }
    else {
      print "the value is not greater than 2 yet!";
    }
    x = x + 1;
  }
```


