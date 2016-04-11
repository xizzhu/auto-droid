/*
 * Copyright (C) 2016 Xizhi Zhu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.zionsoft.auto.droid;

import com.google.auto.value.processor.AutoValueProcessor;
import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourcesSubjectFactory;

import org.junit.Test;

import java.util.Collections;

import javax.tools.JavaFileObject;

public class AutoValueSharedPreferencesExtensionTest {
    @Test
    public void smoke() {
        final JavaFileObject source = JavaFileObjects.forSourceString("net.zionsoft.auto.droid.test.SmokeTest", ""
                + "package net.zionsoft.auto.droid.test;\n"
                + "import android.content.SharedPreferences;\n"
                + "import com.google.auto.value.AutoValue;\n"
                + "import net.zionsoft.auto.droid.SharedPreference;\n"
                + "@AutoValue\n"
                + "public abstract class SmokeTest {\n"
                + "    @SharedPreference(key = \"a_boolean\", defaultValue = \"true\")\n"
                + "    abstract boolean aBoolean();\n"
                + "public static SmokeTest create(SharedPreferences sharedPreferences) {\n"
                + "        return AutoValue_SmokeTest.createFromSharedPreferences(sharedPreferences);\n"
                + "    }\n"
                + "}\n");

        final JavaFileObject expected = JavaFileObjects.forSourceString("net.zionsoft.auto.droid.test.AutoValue_SmokeTest", ""
                + "package net.zionsoft.auto.droid.test;\n"
                + "\n"
                + "import android.content.SharedPreferences;\n"
                + "\n"
                + "final class AutoValue_SmokeTest extends $AutoValue_SmokeTest {\n"
                + "  AutoValue_SmokeTest(boolean aBoolean) {\n"
                + "    super(aBoolean);\n"
                + "  }\n"
                + "\n"
                + "  static AutoValue_SmokeTest createFromSharedPreferences(SharedPreferences sharedPreferences) {\n"
                + "      boolean aBoolean = sharedPreferences.getBoolean(\"a_boolean\", true);\n"
                + "      return new AutoValue_SmokeTest(aBoolean);\n"
                + "    }\n"
                + "}\n");

        Truth.assertAbout(JavaSourcesSubjectFactory.javaSources())
                .that(Collections.singletonList(source))
                .processedWith(new AutoValueProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }
}
