package com.cpmss.staffperformancereview;

import com.cpmss.staffperformancereview.dto.StaffPerformanceReviewResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** MapStruct mapper for {@link StaffPerformanceReview} entities. */
@Mapper(componentModel = "spring")
public interface StaffPerformanceReviewMapper {

    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(source = "staff.id", target = "staffId")
    @Mapping(source = "reviewer.id", target = "reviewerId")
    @Mapping(source = "department.id", target = "departmentId")
    StaffPerformanceReviewResponse toResponse(StaffPerformanceReview entity);
}
