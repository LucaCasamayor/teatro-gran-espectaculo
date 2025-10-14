package com.teatro.backend.models.dtos;


;
import com.teatro.backend.models.enums.EventStatus;
import com.teatro.backend.models.enums.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Event type is required")
    private EventType type;

    @NotNull(message = "Date and time is required")
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;


    private EventStatus status;
    private String description;
    private List<TicketOptionDTO> ticketOptions;
}
