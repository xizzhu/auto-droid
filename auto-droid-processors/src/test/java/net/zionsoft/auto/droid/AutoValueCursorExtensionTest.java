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

public class AutoValueCursorExtensionTest {
    @Test
    public void smoke() {
        final JavaFileObject source = JavaFileObjects.forSourceString("net.zionsoft.auto.droid.test.SmokeTest", ""
                + "package net.zionsoft.auto.droid.test;\n"
                + "import android.database.Cursor;\n"
                + "import com.google.auto.value.AutoValue;\n"
                + "import net.zionsoft.auto.droid.ColumnName;\n"
                + "@AutoValue\n"
                + "public abstract class SmokeTest {\n"
                + "    @ColumnName(\"an_int\")\n"
                + "    abstract int anInt();\n"
                + "public static SmokeTest create(Cursor cursor) {\n"
                + "        return AutoValue_SmokeTest.createFromCursor(cursor);\n"
                + "    }\n"
                + "}\n");

        final JavaFileObject expected = JavaFileObjects.forSourceString("net.zionsoft.auto.droid.test.AutoValue_SmokeTest", ""
                + "package net.zionsoft.auto.droid.test;\n"
                + "\n"
                + "import android.database.Cursor;\n"
                + "\n"
                + "final class AutoValue_SmokeTest extends $AutoValue_SmokeTest {\n"
                + "  AutoValue_SmokeTest(int anInt) {\n"
                + "    super(anInt);\n"
                + "  }\n"
                + "\n"
                + "  static AutoValue_SmokeTest createFromCursor(Cursor cursor) {\n"
                + "      int anInt = cursor.getInt(cursor.getColumnIndexOrThrow(\"an_int\"));\n"
                + "      return new AutoValue_SmokeTest(anInt);\n"
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
