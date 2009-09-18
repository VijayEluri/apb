
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

package apb.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import apb.Environment;
import apb.ModuleHelper;
import apb.ProjectElementHelper;

import apb.metadata.Dependency;
import apb.metadata.DependencyList;
import apb.metadata.PackageType;

import apb.utils.CollectionUtils;
import apb.utils.FileUtils;

import org.jetbrains.annotations.NotNull;
//
// User: emilio
// Date: Oct 22, 2008
// Time: 10:07:04 AM

//
public class JavaTask
    extends CommandTask
{
    //~ Instance fields ......................................................................................

    @NotNull private String             classpath = "";
    private final boolean               executeJar;
    private int                         exitValue;
    private final String                jarOrClass;
    @NotNull private final List<String> javaArgs = new ArrayList<String>();

    /**
     * Max memory in megabytes used by the Java command
     */
    private int memory = 256;

    @NotNull private final Map<String, String> properties = new HashMap<String, String>();

    //~ Constructors .........................................................................................

    public JavaTask(@NotNull Environment env, @NotNull String className, String... arguments)
    {
        this(env, false, className);
        addArguments(arguments);
    }

    public JavaTask(@NotNull Environment env, boolean executeJar, @NotNull String jarOrClass)
    {
        super(env, new ArrayList<String>());
        this.executeJar = executeJar;
        this.jarOrClass = jarOrClass;

        // By default use the classpath of the current module if it is active
        ProjectElementHelper mod = env.getCurrent();

        if (mod != null && mod instanceof ModuleHelper) {
            final ModuleHelper m = (ModuleHelper) mod;
            classpath = FileUtils.makePath(m.runtimePath());
        }
    }

    //~ Methods ..............................................................................................

    public static void executeClass(@NotNull ModuleHelper helper, @NotNull String className,
                                    @NotNull String... args)
    {
        JavaTask j = new JavaTask(helper.getEnv(), false, className);
        j.addArguments(args);
        j.setClasspath(helper);
        j.execute();
    }

    public static void executeJar(@NotNull ModuleHelper helper, @NotNull String jarName,
                                  @NotNull String... args)
    {
        JavaTask j = new JavaTask(helper.getEnv(), true, jarName);
        j.addArguments(args);
        j.setClasspath(helper);
        j.execute();
    }

    public static List<File> fileList(Environment env, DependencyList dependencies)
    {
        List<File> result = new ArrayList<File>();

        for (Dependency dependency : dependencies) {
            if (dependency.isLibrary()) {
                CollectionUtils.addIfNotNull(result,
                                             dependency.asLibrary().getArtifact(env, PackageType.JAR));
            }
            else if (dependency.isModule()) {
                final ModuleHelper module = (ModuleHelper) env.getHelper(dependency.asModule());
                result.addAll(module.deepClassPath(false, true));
            }
        }

        return result;
    }

    public void setClasspath(@NotNull DependencyList dependencies)
    {
        classpath = FileUtils.makePath(fileList(env, dependencies));
    }

    public void setClasspath(@NotNull String classpath)
    {
        this.classpath = classpath;
    }

    public void setClasspath(Dependency... dependencies)
    {
        setClasspath(DependencyList.create(dependencies));
    }

    public void setClasspath(ModuleHelper helper)
    {
        classpath = FileUtils.makePath(helper.runtimePath());
    }

    public void execute()
    {
        List<String> args = new ArrayList<String>();

        // add Java executable
        args.add(FileUtils.findJavaExecutable("java", env));

        // Memory
        args.add("-Xmx" + memory + 'm');

        // Pass properties
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            args.add("-D" + entry.getKey() + '=' + entry.getValue());
        }

        args.addAll(javaArgs);

        // add classpath
        if (!classpath.isEmpty()) {
            args.add("-classpath");
            args.add(classpath);
        }

        if (executeJar) {
            args.add("-jar");
        }

        args.add(jarOrClass);
        args.addAll(getArguments());
        ExecTask task = new ExecTask(env, args);

        task.setCurrentDirectory(getCurrentDirectory());

        task.putAll(getEnvironment());
        task.execute();
        exitValue = task.getExitValue();
    }

    public int getExitValue()
    {
        return exitValue;
    }

    /**
     * Set a property to be passed to the new java process
     * @param key The property key
     * @param value The property value
     */
    public void setProperty(String key, String value)
    {
        properties.put(key, value);
    }

    /**
     * Add an argument to java executable
     * @param arg the argument to be added
     */
    public void addJavaArg(String arg)
    {
        javaArgs.add(arg);
    }

    /**
     * Set all properties to the ones specified
     * @param ps The set of properties
     */
    public void setProperties(@NotNull Map<String, String> ps)
    {
        properties.clear();
        properties.putAll(ps);
    }

    public void setMemory(int memory)
    {
        this.memory = memory;
    }
}
