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
Single line `comments` are applied using the `//`, and multi line comments are applied this way `/* ...this is a multi-line comment...**/`

`Print statement` is a native statement, and is used by writing the `print` keyword followed by the experession whose value needs to be printed.

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

Anonymous functions can also be passed on to other functions as arguments,
```
  fun function(a){
    a("Hello, world!");
  }

  function(anonymous (argument){print argument;});
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

## Classes and objects
Classes are defined using the `class` keyword and the constructors are defined using the `init`, furthermore, after the instance creation, the constructors can be used as regular functions. Along with this, the user can define multiple constructors (each with it's own signature, but the Benzene parser would only treat the `last` defined constructor as a valid constructor.

Instance functions are defined using regular functions, but with `fun` ommitted. Static functions are defined using the `static` keyword followed by the instance function creation.

```
  class a{
    init(){return 123;};

    init(){return "Hello, world";};

    static staticFunction(){print "Static Function.";}

    otherFunction(){
      return this.init();   // returns "Hello, world"
    }

  var b = a(); // this is valid

  var c = a;
  var d = c() // this is valid too, during the object creation, the constructor returns the class instance and not the value in the return statement

  print d.otherFunction();   // prints "Hello, world"

  d.staticFunction() // This code is NOT valid, as staticFunction is not an instance function.

  a.staticFunction() // this is valid
  c.staticFunction () // this is valid too
```
