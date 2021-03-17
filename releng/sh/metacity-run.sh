#!/bin/bash

# Runs a command using a background Xvfb server with a running window manager (metacity)

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
kill $METACITY_PID
kill $XVFB_PID

# Exit with the code of the executed program.
# If this is e.g. a test execution, this script should
# fail when the program failed.

exit $COMMAND_EXIT