

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

import org.jetbrains.annotations.NotNull;

//
// User: emilio
// Date: Sep 8, 2008
// Time: 4:30:40 PM

//
public class BuildException
    extends RuntimeException
{
    //~ Constructors .........................................................................................

    public BuildException(@NotNull String msg)
    {
        super(msg);
    }

    public BuildException(Throwable cause)
    {
        super(cause);
    }

    public BuildException(String msg, Throwable cause)
    {
        super(msg, cause);
    }

    /**
     * If you use this constructor in a subclass yu will need to redefine the getMessage() method
     */
    protected BuildException()
    {
        super();
    }

    //~ Static fields/initializers ...........................................................................

    private static final long serialVersionUID = -3287928686099873402L;
}
