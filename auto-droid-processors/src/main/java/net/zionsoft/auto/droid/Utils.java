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

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;

class Utils {
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
}
