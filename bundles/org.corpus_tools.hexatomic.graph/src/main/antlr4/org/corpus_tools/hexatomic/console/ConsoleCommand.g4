grammar ConsoleCommand;

options
{
  language=Java;
}


start
    : command  # CommandChain
    ;


command
    : 'n' n_attribute* # NewNode
    | 'a' a_attribute* # Annotate
    | 'd' d_attribute+ # Delete
    | 't' string+ # Tokenize
    | 'clear' # Clear
    ;

n_attribute
    : attribute # NewNodeAttribute
    | node_reference # NewNodeReference
    | IDENTIFIER # NewNodeLayer    
    ;
    
a_attribute
    : attribute # AnnotateAttribute
    | node_reference # AnnotateNodeReference
    | edge_reference # AnnotateEdgeReference
    ;

d_attribute
    : node_reference # DeleteNodeReference
    | edge_reference # DeleteEdgeReference
    ;
    
attribute
    : (namespace=IDENTIFIER ':')? name=string  ':' value=string # NonEmptyAttribute
    | (namespace=IDENTIFIER ':')? name=string  ':' # EmptyAttribute
    ;

annotation_shortcut
    : IDENTIFIER
    ;
    
node_reference
    : name=NODE_REF # NamedNodeReference
    ;
    
edge_reference
    : source=node_reference '->' target=node_reference #PointingEdgeReference
    | source=node_reference '>' target=node_reference #DominanceEdgeReference
    ;
    

string
    : IDENTIFIER # RawString
    | QUOTED_STRING # QuotedString
    ;

NUMBER: [0-9]+;
NODE_REF: '#'[a-zA-Z0-9_]+;
IDENTIFIER: [a-zA-Z0-9_]+;
TYPE_STR : ('-d' | '-p' | '-r' | '-o') ;
QUOTED_STRING: '"' ~('"' | '\\')* (.~('"'|'\\'))* '"';
NEWLINE : [\r\n]+;
WS : [ \t\r\n]+ -> skip;
