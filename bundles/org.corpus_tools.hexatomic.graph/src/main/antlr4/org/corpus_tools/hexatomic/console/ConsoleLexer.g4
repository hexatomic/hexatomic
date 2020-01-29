lexer grammar ConsoleLexer;

NEW_NODE: 'n' -> pushMode(ARGUMENTS);
NEW_EDGE: 'e' -> pushMode(ARGUMENTS);
SET_ATTRIBUTE: 'a' -> pushMode(ARGUMENTS);
DELETE: 'd' -> pushMode(ARGUMENTS);
TOKENIZE: 't' -> pushMode(ARGUMENTS);
TOKENIZE_AFTER: 'ta' -> pushMode(ARGUMENTS);
TOKENIZE_BEFORE: 'tb' -> pushMode(ARGUMENTS);
CLEAR: 'clear' -> pushMode(ARGUMENTS);

WS : [ \t\r\n]+ -> skip;


mode ARGUMENTS;

NUMBER: [0-9]+;
NODE_REF: '#'[a-zA-Z0-9_]+;
IDENTIFIER: [a-zA-Z0-9_]+;
PUNCTUATION: [.!?:]+;
TYPE_STR : ('-d' | '-p' | '-r' | '-o') ;
QUOTED_STRING: '"' ~('"' | '\\')* (.~('"'|'\\'))* '"';
NEWLINE: [\r\n]+;
COLON: ':';
POINTING_ARROW: '->';
DOMINANCE_ARROW: '>';

WS_ARGUMENT: [ \t\r\n]+ -> skip;