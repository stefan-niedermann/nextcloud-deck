fastlane documentation
================
# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```
xcode-select --install
```

Install _fastlane_ using
```
[sudo] gem install fastlane -NV
```
or alternatively using `brew cask install fastlane`

# Available Actions
## Android
### android release_fdroid
```
fastlane android release_fdroid
```
Tags the current version and publish it on F-Droid
### android release_play_store_beta
```
fastlane android release_play_store_beta
```
Publish on Google Play Store Beta channel
### android release_play_store_prod
```
fastlane android release_play_store_prod
```
Publish on Google Play Store Production channel

----

This README.md is auto-generated and will be re-generated every time [fastlane](https://fastlane.tools) is run.
More information about fastlane can be found on [fastlane.tools](https://fastlane.tools).
The documentation of fastlane can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
