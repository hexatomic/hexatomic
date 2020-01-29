grammar ConsoleCommand;
options
{
  language=Java;
  tokenVocab = ConsoleLexer;
}


start
    : command  # CommandChain
    ;


command
    : NEW_NODE (attribute | node_reference | layer_reference)+ # NewNode
    | NEW_EDGE (attribute | edge_reference | layer_reference)+ # NewEdge
    | SET_ATTRIBUTE (attribute | node_reference | edge_reference)+ # Annotate
    | DELETE (node_reference | edge_reference)+ # Delete
    | TOKENIZE string+ # Tokenize
    | TOKENIZE_AFTER node_reference string+ # TokenizeAfter
    | TOKENIZE_BEFORE node_reference string+ # TokenizeBefore
    | CLEAR # Clear
    ;

        
attribute
    : (namespace=IDENTIFIER COLON)? name=string  COLON value=string # NonEmptyAttribute
    | (namespace=IDENTIFIER COLON)? name=string  COLON # EmptyAttribute
    ;
    
node_reference
    : name=NODE_REF # NamedNodeReference
    ;

layer_reference
	: IDENTIFIER # LayerReference
	;
    
edge_reference
    : source=NODE_REF POINTING_ARROW target=NODE_REF #PointingEdgeReference
    | source=NODE_REF DOMINANCE_ARROW target=NODE_REF #DominanceEdgeReference
    ;
    

string
    : IDENTIFIER # RawString
    | PUNCTUATION # Punctuation
    | QUOTED_STRING # QuotedString
    ;
