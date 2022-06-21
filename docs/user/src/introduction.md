# Welcome to Hexatomic!

You are reading the user documentation for [Hexatomic](https://corpus-tools.org/hexatomic).

## What is Hexatomic?

Hexatomic is an extensible OS-independent platform for deep multi-layer linguistic corpus annotation.

Research projects want to answer very specific research questions.
When they use corpora, they may have to use specific software that can

- handle the format their corpus data is available in, or
- provide annotation functionality for the annotation types they want to use.

Projects may also need additional software to search their data.
If they have corpora in more than one format, they may have to duplicate the number of software tools they have to use in order to answer their research question. And if this software doesn't exist yet, they have to implement a new tool from scratch.

Hexatomic aims to alleviate this situation, and reduce the number of tools a project will have to use (and install, and maintain, and learn) to 1.

##### How does it do that?

1. It works with a generic graph-based data model, that can handle many different types of annotations.
2. It includes a converter framework, which allows the import of a multitude of different corpus formats, provides them in the generic graph-based model
for manipulation (corpus-building, annotation, cleaning, etc.), and can export the corpus data to yet another multitude of corpus and other data formats.
3. It includes powerful corpus search functionality, that offers the usual free text- and regex-based search, but can also search across linguistic
structures and build complex queries across layers.

##### What if Hexatomic cannot handle my specific annotation type/use case?

Hexatomic is built to be extensible through plugins.
If it doesn't offer what you are looking for, you don't have to implement a new tool from scratch, 
but can instead build a plugin that does what you need.
On top, the existing functionality, the data model, import and export functionality, and search come for free with that.

##### What kind of software is Hexatomic?

Hexatomic is software for the desktop.
You download it to your computer.
It does not need an internet connection to run, and therefore you can also use it in the field, or on the train en route to a conference.

Specifically, it is an Eclipse e4 application implemented in Java.

Hexatomic is <i class="fa fa-heart"></i> free and open source under the [Apache License, Version 2.0](https://github.com/hexatomic/hexatomic/tree/main/LICENSE).

## How can Hexatomic be used?

You can use Hexatomic to do, for example, any or all of the following:

<i class="fa fa-wrench"></i> Build a corpus from scratch

<i class="fa fa-object-group"></i> Merge different corpora into a new one

<i class="fa fa-pencil"></i> Annotate an existing corpus

<i class="fa fa-bug"></i> Error correct a corpus

<i class="fa fa-search"></i> Search a corpus


