

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

import org.jetbrains.annotations.NotNull;
//
// User: emilio
// Date: Sep 4, 2009
// Time: 4:49:37 PM

public class MkdirTask
    extends Task
{
    //~ Instance fields ......................................................................................

    @NotNull private File file;

    //~ Constructors .........................................................................................

    MkdirTask(@NotNull File file)
    {
        this.file = file;
    }

    //~ Methods ..............................................................................................

    @Override public void execute()
    {
        if (!file.exists()) {
            if (!file.mkdirs()) {
                env.logWarning("Unable to create directory: " + file.getAbsolutePath());
            }
        }
    }
}