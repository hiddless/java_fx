package com.hiddless.java_fx.dto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotebookDTO {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String category;
    private boolean pinned;
    private UserDTO userDTO;

    @Override
    public String toString() {
        return "NotebookDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                ", pinned=" + pinned +
                ", userDTO=" + userDTO +
                '}';
    }

}