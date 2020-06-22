# Energy Debt Rules for Java Plugin

This guide instructs on the installation and usage of Energy Debt in SonarQube 8.x

## Install

Installing the plugin is as simple as building it using `maven clean package` and copying the `.jar` file in the resulting `target` folder, then moving it to your SonarQube's `extensions/plugins/` folder and restarting SonarQube.

## Usage

Once installed, you will need to activate the Energy Debt rules in your Java Quality Profile (if using the default Sonar Way profile, you will need to extend it into a new profile) through an administrative account.

![Once you have a Quality Profile, select the option Activate More Rules](images/activate_rules.png)

This will bring up the list of Java rules which can be activated. You can easily find the Energy Debt Rules by filtering the `energy-smell` tag. Once found, all rules can be activated by selecting `Bulk Change` and choosing `Activate In [Quality Profile name]`.

![](images/bulk_change.png)

Once this is complete, executing a Java code analysis will yield its Energy Debt, visible by accessing the project page, selecting `More` and choosing `Energy Debt`.
