# Frequently asked questions

## I have experienced an error

Sorry. There are so many different environments, that it is impossible for us to test each and every constellation.

In case you receive a `NextcloudApiNotRespondingException`, try to disable the battery optimization for both apps.
In all other cases please try  to clear the storage of **both** apps, Nextcloud Android **and** Nextcloud Deck Android.

You can achieve this by navigating to

```
Android settings
 ↳ Apps
   ↳ Nextcloud / Deck
     ↳ Storage
       ↳ Clear storage
```

Often there is an issue with the state of the server app. Try to create a new test account at your Nextcloud instance and connect to it. If there are no errors, share the boards of your actual account one by one with your test account to find the "faulty" one.

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