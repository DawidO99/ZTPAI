package ochman.dawid.f1_api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverDto {
    private Long id;

    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @NotNull(message = "Car number is mandatory")
    @Min(value = 1, message = "Car number must be greater than 0")
    private Integer carNumber;

    @NotNull(message = "Points validation error")
    @Min(value = 0, message = "Points cannot be negative")
    private Integer points;

    @NotBlank(message = "Team name is mandatory")
    private String team;
}

