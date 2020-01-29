grammar ConsoleCommand;

options
{
  language=Java;
}


start
    : command  # CommandChain
    ;


command
    : 'n' (attribute | node_reference | layer_reference)+ # NewNode
    | 'e' (attribute | edge_reference | layer_reference)+ # NewEdge
    | 'a' (attribute | node_reference | edge_reference)+ # Annotate
    | 'd' (node_reference | edge_reference)+ # Delete
    | 't' string+ # Tokenize
    | 'ta' node_reference string+ # TokenizeAfter
    | 'tb' node_reference string+ # TokenizeBefore
    | 'clear' # Clear
    ;

        
attribute
    : (namespace=IDENTIFIER ':')? name=string  ':' value=string # NonEmptyAttribute
    | (namespace=IDENTIFIER ':')? name=string  ':' # EmptyAttribute
    ;
    
node_reference
    : name=NODE_REF # NamedNodeReference
    ;

layer_reference
	: IDENTIFIER # LayerReference
	;
    
edge_reference
    : source=NODE_REF '->' target=NODE_REF #PointingEdgeReference
    | source=NODE_REF '>' target=NODE_REF #DominanceEdgeReference
    ;
    

string
    : IDENTIFIER # RawString
    | PUNCTUATION # Punctuation
    | QUOTED_STRING # QuotedString
    ;

NUMBER: [0-9]+;
NODE_REF: '#'[a-zA-Z0-9_]+;
IDENTIFIER: [a-zA-Z0-9_]+;
PUNCTUATION: [.!?:]+;
TYPE_STR : ('-d' | '-p' | '-r' | '-o') ;
QUOTED_STRING: '"' ~('"' | '\\')* (.~('"'|'\\'))* '"';
NEWLINE : [\r\n]+;
WS : [ \t\r\n]+ -> skip;
