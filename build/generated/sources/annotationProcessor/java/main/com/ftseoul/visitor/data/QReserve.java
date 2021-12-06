package com.ftseoul.visitor.data;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QReserve is a Querydsl query type for Reserve
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QReserve extends EntityPathBase<Reserve> {

    private static final long serialVersionUID = 1140339309L;

    public static final QReserve reserve = new QReserve("reserve");

    public final DateTimePath<java.time.LocalDateTime> date = createDateTime("date", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath place = createString("place");

    public final StringPath purpose = createString("purpose");

    public final NumberPath<Long> targetStaff = createNumber("targetStaff", Long.class);

    public QReserve(String variable) {
        super(Reserve.class, forVariable(variable));
    }

    public QReserve(Path<? extends Reserve> path) {
        super(path.getType(), path.getMetadata());
    }

    public QReserve(PathMetadata metadata) {
        super(Reserve.class, metadata);
    }

}

