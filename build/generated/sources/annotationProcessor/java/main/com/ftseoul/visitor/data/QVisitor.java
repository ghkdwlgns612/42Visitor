package com.ftseoul.visitor.data;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QVisitor is a Querydsl query type for Visitor
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QVisitor extends EntityPathBase<Visitor> {

    private static final long serialVersionUID = 510024223L;

    public static final QVisitor visitor = new QVisitor("visitor");

    public final DateTimePath<java.time.LocalDateTime> checkInTime = createDateTime("checkInTime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> checkOutTime = createDateTime("checkOutTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath organization = createString("organization");

    public final StringPath phone = createString("phone");

    public final NumberPath<Long> reserveId = createNumber("reserveId", Long.class);

    public final EnumPath<com.ftseoul.visitor.data.visitor.VisitorStatus> status = createEnum("status", com.ftseoul.visitor.data.visitor.VisitorStatus.class);

    public QVisitor(String variable) {
        super(Visitor.class, forVariable(variable));
    }

    public QVisitor(Path<? extends Visitor> path) {
        super(path.getType(), path.getMetadata());
    }

    public QVisitor(PathMetadata metadata) {
        super(Visitor.class, metadata);
    }

}

