package com.ftseoul.visitor.dto.visitor.projection;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.Generated;

/**
 * com.ftseoul.visitor.dto.visitor.projection.QCheckInVisitorDecrypt is a Querydsl Projection type for CheckInVisitorDecrypt
 */
@Generated("com.querydsl.codegen.ProjectionSerializer")
public class QCheckInVisitorDecrypt extends ConstructorExpression<CheckInVisitorDecrypt> {

    private static final long serialVersionUID = -190039759L;

    public QCheckInVisitorDecrypt(com.querydsl.core.types.Expression<String> checkInDate, com.querydsl.core.types.Expression<java.time.LocalDateTime> checkIn, com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> name, com.querydsl.core.types.Expression<String> phone, com.querydsl.core.types.Expression<String> organization, com.querydsl.core.types.Expression<com.ftseoul.visitor.data.visitor.VisitorStatus> status, com.querydsl.core.types.Expression<String> staffName, com.querydsl.core.types.Expression<String> staffPhone, com.querydsl.core.types.Expression<String> staffDepartment, com.querydsl.core.types.Expression<String> purpose, com.querydsl.core.types.Expression<String> place) {
        super(CheckInVisitorDecrypt.class, new Class<?>[]{String.class, java.time.LocalDateTime.class, long.class, String.class, String.class, String.class, com.ftseoul.visitor.data.visitor.VisitorStatus.class, String.class, String.class, String.class, String.class, String.class}, checkInDate, checkIn, id, name, phone, organization, status, staffName, staffPhone, staffDepartment, purpose, place);
    }

}

