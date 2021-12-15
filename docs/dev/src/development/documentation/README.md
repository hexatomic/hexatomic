# Documentation

Documentation is an integral part of the development of Hexatomic!
Without documentation, users will not be able to use Hexatomic successfully and effectively.
Also, other developers - and even your future self - will not be able to understand the code, or why things are done how they are done without documentation.
We therefore require that you document the changes you make to Hexatomic.
Without providing complete documentation for the changes you make, your changes will not be accepted into the codebase.

We generally have the following types of documentation in Hexatomic:

1. **Semantic code:** Semantic code is code that explains itself. A method and its arguments, for example, should be named so that people who read the code can understand easily what the method does.
2. **Code comments:** Comments within the code explain small parts of the code that may otherwise be hard to understand.
3. **API documentation:** Hexatomic uses [Javadoc](https://www.oracle.com/java/technologies/javase/javadoc-tool.html) strings to formally document the different parts of the Java code: types, methods, variables, etc. Javadoc strings are also part of the code.
4. **Developer and maintainer documentation:** Text documentation that describes how development and maintenance work in the Hexatomic project. You can find the sources in the `./docs/dev/` directory.
5. **User documentation:** Text documentation that describes how to use Hexatomic. You can find the sources in the `./docs/user/` directory.
6. **Repository documentation:** The following files are in the root directory of the GitHub repository: The README file in the root of the GitHub repository is a landing page to get an overview of the Hexatomic software project; the contribution guidelines provide details on how to contribute to Hexatomic; the code of conduct provides ground rules for collaboration; the license file details under which license requirements Hexatomic can be used and changed; the changelog documents how Hexatomic has changed across released versions.
7. **Metadata:** The CITATION.cff file provides information on how to cite Hexatomic. You can find it in the root directory of the repository.
8. **Templates:** Issue and pull request templates provide information on what is necessary to create an issue or pull request. You can find them in the `./.github/` directory.

It is actually quite easy to provide documentation if you follow some basic rules.

## Ground rules for documentation

1. [Document as you make changes](#document-as-you-make-changes).
2. [Only document what needs to be documented](#only-document-what-needs-to-be-documented).
3. [Document where it makes the most sense](#document-where-it-makes-the-most-sense).
4. [Document for everyone who is affected by the changes you make](#document-for-everyone-who-is-affected-by-the-changes-you-make).
5. [Keep it as simple as possible](#keep-it-as-simple-as-possible).
6. [Be consistent](#be-consistent).

Here are some more detailed explanations of these rules.

### Document as you make changes

Don't push documentation to the end of the task, you will forget important details and it will become a chore.
Always document at the same time as you make your changes.

It is helpful to keep changes as small as possible, and not to mix different changes.
This way you will always only have little documentation to write.

A general tip is to encapsulate small defined change sets in separate pull requests, and to make sure that all the necessary documentation is there before you ask for a review of the pull request.

### Only document what needs to be documented

Don't overdo it!
You don't need to write code comments for every single line of the code.
You also probably don't need to explain how to use a mouse.

Try not to unnecessarily duplicate information from other sources, provide a link to them instead.

### Document where it makes the most sense

Documentation should be put as closely as possible to the thing it describes.
If an implementation detail within a method needs explanation, put it in a code comment or the Javadoc string for the method, not in the developer documentation.

### Document for everyone who is affected by the changes you make

For example:

- If you change functionality, add a section in the user documentation.
- If you change configuration details for the continuous integration pipeline, document it in the developer documentation.
- If you accept a PR from a new contributor, make sure that the citation metadata is updated, and that they are added to the list of contributors in the README.

### Keep it as simple as possible

Try to write your documentation so that the largest possible audience will understand it.
This means, for example, that you should avoid jargon and abbreviations, even if they are very familiar to you.
Also keep in mind that not everyone understands English equally well, therefore try to keep sentences short and simple.

### Be consistent

Try to be consistent in how you document.
Make yourself familiar with a documentation style, and follow it.
A good example for a documentation style is the [Google developer documentation style guide](https://developers.google.com/style/highlights).

Some consistency in documentation style for Javadoc strings and code comments is also enforced by [Checkstyle](../setup.md##list-of-eclipse-ide-plugins-required-for-hexatomic-development), which is run during builds.
