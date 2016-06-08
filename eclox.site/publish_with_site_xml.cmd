set ECLIPSE_EQUINOX_LAUNCHER_JAR=c:\Progs\eclipse-SDK-4.6-win32-x86_64\plugins\org.eclipse.equinox.launcher_1.3.200.v20160318-1642.jar
PUSHD %~dp0
set REPOSITORY_OUT=%CD%\repository
POPD
java -jar %ECLIPSE_EQUINOX_LAUNCHER_JAR% -application org.eclipse.equinox.p2.publisher.UpdateSitePublisher -metadataRepository file:/%REPOSITORY_OUT% -artifactRepository file:/%REPOSITORY_OUT% -source . -compress -publishArtifacts
