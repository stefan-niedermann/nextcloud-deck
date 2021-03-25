#!/bin/bash
git checkout master
LANG=en_US
git branch -vv | grep ': gone]'|  grep -v "\*" | awk '{ print $1; }' | xargs -r git branch -d