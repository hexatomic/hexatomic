# Data model

Hexatomic works on instances of a graph-based meta model for linguistic data called **Salt**.

In Salt, linguistic data is organized in projects: 

- A **Salt project** contains at least one - and often only one - **corpus graph**.
- A corpus graph contains **corpora** as nodes. 
- The child nodes of a corpus can again be corpora (so-called *sub-corpora*) and **documents**.
- Each document has a **document graph**, which is the model element containing the actual linguistic data: 
    - primary data sources (text, audio, or video material), and
    - annotations.

Salt is very powerful in that it is theory-neutral and tagset-independent, and can model a vast variety of linguistic data and annotations.

To find out more about the Salt meta model for linguistic data and its Java API, please refer to the [Salt homepage](https://corpus-tools.org/salt), and the [Salt documentation](https://korpling.github.io/salt/doc/).