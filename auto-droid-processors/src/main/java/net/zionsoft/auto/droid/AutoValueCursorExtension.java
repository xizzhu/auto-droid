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
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;

@AutoService(AutoValueExtension.class)
public class AutoValueCursorExtension extends AutoValueExtension {
    @Override
    public boolean applicable(Context context) {
        final TypeMirror annotationType = context.processingEnvironment()
                .getElementUtils()
                .getTypeElement(ColumnName.class.getName())
                .asType();
        for (ExecutableElement element : context.properties().values()) {
            for (AnnotationMirror annotation : element.getAnnotationMirrors()) {
                if (annotation.getAnnotationType().equals(annotationType)) {
                    return true;
                }
            }
        }

        return false;
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
        final MethodSpec.Builder factoryMethod = MethodSpec.methodBuilder("createFromCursor")
                .addModifiers(Modifier.STATIC)
                .addParameter(ClassName.get("android.database", "Cursor"), "cursor");

        final String classSimpleName = "AutoValue_" + context.autoValueClass().getSimpleName().toString();
        factoryMethod.returns(ClassName.get(context.packageName(), classSimpleName));

        for (Map.Entry<String, ExecutableElement> entry : properties.entrySet()) {
            final String name = entry.getKey();
            final ExecutableElement element = entry.getValue();
            final TypeName typeName = TypeName.get(element.getReturnType());
            final ColumnName columnName = element.getAnnotation(ColumnName.class);
            if (columnName == null) {
                // not annotated, use default value
                if (typeName.equals(ArrayTypeName.of(TypeName.BYTE)) || typeName.equals(ArrayTypeName.of(TypeName.BYTE.box()))) {
                    factoryMethod.addStatement("byte[] $N = null", name);
                } else if (typeName.equals(TypeName.DOUBLE) || typeName.equals(TypeName.DOUBLE.box())) {
                    factoryMethod.addStatement("double $N = 0.0", name);
                } else if (typeName.equals(TypeName.FLOAT) || typeName.equals(TypeName.FLOAT.box())) {
                    factoryMethod.addStatement("float $N = 0.0F", name);
                } else if (typeName.equals(TypeName.INT) || typeName.equals(TypeName.INT.box())) {
                    factoryMethod.addStatement("int $N = 0", name);
                } else if (typeName.equals(TypeName.LONG) || typeName.equals(TypeName.LONG.box())) {
                    factoryMethod.addStatement("long $N = 0L", name);
                } else if (typeName.equals(TypeName.SHORT) || typeName.equals(TypeName.SHORT.box())) {
                    factoryMethod.addStatement("short $N = 0", name);
                } else {
                    factoryMethod.addStatement("$T $N = null", typeName, name);
                }
            } else {
                final String key = columnName.value();
                if (typeName.equals(ArrayTypeName.of(TypeName.BYTE)) || typeName.equals(ArrayTypeName.of(TypeName.BYTE.box()))) {
                    factoryMethod.addStatement("byte[] $N = cursor.getBlob(cursor.getColumnIndexOrThrow($S))",
                            name, key);
                } else if (typeName.equals(TypeName.DOUBLE) || typeName.equals(TypeName.DOUBLE.box())) {
                    factoryMethod.addStatement("double $N = cursor.getDouble(cursor.getColumnIndexOrThrow($S))",
                            name, key);
                } else if (typeName.equals(TypeName.FLOAT) || typeName.equals(TypeName.FLOAT.box())) {
                    factoryMethod.addStatement("float $N = cursor.getFloat(cursor.getColumnIndexOrThrow($S))",
                            name, key);
                } else if (typeName.equals(TypeName.INT) || typeName.equals(TypeName.INT.box())) {
                    factoryMethod.addStatement("int $N = cursor.getInt(cursor.getColumnIndexOrThrow($S))",
                            name, key);
                } else if (typeName.equals(TypeName.LONG) || typeName.equals(TypeName.LONG.box())) {
                    factoryMethod.addStatement("long $N = cursor.getLong(cursor.getColumnIndexOrThrow($S))",
                            name, key);
                } else if (typeName.equals(TypeName.SHORT) || typeName.equals(TypeName.SHORT.box())) {
                    factoryMethod.addStatement("short $N = cursor.getShort(cursor.getColumnIndexOrThrow($S))",
                            name, key);
                } else if (typeName.equals(TypeName.get(String.class))) {
                    factoryMethod.addStatement("String $N = cursor.getString(cursor.getColumnIndexOrThrow($S))",
                            name, key);
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
