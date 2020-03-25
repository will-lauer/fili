grammar Havings;

options {
    tokenVocab=HavingsLex;
}

havings
    : ( havingComponent ( COMMA havingComponent )* )? EOF;

havingComponent
    : metric DASH operator OPEN_BRACKET values CLOSE_BRACKET;

operator
    : ID
    ;

metric
    : ID ( OPEN_PARENTHESIS ID EQUALS ID CLOSE_PARENTHESIS )?
    ;

values
    : VALUE ( COMMA VALUE )*
    ;
