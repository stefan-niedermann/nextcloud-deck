#!/usr/bin/env bash

adb logcat -c
adb logcat *:E -v color &
if ./gradlew connectedDebugAndroidTest; then
  echo "connectedDebugAndroidTest succeeded" >&2
  echo "ls -all (1)"
  ls -all
  adb pull /sdcard/screenshots screenshots
  echo "ls -all (1.1)"
  ls -all
  adb exec-out /sdcard/screenshots -p >screenshots
  echo "ls -all (1.2)"
  ls -all
  exit 0
else
  echo "ls -all (2)"
  ls -all
  adb pull /sdcard/screenshots screenshots
  echo "ls -all (2.1)"
  ls -all
  adb exec-out /sdcard/screenshots -p >screenshots
  echo "ls -all (2.2)"
  ls -all
  exit 1
fi