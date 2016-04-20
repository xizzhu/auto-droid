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

import java.util.Arrays;
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
                + "    int anInt = cursor.getInt(cursor.getColumnIndexOrThrow(\"an_int\"));\n"
                + "    return new AutoValue_SmokeTest(anInt);\n"
                + "  }\n"
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
                + "import android.content.ContentValues;\n"
                + "import android.database.Cursor;\n"
                + "import com.google.auto.value.AutoValue;\n"
                + "import net.zionsoft.auto.droid.ColumnName;\n"
                + "@AutoValue\n"
                + "public abstract class AllTypesTest {\n"
                + "    @ColumnName(\"a_blob\")\n"
                + "    abstract byte[] aBlob();\n"
                + "    @ColumnName(\"a_double\")\n"
                + "    abstract double aDouble();\n"
                + "    @ColumnName(\"another_double\")\n"
                + "    abstract Double anotherDouble();\n"
                + "    @ColumnName(\"a_float\")\n"
                + "    abstract float aFloat();\n"
                + "    @ColumnName(\"another_float\")\n"
                + "    abstract Float anotherFloat();\n"
                + "    @ColumnName(\"an_int\")\n"
                + "    abstract int anInt();\n"
                + "    @ColumnName(\"another_int\")\n"
                + "    abstract Integer anotherInt();\n"
                + "    @ColumnName(\"a_long\")\n"
                + "    abstract long aLong();\n"
                + "    @ColumnName(\"another_long\")\n"
                + "    abstract Long anotherLong();\n"
                + "    @ColumnName(\"a_short\")\n"
                + "    abstract short aShort();\n"
                + "    @ColumnName(\"another_short\")\n"
                + "    abstract Short anotherShort();\n"
                + "    @ColumnName(\"a_string\")\n"
                + "    abstract String aString();\n"
                + "    public abstract ContentValues toContentValues();\n"
                + "public static AllTypesTest create(Cursor cursor) {\n"
                + "        return AutoValue_AllTypesTest.createFromCursor(cursor);\n"
                + "    }\n"
                + "}\n");

        final JavaFileObject expected = JavaFileObjects.forSourceString("net.zionsoft.auto.droid.test.AutoValue_AllTypesTest", ""
                + "package net.zionsoft.auto.droid.test;\n"
                + "\n"
                + "import android.content.ContentValues;\n"
                + "import android.database.Cursor;\n"
                + "import java.lang.Double;\n"
                + "import java.lang.Float;\n"
                + "import java.lang.Integer;\n"
                + "import java.lang.Long;\n"
                + "import java.lang.Override;\n"
                + "import java.lang.Short;\n"
                + "import java.lang.String;\n"
                + "\n"
                + "final class AutoValue_AllTypesTest extends $AutoValue_AllTypesTest {\n"
                + "  AutoValue_AllTypesTest(byte[] aBlob, double aDouble, Double anotherDouble, float aFloat, Float anotherFloat, int anInt, Integer anotherInt, long aLong, Long anotherLong, short aShort, Short anotherShort, String aString) {\n"
                + "    super(aBlob, aDouble, anotherDouble, aFloat, anotherFloat, anInt, anotherInt, aLong, anotherLong, aShort, anotherShort, aString);\n"
                + "  }\n"
                + "\n"
                + "  static AutoValue_AllTypesTest createFromCursor(Cursor cursor) {\n"
                + "      byte[] aBlob = cursor.getBlob(cursor.getColumnIndexOrThrow(\"a_blob\"));\n"
                + "      double aDouble = cursor.getDouble(cursor.getColumnIndexOrThrow(\"a_double\"));\n"
                + "      double anotherDouble = cursor.getDouble(cursor.getColumnIndexOrThrow(\"another_double\"));\n"
                + "      float aFloat = cursor.getFloat(cursor.getColumnIndexOrThrow(\"a_float\"));\n"
                + "      float anotherFloat = cursor.getFloat(cursor.getColumnIndexOrThrow(\"another_float\"));\n"
                + "      int anInt = cursor.getInt(cursor.getColumnIndexOrThrow(\"an_int\"));\n"
                + "      int anotherInt = cursor.getInt(cursor.getColumnIndexOrThrow(\"another_int\"));\n"
                + "      long aLong = cursor.getLong(cursor.getColumnIndexOrThrow(\"a_long\"));\n"
                + "      long anotherLong = cursor.getLong(cursor.getColumnIndexOrThrow(\"another_long\"));\n"
                + "      short aShort = cursor.getShort(cursor.getColumnIndexOrThrow(\"a_short\"));\n"
                + "      short anotherShort = cursor.getShort(cursor.getColumnIndexOrThrow(\"another_short\"));\n"
                + "      String aString = cursor.getString(cursor.getColumnIndexOrThrow(\"a_string\"));\n"
                + "      return new AutoValue_AllTypesTest(aBlob, aDouble, anotherDouble, aFloat, anotherFloat, anInt, anotherInt, aLong, anotherLong, aShort, anotherShort, aString);\n"
                + "    }\n"
                + "\n"
                + "  @Override\n"
                + "  public ContentValues toContentValues() {\n"
                + "    ContentValues contentValues = new ContentValues(12);\n"
                + "    contentValues.put(\"a_blob\", aBlob());\n"
                + "    contentValues.put(\"a_double\", aDouble());\n"
                + "    contentValues.put(\"another_double\", anotherDouble());\n"
                + "    contentValues.put(\"a_float\", aFloat());\n"
                + "    contentValues.put(\"another_float\", anotherFloat());\n"
                + "    contentValues.put(\"an_int\", anInt());\n"
                + "    contentValues.put(\"another_int\", anotherInt());\n"
                + "    contentValues.put(\"a_long\", aLong());\n"
                + "    contentValues.put(\"another_long\", anotherLong());\n"
                + "    contentValues.put(\"a_short\", aShort());\n"
                + "    contentValues.put(\"another_short\", anotherShort());\n"
                + "    contentValues.put(\"a_string\", aString());\n"
                + "    return contentValues;\n"
                + "  }\n"
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
                + "import android.content.ContentValues;\n"
                + "import android.database.Cursor;\n"
                + "import com.google.auto.value.AutoValue;\n"
                + "import net.zionsoft.auto.droid.ColumnName;\n"
                + "@AutoValue\n"
                + "public abstract class NotAnnotatedTest {\n"
                + "    @ColumnName(\"a_blob\")\n"
                + "    abstract byte[] aBlob();\n"
                + "    @ColumnName(\"a_double\")\n"
                + "    abstract double aDouble();\n"
                + "    @ColumnName(\"another_double\")\n"
                + "    abstract Double anotherDouble();\n"
                + "    @ColumnName(\"a_float\")\n"
                + "    abstract float aFloat();\n"
                + "    @ColumnName(\"another_float\")\n"
                + "    abstract Float anotherFloat();\n"
                + "    @ColumnName(\"an_int\")\n"
                + "    abstract int anInt();\n"
                + "    @ColumnName(\"another_int\")\n"
                + "    abstract Integer anotherInt();\n"
                + "    @ColumnName(\"a_long\")\n"
                + "    abstract long aLong();\n"
                + "    @ColumnName(\"another_long\")\n"
                + "    abstract Long anotherLong();\n"
                + "    @ColumnName(\"a_short\")\n"
                + "    abstract short aShort();\n"
                + "    @ColumnName(\"another_short\")\n"
                + "    abstract Short anotherShort();\n"
                + "    @ColumnName(\"a_string\")\n"
                + "    abstract String aString();\n"
                + "    abstract byte[] aNotAnnotatedBlob();\n"
                + "    abstract double aNotAnnotatedDouble();\n"
                + "    abstract Double anotherNotAnnotatedDouble();\n"
                + "    abstract float aNotAnnotatedFloat();\n"
                + "    abstract Float anotherNotAnnotatedFloat();\n"
                + "    abstract int aNotAnnotatedInt();\n"
                + "    abstract Integer anotherNotAnnotatedInt();\n"
                + "    abstract long aNotAnnotatedLong();\n"
                + "    abstract Long anotherNotAnnotatedLong();\n"
                + "    abstract short aNotAnnotatedShort();\n"
                + "    abstract Short anotherNotAnnotatedShort();\n"
                + "    abstract String aNotAnnotatedString();\n"
                + "    public abstract ContentValues toContentValuesWithParam(ContentValues values);\n"
                + "public static NotAnnotatedTest create(Cursor cursor) {\n"
                + "        return AutoValue_NotAnnotatedTest.createFromCursor(cursor);\n"
                + "    }\n"
                + "}\n");

        final JavaFileObject expected = JavaFileObjects.forSourceString("net.zionsoft.auto.droid.test.AutoValue_NotAnnotatedTest", ""
                + "package net.zionsoft.auto.droid.test;\n"
                + "\n"
                + "import android.content.ContentValues;\n"
                + "import android.database.Cursor;\n"
                + "import java.lang.Double;\n"
                + "import java.lang.Float;\n"
                + "import java.lang.Integer;\n"
                + "import java.lang.Long;\n"
                + "import java.lang.Override;\n"
                + "import java.lang.Short;\n"
                + "import java.lang.String;\n"
                + "\n"
                + "final class AutoValue_NotAnnotatedTest extends $AutoValue_NotAnnotatedTest {\n"
                + "  AutoValue_NotAnnotatedTest(byte[] aBlob, double aDouble, Double anotherDouble, float aFloat, Float anotherFloat, int anInt, Integer anotherInt, long aLong, Long anotherLong, short aShort, Short anotherShort, String aString, byte[] aNotAnnotatedBlob, double aNotAnnotatedDouble, Double anotherNotAnnotatedDouble, float aNotAnnotatedFloat, Float anotherNotAnnotatedFloat, int aNotAnnotatedInt, Integer anotherNotAnnotatedInt, long aNotAnnotatedLong, Long anotherNotAnnotatedLong, short aNotAnnotatedShort, Short anotherNotAnnotatedShort, String aNotAnnotatedString) {\n"
                + "    super(aBlob, aDouble, anotherDouble, aFloat, anotherFloat, anInt, anotherInt, aLong, anotherLong, aShort, anotherShort, aString, aNotAnnotatedBlob, aNotAnnotatedDouble, anotherNotAnnotatedDouble, aNotAnnotatedFloat, anotherNotAnnotatedFloat, aNotAnnotatedInt, anotherNotAnnotatedInt, aNotAnnotatedLong, anotherNotAnnotatedLong, aNotAnnotatedShort, anotherNotAnnotatedShort, aNotAnnotatedString);\n"
                + "  }\n"
                + "\n"
                + "  static AutoValue_NotAnnotatedTest createFromCursor(Cursor cursor) {\n"
                + "      byte[] aBlob = cursor.getBlob(cursor.getColumnIndexOrThrow(\"a_blob\"));\n"
                + "      double aDouble = cursor.getDouble(cursor.getColumnIndexOrThrow(\"a_double\"));\n"
                + "      double anotherDouble = cursor.getDouble(cursor.getColumnIndexOrThrow(\"another_double\"));\n"
                + "      float aFloat = cursor.getFloat(cursor.getColumnIndexOrThrow(\"a_float\"));\n"
                + "      float anotherFloat = cursor.getFloat(cursor.getColumnIndexOrThrow(\"another_float\"));\n"
                + "      int anInt = cursor.getInt(cursor.getColumnIndexOrThrow(\"an_int\"));\n"
                + "      int anotherInt = cursor.getInt(cursor.getColumnIndexOrThrow(\"another_int\"));\n"
                + "      long aLong = cursor.getLong(cursor.getColumnIndexOrThrow(\"a_long\"));\n"
                + "      long anotherLong = cursor.getLong(cursor.getColumnIndexOrThrow(\"another_long\"));\n"
                + "      short aShort = cursor.getShort(cursor.getColumnIndexOrThrow(\"a_short\"));\n"
                + "      short anotherShort = cursor.getShort(cursor.getColumnIndexOrThrow(\"another_short\"));\n"
                + "      String aString = cursor.getString(cursor.getColumnIndexOrThrow(\"a_string\"));\n"
                + "      byte[] aNotAnnotatedBlob = null;\n"
                + "      double aNotAnnotatedDouble = 0.0;\n"
                + "      double anotherNotAnnotatedDouble = 0.0;\n"
                + "      float aNotAnnotatedFloat = 0.0F;\n"
                + "      float anotherNotAnnotatedFloat = 0.0F;\n"
                + "      int aNotAnnotatedInt = 0;\n"
                + "      int anotherNotAnnotatedInt = 0;\n"
                + "      long aNotAnnotatedLong = 0L;\n"
                + "      long anotherNotAnnotatedLong = 0L;\n"
                + "      short aNotAnnotatedShort = 0;\n"
                + "      short anotherNotAnnotatedShort = 0;\n"
                + "      String aNotAnnotatedString = null;\n"
                + "      return new AutoValue_NotAnnotatedTest(aBlob, aDouble, anotherDouble, aFloat, anotherFloat, anInt, anotherInt, aLong, anotherLong, aShort, anotherShort, aString, aNotAnnotatedBlob, aNotAnnotatedDouble, anotherNotAnnotatedDouble, aNotAnnotatedFloat, anotherNotAnnotatedFloat, aNotAnnotatedInt, anotherNotAnnotatedInt, aNotAnnotatedLong, anotherNotAnnotatedLong, aNotAnnotatedShort, anotherNotAnnotatedShort, aNotAnnotatedString);\n"
                + "    }\n"
                + "\n"
                + "  @Override\n"
                + "  public ContentValues toContentValuesWithParam(ContentValues contentValues) {\n"
                + "    if (contentValues == null) {\n"
                + "      contentValues = new ContentValues(12);\n"
                + "    }\n"
                + "    contentValues.put(\"a_blob\", aBlob());\n"
                + "    contentValues.put(\"a_double\", aDouble());\n"
                + "    contentValues.put(\"another_double\", anotherDouble());\n"
                + "    contentValues.put(\"a_float\", aFloat());\n"
                + "    contentValues.put(\"another_float\", anotherFloat());\n"
                + "    contentValues.put(\"an_int\", anInt());\n"
                + "    contentValues.put(\"another_int\", anotherInt());\n"
                + "    contentValues.put(\"a_long\", aLong());\n"
                + "    contentValues.put(\"another_long\", anotherLong());\n"
                + "    contentValues.put(\"a_short\", aShort());\n"
                + "    contentValues.put(\"another_short\", anotherShort());\n"
                + "    contentValues.put(\"a_string\", aString());\n"
                + "    return contentValues;\n"
                + "  }\n"
                + "}\n");

        Truth.assertAbout(JavaSourcesSubjectFactory.javaSources())
                .that(Collections.singletonList(source))
                .processedWith(new AutoValueProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

    @Test
    public void missingColumnAdapterAnnotation() {
        final JavaFileObject source = JavaFileObjects.forSourceString("net.zionsoft.auto.droid.test.MissingColumnAdapterAnnotationTest", ""
                + "package net.zionsoft.auto.droid.test;\n"
                + "import android.database.Cursor;\n"
                + "import com.google.auto.value.AutoValue;\n"
                + "import net.zionsoft.auto.droid.ColumnName;\n"
                + "import java.util.Map;\n"
                + "@AutoValue\n"
                + "public abstract class MissingColumnAdapterAnnotationTest {\n"
                + "    @ColumnName(\"missing_adapter\")\n"
                + "    abstract Map missingAdapter();\n"
                + "}\n");

        Truth.assertAbout(JavaSourcesSubjectFactory.javaSources())
                .that(Collections.singletonList(source))
                .processedWith(new AutoValueProcessor())
                .failsToCompile();
    }

    @Test
    public void missingColumnAdapterMethod() {
        final JavaFileObject columnAdapter = JavaFileObjects.forSourceString("net.zionsoft.auto.droid.test.CustomColumnAdapter", ""
                + "package net.zionsoft.auto.droid.test;\n"
                + "public class CustomColumnAdapter {\n"
                + "}\n");

        final JavaFileObject columnAdapterClient = JavaFileObjects.forSourceString("net.zionsoft.auto.droid.test.ColumnAdapterClient", ""
                + "package net.zionsoft.auto.droid.test;\n"
                + "import android.database.Cursor;\n"
                + "import com.google.auto.value.AutoValue;\n"
                + "import net.zionsoft.auto.droid.ColumnAdapter;\n"
                + "import net.zionsoft.auto.droid.ColumnName;\n"
                + "@AutoValue\n"
                + "public abstract class ColumnAdapterClient {\n"
                + "    @ColumnAdapter(CustomColumnAdapter.class)\n"
                + "    abstract CustomColumnAdapter anAdapter();\n"
                + "public static ColumnAdapterClient create(Cursor cursor) {\n"
                + "        return AutoValue_ColumnAdapterClient.createFromCursor(cursor);\n"
                + "    }\n"
                + "}\n");

        Truth.assertAbout(JavaSourcesSubjectFactory.javaSources())
                .that(Arrays.asList(columnAdapter, columnAdapterClient))
                .processedWith(new AutoValueProcessor())
                .failsToCompile();
    }

    @Test
    public void columnAdapter() {
        final JavaFileObject columnAdapter = JavaFileObjects.forSourceString("net.zionsoft.auto.droid.test.CustomColumnAdapter", ""
                + "package net.zionsoft.auto.droid.test;\n"
                + "import android.content.ContentValues;\n"
                + "import android.database.Cursor;\n"
                + "public class CustomColumnAdapter {\n"
                + "  public static CustomColumnAdapter fromCursor2() { return null; }\n"
                + "  public static void fromCursor3(Cursor cursor) {}\n"
                + "  public CustomColumnAdapter fromCursor4() { return null; }\n"
                + "  public static CustomColumnAdapter fromCursor5(int anInt) { return null; }\n"
                + "  public static CustomColumnAdapter fromCursor(Cursor cursor) { return null; }\n"
                + "}\n");

        final JavaFileObject columnAdapterClient = JavaFileObjects.forSourceString("net.zionsoft.auto.droid.test.ColumnAdapterClient", ""
                + "package net.zionsoft.auto.droid.test;\n"
                + "import android.database.Cursor;\n"
                + "import com.google.auto.value.AutoValue;\n"
                + "import net.zionsoft.auto.droid.ColumnAdapter;\n"
                + "import net.zionsoft.auto.droid.ColumnName;\n"
                + "import java.util.Map;\n"
                + "@AutoValue\n"
                + "public abstract class ColumnAdapterClient {\n"
                + "    @ColumnAdapter(CustomColumnAdapter.class)\n"
                + "    abstract CustomColumnAdapter anAdapter();\n"
                + "public static ColumnAdapterClient create(Cursor cursor) {\n"
                + "        return AutoValue_ColumnAdapterClient.createFromCursor(cursor);\n"
                + "    }\n"
                + "}\n");

        final JavaFileObject expected = JavaFileObjects.forSourceString("net.zionsoft.auto.droid.test.AutoValue_ColumnAdapterClient", ""
                + "package net.zionsoft.auto.droid.test;\n"
                + "\n"
                + "import android.database.Cursor;\n"
                + "\n"
                + "final class AutoValue_ColumnAdapterClient extends $AutoValue_ColumnAdapterClient {\n"
                + "  AutoValue_SmokeTest(CustomColumnAdapter anAdapter) {\n"
                + "    super(anAdapter);\n"
                + "  }\n"
                + "\n"
                + "  static AutoValue_ColumnAdapterClient createFromCursor(Cursor cursor) {\n"
                + "    CustomColumnAdapter anAdapter = CustomColumnAdapter.fromCursor(cursor);\n"
                + "    return new AutoValue_ColumnAdapterClient(anAdapter);\n"
                + "  }\n"
                + "}\n");

        Truth.assertAbout(JavaSourcesSubjectFactory.javaSources())
                .that(Arrays.asList(columnAdapter, columnAdapterClient))
                .processedWith(new AutoValueProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

    @Test
    public void columnAdapterToContentValues() {
        final JavaFileObject columnAdapter = JavaFileObjects.forSourceString("net.zionsoft.auto.droid.test.CustomColumnAdapter", ""
                + "package net.zionsoft.auto.droid.test;\n"
                + "import android.content.ContentValues;\n"
                + "import android.database.Cursor;\n"
                + "public class CustomColumnAdapter {\n"
                + "  public static CustomColumnAdapter fromCursor(Cursor cursor) { return null; }\n"
                + "  public String random() { return null; }\n"
                + "  public ContentValues toContentValues(String aString) { return null; }\n"
                + "  public ContentValues toContentValues() { return null; }\n"
                + "}\n");

        final JavaFileObject columnAdapterClient = JavaFileObjects.forSourceString("net.zionsoft.auto.droid.test.ColumnAdapterClient", ""
                + "package net.zionsoft.auto.droid.test;\n"
                + "import android.content.ContentValues;\n"
                + "import android.database.Cursor;\n"
                + "import com.google.auto.value.AutoValue;\n"
                + "import net.zionsoft.auto.droid.ColumnAdapter;\n"
                + "import net.zionsoft.auto.droid.ColumnName;\n"
                + "import java.util.Map;\n"
                + "@AutoValue\n"
                + "public abstract class ColumnAdapterClient {\n"
                + "    @ColumnAdapter(CustomColumnAdapter.class)\n"
                + "    abstract CustomColumnAdapter anAdapter();\n"
                + "    abstract ContentValues toContentValues();\n"
                + "public static ColumnAdapterClient create(Cursor cursor) {\n"
                + "        return AutoValue_ColumnAdapterClient.createFromCursor(cursor);\n"
                + "    }\n"
                + "}\n");

        final JavaFileObject expected = JavaFileObjects.forSourceString("net.zionsoft.auto.droid.test.AutoValue_ColumnAdapterClient", ""
                + "package net.zionsoft.auto.droid.test;\n"
                + "\n"
                + "import android.content.ContentValues;\n"
                + "import android.database.Cursor;\n"
                + "import java.lang.Override;\n"
                + "\n"
                + "final class AutoValue_ColumnAdapterClient extends $AutoValue_ColumnAdapterClient {\n"
                + "  AutoValue_SmokeTest(CustomColumnAdapter anAdapter) {\n"
                + "    super(anAdapter);\n"
                + "  }\n"
                + "\n"
                + "  static AutoValue_ColumnAdapterClient createFromCursor(Cursor cursor) {\n"
                + "    CustomColumnAdapter anAdapter = CustomColumnAdapter.fromCursor(cursor);\n"
                + "    return new AutoValue_ColumnAdapterClient(anAdapter);\n"
                + "  }\n"
                + "\n"
                + "  @Override\n"
                + "  ContentValues toContentValues() {\n"
                + "    ContentValues contentValues = new ContentValues(0);\n"
                + "    contentValues.putAll(anAdapter().toContentValues());\n"
                + "    return contentValues;\n"
                + "  }\n"
                + "}\n");

        Truth.assertAbout(JavaSourcesSubjectFactory.javaSources())
                .that(Arrays.asList(columnAdapter, columnAdapterClient))
                .processedWith(new AutoValueProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

    @Test
    public void columnAdapterToContentValuesWithParam() {
        final JavaFileObject columnAdapter = JavaFileObjects.forSourceString("net.zionsoft.auto.droid.test.CustomColumnAdapter", ""
                + "package net.zionsoft.auto.droid.test;\n"
                + "import android.content.ContentValues;\n"
                + "import android.database.Cursor;\n"
                + "public class CustomColumnAdapter {\n"
                + "  public static CustomColumnAdapter fromCursor(Cursor cursor) { return null; }\n"
                + "  public ContentValues toContentValues(ContentValues contentValues) { return null; }\n"
                + "}\n");

        final JavaFileObject columnAdapterClient = JavaFileObjects.forSourceString("net.zionsoft.auto.droid.test.ColumnAdapterClient", ""
                + "package net.zionsoft.auto.droid.test;\n"
                + "import android.content.ContentValues;\n"
                + "import android.database.Cursor;\n"
                + "import com.google.auto.value.AutoValue;\n"
                + "import net.zionsoft.auto.droid.ColumnAdapter;\n"
                + "import net.zionsoft.auto.droid.ColumnName;\n"
                + "import java.util.Map;\n"
                + "@AutoValue\n"
                + "public abstract class ColumnAdapterClient {\n"
                + "    @ColumnAdapter(CustomColumnAdapter.class)\n"
                + "    abstract CustomColumnAdapter anAdapter();\n"
                + "    abstract ContentValues toContentValues();\n"
                + "public static ColumnAdapterClient create(Cursor cursor) {\n"
                + "        return AutoValue_ColumnAdapterClient.createFromCursor(cursor);\n"
                + "    }\n"
                + "}\n");

        final JavaFileObject expected = JavaFileObjects.forSourceString("net.zionsoft.auto.droid.test.AutoValue_ColumnAdapterClient", ""
                + "package net.zionsoft.auto.droid.test;\n"
                + "\n"
                + "import android.content.ContentValues;\n"
                + "import android.database.Cursor;\n"
                + "import java.lang.Override;\n"
                + "\n"
                + "final class AutoValue_ColumnAdapterClient extends $AutoValue_ColumnAdapterClient {\n"
                + "  AutoValue_SmokeTest(CustomColumnAdapter anAdapter) {\n"
                + "    super(anAdapter);\n"
                + "  }\n"
                + "\n"
                + "  static AutoValue_ColumnAdapterClient createFromCursor(Cursor cursor) {\n"
                + "    CustomColumnAdapter anAdapter = CustomColumnAdapter.fromCursor(cursor);\n"
                + "    return new AutoValue_ColumnAdapterClient(anAdapter);\n"
                + "  }\n"
                + "\n"
                + "  @Override\n"
                + "  ContentValues toContentValues() {\n"
                + "    ContentValues contentValues = new ContentValues(0);\n"
                + "    anAdapter().toContentValues(contentValues);\n"
                + "    return contentValues;\n"
                + "  }\n"
                + "}\n");

        Truth.assertAbout(JavaSourcesSubjectFactory.javaSources())
                .that(Arrays.asList(columnAdapter, columnAdapterClient))
                .processedWith(new AutoValueProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }
}
