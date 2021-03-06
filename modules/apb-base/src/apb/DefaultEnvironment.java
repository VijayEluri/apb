

// Copyright 2008-2009 Emilio Lopez-Gabeiras
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License
//


package apb;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import apb.utils.DebugOption;
import apb.utils.FileUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.lang.Character.isJavaIdentifierPart;

import static apb.Logger.Level.*;

import static apb.utils.StringUtils.isEmpty;

// User: emilio
// Date: Aug 24, 2009
// Time: 7:34:40 PM

abstract class DefaultEnvironment
    implements Environment
{
    //~ Instance fields ......................................................................................

    /**
     * The base property map
     */
    @NotNull final PropertyMap properties;

    @Nullable private File basedir;

    /**
     * The list of jars that comprise the extension class path
     * It is initialized in a lazy way
     */
    @Nullable private List<File> extClassPath;

    /**
     * The logger to display messages
     */
    @NotNull private final Logger logger;

    //~ Constructors .........................................................................................

    protected DefaultEnvironment(@NotNull Logger logger)
    {
        this.logger = logger;
        properties = new PropertyMap();
    }

    //~ Methods ..............................................................................................

    public abstract boolean mustShow(DebugOption option);

    @Nullable public final String getOptionalProperty(@NotNull String id)
    {
        String result = overrideProperty(id);

        if (result == null) {
            result = id.startsWith(ENV) ? System.getenv(id.substring(ENV.length())) : retrieveProperty(id);
        }

        return result;
    }

    public final void putProperty(@NotNull String name, @NotNull String value)
    {
        if (mustShow(DebugOption.PROPERTIES)) {
            logVerbose("Setting property %s:%s=%s\n", getId(), name, value);
        }

        properties.put(name, value);
    }

    /**
     * Process the string expanding property values.
     * The `$' character introduces property expansion.
     * The property  name  or  symbol  to  be expanded  may be enclosed in braces,
     * which are optional but serve to protect the variable to be expanded from characters
     * immediately following it which could be interpreted as part of the name.
     * When braces are used, the matching ending brace is the first `}' not escaped by a backslash
     *
     * @param string The string to be expanded.
     * @return An String with properties expanded.
     */
    @NotNull public final String expand(@Nullable String string)
    {
        if (isEmpty(string)) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        StringBuilder id = new StringBuilder();

        boolean insideId = false;
        boolean closeWithBrace = false;

        for (int i = 0; i < string.length(); i++) {
            char chr = string.charAt(i);

            if (insideId) {
                insideId = closeWithBrace ? chr != '}' : isPropertyIdPart(chr);

                if (insideId) {
                    id.append(chr);
                }
                else {
                    result.append(getProperty(id.toString()));
                    id.setLength(0);

                    if (!closeWithBrace) {
                        result.append(chr);
                    }

                    closeWithBrace = false;
                }
            }
            else if (chr == '$') {
                insideId = true;

                if (i + 1 < string.length() && string.charAt(i + 1) == '{') {
                    i++;
                    closeWithBrace = true;
                }
            }
            else if (chr == '\\' && i + 1 < string.length() && string.charAt(i + 1) == '$') {
                result.append('$');
                i++;
            }
            else {
                result.append(chr);
            }
        }

        if (insideId) {
            result.append(getProperty(id.toString()));
        }

        return result.toString();
    }

    public final void logInfo(String msg, Object... args)
    {
        logger.log(INFO, msg, args);
    }

    public final void logWarning(String msg, Object... args)
    {
        logger.log(WARNING, msg, args);
    }

    public final void logSevere(String msg, Object... args)
    {
        logger.log(SEVERE, msg, args);
    }

    public final void logVerbose(String msg, Object... args)
    {
        logger.log(VERBOSE, msg, args);
    }

    /**
     * Handle an Error. It creates a build Exception with the specified msg.
     * And delegates the handling to {@link #handle(Throwable t)}
     * @param msg The message used to create the build exception
     */
    public final void handle(@NotNull String msg)
    {
        handle(new BuildException(msg));
    }

    /**
     * Handle an Error.
     * Either raise the exception or log it depending on the value of the failOnError flag
     * @param e The Exception causing the failure
     */
    public final void handle(@NotNull Throwable e)
    {
        if (isFailOnError()) {
            throw (e instanceof BuildException) ? (BuildException) e : new BuildException(e);
        }

        logSevere(e.getMessage());
    }

    public final void abort(String msg)
    {
        logInfo(msg);
        Apb.exit(1);
    }

    /**
     * Get the Extension Jars to be searched when we compile definitions
     * @return the extension Jars to be searched when we compiled definitions
     */
    @NotNull public Collection<File> getExtClassPath()
    {
        if (extClassPath == null) {
            final ArrayList<File> files = new ArrayList<File>();
            String                path = getProperty(Constants.EXT_PATH_PROPERTY, "");

            if (!path.isEmpty()) {
                for (String p : path.split(File.pathSeparator)) {
                    files.addAll(FileUtils.listAllFilesWithExt(new File(p), ".jar"));
                }
            }

            extClassPath = files;
        }

        return extClassPath;
    }

    /**
     * Return the value of the specified boolean property
     * @param id The property to search
     * @param defaultValue The default value
     * @return The value of the property or false if the property is not set
     */
    public final boolean getBooleanProperty(@NotNull String id, boolean defaultValue)
    {
        return Boolean.parseBoolean(getProperty(id, Boolean.toString(defaultValue)));
    }

    /**
     * Return the value of the specified property
     * @param id The property to search
     * @param defaultValue The default value to return in case the property is not set
     * @return The value of the property
     */
    @NotNull public final String getProperty(@NotNull String id, @NotNull String defaultValue)
    {
        String result = getOptionalProperty(id);
        return result == null ? defaultValue : result;
    }

    /**
     * Return the value of the specified property
     * @param id The property to search
     * @return The value of the property or the empty String if the Property is not found and failOnError is not set
     */
    @NotNull public final String getProperty(@NotNull String id)
    {
        String result = getOptionalProperty(id);

        if (result == null) {
            Throwable e = new PropertyException(id);

            if (isFailOnError() && Apb.failOnAbsentProperty()) {
                throw new BuildException(e);
            }

            logSevere(e.getMessage());
            result = "";
        }

        return result;
    }

    public final boolean hasProperty(@NotNull String id)
    {
        return getOptionalProperty(id) != null;
    }

    /**
     * Returns a File object whose path is relative to the basedir
     * @param name The (Usually relative to the basedir) file name.
     * @return A file whose path is relative to the basedir.
     */
    @NotNull public final File fileFromBase(@NotNull String name)
    {
        final File file = new File(expand(name));
        return FileUtils.normalizeFile(file.isAbsolute() ? file : new File(getBaseDir(), file.getPath()));
    }

    /**
     * Returns a File object whose path is relative to the source directory of the current module
     * @param name The (Usually relative to the source directory of the module) file name.
     * @return A file whose path is relative to the source directory of the current module.
     */
    @NotNull public final File fileFromSource(@NotNull String name)
    {
        final File child = new File(expand(name));
        return child.isAbsolute() ? child : new File(getModuleHelper().getSourceDir(), child.getPath());
    }

    /**
     * Returns a File object whose path is relative to the generated source directory of the current module
     * @param name The (Usually relative to the generated source directory of the module) file name.
     * @return A file whose path is relative to the generated source directory of the current module.
     */
    @NotNull public final File fileFromGeneratedSource(@NotNull String name)
    {
        final File child = new File(expand(name));
        return child.isAbsolute() ? child : new File(getModuleHelper().getGeneratedSource(), child.getPath());
    }

    /**
     * Get the base directory of the current Module
     * @return the base directory of the current Module
     * @throws IllegalStateException If there is no current module
     */
    @NotNull public File getBaseDir()
    {
        if (basedir == null) {
            basedir = new File(expand("$basedir"));
        }

        return basedir;
    }

    /**
     * Get current module output directory
     * @return current module output directory
     * @throws IllegalStateException If there is no current module
     */
    @NotNull public File getOutputDir()
    {
        return getModuleHelper().getOutput();
    }

    @NotNull public final Logger getLogger()
    {
        return logger;
    }

    /**
     * @exclude
     */
    @Nullable protected String overrideProperty(@NotNull String id)
    {
        return null;
    }

    /**
     * @exclude
     */
    @Nullable protected String retrieveProperty(@NotNull String id)
    {
        return retrieveLocalProperty(id);
    }

    /**
     * Package private method. Register the current Project builder
     * @param pb
     */
    abstract void register(ProjectBuilder pb);

    /**
     * Package private method. Return the current Project builder
     */
    abstract ProjectBuilder getCurrentProjectBuilder();

    /**
     * Return current ModuleHelper
     * @return current Module Helper
     */
    @NotNull ModuleHelper getModuleHelper()
    {
        throw new IllegalStateException("Not current Module");
    }

    /**
     * Get a property defined in THIS environment (Not inherited)
     * @param id The property name
     * @return The propertu value
     */
    @Nullable String retrieveLocalProperty(String id)
    {
        return properties.get(id);
    }

    private static boolean isPropertyIdPart(char chr)
    {
        return isJavaIdentifierPart(chr) || chr == '.' || chr == '-';
    }

    //~ Static fields/initializers ...........................................................................

    private static final String ENV = "ENV.";

    //~ Inner Classes ........................................................................................

    /**
    * A map that applies the idFromJavaId conversion to its keys
     * (Not a full Map but only the needed methods).
     */
    static class PropertyMap
    {
        private TreeMap<String, String> delegated;

        PropertyMap()
        {
            delegated = new TreeMap<String, String>();
        }

        void put(String id, String value)
        {
            delegated.put(apb.utils.NameUtils.idFromJavaId(id), value);
        }

        String get(String id)
        {
            return delegated.get(apb.utils.NameUtils.idFromJavaId(id));
        }

        void putAll(Map<String, String> map)
        {
            for (Map.Entry<String, String> e : map.entrySet()) {
                put(e.getKey(), e.getValue());
            }
        }
    }
}
