package ochman.dawid.f1_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamStandingDto {
    private String teamName;
    private Integer points;
}

