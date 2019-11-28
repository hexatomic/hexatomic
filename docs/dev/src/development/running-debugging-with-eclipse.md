# Running and debugging Hexatomic in Eclipse

Hexatomic is a modular project, and while it is possible to debug a user-defined subset of its bundles,
it may be more useful to debug the complete application.

To run or debug Hexatomic from within the Eclipse IDE, with all the features and bundles, you have to open the  `org.corpus_tools.hexatomic.product` project which contains the Hexatomic product definition.
In this project, open the `org.corpus_tools.hexatomic.product` file in Eclipse.

![Location of the product definition file in the Project Explorer](./product-file-location.png)

To debug Hexatomic, click on the **Launch an Eclipse application in Debug mode** link in the overview tab of the Product Configuration Editor.
To run the product without debugging, click **Launch an Eclipse application** on the same page.
If you don't see the Product Configuration Editor, you may have opened the file with a different editor instead.
In this case, right-click on the `org.corpus_tools.hexatomic.product` file, and choose **Open With** > **Product Configuration Editor**.

![Product configuration file editor with launch links](./product-launch.png)



