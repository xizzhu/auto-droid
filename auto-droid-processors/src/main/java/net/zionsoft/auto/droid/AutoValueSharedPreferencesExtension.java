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

import com.google.auto.service.AutoService;
import com.google.auto.value.extension.AutoValueExtension;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;

@AutoService(AutoValueExtension.class)
public final class AutoValueSharedPreferencesExtension extends AutoValueExtension {
    @Override
    public boolean applicable(Context context) {
        return Utils.containsAnnotation(context, SharedPreference.class);
    }

    @Override
    public String generateClass(Context context, String className, String classToExtend, boolean isFinal) {
        final String packageName = context.packageName();
        final Map<String, ExecutableElement> properties = context.properties();
        final TypeSpec subclass = TypeSpec.classBuilder(className)
                .addModifiers(isFinal ? Modifier.FINAL : Modifier.ABSTRACT)
                .superclass(ClassName.get(packageName, classToExtend))
                .addMethod(Utils.generateConstructor(properties))
                .addMethod(generateFactoryMethod(context, properties))
                .build();
        return JavaFile.builder(packageName, subclass).build().toString();
    }

    private static MethodSpec generateFactoryMethod(Context context, Map<String, ExecutableElement> properties) {
        final MethodSpec.Builder factoryMethod = MethodSpec.methodBuilder("createFromSharedPreferences")
                .addModifiers(Modifier.STATIC)
                .addParameter(ClassName.get("android.content", "SharedPreferences"), "sharedPreferences");

        final String classSimpleName = "AutoValue_" + context.autoValueClass().getSimpleName().toString();
        factoryMethod.returns(ClassName.get(context.packageName(), classSimpleName));

        for (Map.Entry<String, ExecutableElement> entry : properties.entrySet()) {
            final String name = entry.getKey();
            final ExecutableElement element = entry.getValue();
            final TypeName typeName = TypeName.get(element.getReturnType());
            final SharedPreference sharedPreference = element.getAnnotation(SharedPreference.class);
            if (sharedPreference == null) {
                // not annotated, use default value
                if (typeName.equals(TypeName.BOOLEAN) || typeName.equals(TypeName.BOOLEAN.box())) {
                    factoryMethod.addStatement("boolean $N = false", name);
                } else if (typeName.equals(TypeName.FLOAT) || typeName.equals(TypeName.FLOAT.box())) {
                    factoryMethod.addStatement("float $N = 0.0F", name);
                } else if (typeName.equals(TypeName.INT) || typeName.equals(TypeName.INT.box())) {
                    factoryMethod.addStatement("int $N = 0", name);
                } else if (typeName.equals(TypeName.LONG) || typeName.equals(TypeName.LONG.box())) {
                    factoryMethod.addStatement("long $N = 0L", name);
                } else {
                    factoryMethod.addStatement("$T $N = null", typeName, name);
                }
            } else {
                final String key = sharedPreference.key();
                String defaultValue = sharedPreference.defaultValue();
                if (typeName.equals(TypeName.BOOLEAN) || typeName.equals(TypeName.BOOLEAN.box())) {
                    if ("".equals(defaultValue)) {
                        defaultValue = "false";
                    }
                    factoryMethod.addStatement("boolean $N = sharedPreferences.getBoolean($S, $L)",
                            name, key, Boolean.parseBoolean(defaultValue));
                } else if (typeName.equals(TypeName.FLOAT) || typeName.equals(TypeName.FLOAT.box())) {
                    if ("".equals(defaultValue)) {
                        defaultValue = "0.0F";
                    }
                    factoryMethod.addStatement("float $N = sharedPreferences.getFloat($S, $LF)", name,
                            key, Float.parseFloat(defaultValue));
                } else if (typeName.equals(TypeName.INT) || typeName.equals(TypeName.INT.box())) {
                    if ("".equals(defaultValue)) {
                        defaultValue = "0";
                    }
                    factoryMethod.addStatement("int $N = sharedPreferences.getInt($S, $L)", name, key,
                            Integer.parseInt(defaultValue));
                } else if (typeName.equals(TypeName.LONG) || typeName.equals(TypeName.LONG.box())) {
                    if ("".equals(defaultValue)) {
                        defaultValue = "0";
                    }
                    factoryMethod.addStatement("long $N = sharedPreferences.getLong($S, $LL)", name,
                            key, Long.parseLong(defaultValue));
                } else if (typeName.equals(TypeName.get(String.class))) {
                    factoryMethod.addStatement("String $N = sharedPreferences.getString($S, $S)", name,
                            key, defaultValue);
                } else if (typeName.equals(ParameterizedTypeName.get(ClassName.get("java.util", "Set"),
                        TypeName.get(String.class)))) {
                    factoryMethod.addStatement(
                            "Set<String> $N = sharedPreferences.getStringSet($S, null)", name, key);
                } else {
                    // TODO support other types
                }
            }
        }

        factoryMethod.addCode("return ")
                .addCode(Utils.generateObjectConstruction(classSimpleName, properties));

        return factoryMethod.build();
    }
}
