package com.ftseoul.visitor.dto.visitor.projection;

import com.ftseoul.visitor.data.visitor.VisitorStatus;
import java.time.LocalDateTime;

public interface CheckInVisitor {
    LocalDateTime getReserveDate();
    LocalDateTime getCheckIn();
    LocalDateTime getCheckOut();
    long getId();
    String getName();
    String getPhone();
    String getOrganization();
    VisitorStatus getStatus();
    String getStaffName();
    String getStaffPhone();
    String getStaffDepartment();
    String getPurpose();
    String getPlace();
}
