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
import com.google.common.collect.Sets;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;

@AutoService(AutoValueExtension.class)
public class AutoValueParcelableExtension extends AutoValueExtension {
    @Override
    public boolean applicable(Context context) {
        final ProcessingEnvironment processingEnvironment = context.processingEnvironment();
        final TypeMirror parcelableType = processingEnvironment
                .getElementUtils()
                .getTypeElement("android.os.Parcelable")
                .asType();
        final TypeMirror autoValueClass = context.autoValueClass().asType();
        return processingEnvironment.getTypeUtils().isAssignable(autoValueClass, parcelableType);
    }

    @Override
    public boolean mustBeFinal(Context context) {
        return true;
    }

    @Override
    public Set<String> consumeProperties(Context context) {
        return Sets.newHashSet("describeContents", "writeToParcel");
    }

    @Override
    public String generateClass(Context context, String className, String classToExtend, boolean isFinal) {
        final String packageName = context.packageName();
        final Map<String, ExecutableElement> properties = context.properties();
        final TypeSpec subclass = TypeSpec.classBuilder(className)
                .addModifiers(isFinal ? Modifier.FINAL : Modifier.ABSTRACT)
                .superclass(ClassName.get(packageName, classToExtend))
                .addMethod(Utils.generateConstructor(properties))
                .addMethod(generateDescribeContentsMethod())
                .addMethod(generateWriteToParcelMethod(properties))
                .addField(generateCreatorField(packageName, className, properties))
                .build();
        return JavaFile.builder(packageName, subclass).build().toString();
    }

    private static MethodSpec generateDescribeContentsMethod() {
        return MethodSpec.methodBuilder("describeContents")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(TypeName.INT)
                .addCode("return 0;")
                .build();
    }

    private static MethodSpec generateWriteToParcelMethod(Map<String, ExecutableElement> properties) {
        final MethodSpec.Builder builder = MethodSpec.methodBuilder("writeToParcel")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(ClassName.get("android.os", "Parcel"), "dest")
                .addParameter(TypeName.INT, "flags")
                .returns(TypeName.VOID);

        for (Map.Entry<String, ExecutableElement> entry : properties.entrySet()) {
            builder.addStatement("dest.writeValue($N())", entry.getKey());
        }

        return builder.build();
    }

    private static FieldSpec generateCreatorField(String packageName, String className,
                                                  Map<String, ExecutableElement> properties) {
        final TypeName classType = ClassName.get(packageName, className);
        final TypeName type = ParameterizedTypeName.get(ClassName.get("", "android.os.Parcelable.Creator"), classType);
        final FieldSpec.Builder creatorBuilder = FieldSpec.builder(
                type, "CREATOR", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);

        final TypeSpec initializer = TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(type)
                .addMethod(generateCreateFromParcelMethod(className, classType, properties))
                .addMethod(generateNewArrayMethod(classType))
                .build();
        creatorBuilder.initializer("$L", initializer);

        return creatorBuilder.build();
    }

    private static MethodSpec generateCreateFromParcelMethod(String className, TypeName classType,
                                                             Map<String, ExecutableElement> properties) {
        final MethodSpec.Builder createFromParcelBuilder = MethodSpec.methodBuilder("createFromParcel")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(ClassName.get("android.os", "Parcel"), "in")
                .returns(classType);

        createFromParcelBuilder.addStatement("$T classLoader = $T.class.getClassLoader()",
                ClassName.get("java.lang", "ClassLoader"), classType);

        for (Map.Entry<String, ExecutableElement> entry : properties.entrySet()) {
            final String name = entry.getKey();
            final ExecutableElement element = entry.getValue();
            final TypeName typeName = TypeName.get(element.getReturnType());
            createFromParcelBuilder.addStatement("$T $N = ($T) in.readValue(classLoader)", typeName, name, typeName);
        }

        final Object[] propertyNames = properties.keySet().toArray();
        final StringBuilder construct = new StringBuilder("new ").append(className).append("(");
        for (int i = propertyNames.length; i > 0; --i) {
            construct.append("$N, ");
        }
        if (propertyNames.length > 0) {
            construct.setLength(construct.length() - 2); // removes the trailing ", "
        }
        construct.append(")");
        createFromParcelBuilder.addCode("return ")
                .addCode(CodeBlock.builder().addStatement(construct.toString(), propertyNames).build());

        return createFromParcelBuilder.build();
    }

    private static MethodSpec generateNewArrayMethod(TypeName classType) {
        return MethodSpec.methodBuilder("newArray")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(TypeName.INT, "size")
                .returns(ArrayTypeName.of(classType))
                .addStatement("return new $T[size]", classType)
                .build();
    }
}
