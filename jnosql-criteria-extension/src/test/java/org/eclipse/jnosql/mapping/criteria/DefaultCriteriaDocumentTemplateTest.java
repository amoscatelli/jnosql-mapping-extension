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
 *   Otavio Santana
 *   Alessandro Moscatelli
 */
package org.eclipse.jnosql.mapping.criteria;

import jakarta.nosql.mapping.document.DocumentEntityConverter;
import jakarta.nosql.tck.test.CDIExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;
import jakarta.nosql.document.Document;
import jakarta.nosql.document.DocumentCondition;
import jakarta.nosql.document.DocumentEntity;
import jakarta.nosql.document.DocumentManager;
import jakarta.nosql.document.DocumentQuery;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import org.eclipse.jnosql.mapping.criteria.api.CriteriaQuery;
import org.eclipse.jnosql.mapping.criteria.api.EntityQueryResult;
import org.eclipse.jnosql.mapping.criteria.api.ExpressionQueryResult;
import org.eclipse.jnosql.mapping.criteria.api.ExpressionQueryResultRow;
import org.eclipse.jnosql.mapping.criteria.api.NumberExpression;
import org.eclipse.jnosql.mapping.criteria.api.StringExpression;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import org.eclipse.jnosql.mapping.criteria.api.CriteriaDocumentTemplate;
import org.eclipse.jnosql.mapping.criteria.api.CriteriaDocumentTemplateProducer;

@CDIExtension
class DefaultCriteriaDocumentTemplateTest {

    @Inject
    private DocumentEntityConverter converter;

    private DocumentManager documentManager;

    @Inject
    private CriteriaDocumentTemplateProducer criteriaDocumentTemplateProducer;

    private CriteriaDocumentTemplate criteriaDocumentTemplate;

    @BeforeEach
    public void setUp() {
        this.documentManager = mock(DocumentManager.class);
        criteriaDocumentTemplate = criteriaDocumentTemplateProducer.get(documentManager);
    }

    @Test
    public void shouldSelectEntitiesWithCriteria() {

        DocumentEntity documentEntity = DocumentEntity.of(
                "Person",
                Arrays.asList(
                        Document.of("_id", "Poliana"),
                        Document.of("age", 17)
                )
        );

        Mockito.when(
                documentManager.select(
                        DocumentQuery.builder().from(
                                "Person"
                        ).where(
                                DocumentCondition.and(
                                        new DocumentCondition[]{
                                            DocumentCondition.or(
                                                    DocumentCondition.eq(
                                                            "name",
                                                            "Poliana"
                                                    ),
                                                    DocumentCondition.gte(
                                                            "age",
                                                            17
                                                    )
                                            )
                                        }
                                )
                        ).build()
                )
        ).thenReturn(
                Stream.of(
                        documentEntity
                )
        );

        CriteriaQuery<Person> personQuery = criteriaDocumentTemplate.createQuery(Person.class);

        EntityQueryResult<Person> executeQuery = criteriaDocumentTemplate.executeQuery(
                personQuery.select().where(
                        personQuery.from().get(
                                Person_.name
                        ).equal(
                                "Poliana"
                        ).or(
                                personQuery.from().get(
                                        Person_.age
                                ).greaterThanOrEqualTo(17)
                        )
                )
        );

        Optional<Person> findFirst = executeQuery.getEntities().findFirst();

        assertTrue(
                findFirst.isPresent()
        );
        assertEquals(
                converter.toDocument(
                        findFirst.get()
                ),
                documentEntity
        );

    }

    @Test
    public void shouldSelectProjectionsWithCriteria() {

        DocumentEntity documentEntity = DocumentEntity.of(
                "Person",
                Arrays.asList(
                        Document.of("_id", "Poliana"),
                        Document.of("age", 17)
                )
        );

        Mockito.when(
                documentManager.select(
                        DocumentQuery.builder(
                                "name",
                                "age"
                        ).from(
                                "Person"
                        ).where(
                                DocumentCondition.and(
                                        new DocumentCondition[]{
                                            DocumentCondition.or(
                                                    DocumentCondition.eq(
                                                            "name",
                                                            "Poliana"
                                                    ),
                                                    DocumentCondition.gte(
                                                            "age",
                                                            17
                                                    )
                                            )
                                        }
                                )
                        ).build()
                )
        ).thenReturn(
                Stream.of(
                        documentEntity
                )
        );

        CriteriaQuery<Person> personQuery = criteriaDocumentTemplate.createQuery(Person.class);

        StringExpression<Person, Person> nameExpression = personQuery.from().get(
                Person_.name
        );
        NumberExpression<Person, Person, Integer> ageExpression = personQuery.from().get(
                Person_.age
        );

        ExpressionQueryResult<Person> executeQuery = criteriaDocumentTemplate.executeQuery(
                personQuery.select(
                        nameExpression,
                        ageExpression
                ).where(
                        nameExpression.equal(
                                "Poliana"
                        ).or(
                                ageExpression.greaterThanOrEqualTo(17)
                        )
                )
        );

        Optional<ExpressionQueryResultRow<Person>> findFirst = executeQuery.getRows().findFirst();

        assertTrue(
                findFirst.isPresent()
        );
        assertEquals(
                findFirst.get().get(
                        nameExpression
                ),
                "Poliana"
        );
        assertEquals(
                findFirst.get().get(
                        ageExpression
                ),
                17
        );

    }

}
