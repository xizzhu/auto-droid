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
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

@AutoService(AutoValueExtension.class)
public class AutoValueCursorExtension extends AutoValueExtension {
    private static final TypeName CONTENT_VALUES = ClassName.get("android.content", "ContentValues");
    private static final TypeName CURSOR_VALUES = ClassName.get("android.database", "Cursor");

    @Override
    public boolean applicable(Context context) {
        return Utils.containsAnnotation(context, ColumnName.class)
                || Utils.containsAnnotation(context, ColumnAdapter.class);
    }

    @Override
    public Set<String> consumeProperties(Context context) {
        final ExecutableElement method = findToContentValues(context.autoValueClass());
        if (method != null) {
            return Collections.singleton(method.getSimpleName().toString());
        }
        return super.consumeProperties(context);
    }

    private static ExecutableElement findToContentValues(TypeElement cls) {
        for (Element element : cls.getEnclosedElements()) {
            if (element.getKind() != ElementKind.METHOD) {
                continue;
            }

            final ExecutableElement executableElement = (ExecutableElement) element;
            if (!CONTENT_VALUES.equals(ClassName.get(executableElement.getReturnType()))) {
                continue;
            }

            final List<? extends VariableElement> parameters = executableElement.getParameters();
            switch (parameters.size()) {
                case 0:
                    return executableElement;
                case 1:
                    if (CONTENT_VALUES.equals(ClassName.get(parameters.get(0).asType()))) {
                        return executableElement;
                    }
                    break;
            }
        }
        return null;
    }

    @Override
    public String generateClass(Context context, String className, String classToExtend, boolean isFinal) {
        final String packageName = context.packageName();
        final Map<String, ExecutableElement> properties = context.properties();
        final TypeSpec.Builder builder = TypeSpec.classBuilder(className)
                .addModifiers(isFinal ? Modifier.FINAL : Modifier.ABSTRACT)
                .superclass(ClassName.get(packageName, classToExtend))
                .addMethod(Utils.generateConstructor(properties))
                .addMethod(generateFactoryMethod(context, properties));
        final MethodSpec toContentValuesMethod = generateToContentValuesMethod(context, properties);
        if (toContentValuesMethod != null) {
            builder.addMethod(toContentValuesMethod);
        }
        return JavaFile.builder(packageName, builder.build()).build().toString();
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

            final ColumnAdapter columnAdapter = element.getAnnotation(ColumnAdapter.class);
            if (columnAdapter != null) {
                final TypeMirror adapterType = findAdapterType(element);
                if (adapterType == null) {
                    context.processingEnvironment().getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "Failed to find adapter type: " + columnAdapter.value(),
                            context.autoValueClass());
                }

                final ExecutableElement adapterFactoryMethod = findAdapterFactoryMethod(context, adapterType);
                if (adapterFactoryMethod == null) {
                    context.processingEnvironment().getMessager().printMessage(Diagnostic.Kind.ERROR,
                            String.format("Adapter class `%s` needs to implements a `static` method" +
                                            " taking a `Cursor` and returning `%s`",
                                    columnAdapter.value(), element.getReturnType()),
                            context.autoValueClass());
                    continue;
                }
                factoryMethod.addStatement("$T $N = $T.$N(cursor)", typeName, name, adapterType,
                        adapterFactoryMethod.getSimpleName().toString());

                continue;
            }

            final ColumnName columnName = element.getAnnotation(ColumnName.class);
            if (columnName == null) {
                // not annotated, use default value
                if (typeName.equals(ArrayTypeName.of(TypeName.BYTE))) {
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
                if (typeName.equals(ArrayTypeName.of(TypeName.BYTE))) {
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
                    context.processingEnvironment().getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "Unsupported type: " + typeName.toString(), context.autoValueClass());
                }
            }
        }

        factoryMethod.addCode("return ")
                .addCode(Utils.generateObjectConstruction(classSimpleName, properties));

        return factoryMethod.build();
    }

    private static TypeMirror findAdapterType(ExecutableElement element) {
        final String className = ColumnAdapter.class.getName();
        AnnotationMirror annotationMirror = null;
        for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
            if (mirror.getAnnotationType().toString().equals(className)) {
                annotationMirror = mirror;
                break;
            }
        }
        if (annotationMirror == null) {
            return null;
        }

        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry
                : annotationMirror.getElementValues().entrySet()) {
            if (entry.getKey().getSimpleName().toString().equals("value")) {
                return (TypeMirror) entry.getValue().getValue();
            }
        }

        return null;
    }

    private static ExecutableElement findAdapterFactoryMethod(Context context, TypeMirror type) {
        final TypeName factoryTypeName = TypeName.get(type);
        final TypeElement factoryType = (TypeElement) context.processingEnvironment()
                .getTypeUtils().asElement(type);
        for (Element e : factoryType.getEnclosedElements()) {
            if (e.getKind() != ElementKind.METHOD) {
                continue;
            }
            if (!e.getModifiers().contains(Modifier.STATIC)) {
                continue;
            }

            final ExecutableElement executableElement = (ExecutableElement) e;

            if (!factoryTypeName.equals(ClassName.get(executableElement.getReturnType()))) {
                continue;
            }

            final List<? extends VariableElement> parameters = executableElement.getParameters();
            if (parameters.size() == 1 && CURSOR_VALUES.equals(ClassName.get(parameters.get(0).asType()))) {
                return executableElement;
            }
        }
        return null;
    }

    private static MethodSpec generateToContentValuesMethod(Context context, Map<String, ExecutableElement> properties) {
        final ExecutableElement toContentValuesElement = findToContentValues(context.autoValueClass());
        if (toContentValuesElement == null) {
            return null;
        }

        final MethodSpec.Builder toContentValuesMethod = MethodSpec.methodBuilder(
                toContentValuesElement.getSimpleName().toString())
                .addAnnotation(Override.class)
                .returns(CONTENT_VALUES);

        final Set<Modifier> modifiers = new HashSet<>(toContentValuesElement.getModifiers());
        modifiers.remove(Modifier.ABSTRACT);
        toContentValuesMethod.addModifiers(modifiers);

        int annotatedProperties = 0;
        for (Map.Entry<String, ExecutableElement> entry : properties.entrySet()) {
            if (entry.getValue().getAnnotation(ColumnName.class) != null) {
                ++annotatedProperties;
            }
        }

        final List<? extends VariableElement> parameters = toContentValuesElement.getParameters();
        if (parameters.size() == 1 && CONTENT_VALUES.equals(ClassName.get(parameters.get(0).asType()))) {
            toContentValuesMethod.addParameter(ParameterSpec.builder(CONTENT_VALUES, "contentValues").build());
            toContentValuesMethod.beginControlFlow("if (contentValues == null)")
                    .addStatement("contentValues = new $T($L)", CONTENT_VALUES, annotatedProperties)
                    .endControlFlow();
        } else {
            toContentValuesMethod.addStatement("$T contentValues = new $T($L)",
                    CONTENT_VALUES, CONTENT_VALUES, annotatedProperties);
        }

        for (Map.Entry<String, ExecutableElement> entry : properties.entrySet()) {
            final ExecutableElement element = entry.getValue();

            final ColumnAdapter columnAdapter = element.getAnnotation(ColumnAdapter.class);
            if (columnAdapter != null) {
                final TypeMirror adapterType = findAdapterType(element);
                if (adapterType == null) {
                    context.processingEnvironment().getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "Failed to find adapter type: " + columnAdapter.value(),
                            context.autoValueClass());
                }

                final ExecutableElement adapterToContentValuesMethod
                        = findAdapterToContentValuesMethod(context, adapterType);
                if (adapterToContentValuesMethod == null) {
                    context.processingEnvironment().getMessager().printMessage(Diagnostic.Kind.ERROR,
                            String.format("Adapter class `%s` needs to implements a method" +
                                            " taking an optional `ContentValues` and returning `ContentValues`",
                                    columnAdapter.value()),
                            context.autoValueClass());
                    continue;
                }

                if (adapterToContentValuesMethod.getParameters().size() == 0) {
                    toContentValuesMethod.addStatement("contentValues.putAll($L().$N())",
                            entry.getKey(), adapterToContentValuesMethod.getSimpleName().toString());
                } else {
                    toContentValuesMethod.addStatement("$L().$N(contentValues)",
                            entry.getKey(), adapterToContentValuesMethod.getSimpleName().toString());
                }

                continue;
            }

            final ColumnName columnName = element.getAnnotation(ColumnName.class);
            if (columnName != null) {
                toContentValuesMethod.addStatement("contentValues.put($S, $L())",
                        columnName.value(), entry.getKey());
            }
        }
        toContentValuesMethod.addStatement("return contentValues");

        return toContentValuesMethod.build();
    }

    private static ExecutableElement findAdapterToContentValuesMethod(Context context, TypeMirror type) {
        final TypeElement factoryType = (TypeElement) context.processingEnvironment()
                .getTypeUtils().asElement(type);
        for (Element e : factoryType.getEnclosedElements()) {
            if (e.getKind() != ElementKind.METHOD) {
                continue;
            }
            if (e.getModifiers().contains(Modifier.STATIC)) {
                continue;
            }

            final ExecutableElement executableElement = (ExecutableElement) e;

            if (!CONTENT_VALUES.equals(ClassName.get(executableElement.getReturnType()))) {
                continue;
            }

            final List<? extends VariableElement> parameters = executableElement.getParameters();
            final int size = parameters.size();
            if (size == 0
                    || (size == 1 && CONTENT_VALUES.equals(ClassName.get(parameters.get(0).asType())))) {
                return executableElement;
            }
        }
        return null;
    }
}
