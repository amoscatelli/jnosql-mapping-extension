/*
 *  Copyright (c) 2022 Contributors to the Eclipse Foundation
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   and Apache License v2.0 which accompanies this distribution.
 *   The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *   and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *   You may elect to redistribute this code under either of these licenses.
 *
 *   Contributors:
 *
 *   Alessandro Moscatelli
 */
package org.eclipse.jnosql.mapping.metamodel.processor;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import org.eclipse.jnosql.mapping.metamodel.api.Attribute;
import java.util.Arrays;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import org.eclipse.jnosql.mapping.metamodel.DefaultAttribute;

/**
 * Utility class to extend for simple field builders.
 * @param <A> attribute interface 
 * @param <D> attribute implementation 
 */
public abstract class AbstractSimpleFieldBuilder<A extends Attribute, D extends DefaultAttribute> extends AbstractFieldBuilder {

    public abstract void buildField(JCodeModel codeModel, JDefinedClass jClass, TypeElement typeElement, Element element);

    protected void buildField(JCodeModel codeModel, JDefinedClass jClass, TypeElement typeElement, Element element, Class<A> attributeInterface, Class<D> attributeClass) {
        super.buildField(jClass,
                element,
                codeModel.ref(
                        attributeInterface
                ).narrow(
                        codeModel.ref(typeElement.getQualifiedName().toString())
                ),
                buildInvocation(
                        codeModel.ref(
                                attributeClass
                        ),
                        Arrays.asList(
                                codeModel.ref(typeElement.getQualifiedName().toString()).dotclass(),
                                JExpr.lit(element.getSimpleName().toString())
                        )
                )
        );
    }

}
