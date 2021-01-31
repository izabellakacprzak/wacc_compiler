parser grammar WACCParser;

options {
  tokenVocab=WACCLexer;
}

// program
program: BEGIN (func)* stat END EOF ;


// function and parameters
func: type IDENT OPEN_PARENTHESES (param_list)? CLOSE_PARENTHESES IS stat END ;
param: type IDENT ;
param_list: param (COMMA param)* ;


// statement
stat: SKP
| type IDENT ASSIGN assign_rhs
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


// assignents
assign_lhs: IDENT
| array_elem
| pair_elem ;

assign_rhs: expr
| array_liter
| NEWPAIR OPEN_PARENTHESES expr COMMA expr CLOSE_PARENTHESES
| pair_elem
| CALL IDENT OPEN_PARENTHESES (arg_list)? CLOSE_PARENTHESES ;

arg_list: expr (COMMA expr)* ;


// types
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

array_elem: IDENT (OPEN_SQUARE_BRACKET expr CLOSE_SQUARE_BRACKET)+ ;

array_liter: OPEN_SQUARE_BRACKET (expr (COMMA expr)* )? CLOSE_SQUARE_BRACKET ;

pair_type: PAIR OPEN_PARENTHESES pair_elem_type COMMA pair_elem_type CLOSE_PARENTHESES ;

pair_elem_type: base_type
| array_type
| PAIR ;

pair_elem: (FST | SND) expr ;
pair_liter: NULL ;


// expressions
expr: INT_LITER
| BOOL_LITER
| CHAR_LITER
| STR_LITER
| pair_liter
| IDENT
| array_elem
| (NEGATION | MINUS | LENGTH | ORD | CHR) expr  // unary expressions
| expr (MULT | DIV | MOD | PLUS | MINUS) expr   // arithmetic expressions
| expr (GT | GTE | LT | LTE | EQ | NEQ) expr    // comparison expressions
| expr (AND | OR) expr                          // logical expressions
| OPEN_PARENTHESES expr CLOSE_PARENTHESES ;
