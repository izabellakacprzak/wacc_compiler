lexer grammar BasicLexer;

COMMENT: '#' ~('\n')* '\n' -> skip ;

BEGIN: 'begin';
END: 'end' ;
IS: 'is' ;

SEMICOLON: ';' ;
COMMA: ',' ;
SKIP_A: 'skip' ;
ASSIGN: '=' ;
READ: 'read' ;
FREE: 'free' ;
RETURN: 'return' ;
EXIT: 'exit' ;
PRINT: 'print' ;
PRINTLN: 'println' ;
PAIR: 'pair' ;
NEWPAIR: 'newpair' ;
CALL: 'call' ;
FST: 'fst' ;
SND: 'snd' ;

IF: 'if' ;
THEN: 'then' ;
ELSE: 'else' ;
FI: 'fi' ;
WHILE: 'while' ;
DO: 'do' ;
DONE: 'done' ;

// operators
PLUS: '+' ;
MINUS: '-' ;
MULT: '*' ;
DIV: '/' ;
EXCLAMATION_MARK: '!' ;
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
UNDERSCORE: '_' ;
NULL: 'null' ;
HASH: '#' ;
PERCENTAGE: '%' ;

// brackets
OPEN_PARENTHESES: '(' ;
CLOSE_PARENTHESES: ')' ;
OPEN_SQUARE_BRACKET: '[' ;
CLOSE_SQUARE_BRACKET: ']' ;

// numbers and bools
DIGIT: '0'..'9' ;

// types
INT: 'int' ;
BOOL: 'bool' ;
CHAR: 'char' ;
STRING: 'string' ;

INT_LITER: (INT_SIGN)? (DIGIT)+ ;

INT_SIGN: PLUS | MINUS ;

BOOL_LITER: 'true' | 'false' ;

CHAR_LITER: '\'' CHARACTER '\'' ;

STR_LITER: '"' CHARACTER* '"' ;

ESCAPED_CHARACTER: '0' | 'b' | 't' | 'n' | 'f' | 'r' | '"' | '\'' | '\\' ;

CHARACTER: ~('\\' | '\'' | '"')
| '\\' ESCAPED_CHARACTER ;

LETTER: 'a'..'z' | 'A'..'Z' ;