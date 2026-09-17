package com.englishlms.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Phản hồi danh sách phân trang cho ai-service")
public class PageResponse<T> {

    @Schema(description = "Danh sách phần tử của trang hiện tại")
    private List<T> items;

    @Schema(description = "Số trang hiện tại (0-indexed)", example = "0")
    private int pageNumber;

    @Schema(description = "Kích thước trang", example = "10")
    private int pageSize;

    @Schema(description = "Tổng số phần tử", example = "45")
    private long totalElements;

    @Schema(description = "Tổng số trang", example = "5")
    private int totalPages;

    @Schema(description = "Có phải trang cuối cùng hay không", example = "false")
    private boolean isLast;
}
