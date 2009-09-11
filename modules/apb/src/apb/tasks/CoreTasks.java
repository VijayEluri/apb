

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
import java.util.Arrays;
import java.util.List;

import apb.Apb;
import apb.Environment;

import apb.metadata.UpdatePolicy;

import apb.utils.CollectionUtils;

import org.jetbrains.annotations.NotNull;

import static java.util.Arrays.asList;

public class CoreTasks
{
    //~ Methods ..............................................................................................

    /**
     * Copy a given file
     * @param from The File or Directory to copy from
     */
    @NotNull public static CopyTask.Builder copy(@NotNull String from)
    {
        return new CopyTask.Builder(from);
    }

    /**
     * Copy a given file
     * @param from The File or Directory to copy from
     */
    @NotNull public static CopyTask.Builder copy(@NotNull File from)
    {
        return new CopyTask.Builder(from);
    }

    /**
     * Copy one or more filesets to the given file
     * @param fileSets The FileSets to copy from
     * @see apb.tasks.FileSet
     */
    @NotNull public static CopyTask.Builder copy(@NotNull FileSet... fileSets)
    {
        return new CopyTask.Builder(fileSets);
    }

    /**
     * Copy a given file doing filtering (keyword expansion)
     * @param from The File or Directory to copy from
     */
    @NotNull public static FilterTask.Builder copyFiltering(@NotNull String from)
    {
        return new FilterTask.Builder(from);
    }

    /**
     * Copy a given file doing filtering (keyword expansion)
     * @param from The File or Directory to copy from
     */
    @NotNull public static FilterTask.Builder copyFiltering(@NotNull File from)
    {
        return new FilterTask.Builder(from);
    }

    /**
     * A convenience method to write a formatted string to the apb output as a log with INFO level.
     *
     * @param  format
     *         A format string as described in {@link java.util.Formatter}.
     *         Properties in the string will be expanded.
     *
     * @param  args
     *         Arguments referenced by the format specifiers in the format
     *         string.
     */
    public static void printf(String format, Object... args)
    {
        final Environment env = Apb.getEnv();
        env.logInfo(env.expand(format), args);
    }

    /**
     * Deletes a file or a directory
     * @param name The File or Directory to copy from
     *             Properties in the name will be expanded.
     */
    @NotNull public static DeleteTask delete(@NotNull String name)
    {
        final Environment env = Apb.getEnv();
        return new DeleteTask(env.fileFromBase(name));
    }

    /**
     * Deletes a file or a directory
     * @param file The File or Directory to copy from
     */
    @NotNull public static DeleteTask delete(@NotNull File file)
    {
        return new DeleteTask(file);
    }

    /**
     * Delete one or more filesets
     * @param fileSets The FileSets to delete
     * @see apb.tasks.FileSet
     */
    @NotNull public static DeleteTask delete(@NotNull FileSet... fileSets)
    {
        return new DeleteTask(Arrays.asList(fileSets));
    }

    /**
     * Downloads a remote file
     * @param url The File or Directory to copy from
     */
    @NotNull public static Download download(@NotNull String url)
    {
        return new Download(url);
    }

    /**
     * Executes a system command.
     * @param args The command to execute
     */
    @NotNull public static ExecTask exec(@NotNull String cmd, @NotNull String... args)
    {
        return exec(cmd, Arrays.asList(args));
    }

    /**
     * Executes a system command.
     * @param args The command to execute
     */
    @NotNull public static ExecTask exec(@NotNull String cmd, @NotNull List<String> args)
    {
        final Environment env = Apb.getEnv();
        return new ExecTask(env.expand(cmd), CollectionUtils.expandAll(env, args));
    }

    /**
     * Launches a java application
     * @param className The class file to launch
     * @param args Argument passed to the main function.
     */
    public static JavaTask java(@NotNull String className, @NotNull String... args)
    {
        return java(className, Arrays.asList(args));
    }

    /**
     * Launches a java application
     * @param className The class file to launch
     * @param args Argument passed to the main function.
     */
    public static JavaTask java(@NotNull String className, @NotNull List<String> args)
    {
        final Environment env = Apb.getEnv();
        return new JavaTask(false, className, CollectionUtils.expandAll(env, args));
    }

    /**
     * Launches a java application based on a jar file
     * @param jarName The jar to execute
     * @param args Argument passed to the main function.
     */
    public static JavaTask javaJar(@NotNull String jarName, @NotNull String... args)
    {
        return javaJar(jarName, Arrays.asList(args));
    }

    /**
     * Launches a java application
     * @param jarName The jar to execute
     * @param args Argument passed to the main function.
     */
    public static JavaTask javaJar(@NotNull String jarName, @NotNull List<String> args)
    {
        final Environment env = Apb.getEnv();
        return new JavaTask(true, jarName, CollectionUtils.expandAll(env, args));
    }

    /**
     * Compile the specified sources using javac
     * @param sourceDirectory The directory  to scan for sources
     */
    public static JavacTask.Builder javac(@NotNull String sourceDirectory)
    {
        return new JavacTask.Builder(sourceDirectory);
    }

    /**
     * Compile the specified sources using javac
     * @param fileSets the filesets defining the file to be compiled
     */
    public static JavacTask.Builder javac(@NotNull FileSet... fileSets)
    {
        return javac(asList(fileSets));
    }

    /**
     * Compile the specified sources using javac
     * @param fileSets the filesets defining the file to be compiled
     */
    public static JavacTask.Builder javac(@NotNull List<FileSet> fileSets)
    {
        return new JavacTask.Builder(fileSets);
    }

    /**
     * Create a jarfile
     * @param jarFileName  The jarfile to be created
     */
    public static JarTask.Builder jar(@NotNull String jarFileName)
    {
        return new JarTask.Builder(jarFileName);
    }

    /**
     * Create a jarfile
     * @param jarFile  The jarfile to be created
     */
    public static JarTask.Builder jar(@NotNull File jarFile)
    {
        return new JarTask.Builder(jarFile);
    }

    //~ Inner Classes ........................................................................................

    //

    static class Download
        extends Task
    {
        @NotNull private String target;
        @NotNull private String url;
        private UpdatePolicy    updatePolicy;

        Download(@NotNull String url)
        {
            this.url = url;
            updatePolicy = UpdatePolicy.DAILY;
        }

        @Override public void execute()
        {
            final DownloadTask task = new DownloadTask(env, url, target);
            task.setUpdatePolicy(updatePolicy);
            task.execute();
        }

        /**
         * Specify the target file or directory
         * If not specified, then the file/s will be copied to the current module output
         * @param to The File or directory to copy from
         * @throws IllegalArgumentException if trying to copy a directoy to a single file.
         */
        @NotNull public Download to(@NotNull String to)
        {
            target = to;
            return this;
        }

        /**
         * Define the update policy that specified the frecuency used to check if the source has been updated
         * <p>
         * Examples:
         * <table>
         * <tr>
         *      <td><code>UpdatePolicy.ALWAYS</code>
         *      <td> Check every time the task is executed
         * <tr>
         *      <td><code>UpdatePolicy.NEVER</code>
         *      <td> Only downloads the file if it does not exist
         * <tr>
         *      <td><code>UpdatePolicy.DAILY</code>
         *      <td> Check the source if the local file is older than a day.
         * <tr>
         *      <td><code>UpdatePolicy.every(6)</code>
         *      <td> Check the source every 6 hours
         * <tr>
         *      <td><code>UpdatePolicy.every(0.5)</code>
         *      <td> Check the source every 30 minutes
         * </table>
         * </p>
         * @param policy The update policy to be used.
         */
        public void withUpdatePolicy(@NotNull UpdatePolicy policy)
        {
            updatePolicy = policy;
        }
    }
}
