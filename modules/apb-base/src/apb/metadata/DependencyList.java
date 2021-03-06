

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

import org.jetbrains.annotations.NotNull;
//
// User: emilio
// Date: Sep 3, 2008
// Time: 12:29:57 PM

//
public class DependencyList
    extends ArrayList<Dependency>
    implements Dependencies
{
    //~ Methods ..............................................................................................

    public static DependencyList create(Dependencies... deps)
    {
        DependencyList result = new DependencyList();
        result.addAll(deps);
        return result;
    }

    @Override public boolean add(Dependency o)
    {
        return super.add(NameRegistry.intern(o));
    }

    public void addAll(Dependencies... dependencies)
    {
        for (Dependencies dep : dependencies) {
            if (dep == null) {
                throw new NullDependencyException();
            }

            add(dep);
        }
    }

    public void add(@NotNull Dependencies dep)
    {
        if (dep instanceof Dependency) {
            add((Dependency) dep);
        }
        else {
            for (Dependency d : (DependencyList) dep) {
                add(d);
            }
        }
    }

    //~ Static fields/initializers ...........................................................................

    private static final long serialVersionUID = -8296818109856223135L;

    //~ Inner Classes ........................................................................................

    public static class NullDependencyException
        extends NullPointerException
    {
        public NullDependencyException()
        {
            super("Trying to add a Null dependency. Can be caused by a circular dependency.");
        }

        private static final long serialVersionUID = 1260914719294282236L;
    }
}
