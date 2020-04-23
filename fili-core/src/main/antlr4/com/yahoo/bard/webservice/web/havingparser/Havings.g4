grammar Havings;

options {
    tokenVocab=HavingsLex;
}

havings
    : ( havingComponent ( COMMA havingComponent )* )? EOF;

havingComponent
    : metric DASH OPERATOR OPEN_BRACKET havingValues CLOSE_BRACKET;

metric
    : metricName ( OPEN_PARENTHESIS params? CLOSE_PARENTHESIS )?
    ;
metricName
    : ID
    ;
params
    : paramValue ( COMMA paramValue )*
    ;
paramValue
    : ID EQUALS VALUE
    ;

havingValues
    : VALUE ( COMMA VALUE )*
    ;
