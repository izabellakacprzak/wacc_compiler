parser grammar WACCParser;

options {
  tokenVocab=WACCLexer;
}
@parser::members {
    private void inbounds(RuleContext t) {
        try {
          int n = Integer.parseInt(t.getText());
        } catch (NumberFormatException e) {
          notifyErrorListeners("Integer overflow");
        }
    }
}
// program
program: BEGIN (func)* stat END EOF ;


// function and parameters
func: type IDENT OPEN_PARENTHESES (param_list)? CLOSE_PARENTHESES IS stat END ;
param_list: type IDENT (COMMA type IDENT)* ;


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

// assignments
assign_lhs: IDENT  #IdentLHS
| array_elem       #ArrayElemLHS
| pair_elem        #PairElemLHS
;

assign_rhs: expr                                                            #ExprRHS
| OPEN_SQUARE_BRACKET (expr (COMMA expr)* )? CLOSE_SQUARE_BRACKET           #ArrayLiterRHS
| NEWPAIR OPEN_PARENTHESES expr COMMA expr CLOSE_PARENTHESES                #NewPairRHS
| pair_elem                                                                 #PairElemRHS
| CALL IDENT OPEN_PARENTHESES (expr (COMMA expr)*)? CLOSE_PARENTHESES       #FuncCallRHS
;


// types
type: base_type                                                                 #BaseType
| type OPEN_SQUARE_BRACKET CLOSE_SQUARE_BRACKET                                 #ArrayType
| PAIR OPEN_PARENTHESES pair_elem_type COMMA pair_elem_type CLOSE_PARENTHESES   #PairType
;

base_type: INT
| BOOL
| CHAR
| STRING ;



array_elem: IDENT (OPEN_SQUARE_BRACKET expr CLOSE_SQUARE_BRACKET)+ ;

pair_elem_type: base_type
| type
| PAIR ;

pair_elem: FST expr #FstPairExpr
| SND expr          #SndPairExpr
;

pair_liter: NULL ;


// expressions
expr: int_liter {inbounds(_localctx);}          #IntLiterExpr
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
int_liter: (PLUS | MINUS)? INT_LITER;