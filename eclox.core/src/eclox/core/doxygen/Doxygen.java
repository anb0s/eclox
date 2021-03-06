/*******************************************************************************
 * Copyright (C) 2003-2008, 2013, Guillaume Brocker
 * Copyright (C) 2015-2018, Andre Bossert
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Brocker
 *     - Initial API and implementation
 *     Andre Bossert
 *     - added support of eclipse variables to resolve doxygen path
 *     - added support of eclipse variables passed to environment
 *     - Add ability to use Doxyfile not in project scope
 *     - Refactoring of deprecated API usage
 *     - fixed Doxygen Build Console: stderr and stdin are not synchronized
 *       see https://github.com/anb0s/eclox/issues/201
 *
 ******************************************************************************/

package eclox.core.doxygen;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IStringVariable;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.VariablesPlugin;

import eclox.core.IPreferences;
import eclox.core.Plugin;

/**
 * Implements the abstract doxygen frontend. Sub-classes provides concret
 * doxygen access.
 *
 * @author gbrocker
 */
public abstract class Doxygen {

    /**
     * a string containing defining the default doxygen command to use
     */
    protected final static String COMMAND_NAME = "doxygen";

    /**
     * a string containing command option for 'help'
     */
    protected final static String COMMAND_OPTION_HELP = "--help";

    /**
     * a string containing command option for 'build'
     */
    protected final static String COMMAND_OPTION_BUILD = "-b";

    /**
     * a string containing command option for 'update'
     */
    protected final static String COMMAND_OPTION_UPDATE = "-u";

    /**
     * a string containing command option for 'generate'
     */
    protected final static String COMMAND_OPTION_GENERATE = "-g";

    /**
     * Retrieves the default doxygen instance to use.
     */
    public static Doxygen getDefault() {
        Doxygen doxygen = null;
        // get the actual default preference store
        //final String identifier  = Plugin.getDefault().getPluginPreferences().getString( IPreferences.DEFAULT_DOXYGEN );
        IPreferencesService service = Platform.getPreferencesService();
        final String PLUGIN_ID = Plugin.getDefault().getBundle().getSymbolicName();
        IEclipsePreferences defaultNode = DefaultScope.INSTANCE.getNode(PLUGIN_ID);
        IEclipsePreferences instanceNode = InstanceScope.INSTANCE.getNode(PLUGIN_ID);
        IEclipsePreferences[] nodes = new IEclipsePreferences[] { instanceNode, defaultNode };
        final String identifier = service.get(IPreferences.DEFAULT_DOXYGEN, "", nodes);

        List<Class<? extends Doxygen>> doxygenClassList = new ArrayList<Class<? extends Doxygen>>();
        doxygenClassList.add(DefaultDoxygen.class);
        doxygenClassList.add(CustomDoxygen.class);
        doxygenClassList.add(BundledDoxygen.class);
        for (Class<? extends Doxygen> doxygenClass : doxygenClassList) {
            doxygen = getFromClassAndIdentifier(doxygenClass, identifier);
            if (doxygen != null)
                break;
        }
        return doxygen;
    }

    public static Doxygen getFromClassAndIdentifier(Class<? extends Doxygen> doxygenClass, String identifier) {
        Doxygen doxygen = null;
        if (identifier.startsWith(doxygenClass.getName())) {
            String location = null;
            int locationIndex = identifier.indexOf('=');
            if (locationIndex == -1)
                locationIndex = identifier.indexOf(' ');
            if (locationIndex != -1)
                location = identifier.substring(locationIndex + 1);
            try {
                doxygen = doxygenClass.newInstance();
                if (location != null)
                    doxygen.setLocation(location);
            } catch (InstantiationException | IllegalAccessException e) {
                doxygen = null;
            }
        }
        return doxygen;
    }

    private String resolveOneVariable(String key, IStringVariableManager variableManager, boolean dynamicAllowed) {
        if (key != null) {
            if (variableManager == null) {
                variableManager = VariablesPlugin.getDefault().getStringVariableManager();
            }
            if (variableManager != null) {
                // static variable
                IValueVariable staticVar = variableManager.getValueVariable(key);
                if (staticVar != null) {
                    return staticVar.getValue();
                }
                // dynamic variable
                else if (dynamicAllowed) {
                    String varName = key;
                    String valuePar = null;
                    // check if parameterized and get parameter
                    int index = key.indexOf(':');
                    if (index > 1) {
                        varName = key.substring(0, index);
                        if (key.length() > index + 1)
                            valuePar = key.substring(index + 1);
                    }
                    // get dynamic variable
                    IDynamicVariable dynVar = variableManager.getDynamicVariable(varName);
                    if (dynVar == null)
                        return null;
                    try {
                        return dynVar.getValue(valuePar);
                    } catch (CoreException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    private void resolveAllVariables(Map<String, String> varMap) {
        IStringVariableManager variableManager = VariablesPlugin.getDefault().getStringVariableManager();
        for (IStringVariable strVar : variableManager.getVariables()) {
            String name = strVar.getName();
            if (name != null && !name.isEmpty()) {
                String value = resolveOneVariable(name, variableManager, false);
                if (value != null) {
                    varMap.put(name, value);
                }
            }
        }
    }

    private void addAllVarsToEnvironment(Map<String, String> env) {
        Map<String, String> resolvedVars = new HashMap<String, String>();
        resolveAllVariables(resolvedVars);
        env.putAll(resolvedVars);
    }

    /*
    private void addEcloxVarsToEnvironment(Map<String, String> env) {
        List<String> vars = new ArrayList<String>();
        vars.add("GRAPHVIZ_PATH");
        for (String name : vars) {
            if (name != null && !name.isEmpty())
            {
                String value = resolveOneVariable(name, null, true);
                if (value != null) {
                    env.put(name, value);
                }
            }
        }
    }
    */

    private void setEnvironment(ProcessBuilder pb) {
    	// get passed system environment
        Map<String, String> envs = pb.environment();
        /*
         * the library search path environment variable name differs between different systems:
         * Windows: PATH
         * Linux  : LD_LIBRARY_PATH
         */
        String commandFolder = getCommandFolder();
        if ((commandFolder != null) && !commandFolder.isEmpty()) {
            envs.put("PATH", commandFolder + File.pathSeparator + System.getenv("PATH"));
            envs.put("LD_LIBRARY_PATH", commandFolder + File.pathSeparator + System.getenv("LD_LIBRARY_PATH"));
        }

        // add own variables, like GRAPHVIZ_PATH etc.
        //addEcloxVarsToEnvironment(envs);
        // add all defined variables
        addAllVarsToEnvironment(envs);
    }

    private Process createProcess(List<String> command, File workingDir) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(command);
        setEnvironment(pb);
        if ((workingDir != null) && workingDir.exists()) {
        	pb.directory(workingDir);
        }
        return pb.start();
    }

	/**
     * Retrieves the version string of wrapped doxygen.
     *
     * @return	a string containing the doxygen version string
     */
    public String getVersion() {
        try {
        	List<String> command = new ArrayList<String>();
        	command.add(getCommand());
        	command.add(COMMAND_OPTION_HELP);
        	Process process = createProcess(command, null);
            process.waitFor();

            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            // matches the doxygen welcome message
            Pattern pattern = Pattern.compile("^doxygen\\s+version\\s+([\\d\\.]+).*",
                    Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Matcher matcher = null;
            String inputLine = input.readLine();

            if (inputLine != null && inputLine.length() > 0) {
                matcher = pattern.matcher(inputLine);
            }

            if (matcher != null && matcher.matches()) {
                return matcher.group(1);
            } else {
                String errorMessage = new String();
                String line;
                while ((line = error.readLine()) != null) {
                    errorMessage = errorMessage.concat(line);
                }
                throw new RuntimeException("Unable to get doxygen version: " + errorMessage);
            }
        } catch (Throwable t) {
            Plugin.log(t);
            return null;
        }
    }

    /**
     * Launch a doxygen with given file an mode
     *
     * @param   file   the file representing the doxygen configuration to use for the build
     *
     * @return  The process that run the build.
     */
    public Process run(File file, boolean checkIfFileExisting, String commandOption)
            throws InvokeException, RunException {
        if (checkIfFileExisting && (file.exists() == false)) {
            throw new RunException("Missing or bad doxyfile");
        }
        try {
        	// create process with doxygen command
        	List<String> command = new ArrayList<String>();
        	command.add(getCommand());
        	command.add(commandOption);
        	command.add(file.getCanonicalPath());
        	return createProcess(command, file.getParentFile().getAbsoluteFile());
        } catch (IOException ioException) {
            throw new InvokeException(ioException);
        }
    }

    /**
     * Launch a documentation build.
     *
     * @param	ifile	the workspace file representing the doxygen configuration to use for the build
     *
     * @return	The process that run the build.
     */
    public Process build(IFile ifile) throws InvokeException, RunException {
        return build(ifile.getLocation().makeAbsolute().toFile());
    }

    /**
     * Launch a documentation build.
     *
     * @param   file   the file representing the doxygen configuration to use for the build
     *
     * @return  The process that run the build.
     */
    public Process build(File file) throws InvokeException, RunException {
        return run(file, true, COMMAND_OPTION_BUILD);
    }

    /**
     * Generate an empty configuration file.
     *
     * @param   ifile   the configuration file to generate.
     */
    public void generate(IFile ifile) throws InvokeException, RunException {
        generate(ifile.getLocation().makeAbsolute().toFile());
        // Force some refresh to display the file.
        try {
            ifile.refreshLocal(0, null);
        } catch (Throwable throwable) {
            Plugin.log(throwable);
        }
    }

    /**
     * Generate an empty configuration file.
     *
     * @param   file    the configuration file to generate.
     */
    public void generate(File file) throws InvokeException, RunException {
        run_local(file, false, COMMAND_OPTION_GENERATE);
    }

    /**
     * Generate an empty configuration file.
     *
     * @param	file	the configuration file to generate.
     */
    public void run_local(File file, boolean checkIfFileExisting, String commandOption)
            throws InvokeException, RunException {
        try {
            Process process = run(file, checkIfFileExisting, commandOption);
            if (process.waitFor() != 0) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String errorMsg = new String();
                String line;
                for (line = reader.readLine(); line != null; line = reader.readLine()) {
                    errorMsg = errorMsg.concat(line);
                }
                throw new RunException(errorMsg);
            }
        } catch (InvokeException invokeExpection) {
            throw invokeExpection;
        } catch (RunException runException) {
            throw runException;
        } catch (SecurityException securityException) {
            throw new InvokeException(securityException);
        } catch (IOException ioException) {
            throw new InvokeException(ioException);
        } catch (Throwable throwable) {
            Plugin.log(throwable);
        }
    }

    /**
     * Launch update for configuration file.
     *
     * @param   ifile   the configuration file to update.
     */
    public void updateLocal(IFile ifile) throws InvokeException, RunException {
        updateLocal(ifile.getLocation().makeAbsolute().toFile());
        // Force some refresh to display the file.
        try {
            ifile.refreshLocal(0, null);
        } catch (Throwable throwable) {
            Plugin.log(throwable);
        }
    }

    /**
     * Launch update for configuration file.
     *
     * @param   file   the configuration file to update.
     */
    public void updateLocal(File file) throws InvokeException, RunException {
        run_local(file, true, COMMAND_OPTION_UPDATE);
    }

    /**
     * Launch update for configuration file.
     *
     * @param   file   the configuration file to update.
     *
     * @return  The process that run the update.
     */
    public Process update(File file) throws InvokeException, RunException {
        return run(file, true, COMMAND_OPTION_UPDATE);
    }

    /**
     * Launch update for configuration file.
     *
     * @param   ifile   the configuration file to update.
     *
     * @return  The process that run the update.
     */
    public Process update(IFile ifile) throws InvokeException, RunException {
        return update(ifile.getLocation().makeAbsolute().toFile());
    }

    public static String getCommandOptionHelp() {
        return COMMAND_OPTION_HELP;
    }

    public static String getCommandOptionBuild() {
        return COMMAND_OPTION_BUILD;
    }

    public static String getCommandOptionUpdate() {
        return COMMAND_OPTION_UPDATE;
    }

    public static String getCommandOptionGenerate() {
        return COMMAND_OPTION_GENERATE;
    }

    /**
     * Updates the location of the custom doxygen.
     */
    public abstract void setLocation(String location);

    /**
     * Retrieves the string containing the command line to the doxygen binary.
     * Sub-classes must implement this method.
     *
     * @return	a string containing the path to the doxygen binary file
     */
    public abstract String getCommand();

    /**
     * Retrieves the string containing the folder of the doxygen binary.
     * Sub-classes must implement this method.
     *
     * @return	a string containing the path to the folder of the doxygen binary file
     */
    public abstract String getCommandFolder();

    /**
     * Retrieves the description string of the doxygen wrapper instance.
     *
     * @return	a string containing the description of the dowygen wrapper
     */
    public abstract String getDescription();

    /**
     * Retrieves the identifier of the doxygen wrapper.
     *
     * @return	a string containing the doxygen wrapper identifier
     */
    public abstract String getIdentifier();

}
