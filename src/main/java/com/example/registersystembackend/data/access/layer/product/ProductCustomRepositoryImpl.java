package com.example.registersystembackend.data.access.layer.product;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
class ProductCustomRepositoryImpl extends QuerydslRepositorySupport implements ProductCustomRepository {
    private static final QProduct PRODUCT = QProduct.product;

    @Autowired
    ProductCustomRepositoryImpl(MongoTemplate operations) {
        super(operations);
    }

    @Override
    public List<Product> findProductsByName(String name) {
        final Param<String> nameParam = new Param<>(String.class);
        final BooleanBuilder expressions = new BooleanBuilder();

        expressions.and(PRODUCT.isDeleted.eq(Boolean.FALSE));
        if (StringUtils.hasText(name) && !StringUtils.trimWhitespace(name).isEmpty()) {
            expressions.and(PRODUCT.name.containsIgnoreCase(name));
        }

        return from(PRODUCT)
                .where(expressions)
                .orderBy(new OrderSpecifier<>(Order.ASC, PRODUCT.name))
                .set(nameParam, name)
                .fetch();
    }
}
