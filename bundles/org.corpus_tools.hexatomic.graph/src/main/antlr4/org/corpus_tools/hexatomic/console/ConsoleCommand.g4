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
    | NEW_SPAN (attribute | node_reference | layer_reference)+ # NewSpan
    | NEW_EDGE (attribute | new_edge_reference | layer_reference)+ # NewEdge
    | SET_ATTRIBUTE (attribute | node_reference | existing_edge_reference)+ # Annotate
    | DELETE (node_reference | existing_edge_reference)+ # Delete
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
    
existing_edge_reference
    : source=NODE_REF POINTING_ARROW target=NODE_REF #ExistingPointingEdgeReference
    | source=NODE_REF DOMINANCE_ARROW target=NODE_REF #ExistingDominanceEdgeReference
    ;

new_edge_reference
    : source=NODE_REF POINTING_ARROW target=NODE_REF #NewPointingEdgeReference
    | source=NODE_REF DOMINANCE_ARROW target=NODE_REF #NewDominanceEdgeReference
    ;
    

string
    : IDENTIFIER # RawString
    | PUNCTUATION # Punctuation
    | QUOTED_STRING # QuotedString
    ;
