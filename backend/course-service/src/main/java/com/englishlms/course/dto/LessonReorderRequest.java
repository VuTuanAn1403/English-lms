package com.englishlms.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Yêu cầu thay đổi thứ tự sắp xếp bài học")
public class LessonReorderRequest {

    @NotNull(message = "Thứ tự mới không được để trống")
    @Min(value = 1, message = "Thứ tự sắp xếp phải từ 1 trở lên")
    @Schema(description = "Thứ tự sắp xếp mới (Order Index)", example = "2")
    private Integer newOrderIndex;
}
