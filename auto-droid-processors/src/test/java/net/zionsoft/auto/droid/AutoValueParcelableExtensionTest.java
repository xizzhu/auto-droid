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

public class AutoValueParcelableExtensionTest {
    @Test
    public void smoke() {
        final JavaFileObject source = JavaFileObjects.forSourceString("net.zionsoft.auto.droid.test.SmokeTest", ""
                + "package net.zionsoft.auto.droid.test;\n"
                + "import android.os.Parcelable;\n"
                + "import com.google.auto.value.AutoValue;\n"
                + "@AutoValue\n"
                + "public abstract class SmokeTest implements Parcelable {\n"
                + "    abstract boolean aBoolean();\n"
                + "}\n");

        final JavaFileObject expected = JavaFileObjects.forSourceString("net.zionsoft.auto.droid.test.AutoValue_SmokeTest", ""
                + "package net.zionsoft.auto.droid.test;\n"
                + "\n"
                + "import android.os.Parcel;\n"
                + "import java.lang.ClassLoader;\n"
                + "import java.lang.Override;\n"
                + "\n"
                + "final class AutoValue_SmokeTest extends $AutoValue_SmokeTest {\n"
                + "  public static final android.os.Parcelable.Creator<AutoValue_SmokeTest> CREATOR = new android.os.Parcelable.Creator<AutoValue_SmokeTest>() {\n"
                + "    @Override\n"
                + "    public AutoValue_SmokeTest createFromParcel(Parcel in) {\n"
                + "      ClassLoader classLoader = AutoValue_SmokeTest.class.getClassLoader();\n"
                + "      boolean aBoolean = (boolean) in.readValue(classLoader);\n"
                + "      return new AutoValue_SmokeTest(aBoolean);\n"
                + "    }\n"
                + "    @Override\n"
                + "    public AutoValue_SmokeTest[] newArray(int size) {\n"
                + "      return new AutoValue_SmokeTest[size];\n"
                + "    }\n"
                + "  };\n"
                + "\n"
                + "  AutoValue_SmokeTest(boolean aBoolean) {\n"
                + "    super(aBoolean);\n"
                + "  }\n"
                + "\n"
                + "  @Override\n"
                + "  public int describeContents() {\n"
                + "    return 0;\n"
                + "  }\n"
                + "\n"
                + "  @Override\n"
                + "  public void writeToParcel(Parcel dest, int flags) {\n"
                + "    dest.writeValue(aBoolean());\n"
                + "  }\n"
                + "}\n");

        Truth.assertAbout(JavaSourcesSubjectFactory.javaSources())
                .that(Collections.singletonList(source))
                .processedWith(new AutoValueProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }
}
