
// ...........................................................................................................
// (C) Copyright  1996/2008 Fuego Inc.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Fuego Inc.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// Last changed on 2008-12-10 20:02:43 (-0200), by: emilio. $Revision$
// ...........................................................................................................

package apb.metadata;

import java.util.ArrayList;
import java.util.List;

public class PackageInfo
{
    //~ Instance fields ......................................................................................

    /**
     * Whether to generate a Class-Path manifest entry.
     */
    public boolean addClassPath;

    /**
     * The directory for the package.
     */
    @BuildProperty public String dir = "lib";

    /**
     * Whether to generate a jar with the sources or not.
     */
    public boolean generateSourcesJar;

    /**
     * The main class of the package
     */
    @BuildProperty public String mainClass = "";

    /**
     * The name of the package file without the extension
     */
    @BuildProperty public String name = "${group}-${module}-${version}";

    /**
     * The packaging type for the module
     */
    public PackageType type = PackageType.JAR;

    /**
     * Package the following dependencies into the jar
     */
    private final List<Dependency> includeDependencies = new ArrayList<Dependency>();

    //~ Methods ..............................................................................................

    /**
     * The package file with extension
     * @return The package file with extension
     */
    public String getName()
    {
        return name + type.getExt();
    }

    public List<Dependency> includeDependencies()
    {
        return includeDependencies;
    }

    /**
     * Method used to set dependencies to be added to the package
     * @param dependencyList The list of dependencies to be added to the package
    public final void includeDependencies(Dependency... dependencyList)
    {
        includeDependencies.addAll(asList(dependencyList));
    }
    */
}