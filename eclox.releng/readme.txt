!!! ATTENTION do not change other versions manually !!!
!!! all versions must be same BEFORE executiong set-version !!!
!!! e.g. in all pom.xml files: 0.10.1-SNAPSHOT and all eclipse files [MANIFEST.MF, feature.xml, category.xml]: 0.10.1.qualifier !!!

1. update the version in pom.xml: e.g. <newVersion>0.11.0-SNAPSHOT</newVersion>
2. run: mvn 'clean tycho-versions:set-version' all versions are updated now
3. build with 'mvn verify' or 'mvn install'

sources:
http://codeandme.blogspot.co.at/2012/12/tycho-build-9-updating-version-numbers.html
https://wiki.eclipse.org/Tycho/Packaging_Types#eclipse-plugin
https://eclipse.org/tycho/sitedocs/tycho-release/tycho-versions-plugin/plugin-info.html

30.09.2015 anb0s
