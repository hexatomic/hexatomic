lexer grammar ConsoleLexer;

NEW_NODE: 'n' -> pushMode(ARGUMENTS);
NEW_SPAN: 's' -> pushMode(ARGUMENTS);
NEW_EDGE: 'e' -> pushMode(ARGUMENTS);
SET_ATTRIBUTE: 'a' -> pushMode(ARGUMENTS);
DELETE: 'd' -> pushMode(ARGUMENTS);
TOKENIZE: 't' -> pushMode(ARGUMENTS);
TOKENIZE_AFTER: 'ta' -> pushMode(ARGUMENTS);
TOKENIZE_BEFORE: 'tb' -> pushMode(ARGUMENTS);
TOKEN_CHANGE_TEXT: 'tc' -> pushMode(ARGUMENTS);
CLEAR: 'clear' -> pushMode(ARGUMENTS);

WS : [ \t\r\n]+ -> skip;
UNKNOWN: .;


mode ARGUMENTS;

NODE_REF: '#' [\p{Letter}0-9_]+;
IDENTIFIER: [\p{Letter}0-9_]+;
PUNCTUATION: [.,\-!?\u3002]+;
TYPE_STR : ('-d' | '-p' | '-r' | '-o') ;
QUOTED_STRING: '"' ~('"' | '\\')* (.~('"'|'\\'))* '"';
NEWLINE: [\r\n]+;
COLON: ':';
POINTING_ARROW: '->';
DOMINANCE_ARROW: '>';

WS_ARGUMENT: [ \t\r\n]+ -> skip;