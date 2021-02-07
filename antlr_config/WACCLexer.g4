lexer grammar WACCLexer;

// comments or whitespace which should be ignored
COMMENT: '#' ~('\n')* '\n' -> skip ;
WHITESPACE: [ \t\n]+ -> skip ;

SEMICOLON: ';' ;

// assignment
ASSIGN: '=' ;

// statement
COMMA: ',' ;
SKP: 'skip' ;
READ: 'read' ;
FREE: 'free' ;
RETURN: 'return' ;
EXIT: 'exit' ;
PRINT: 'print' ;
PRINTLN: 'println' ;
IF: 'if' ;
THEN: 'then' ;
ELSE: 'else' ;
FI: 'fi' ;
WHILE: 'while' ;
DO: 'do' ;
DONE: 'done' ;
BEGIN: 'begin';
END: 'end' ;

// functions
CALL: 'call' ;
IS: 'is' ;

// types
INT: 'int' ;
BOOL: 'bool' ;
CHAR: 'char' ;
STRING: 'string' ;
PAIR: 'pair' ;
NEWPAIR: 'newpair' ;
FST: 'fst' ;
SND: 'snd' ;

// operators
PLUS: '+' ;
MINUS: '-' ;
MULT: '*' ;
DIV: '/' ;
NEGATION: '!' ;
LENGTH: 'len' ;
ORD: 'ord' ;
CHR: 'chr' ;
GT: '>' ;
GTE: '>=' ;
LT: '<' ;
LTE: '<=' ;
EQ: '==' ;
NEQ: '!=' ;
AND: '&&' ;
OR: '||' ;
HASH: '#' ;
MOD: '%' ;

// brackets
OPEN_PARENTHESES: '(' ;
CLOSE_PARENTHESES: ')' ;
OPEN_SQUARE_BRACKET: '[' ;
CLOSE_SQUARE_BRACKET: ']' ;

// numbers
DIGIT: '0'..'9' ;
//INT_LITER: (INT_SIGN)? DIGIT+ ;
//INT_SIGN: PLUS | MINUS ;

// literals
BOOL_LITER: 'true' | 'false' ;

fragment CHARACTER: ~('\\' | '\'' | '"')
| ESCAPED_CHARACTER ;

CHAR_LITER: '\'' CHARACTER '\'' ;

STR_LITER: '"' CHARACTER* '"' ;

ESCAPED_CHARACTER: '\\' ('0' | 'b' | 't' | 'n' | 'f' | 'r' | '"' | '\'' | '\\') ;


NULL: 'null' ;


// identifier
IDENT: [_A-Za-z] [_A-Za-z0-9]* ;