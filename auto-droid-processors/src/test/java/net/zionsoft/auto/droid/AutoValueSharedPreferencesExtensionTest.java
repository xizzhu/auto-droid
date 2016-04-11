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

    @Test
    public void allTypes() {
        final JavaFileObject source = JavaFileObjects.forSourceString("net.zionsoft.auto.droid.test.AllTypesTest", ""
                + "package net.zionsoft.auto.droid.test;\n"
                + "import android.content.SharedPreferences;\n"
                + "import com.google.auto.value.AutoValue;\n"
                + "import net.zionsoft.auto.droid.SharedPreference;\n"
                + "import java.util.Set;\n"
                + "@AutoValue\n"
                + "public abstract class AllTypesTest {\n"
                + "    @SharedPreference(key = \"a_boolean\", defaultValue = \"true\")\n"
                + "    abstract boolean aBoolean();\n"
                + "    @SharedPreference(key = \"another_boolean\", defaultValue = \"false\")\n"
                + "    abstract Boolean anotherBoolean();\n"
                + "    @SharedPreference(key = \"a_float\", defaultValue = \"39.27\")\n"
                + "    abstract float aFloat();\n"
                + "    @SharedPreference(key = \"another_float\", defaultValue = \"77.77\")\n"
                + "    abstract Float anotherFloat();\n"
                + "    @SharedPreference(key = \"an_int\", defaultValue = \"1189\")\n"
                + "    abstract int anInt();\n"
                + "    @SharedPreference(key = \"another_int\", defaultValue = \"31102\")\n"
                + "    abstract Integer anotherInt();\n"
                + "    @SharedPreference(key = \"a_long\", defaultValue = \"929260\")\n"
                + "    abstract long aLong();\n"
                + "    @SharedPreference(key = \"another_long\", defaultValue = \"231457957\")\n"
                + "    abstract Long anotherLong();\n"
                + "    @SharedPreference(key = \"a_string\", defaultValue = \"John 3:16\")\n"
                + "    abstract String aString();\n"
                + "    @SharedPreference(key = \"another_string\", defaultValue = \"For God so loved the world, that he gave his only begotten Son, that whosoever believeth in him should not perish, but have everlasting life.\")\n"
                + "    abstract String anotherString();\n"
                + "    @SharedPreference(key = \"a_string_set\", defaultValue = \"ignored\")\n"
                + "    abstract Set<String> aStringSet();\n"
                + "public static AllTypesTest create(SharedPreferences sharedPreferences) {\n"
                + "        return AutoValue_AllTypesTest.createFromSharedPreferences(sharedPreferences);\n"
                + "    }\n"
                + "}\n");

        final JavaFileObject expected = JavaFileObjects.forSourceString("net.zionsoft.auto.droid.test.AutoValue_AllTypesTest", ""
                + "package net.zionsoft.auto.droid.test;\n"
                + "\n"
                + "import android.content.SharedPreferences;\n"
                + "import java.lang.Boolean;\n"
                + "import java.lang.Float;\n"
                + "import java.lang.Integer;\n"
                + "import java.lang.Long;\n"
                + "import java.lang.String;\n"
                + "import java.util.Set;\n"
                + "\n"
                + "final class AutoValue_AllTypesTest extends $AutoValue_AllTypesTest {\n"
                + "  AutoValue_AllTypesTest(boolean aBoolean, Boolean anotherBoolean, float aFloat, Float anotherFloat, int anInt, Integer anotherInt, long aLong, Long anotherLong, String aString, String anotherString, Set<String> aStringSet) {\n"
                + "    super(aBoolean, anotherBoolean, aFloat, anotherFloat, anInt, anotherInt, aLong, anotherLong, aString, anotherString, aStringSet);\n"
                + "  }\n"
                + "\n"
                + "  static AutoValue_AllTypesTest createFromSharedPreferences(SharedPreferences sharedPreferences) {\n"
                + "      boolean aBoolean = sharedPreferences.getBoolean(\"a_boolean\", true);\n"
                + "      boolean anotherBoolean = sharedPreferences.getBoolean(\"another_boolean\", false);\n"
                + "      float aFloat = sharedPreferences.getFloat(\"a_float\", 39.27F);\n"
                + "      float anotherFloat = sharedPreferences.getFloat(\"another_float\", 77.77F);\n"
                + "      int anInt = sharedPreferences.getInt(\"an_int\", 1189);\n"
                + "      int anotherInt = sharedPreferences.getInt(\"another_int\", 31102);\n"
                + "      long aLong = sharedPreferences.getLong(\"a_long\", 929260L);\n"
                + "      long anotherLong = sharedPreferences.getLong(\"another_long\", 231457957L);\n"
                + "      String aString = sharedPreferences.getString(\"a_string\", \"John 3:16\");\n"
                + "      String anotherString = sharedPreferences.getString(\"another_string\", \"For God so loved the world, that he gave his only begotten Son, that whosoever believeth in him should not perish, but have everlasting life.\");\n"
                + "      Set<String> aStringSet = sharedPreferences.getStringSet(\"a_string_set\", null);\n"
                + "      return new AutoValue_AllTypesTest(aBoolean, anotherBoolean, aFloat, anotherFloat, anInt, anotherInt, aLong, anotherLong, aString, anotherString, aStringSet);\n"
                + "    }\n"
                + "}\n");

        Truth.assertAbout(JavaSourcesSubjectFactory.javaSources())
                .that(Collections.singletonList(source))
                .processedWith(new AutoValueProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

    @Test
    public void defaultValues() {
        final JavaFileObject source = JavaFileObjects.forSourceString("net.zionsoft.auto.droid.test.DefaultValuesTest", ""
                + "package net.zionsoft.auto.droid.test;\n"
                + "import android.content.SharedPreferences;\n"
                + "import com.google.auto.value.AutoValue;\n"
                + "import net.zionsoft.auto.droid.SharedPreference;\n"
                + "import java.util.Set;\n"
                + "@AutoValue\n"
                + "public abstract class DefaultValuesTest {\n"
                + "    @SharedPreference(key = \"a_boolean\", defaultValue = \"\")\n"
                + "    abstract boolean aBoolean();\n"
                + "    @SharedPreference(key = \"another_boolean\", defaultValue = \"\")\n"
                + "    abstract Boolean anotherBoolean();\n"
                + "    @SharedPreference(key = \"a_float\", defaultValue = \"\")\n"
                + "    abstract float aFloat();\n"
                + "    @SharedPreference(key = \"another_float\", defaultValue = \"\")\n"
                + "    abstract Float anotherFloat();\n"
                + "    @SharedPreference(key = \"an_int\", defaultValue = \"\")\n"
                + "    abstract int anInt();\n"
                + "    @SharedPreference(key = \"another_int\", defaultValue = \"\")\n"
                + "    abstract Integer anotherInt();\n"
                + "    @SharedPreference(key = \"a_long\", defaultValue = \"\")\n"
                + "    abstract long aLong();\n"
                + "    @SharedPreference(key = \"another_long\", defaultValue = \"\")\n"
                + "    abstract Long anotherLong();\n"
                + "    @SharedPreference(key = \"a_string\", defaultValue = \"\")\n"
                + "    abstract String aString();\n"
                + "    @SharedPreference(key = \"a_string_set\", defaultValue = \"\")\n"
                + "    abstract Set<String> aStringSet();\n"
                + "public static DefaultValuesTest create(SharedPreferences sharedPreferences) {\n"
                + "        return AutoValue_DefaultValuesTest.createFromSharedPreferences(sharedPreferences);\n"
                + "    }\n"
                + "}\n");

        final JavaFileObject expected = JavaFileObjects.forSourceString("net.zionsoft.auto.droid.test.AutoValue_DefaultValuesTest", ""
                + "package net.zionsoft.auto.droid.test;\n"
                + "\n"
                + "import android.content.SharedPreferences;\n"
                + "import java.lang.Boolean;\n"
                + "import java.lang.Float;\n"
                + "import java.lang.Integer;\n"
                + "import java.lang.Long;\n"
                + "import java.lang.String;\n"
                + "import java.util.Set;\n"
                + "\n"
                + "final class AutoValue_DefaultValuesTest extends $AutoValue_DefaultValuesTest {\n"
                + "  AutoValue_DefaultValuesTest(boolean aBoolean, Boolean anotherBoolean, float aFloat, Float anotherFloat, int anInt, Integer anotherInt, long aLong, Long anotherLong, String aString, Set<String> aStringSet) {\n"
                + "    super(aBoolean, anotherBoolean, aFloat, anotherFloat, anInt, anotherInt, aLong, anotherLong, aString, aStringSet);\n"
                + "  }\n"
                + "\n"
                + "  static AutoValue_DefaultValuesTest createFromSharedPreferences(SharedPreferences sharedPreferences) {\n"
                + "      boolean aBoolean = sharedPreferences.getBoolean(\"a_boolean\", false);\n"
                + "      boolean anotherBoolean = sharedPreferences.getBoolean(\"another_boolean\", false);\n"
                + "      float aFloat = sharedPreferences.getFloat(\"a_float\", 0.0F);\n"
                + "      float anotherFloat = sharedPreferences.getFloat(\"another_float\", 0.0F);\n"
                + "      int anInt = sharedPreferences.getInt(\"an_int\", 0);\n"
                + "      int anotherInt = sharedPreferences.getInt(\"another_int\", 0);\n"
                + "      long aLong = sharedPreferences.getLong(\"a_long\", 0L);\n"
                + "      long anotherLong = sharedPreferences.getLong(\"another_long\", 0L);\n"
                + "      String aString = sharedPreferences.getString(\"a_string\", \"\");\n"
                + "      Set<String> aStringSet = sharedPreferences.getStringSet(\"a_string_set\", null);\n"
                + "      return new AutoValue_DefaultValuesTest(aBoolean, anotherBoolean, aFloat, anotherFloat, anInt, anotherInt, aLong, anotherLong, aString, aStringSet);\n"
                + "    }\n"
                + "}\n");

        Truth.assertAbout(JavaSourcesSubjectFactory.javaSources())
                .that(Collections.singletonList(source))
                .processedWith(new AutoValueProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

    @Test
    public void notAnnotated() {
        final JavaFileObject source = JavaFileObjects.forSourceString("net.zionsoft.auto.droid.test.NotAnnotatedTest", ""
                + "package net.zionsoft.auto.droid.test;\n"
                + "import android.content.SharedPreferences;\n"
                + "import com.google.auto.value.AutoValue;\n"
                + "import net.zionsoft.auto.droid.SharedPreference;\n"
                + "import java.util.Set;\n"
                + "@AutoValue\n"
                + "public abstract class NotAnnotatedTest {\n"
                + "    @SharedPreference(key = \"a_boolean\", defaultValue = \"\")\n"
                + "    abstract boolean aBoolean();\n"
                + "    @SharedPreference(key = \"another_boolean\", defaultValue = \"\")\n"
                + "    abstract Boolean anotherBoolean();\n"
                + "    @SharedPreference(key = \"a_float\", defaultValue = \"\")\n"
                + "    abstract float aFloat();\n"
                + "    @SharedPreference(key = \"another_float\", defaultValue = \"\")\n"
                + "    abstract Float anotherFloat();\n"
                + "    @SharedPreference(key = \"an_int\", defaultValue = \"\")\n"
                + "    abstract int anInt();\n"
                + "    @SharedPreference(key = \"another_int\", defaultValue = \"\")\n"
                + "    abstract Integer anotherInt();\n"
                + "    @SharedPreference(key = \"a_long\", defaultValue = \"\")\n"
                + "    abstract long aLong();\n"
                + "    @SharedPreference(key = \"another_long\", defaultValue = \"\")\n"
                + "    abstract Long anotherLong();\n"
                + "    @SharedPreference(key = \"a_string\", defaultValue = \"\")\n"
                + "    abstract String aString();\n"
                + "    @SharedPreference(key = \"a_string_set\", defaultValue = \"\")\n"
                + "    abstract Set<String> aStringSet();\n"
                + "    abstract boolean aNotAnnotatedBoolean();\n"
                + "    abstract Boolean anotherNotAnnotatedBoolean();\n"
                + "    abstract float aNotAnnotatedFloat();\n"
                + "    abstract Float anotherNotAnnotatedFloat();\n"
                + "    abstract int aNotAnnotatedInt();\n"
                + "    abstract Integer anotherNotAnnotatedInt();\n"
                + "    abstract long aNotAnnotatedLong();\n"
                + "    abstract Long anotherNotAnnotatedLong();\n"
                + "    abstract String aNotAnnotatedString();\n"
                + "    abstract Set<String> aNotAnnotatedStringSet();\n"
                + "public static NotAnnotatedTest create(SharedPreferences sharedPreferences) {\n"
                + "        return AutoValue_NotAnnotatedTest.createFromSharedPreferences(sharedPreferences);\n"
                + "    }\n"
                + "}\n");

        final JavaFileObject expected = JavaFileObjects.forSourceString("net.zionsoft.auto.droid.test.AutoValue_NotAnnotatedTest", ""
                + "package net.zionsoft.auto.droid.test;\n"
                + "\n"
                + "import android.content.SharedPreferences;\n"
                + "import java.lang.Boolean;\n"
                + "import java.lang.Float;\n"
                + "import java.lang.Integer;\n"
                + "import java.lang.Long;\n"
                + "import java.lang.String;\n"
                + "import java.util.Set;\n"
                + "\n"
                + "final class AutoValue_NotAnnotatedTest extends $AutoValue_NotAnnotatedTest {\n"
                + "  AutoValue_NotAnnotatedTest(boolean aBoolean, Boolean anotherBoolean, float aFloat, Float anotherFloat, int anInt, Integer anotherInt, long aLong, Long anotherLong, String aString, Set<String> aStringSet, boolean aNotAnnotatedBoolean, Boolean anotherNotAnnotatedBoolean, float aNotAnnotatedFloat, Float anotherNotAnnotatedFloat, int aNotAnnotatedInt, Integer anotherNotAnnotatedInt, long aNotAnnotatedLong, Long anotherNotAnnotatedLong, String aNotAnnotatedString, Set<String> aNotAnnotatedStringSet) {\n"
                + "    super(aBoolean, anotherBoolean, aFloat, anotherFloat, anInt, anotherInt, aLong, anotherLong, aString, aStringSet, aNotAnnotatedBoolean, anotherNotAnnotatedBoolean, aNotAnnotatedFloat, anotherNotAnnotatedFloat, aNotAnnotatedInt, anotherNotAnnotatedInt, aNotAnnotatedLong, anotherNotAnnotatedLong, aNotAnnotatedString, aNotAnnotatedStringSet);\n"
                + "  }\n"
                + "\n"
                + "  static AutoValue_NotAnnotatedTest createFromSharedPreferences(SharedPreferences sharedPreferences) {\n"
                + "      boolean aBoolean = sharedPreferences.getBoolean(\"a_boolean\", false);\n"
                + "      boolean anotherBoolean = sharedPreferences.getBoolean(\"another_boolean\", false);\n"
                + "      float aFloat = sharedPreferences.getFloat(\"a_float\", 0.0F);\n"
                + "      float anotherFloat = sharedPreferences.getFloat(\"another_float\", 0.0F);\n"
                + "      int anInt = sharedPreferences.getInt(\"an_int\", 0);\n"
                + "      int anotherInt = sharedPreferences.getInt(\"another_int\", 0);\n"
                + "      long aLong = sharedPreferences.getLong(\"a_long\", 0L);\n"
                + "      long anotherLong = sharedPreferences.getLong(\"another_long\", 0L);\n"
                + "      String aString = sharedPreferences.getString(\"a_string\", \"\");\n"
                + "      Set<String> aStringSet = sharedPreferences.getStringSet(\"a_string_set\", null);\n"
                + "      boolean aNotAnnotatedBoolean = false;\n"
                + "      boolean anotherNotAnnotatedBoolean = false;\n"
                + "      float aNotAnnotatedFloat = 0.0F;\n"
                + "      float anotherNotAnnotatedFloat = 0.0F;\n"
                + "      int aNotAnnotatedInt = 0;\n"
                + "      int anotherNotAnnotatedInt = 0;\n"
                + "      long aNotAnnotatedLong = 0L;\n"
                + "      long anotherNotAnnotatedLong = 0L;\n"
                + "      String aNotAnnotatedString = null;\n"
                + "      Set<String> aNotAnnotatedStringSet = null;\n"
                + "      return new AutoValue_NotAnnotatedTest(aBoolean, anotherBoolean, aFloat, anotherFloat, anInt, anotherInt, aLong, anotherLong, aString, aStringSet, aNotAnnotatedBoolean, anotherNotAnnotatedBoolean, aNotAnnotatedFloat, anotherNotAnnotatedFloat, aNotAnnotatedInt, anotherNotAnnotatedInt, aNotAnnotatedLong, anotherNotAnnotatedLong, aNotAnnotatedString, aNotAnnotatedStringSet);\n"
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
