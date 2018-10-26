## Description:
A Bare Bones Interpreter that interprets the "clear", "incr", "decr" and "while" commands as defined by Brookshear.

This is the extended version, that allows for the following extra commands:
  * Comments using the "//" symbols for a single line, or "/\*" and "*/" over multiple lines.
  * The ability to set variables using the following syntax:  
    ```
    set <variable> to <integer>;
    ```
  * Performing operations using the set keyword, as seen in the following syntax:  
    ```
    set <variable> to <integer/variable> <+,-,*,/> <integer/variable>;
    ```
  * If/else statements that follow the following syntax:  
    ```
    if <variable> is [not] <integer/variable> do;
      <code> 
    [else;]  
      <code>  
    next;
    ```
_(Note: [] brackets indicate that the statement is optional)_

## Configuration
This application was developed in the Eclipse IDE, but will also work in any console.
