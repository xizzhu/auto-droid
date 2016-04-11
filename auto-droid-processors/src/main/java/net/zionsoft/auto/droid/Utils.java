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

import com.google.auto.value.extension.AutoValueExtension;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

class Utils {
    static boolean containsAnnotation(AutoValueExtension.Context context, Class annotation) {
        final TypeElement typeElement = context.processingEnvironment()
                .getElementUtils()
                .getTypeElement(annotation.getName());
        if (typeElement == null) {
            return false;
        }
        final TypeMirror annotationType = typeElement.asType();
        for (ExecutableElement element : context.properties().values()) {
            for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
                if (annotationMirror.getAnnotationType().equals(annotationType)) {
                    return true;
                }
            }
        }

        return false;
    }

    static MethodSpec generateConstructor(Map<String, ExecutableElement> properties) {
        final List<ParameterSpec> params = new ArrayList<>();
        for (Map.Entry<String, ExecutableElement> entry : properties.entrySet()) {
            final TypeName typeName = TypeName.get(entry.getValue().getReturnType());
            params.add(ParameterSpec.builder(typeName, entry.getKey()).build());
        }

        final StringBuilder body = new StringBuilder("super(");
        for (int i = properties.size(); i > 0; --i) {
            body.append("$N, ");
        }
        if (properties.size() > 0) {
            body.setLength(body.length() - 2); // removes the last ", "
        }
        body.append(")");

        return MethodSpec.constructorBuilder()
                .addParameters(params)
                .addStatement(body.toString(), properties.keySet().toArray())
                .build();
    }

    static CodeBlock generateObjectConstruction(String className, Map<String, ExecutableElement> properties) {
        final Object[] propertyNames = properties.keySet().toArray();
        final StringBuilder construct = new StringBuilder("new ").append(className).append("(");
        for (int i = propertyNames.length; i > 0; --i) {
            construct.append("$N, ");
        }
        if (propertyNames.length > 0) {
            construct.setLength(construct.length() - 2); // removes the trailing ", "
        }
        construct.append(")");
        return CodeBlock.builder()
                .addStatement(construct.toString(), propertyNames)
                .build();
    }
}
