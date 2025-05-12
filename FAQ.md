# Frequently asked questions

- [I have experienced an error](#i-have-experienced-an-error)
- [Why has my bug report been closed?](#why-has-my-bug-report-been-closed)
- [Why is my card not visible in the upcoming cards view?](#why-is-my-card-not-visible-in-the-upcoming-cards-view)
- [Why does the browser open when clicking on an attachment?](#why-does-the-browser-open-when-clicking-on-an-attachment)
- [Known issues](#known-issues)
- [Why don't you make an option for…?](#why-dont-you-make-an-option-for)
- [Why don't you make an iOS app?](#why-dont-you-make-an-ios-app)
- [How to receive notifications when a due date is reached?](#how-to-receive-notifications-when-a-due-date-is-reached)
- [Using the DEV App](#using-the-dev-app)

## I have experienced an error

Sorry. There are so many different environments, that it is impossible for us to test each and every constellation.

First of all make sure you have updated to and tried with the latest available versions of both, this app and the [Deck server app](https://apps.nextcloud.com/apps/deck).

In case you receive a `NextcloudApiNotRespondingException`, try to disable the battery optimization for both apps.
In all other cases please try  to clear the storage of **both** apps, Nextcloud Android **and** Nextcloud Deck Android.

You can achieve this by navigating to

```
Android settings
       ↓
     Apps
       ↓
Nextcloud / Deck 
       ↓
    Storage
       ↓
 Clear storage
```

Often there is an issue with the state of the server app. Try to create a new test account at your Nextcloud instance and connect to it.
If there are no errors, share the boards of your actual account one by one with your test account to find the defect one.

If the issue persists, [open a bug report in our issue tracker](https://github.com/stefan-niedermann/nextcloud-deck/issues/new?assignees=&labels=bug&template=bug_report.md&title=).

## Why has my bug report been closed?

As stated in the bug templates, we reserve to close issues which do not fill the **complete** issue template. The information we ask for is urgently needed, even if it might not seem to be important or relevant to you.

We have very limited resources and capacity and we really want to help you fixing different bugs, but we can impossibly know your environment, your different software versions, the store you used.
Therefore it is extremely important for you to describe the **exact steps to reproduce**. This includes information about your environment.

Example for a bad description:

> 1. The app crashes when i save a card

Example for a good description:

> 1. Open any existing card\
> 2. Add text to the description\
> 3. Click on the ✕ in the top left\
> 4. Answer "Save" when asking to discard or save the changes\
> 5. See app crash

We also preserve to close issues where the original reporter does not answer within a certain time frame. We usually answer issues within a hour and expect you to respond to our questions within a week.

This is necessary for two reasons:

1. We have a rapid development cycle - bugs which have been reported weeks ago might no longer relevant
2. We are loosing the context of a report or a question over the time. We have many things to care about and digging into an issue deep and then relying on an response which is not coming is a waste of our limited free time

## Why is my card not visible in the upcoming cards view?

Checkout the [Upcoming Analyzer](https://upcoming-analyzer.niedermann.it/) to check out the criterias your card needs to match to be shown in the upcoming cards view.

## Why does the browser open when clicking on an attachment?

The Deck server app supports two different type of attachments:
- Attachments added prior to Deck server 1.3 have been added as `deck_file` attachments
- Attachments added with Deck server 1.3 or later are added as `file` attachments

The latter are stored as usual files in your Nextcloud, usually in the `/Deck` folder of your account.  
Only `file` attachments are able show preview images and to get opened directly, while `deck_file` attachments are effectively only available via the Web UI.  
We opened a [feature request](https://github.com/nextcloud/deck/issues/3101) to implement an `occ` migration command, but the current state of implementation is unclear.

## Known issues

Here is a collection of issues which are caused by the (not by us developed) Deck server app and can not be fixed within Deck Android. Please [contact the Nextcloud GmbH](https://nextcloud.com/contact/) if you want them to be fixed.

- Viewing archived cards is not possible: https://github.com/nextcloud/deck/issues/2613
- Renaming a list in the web UI does not update in Deck Android: https://github.com/nextcloud/deck/issues/2866

## Why don't you make an option for…?

We prefer good defaults over providing an option for each edge case. Our resources are quite limited, so we have to consider introducing new options very carefully.

1. A feature is implemented quickly, but who will maintain it for the next 5 years?
2. Each option increases the test matrix exponentially and leads to huge efforts to test every combination
3. Each option increases the possible constellations, making it hard to track down issues
4. Each option increases the visual noise for people who will *not* use the options
5. Each option increases the maintenance efforts, making it harder over the time to work on actual features
6. Each option introduces new side effects, which might lead to undiscovered bugs or break existing features
7. The Android app aims to mirror feature parity with the corresponding server app

## Why don't you make an iOS app?

We¹ believe, that the idea of Nextcloud - own your data - does not match the mindset of Apple and its products. We believe, that protecting your data is not possible on closed source operation systems like iOS. Yes, also Android often is not fully open source and distributed with proprietary stuff, but you have the choice and the possibility to use the app on the Android Open Source Project because the app does not depend on any proprietary libraries.
Also licenses for SDKs, Hardware and distributing is ridiculous expensive for an FLOSS project.
The API of the Deck server app is well documented and everyone is free to start an iOS client. Of course we appreciate a growing ecosystem even if we will not contribute to this part personally.

¹ Disclaimer: "We" are not working for nor related to Nextcloud Inc. in any way. We are just a few volunteers which contribute to this app in our free time.

## How to receive notifications when a due date is reached?

When a card reaches its due date, users usually want to get notified. The Deck app can send push notifications to you, which are displayed in the browser, via the [Nextcloud Desktop sync client](https://nextcloud.com/install/#install-clients) and with the [Nextcloud Android app](https://apps.nextcloud.com/apps/android_nextcloud_app). While you won't even need the Deck Android app, it will provide an enhanced integration starting with the Nextcloud Android app `3.14.2` and Deck Android `1.10.x`: Clicking on an notification will directly open the affected card in the Deck Android app.

In order to make this all work, you will need two things:
1. Make sure that your Nextcloud Android app is able to receive push notifications (You will need Google Play services for that)
2. Configure in your user settings, that you *want* to receive push notifications:
    - Click in the top right of your browser on your avatar
    - Choose "settings" and then "activities" in the left sidebar.
    - Enable the checkbox `Changes in the Deck app`
       ![Enable push notifications for Deck](https://user-images.githubusercontent.com/4741199/125485987-398f3ee8-f59c-4234-8453-1da679d4d7c5.png)

## Using the DEV app

Sometimes we provide a DEV app for testing purposes, mostly related to an issue. Here's some further information:

- We provide a link to an Artifact built by GitHub 
  - The link should look something like this: https://github.com/stefan-niedermann/nextcloud-deck/actions/runs/[NUMBER]/artifacts/[NUMBER] 
  - Please make sure you download the APK from trusted sources only. 
- Download the APK file and install it
  - In most cases your phone will refuse to install the APK right away, unless you explicitly trust the source
  - We recommend to allow APK installations by unknown sources **temporarily only**, it's best to remove the permission afterwards
  - To do so, just follow the instructions of the dialog that came up
- The app will install **beside** your actual app
  - You end up with two Deck apps
  - The DEV app will have a "DEV" label on its icon
- Open the DEV app
  - As it is a fresh installation, you will need to import your boards
  - To do so, select the account you want to test on and let the app do the initial sync
- Test whatever you want to test
- And maybe provide some feedback ;)

## Gathering Debug-Logs

Sometimes it can be really helpful, if we can see whats going on inside the App in detail. For this, we can gather logs as follows:

- Open the side-menu (burger-button top left corner)
- Open Settings page (at the bottom)
- Switch ON "Debug Logs"

Then go back to the App and do what ever causes problems. Afterwards, to get the gathered logs, do the following:

- Open the side-menu (burger-button top left corner)
- Right beside the Deck-Logo you'll see a little bug-icon
- tap on that icon
- select how you want to share the logfile (e.g. via email)

!! IMPORTANT !! After you got what you need, please remember to deactivate the "Debug Logs" option. Since the Logs are gathered in RAM, this could cause issues on the long run.
