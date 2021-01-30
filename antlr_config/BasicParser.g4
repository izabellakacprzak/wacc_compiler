parser grammar BasicParser;

options {
  tokenVocab=BasicLexer;
}

// program
program: BEGIN (func)* stat END EOF ;

// function
func: type ident OPEN_PARENTHESES (param_list)? CLOSE_PARENTHESES IS stat END ;

// list of parameters
param_list: param (COMMA param)* ;

// parameter
param: type ident ;

// statement
stat: SKIP_A
| type ident ASSIGN assign_rhs
| assign_lhs ASSIGN assign_rhs
| READ assign_lhs
| FREE expr
| RETURN expr
| EXIT expr
| PRINT expr
| PRINTLN expr
| IF expr THEN stat ELSE stat FI
| WHILE expr DO stat DONE
| BEGIN stat END
| stat SEMICOLON stat ;

assign_lhs: ident
| array_elem
| pair_elem ;

assign_rhs: expr
| array_liter
| NEWPAIR OPEN_PARENTHESES expr COMMA expr CLOSE_PARENTHESES
| pair_elem
| CALL ident OPEN_PARENTHESES (arg_list)? CLOSE_PARENTHESES ;

arg_list: expr (COMMA expr)* ;

pair_elem: (FST | SND) expr ;

type: base_type
| array_type
| pair_type ;

base_type: INT
| BOOL
| CHAR
| STRING ;

array_type: base_type OPEN_SQUARE_BRACKET CLOSE_SQUARE_BRACKET
| array_type OPEN_SQUARE_BRACKET CLOSE_SQUARE_BRACKET
| pair_type OPEN_SQUARE_BRACKET CLOSE_SQUARE_BRACKET ;

pair_type: PAIR OPEN_PARENTHESES pair_elem_type COMMA pair_elem_type CLOSE_PARENTHESES ;

pair_elem_type: base_type
| array_type
| PAIR ;

expr: INT_LITER
| BOOL_LITER
| CHAR_LITER
| STR_LITER
| pair_liter
| ident
| array_elem
| unary_oper expr
| expr binary_oper expr
| OPEN_PARENTHESES expr CLOSE_PARENTHESES ;

unary_oper: EXCLAMATION_MARK
| MINUS
| LENGTH
| ORD
| CHR ;

binary_oper: MULT
| DIV
| PERCENTAGE
| PLUS
| MINUS
| GT
| GTE
| LT
| LTE
| EQ
| NEQ
| AND
| OR ;

ident: (UNDERSCORE | LETTER) (UNDERSCORE | LETTER | DIGIT)* ;

array_elem: ident (OPEN_SQUARE_BRACKET expr CLOSE_SQUARE_BRACKET)+ ;

array_liter: OPEN_SQUARE_BRACKET (expr (COMMA expr)* )? CLOSE_SQUARE_BRACKET ;

pair_liter: NULL ;