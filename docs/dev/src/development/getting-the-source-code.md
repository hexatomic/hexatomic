# Getting the source code

The Hexatomic source code is hosted on GitHub at <i class="fa fa-github"></i> <https://github.com/hexatomic/hexatomic>.

You don't have direct write permissions to this main source code repository, unless you are a [core contributor](https://github.com/hexatomic/hexatomic#core-contributors).
This means that you will make your changes in your own copy (*fork*) of the repository, and request that we merge your changes into the main code base when you are finished (*pull request*).[^workflow]

## Downloading the source code to your computer

First, you need to create a fork of the Hexatomic main repository on GitHub:

1. Log in to your GitHub account ([github.com/login](https://github.com/login)).
1. Go to <https://github.com/hexatomic/hexatomic>, and click the **<i class="fa fa-code-fork"></i> Fork** button in the upper right-hand corner of the repository page. This will create a copy of the repository under your own account.

To download the source code onto your computer and put it under local version control with Git, type the following command into the terminal of your computer:

```bash
git clone https://github.com/<your-GitHub-user-name>/hexatomic.git
```

It will download the latest version of the source code in the `master` branch to a sub-directory `hexatomic`,
along with the complete Git version history.

For your actual contribution, you will need to create a new Git branch, but this is explained in detail in section [Workflow](./workflow/).
For now, all that is left to do is build Hexatomic locally, and get its source code into your IDE.

## Build Hexatomic locally

Before you import the Hexatomic source code into the Eclipse IDE, you should now build it locally to check that everything works, and to avoid error messages in the Eclipse IDE.

To build Hexatomic locally, go to the root directory of your local copy of the Hexatomic source code, and run `mvn clean install`.

Maven then builds Hexatomic and installs artifacts in the local Maven repository.

During the build, some tests are being run that start Hexatomic and automatedly interact with the graphical user interface.
You must not use the keyboard or mouse during these GUI tests, as this may interfere with the automated interactions.
If you want to keep working during the local build, you can run the build "headlessly", i.e., with the help of a virtual display server. 
To do this on Linux, for example, you can run the script `releng/sh/metacity-run.sh` from the root of the local repository:

`./releng/sh/metacity-run.sh mvn clean install`

See the [SWTBot documentation on this topic](https://wiki.eclipse.org/SWTBot/Automate_test_execution#use_another_DISPLAY_to_save_time) for further details.

## Getting the source code into the Eclipse IDE

If you have set up the Eclipse IDE for development of Hexatomic, as described in the section [Development setup](./setup.md#suggested-editor-eclipse-integrated-development-environment-ide), you will have to import the source code from your local
Git repository.

To import the Hexatomic into your Eclipse IDE, do the following:

1. Start Eclipse
2. Open the **Import** window via the menu **File** > **Import**
3. Select the import wizard **General** > **Existing Projects into Workspace** and click **Next >**
4. In **Select root directory**, select the directory where you have downloaded the Hexatomic source code to (the Git *root*)
5. In the **Options** section of the window, activate **Search for nested projects**
6. Click **Finish**

Congratulations, you are now set up to start contributing to Hexatomic!

Before starting to work on your contribution, please read the section [Workflow](./workflow/) carefully.

---

[^workflow]: For more info on *forks* and *pull requests*, read the section [Workflow](./workflow/).
