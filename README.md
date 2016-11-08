# eclox  [![Build Status](https://travis-ci.org/anb0s/eclox.svg)](https://travis-ci.org/anb0s/eclox)

Eclox is a simple doxygen frontend plug-in for eclipse. It aims to provide a slim and sleek integration of the code documentation process into eclipse.

Features Overview:
------------------
- Graphical edition of doxyfile settings
- integrated doxygen invocation
- doxygen outputs logging and
- optional packaged doxygen binaries (for windows and linux)

Installation:
-------------
There are two options to install the plugin: using the update site or using the packaged feature.

The update site is the more convenient way to install **eclox** and it is located at: http://anb0s.github.io/eclox

It is also available at Eclipse Markeplace: http://marketplace.eclipse.org/content/eclox

When using the packaged feature, you must extract the archive content into your eclipse's root location.

For additional details, please refer to eclipse's user guide.

Configuration
-------------

Once the plugin installed, you must ensure that the default PATH environment variable makes the Doxygen binary reachable for the plugin. If not, you can update PATH to include to directory containing the Doxygen binary, or you can tell eclox where that binary is located on your system (which is in my opinion the better solution). To do this, open eclipse's preference edition dialog window and go into the new "Doxygen" section.

Usage
-----

You can create new Doxygen projects (also called doxyfiles) using the creation wizard. Go to "File->New->Other->Other->Doxygen Configuration". Press next and set both file location and name. Then a empty doxyfile will be created at the specified location, the wizard automatically adds the ".Doxyfile" extension.

You should now see a file with a blue @-sign icon. This is your new doxyfile. Double-clicking on it will open the editor. You can now browse and edit the settings.

Once your have properly set all doxyfile fields, you can launch a documentation build using the toolbar icon showing a blue @-sign. In the case the button is not visible in the toolbar, your current perspective needs to get configured. Go to "Window->Customize perspective->Commands" and in "Available command groups" check "Doxygen". Additionally, you can browse the latest builds by clicking the down arrow right to the toolbar button.

When the documentation build starts, a new view showing the build log opens. In its toolbar, a button named "Stop" allows you to halt the current build process. The current build also appears in the Eclipse job progress view and you can control the job from there.

The build toolbar action determine the next doxyfile to build depending on the current active workbench part (editor or view) and the current selection in that part. For example, if the active part is a doxyfile editor, the next doxyfile to build will be the one being edited. If the active part is the resource explorer and the current selection is a doxyfile, that doxyfile will be next to get build. In the case the active part selection doesn't correspond to a doxyfile, the last built doxyfile will be rebuiled. And if the build history is empty, you will be asked for the doxyfile to build.
