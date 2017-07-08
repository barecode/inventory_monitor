# Purpose

The following are a set of scripts which monitor SeneGence inventory and notify a distributor when "wishlist" items are in stock.

# Required Packages

* Default Raspbian
* heirloom-mailx - in order to send notification emails
  * `sudo apt-get install heirloom-mailx`

# How To Use

### Step 1: create secure.sh

The secure.sh file should have the following environment variables defined:
* `SENEGENCE_DIST_ID` - this is the SeneGense distributor ID number
* `SENEGENCE_DIST_PASS` - this is the distributor's password for the SeneGense site
* `AUTOMATION_EMAIL_ID` - the gmail account used to send the automated notifications
* `AUTOMATION_EMAIL_PASSWORD` - the gmail account's password
* `NOTIFICATION_EMAIL` - the e-mail to send the notifications

You will need to set the account settings for the gmail account to tolerate [Less Secure Apps](https://myaccount.google.com/lesssecureapps)

### Step 2: install in crontab

It is recommended the run_monitor.sh is installed in your crontab. I have mine configured to run at the bottom of every hour.
`crontab -e`
`30 * * * * run_monitor.sh >> run.log`

That's it! Sit back and relax and wait for the e-mail.
