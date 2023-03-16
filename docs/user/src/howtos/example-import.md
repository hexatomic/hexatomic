# Import an example corpus

We will import an example corpus from the [ANNIS demo corpus
page](https://corpus-tools.org/annis/corpora.html), namely the so-called
“pcc2” corpus, a sample from the [Potsdam Commentary Corpus](http://angcl.ling.uni-potsdam.de/resources/pcc.html).
It contains several annotation layers, like constituent trees, dependency trees and annotation for information structure.

1. Go to <https://corpus-tools.org/annis/corpora.html>.
2. [Download](https://corpus-tools.org/corpora/pcc2_PAULA.zip) the corpus named “pcc2” in the PAULA format.
3. Unzip the file to a folder of your choice
4. Choose the *Import* entry in the *File* menu.
5. Click on the button with the *...* caption and navigate to the unzipped `pcc2_v6_PAULA` folder. Then click on *Next*.
![Select a corpus folder in the import wizard](select-pcc2-folder.png)
1. The importer should correctly identify this corpus as “PAULA format”. Click on *Finish* to import the corpus.
![Format selection wizard step](pcc2-finish.png)
1. Unfold the corpus and in the “Corpus Structure” and right-click on the “4282” document, select “Open with Graph Editor”. You [filter](../usage/graph-editor/filter.md) the view to only show nodes with the `tiger::cat` annotation, to focus on the constituent trees.
![Graph editor with the openend document](pcc2-graph-editor.png)
1. If you like, add new annotations using the [console](../usage/graph-editor/console.md).
2. Save the project via by clicking on the *File* menu and then *Save Salt Project As...* to persist the changes as a [project](../usage/projects.md).