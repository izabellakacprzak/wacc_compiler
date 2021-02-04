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
stat: SKP                         #SkipStat
| type IDENT ASSIGN assign_rhs    #DeclStat
| assign_lhs ASSIGN assign_rhs    #AssignStat
| READ assign_lhs                 #ReadStat
| FREE expr                       #FreeStat
| RETURN expr                     #ReturnStat
| EXIT expr                       #ExitStat
| PRINT expr                      #PrintStat
| PRINTLN expr                    #PrintlnStat
| IF expr THEN stat ELSE stat FI  #IfStat
| WHILE expr DO stat DONE         #WhileStat
| BEGIN stat END                  #ScopeStat
| stat SEMICOLON stat             #StatsListStat
;

// assignents
assign_lhs: IDENT  #IdentLHS
| array_elem       #ArrayElemLHS
| pair_elem        #PairElemLHS
;

assign_rhs: expr                                                  #ExprRHS
| OPEN_SQUARE_BRACKET (expr (COMMA expr)* )? CLOSE_SQUARE_BRACKET #ArrayLiterRHS
| NEWPAIR OPEN_PARENTHESES expr COMMA expr CLOSE_PARENTHESES      #NewPairRHS
| pair_elem                                                       #PairElemRHS
| CALL IDENT OPEN_PARENTHESES (expr (COMMA expr)*)? CLOSE_PARENTHESES       #FuncCallRHS
;


// types
type: base_type                                     #BaseType
| type OPEN_SQUARE_BRACKET CLOSE_SQUARE_BRACKET     #ArrayType
| pair_type                                         #PairType
;

base_type: INT
| BOOL
| CHAR
| STRING ;



array_elem: IDENT (OPEN_SQUARE_BRACKET expr CLOSE_SQUARE_BRACKET)+ ;

pair_type: PAIR OPEN_PARENTHESES pair_elem_type COMMA pair_elem_type CLOSE_PARENTHESES ;

pair_elem_type: base_type
| type
| PAIR ;

pair_elem: FST expr #FstPairExpr
| SND expr          #SndPairExpr
;

pair_liter: NULL ;


// expressions
expr: INT_LITER                                 #IntLiterExpr
| BOOL_LITER                                    #BoolLiterExpr
| CHAR_LITER                                    #CharLiterExpr
| STR_LITER                                     #StringLiterExpr
| pair_liter                                    #PairLiterExpr
| IDENT                                         #IdentExpr
| array_elem                                    #ArrayElemExpr
| (NEGATION | MINUS | LENGTH | ORD | CHR) expr  #UnaryExpr
| expr (MULT | DIV | MOD | PLUS | MINUS) expr   #BinaryExpr
| expr (GT | GTE | LT | LTE | EQ | NEQ) expr    #BinaryExpr
| expr (AND | OR) expr                          #BinaryExpr
| OPEN_PARENTHESES expr CLOSE_PARENTHESES       #BracketExpr
;