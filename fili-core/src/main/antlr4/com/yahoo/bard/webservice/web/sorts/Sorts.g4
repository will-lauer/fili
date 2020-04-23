grammar Sorts;

options {
    tokenVocab=SortsLex;
}

sorts
    : ( sortsComponent ( COMMA sortsComponent )* )? EOF;

sortsComponent
    : metric (PIPE orderValue)?;

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

orderValue
    : VALUE
    ;
