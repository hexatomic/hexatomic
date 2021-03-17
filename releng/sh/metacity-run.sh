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

# run the given program
echo "Executing given command $@"
$@

# kill the started services
kill $METACITY_PID
kill $XVFB_PID
