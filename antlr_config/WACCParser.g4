parser grammar WACCParser;

options {
  tokenVocab=WACCLexer;
}
@parser::members {
    private void inbounds(RuleContext t) {
        try {
          int n = Integer.parseInt(t.getText());
        } catch (NumberFormatException e) {
          notifyErrorListeners("Integer value is badly formatted (either it has a badly defined "
          + "sign or it is too large for a 32-bit signed integer)");
        }
    }
}

// program
program: BEGIN (func)* (classdecl)* stat END EOF ;


// function and parameters
func: type IDENT OPEN_PARENTHESES (param_list)? CLOSE_PARENTHESES IS stat END ;
param_list: type IDENT (COMMA type IDENT)*  (COMMA  IDENT)*
| IDENT (COMMA  IDENT)* ;

// class
classdecl: CLASS IDENT OPEN_PARENTHESES CLOSE_PARENTHESES IS (attribute SEMICOLON)* (constructor)+ (func)* END ;

attrexpr: IDENT DOT IDENT ;

attribute: type IDENT             #AttrNoAssign
| type IDENT ASSIGN assign_rhs    #AttrAssign
;

constructor: IDENT OPEN_PARENTHESES (param_list)? CLOSE_PARENTHESES IS stat END ;

// statement
stat: SKP                                       #SkipStat
| decl_stat                                      #DeclStat
| assign_lhs ASSIGN assign_rhs                  #AssignStat
| READ assign_lhs                               #ReadStat
| FREE expr                                     #FreeStat
| RETURN expr                                   #ReturnStat
| EXIT expr                                     #ExitStat
| PRINT expr                                    #PrintStat
| PRINTLN expr                                  #PrintlnStat
| IF expr THEN stat ELSE stat FI                #IfStat
| IF expr THEN stat FI                          #IfStat
| WHILE expr DO stat DONE                       #WhileStat
| FOR OPEN_PARENTHESES decl_stat SEMICOLON expr
SEMICOLON stat CLOSE_PARENTHESES DO stat DONE   #ForStat
| BEGIN stat END                                #ScopeStat
| stat SEMICOLON stat                           #StatsListStat
| IDENT ASSIGN NEW IDENT OPEN_PARENTHESES
(expr (COMMA expr)*)? CLOSE_PARENTHESES         #ObjectDeclStat
;

decl_stat: type IDENT ASSIGN assign_rhs ;

// assignments
assign_lhs: IDENT  #IdentLHS
| array_elem       #ArrayElemLHS
| pair_elem        #PairElemLHS
| attrexpr         #AttributeLHS
;

assign_rhs: expr                                                           #ExprRHS
| OPEN_SQUARE_BRACKET (expr (COMMA expr)*)? CLOSE_SQUARE_BRACKET           #ArrayLiterRHS
| NEWPAIR OPEN_PARENTHESES expr COMMA expr CLOSE_PARENTHESES               #NewPairRHS
| pair_elem                                                                #PairElemRHS
| CALL IDENT OPEN_PARENTHESES (expr (COMMA expr)*)? CLOSE_PARENTHESES      #FuncCallRHS
| IDENT DOT IDENT OPEN_PARENTHESES (expr (COMMA expr)*)? CLOSE_PARENTHESES #MethodCallRHS
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

pair_elem_type: base_type                       #PairElemTypeBase
| type OPEN_SQUARE_BRACKET CLOSE_SQUARE_BRACKET #PairElemTypeArray
| PAIR                                          #PairElemTypePair
;

pair_elem: FST expr #FstPairExpr
| SND expr          #SndPairExpr
;

pair_liter: NULL ;


// expressions
expr: int_liter {inbounds(_localctx);}            #IntLiterExpr
| BOOL_LITER                                      #BoolLiterExpr
| CHAR_LITER                                      #CharLiterExpr
| STR_LITER                                       #StringLiterExpr
| pair_liter                                      #PairLiterExpr
| IDENT                                           #IdentExpr
| array_elem                                      #ArrayElemExpr
| op=(NEGATION | MINUS | LENGTH | ORD | CHR) expr #UnaryExpr
| expr op=(MULT | DIV | MOD) expr                 #BinaryExpr
| expr op=(PLUS | MINUS) expr                     #BinaryExpr
| expr op=(GT | GTE | LT | LTE) expr              #BinaryExpr
| expr op=(EQ | NEQ) expr                         #BinaryExpr
| expr op=AND expr                                #BinaryExpr
| expr op=OR expr                                 #BinaryExpr
| OPEN_PARENTHESES expr CLOSE_PARENTHESES         #BracketExpr
| attrexpr                                        #AttributeExpr
;

int_liter: (PLUS | MINUS)? INT_LITER;