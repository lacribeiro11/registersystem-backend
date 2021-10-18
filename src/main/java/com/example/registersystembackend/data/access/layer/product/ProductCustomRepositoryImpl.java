package com.example.registersystembackend.data.access.layer.product;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Param;
import com.querydsl.core.types.dsl.PathBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.support.QuerydslRepositorySupport;
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
class ProductCustomRepositoryImpl extends QuerydslRepositorySupport implements ProductCustomRepository {
    private static final QProduct PRODUCT = QProduct.product;

    @Autowired
    ProductCustomRepositoryImpl(MongoTemplate operations) {
        super(operations);
    }

    @Override
    public Page<Product> findProductsByName(String name, Pageable pageable) {
        final Param<String> nameParam = new Param<>(String.class);

        final BooleanBuilder expressions = expressionsBuilder(name);

        final OrderSpecifier<?>[] orderSpecifiers = mapToOrderSpecifiers(pageable);

        final SpringDataMongodbQuery<Product> where = from(PRODUCT).where(expressions);

        final SpringDataMongodbQuery<Product> query = where
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .set(nameParam, name);

        return new PageImpl<>(query.fetch(), pageable, where.fetchCount());
    }

    private static OrderSpecifier<?>[] mapToOrderSpecifiers(Pageable pageable) {
        return pageable.getSort().stream().map(sort -> {
            final PathBuilder<Product> path = new PathBuilder<>(Product.class, "product");
            Order order = sort.isAscending() ? Order.ASC : Order.DESC;
            return new OrderSpecifier(order, path.get(sort.getProperty()));
        }).toArray(OrderSpecifier[]::new);
    }

    private static BooleanBuilder expressionsBuilder(String name) {
        final BooleanBuilder expressions = new BooleanBuilder();

        expressions.and(PRODUCT.isDeleted.eq(Boolean.FALSE));
        if (StringUtils.hasText(name) && !StringUtils.trimWhitespace(name).isEmpty()) {
            expressions.and(PRODUCT.name.containsIgnoreCase(name));
        }
        return expressions;
    }
}
