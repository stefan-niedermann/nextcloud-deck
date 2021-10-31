#!/usr/bin/env bash

adb logcat -c
adb logcat *:E -v color &
if ./gradlew connectedDebugAndroidTest; then
  echo "connectedDebugAndroidTest succeeded" >&2
else
  adb pull /sdcard/screenshots screenshots
  adb exec-out /sdcard/screenshots -p >screenshots
  exit 1
fi