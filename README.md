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

Benzene supprots various types of 
