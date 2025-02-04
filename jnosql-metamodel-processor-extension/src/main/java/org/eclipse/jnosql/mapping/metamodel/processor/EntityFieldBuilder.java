/*
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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
package org.eclipse.jnosql.mapping.metamodel.processor;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import org.eclipse.jnosql.mapping.metamodel.api.EntityAttribute;
import java.util.Arrays;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import org.eclipse.jnosql.mapping.metamodel.DefaultEntityAttribute;

/**
 * Field builder for comparable attributes.
 */
public class EntityFieldBuilder extends AbstractFieldBuilder {

    public void buildField(JCodeModel codeModel, JDefinedClass jClass, TypeElement typeElement, Element element, String toString) {
        super.buildField(
                jClass,
                element,
                codeModel.ref(
                        EntityAttribute.class
                ).narrow(
                        Arrays.asList(
                                codeModel.ref(typeElement.getQualifiedName().toString()),
                                codeModel.ref(toString)
                        )
                ),
                buildInvocation(
                        codeModel.ref(
                                DefaultEntityAttribute.class
                        ),
                        Arrays.asList(
                                codeModel.ref(typeElement.getQualifiedName().toString()).dotclass(),
                                codeModel.ref(toString).dotclass(),
                                JExpr.lit(element.getSimpleName().toString())
                        )
                )
        );
    }

}
