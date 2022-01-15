# eclox [![Version](https://img.shields.io/github/release/anb0s/eclox.svg)](https://github.com/anb0s/eclox/releases) [![Issues](https://img.shields.io/github/issues/anb0s/eclox.svg)](https://github.com/anb0s/eclox/issues) [![build](https://github.com/anb0s/eclox/actions/workflows/maven.yml/badge.svg)](https://github.com/anb0s/eclox/actions/workflows/maven.yml) [![License](https://img.shields.io/badge/License-EPL%202.0-blue.svg)](https://www.eclipse.org/legal/epl-2.0)

Eclox is a simple [Doxygen](http://www.doxygen.nl) frontend plug-in for [Eclipse](http://www.eclipse.org). It aims to provide a slim and sleek integration of the code documentation process into Eclipse.

![eclox_arch](https://raw.githubusercontent.com/anb0s/eclox/main/eclox.site/images/eclox.png "Eclox Architecture")

Features Overview:
------------------
- Graphical edition of Doxygen settings (Doxyfile)
- Integrated Doxygen invocation
- Doxygen outputs logging
- Optional packaged Doxygen binaries (for Windows and Linux)

Not supported:
--------------
- Content assist for Doxygen comments --> Eclipse C/C++ (CDT) supports it

Prerequisites:
-------------
- **Java SE 1.8 or newer**
- **Doxygen executable in PATH or bundled with Eclox or custom location** (see Installation and Configuration below)

Installation:
-------------
From Eclipse Markeplace:

<a href="http://marketplace.eclipse.org/marketplace-client-intro?mpc_install=1536329" class="drag" title="Drag to your running Eclipse* workspace. *Requires Eclipse Marketplace Client"><img class="img-responsive" src="https://marketplace.eclipse.org/sites/all/themes/solstice/public/images/marketplace/btn-install.png" alt="Drag to your running Eclipse* workspace. *Requires Eclipse Marketplace Client" /></a>

http://marketplace.eclipse.org/content/eclox

OR

Using the update site located at:
http://anb0s.github.io/eclox

Select Eclox Plugin and optional bundled Doxygen executable:

![Eclox install](https://raw.githubusercontent.com/anb0s/eclox/main/eclox.site/images/eclox-install.png)

OR

You can download the update sites for offline install --> go to: https://github.com/anb0s/eclox/releases
and download the **org.gna.eclox.site-0.12.1-SNAPSHOT.zip** and optional bundled doxygen executable **org.gna.eclox.doxygen.site-X.Y.Z-SNAPSHOT.zip** files, e.g. for latest stable version: https://github.com/anb0s/eclox/releases/latest

Now use inside Eclipse: "**Help-> Install New Software... -> Add... -> Archive...**", select the zip file and enter name:

![Eclox instal local](https://raw.githubusercontent.com/anb0s/eclox/main/eclox.site/images/eclox-install-local.png)

and select it:

![Eclox instal local-2](https://raw.githubusercontent.com/anb0s/eclox/main/eclox.site/images/eclox-install-local-2.png)

**Optional: similar installation for Doxygen binaries:**

![Eclox Doxygen instal local](https://raw.githubusercontent.com/anb0s/eclox/main/eclox.site/images/eclox-doxygen-install-local.png)

![Eclox instal local-2](https://raw.githubusercontent.com/anb0s/eclox/main/eclox.site/images/eclox-doxygen-install-local-2.png)

For additional details, please refer to Eclipse user guide.

Configuration
-------------

Once the plugin installed, you must ensure that the default PATH environment variable makes the Doxygen binary reachable for the plugin or you have installed the bundled Doxygen executable from Eclox site. If not, you can update PATH to include to directory containing the Doxygen binary, or you can tell eclox where that binary is located on your system (which is in my opinion the better solution). To do this, open eclipse's preference edition dialog window and go into the new "Doxygen" section.

If you have installed Doxygen executable from Eclox update site, check if it was automaticaly selected:
![Eclox Doxygen Bundled](https://raw.githubusercontent.com/anb0s/eclox/main/eclox.site/images/eclox-bundled-doxygen.png)

If not you can select custom Doxygen location with "Add..." and "Browse...":
![Eclox Doxygen Custom](https://raw.githubusercontent.com/anb0s/eclox/main/eclox.site/images/eclox-custom-doxygen.png)

Now just select and apply:
![Eclox Doxygen Custom](https://raw.githubusercontent.com/anb0s/eclox/main/eclox.site/images/eclox-custom-doxygen-enabled.png)

For **Mac OS X** users there is an excellent HowTo: https://github.com/theolind/mahm3lib/wiki/Integrating-Doxygen-with-Eclipse

Usage
-----

You can create new Doxygen projects (also called doxyfiles) using the creation wizard. Go to "**File -> New -> Other -> Other -> Doxyfile**". Press next and set both file location and name. Then an empty doxyfile will be created at the specified location. The wizard automatically adds the ".Doxyfile" extension.

You should now see a file with a blue @-sign icon. This is your new doxyfile. Double-clicking on it will open the editor. You can now browse and edit the settings.

![Eclox basic editor](https://raw.githubusercontent.com/anb0s/eclox/main/eclox.site/images/editor-basic.png)

![Eclox advanced editor](https://raw.githubusercontent.com/anb0s/eclox/main/eclox.site/images/editor-advanced.png)

Once your have properly set all doxyfile fields, you can launch a documentation build using the toolbar icon showing a blue @-sign.
![Eclox toolbar icon](https://raw.githubusercontent.com/anb0s/eclox/main/eclox.site/images/eclox-toolbar-icon.png)

In the case the button is not visible in the toolbar, your current perspective needs to get configured. Go to "**Window -> Customize perspective -> Commands**" and in "**Available command groups**" check "**Doxygen**". Additionally, you can browse the latest builds by clicking the down arrow right to the toolbar button.

![Eclox toolbar icon menu](https://raw.githubusercontent.com/anb0s/eclox/main/eclox.site/images/eclox-toolbar-icon-menu.png)

When the documentation build starts, a new view showing the build log opens.

![Eclox console](https://raw.githubusercontent.com/anb0s/eclox/main/eclox.site/images/eclox-console.png)

In its toolbar, a button named "**Stop**" allows you to halt the current build process. The current build also appears in the Eclipse job progress view and you can control the job from there.

The build toolbar action determine the next doxyfile to build depending on the current active workbench part (editor or view) and the current selection in that part. For example, if the active part is a doxyfile editor, the next doxyfile to build will be the one being edited. If the active part is the resource explorer and the current selection is a doxyfile, that doxyfile will be next to get build. In the case the active part selection doesn't correspond to a doxyfile, the last built doxyfile will be rebuiled. And if the build history is empty, you will be asked for the doxyfile to build.

![Eclox select doxyfile](https://raw.githubusercontent.com/anb0s/eclox/main/eclox.site/images/eclox-select-doxyfile.png)

For developers and contributors
-------------------------------
Please checkout the wiki page:
https://github.com/anb0s/eclox/wiki/How-to-build

License:
--------
https://www.eclipse.org/legal/epl-2.0
