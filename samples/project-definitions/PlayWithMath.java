

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



import apb.metadata.Module;

public class PlayWithMath
    extends Module
{
    //~ Instance initializers ................................................................................

    {
        dependencies(new Math(), localLibrary("../lib/emma.jar"));

        group = "samples";
        version = "1.0";
        pkg.mainClass = "Play";
        pkg.addClassPath = true;
       compiler.validateDependencies = false;
    }
}