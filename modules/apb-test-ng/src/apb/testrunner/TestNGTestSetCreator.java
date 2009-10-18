

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


package apb.testrunner;

import java.io.File;

import apb.utils.ClassUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.testng.TestNG;
import org.testng.annotations.Test;
//
// User: emilio
// Date: Nov 10, 2008
// Time: 3:05:51 PM

//
public class TestNGTestSetCreator
    implements TestSetCreator<Object>
{
    //~ Methods ..............................................................................................

    @Nullable public TestSet<Object> createTestSet(@NotNull Class<Object> testClass,
                                                   @NotNull String        singleTest)
        throws TestSetFailedException
    {
        return testClass.isAnnotationPresent(Test.class) ? new TestNGTestSet(testClass, singleTest) : null;
    }

    @NotNull public String getName()
    {
        return "TestNG";
    }

    @NotNull public File getTestFrameworkJar()
    {
        return ClassUtils.jarFromClass(TestNG.class);
    }
}
