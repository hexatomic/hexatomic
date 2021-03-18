#!/bin/bash

# Runs a command using a background Xvfb server with a running window manager (metacity)

# Check if Xvfb and metacity are installed; if not, prompt user whether to install.
for PACKAGE in Xvfb metacity
do
    dpkg -s "$PACKAGE" >/dev/null 2>&1 && {
        echo "$PACKAGE is installed."
    } || {
        echo -e "$PACKAGE is not installed. Install now? [y/n] \c"
        read install_package
        if ([[ "$install_package" = 'y' ]] || [[ "$install_package" = 'Y' ]]); then
            echo "Installing $PACKAGE ..."
            sudo apt-get install $PACKAGE
        else
            echo "This script needs $PACKAGE to run. Exiting."
            exit 0
        fi
    }
done

# Use this port, change temporarly if it is already taken
export DISPLAY=:99

# Start Xvfb on this display and rememeber its PID
Xvfb -screen 0 1280x1024x24 $DISPLAY &
XVFB_PID=$!
echo "Started Xvfb with PID $XVFB_PID"

# Start the window manager and remember its PID
metacity &
METACITY_PID=$!
echo "Started metacity with PID $METACITY_PID"

# Run the given program and remember its exit code
echo "Executing given command $@"
$@
COMMAND_EXIT=$?
echo "Command exited with code $COMMAND_EXIT"

# kill the started services
echo "Killing metacity (PID $METACITY_PID)"
kill -9 $METACITY_PID
echo "Killing Xvfb server (PID $XVFB_PID)"
kill $XVFB_PID

# Exit with the code of the executed program.
# If this is e.g. a test execution, this script should
# fail when the program failed.

exit $COMMAND_EXIT
