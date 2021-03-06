

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


package apb.metadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Javadoc configuration information.
 */
public class JavadocInfo
{
    //~ Instance fields ......................................................................................

    /**
     * Includes the @author text in the generated docs.
     */
    public boolean author = false;

    /**
     * Generates documentation for deprecated APIs.
     */
    public boolean deprecated = true;

    /**
     * Generates the file containing the list of deprecated APIs (deprecated-list.html)
     * and the link in the navigation bar to that page.
     */
    public boolean generateDeprecatedList = true;

    /**
     * Includes the HELP link in the navigation bars at the top and bottom of each page of output.
     */
    public boolean help = true;

    /**
     * Includes the index in the generated docs.
     */
    public boolean index = true;

    /**
     * Creates an HTML version of each source file (with line numbers)
     * and adds links to them from the standard HTML documentation.
     */
    public boolean linkSource = false;

    /**
     * Includes in the generated docs the "Since" sections associated with the @since tags.
     */
    public boolean since = true;

    /**
     * Generates javadoc for the generated sources.
     */
    public boolean includeGeneratedSource = false;
    
    /**
     * Splits the index file into multiple files, alphabetically, one file per letter,
     * plus a file for any index entries that start with non-alphabetical characters.
     */
    public boolean splitIndexPerLetter = false;

    /**
     * Includes the class/interface hierarchy pages in the generated docs.
     */
    public boolean tree = true;

    /**
     * Includes one "Use" page for each documented class and package.
     * The page describes what packages, classes, methods, constructors
     * and fields use any API of the given class or package.
     */
    public boolean use = false;

    /**
     * Use the ExcludeDoclet (Embedded in APB) that recognize the @exclude tag
     */
    public boolean useExcludeDoclet = false;

    /**
     * Includes the @version text in the generated docs.
     */
    public boolean version = false;

    /**
     * Whether to generate documentatin for dependencies or not
     */
    public IncludeDependencies includeDependencies = IncludeDependencies.NONE;

    /**
     * The max memory in megabytes used by the javadoc command
     */
    public int memory = 256;

    /**
     * Specifies the locale that javadoc uses when generating documentation
     */
    public Locale locale = null;

    /**
     * Specifies the text to be placed at the bottom of each output file.
     * The text will be placed at the bottom of the page, below the lower navigation bar.
     */
    public String bottom = "";

    /**
     * Specifies the class file that starts the doclet used in generating the documentation. Use the fully-qualified name.
     */
    public String doclet = "";

    /**
     * Specifies the encoding of the generated HTML files
     */
    public String encoding = DEFAULT_ENCODING;

    /**
     * Specifies the footer text to be placed at the bottom of each output file.
     * The footer will be placed to the right of the lower navigation bar
     */
    public String footer = "$description<br>$version";

    /**
     * Specifies the header text to be placed at the top of each output file.
     * The header will be placed to the right of the upper navigation bar
     */
    public String header = "$description<br>$version";

    /**
     * The directory to generate the javadoc output
     */
    public String output = "$output-base/javadoc";

    /**
     * Specifies that javadoc should retrieve the text for the overview documentation from the "source" file specified
     */
    public String overview = "";

    /**
     * Specifies the title to be placed near the top of the overview summary file.
     * The title will be placed as a centered, level-one heading directly beneath the upper navigation bar.
     */
    public String title = "$description $version<br>API Specification";

    /**
     * Specifies the title to be placed in the HTML <title> tag.
     * This appears in the window title and in any browser bookmarks (favorite places)
     * that someone creates for this page.
     */
    public String windowTitle = "";

    /**
     * Document classes with the given visibility
     */
    public Visibility visibility = Visibility.PROTECTED;

    /**
     * A set of additional options to be passed to the javadoc command
     */
    private List<String> additionalOptions = new ArrayList<String>();

    /**
     * The list of packages to exclude.
     */
    private final List<String> excludes = new ArrayList<String>();
    private final List<Group>  groups = new ArrayList<Group>();

    /**
     * The list of packages to include (default is everything).
     */
    private final List<String> includes = new ArrayList<String>();

    /**
     * Creates links to existing javadoc-generated documentation of external referenced classes.
     */
    private final List<String> links = new ArrayList<String>();

    /**
     * Creates links to existing javadoc-generated documentation of external referenced classes.
     */
    private final List<OfflineLink> offlineLinks = new ArrayList<OfflineLink>();

    //~ Methods ..............................................................................................

    /**
     * List of packages to include in the documentation
     */
    public List<String> includes()
    {
        return includes;
    }

    public List<String> excludes()
    {
        return excludes;
    }

    /**
     * Method used to set the list of packages to be included (recursively).
     * If nothing is indicated, all packages will be included.
     * @param packages The list of patterns to be included
     */
    public void includes(String... packages)
    {
        includes.addAll(Arrays.asList(packages));
    }

    /**
     * Method used to set the list of packages to be excluded.
     * @param packages The list of packages to be excluded
     */
    public void excludes(String... packages)
    {
        excludes.addAll(Arrays.asList(packages));
    }

    /**
     * Method used to set the list of urls to be used to
     * create links to existing javadoc-generated documentation of external referenced classes.
     * @param urls The list of links
     */
    public void links(String... urls)
    {
        links.addAll(Arrays.asList(urls));
    }

    /**
     * Method used to set the list of offline links pairs to be used to
     * create links to existing javadoc-generated documentation of external referenced classes.
     * @param lnks The list of links
     */
    public void offlineLinks(OfflineLink... lnks)
    {
        offlineLinks.addAll(Arrays.asList(lnks));
    }

    public List<OfflineLink> offlineLinks()
    {
        return offlineLinks;
    }

    /**
    * Separates packages on the overview page into whatever groups you specify.
    * one group per table.
    */
    public void groups(Group... groupSpecification)
    {
        groups.addAll(Arrays.asList(groupSpecification));
    }

    public List<Group> groups()
    {
        return groups;
    }

    public List<String> links()
    {
        return links;
    }

    public List<String> additionalOptions()
    {
        return additionalOptions;
    }

    /**
    * Method used to set the additional options to be passed to the javadoc command
    * @param options The list of options to include
    */
    public void additionalOptions(String... options)
    {
        additionalOptions.addAll(Arrays.asList(options));
    }

    //~ Static fields/initializers ...........................................................................

    public static final String DEFAULT_ENCODING = apb.Constants.UTF8;

    //~ Enums ................................................................................................

    public enum Visibility
    {
        PUBLIC,
        PROTECTED,
        PACKAGE,
        PRIVATE;
    }

    //~ Inner Classes ........................................................................................

    public static class Group
    {
        public String   title;
        public String[] packages;

        public Group(String title, String... packages)
        {
            this.title = title;
            this.packages = packages;
        }
    }

    public static class OfflineLink
    {
        public String location;
        public String url;

        public OfflineLink(String url, String location)
        {
            this.location = location;
            this.url = url;
        }
    }
}
