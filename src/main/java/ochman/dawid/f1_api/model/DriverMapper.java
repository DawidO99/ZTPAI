package ochman.dawid.f1_api.model;

import ochman.dawid.f1_api.dto.DriverDto;
import org.springframework.stereotype.Component;

@Component
public class DriverMapper {

    public DriverDto toDto(Driver driver) {
        if (driver == null) {
            return null;
        }
        return DriverDto.builder()
                .id(driver.getId())
                .firstName(driver.getFirstName())
                .lastName(driver.getLastName())
                .carNumber(driver.getCarNumber())
                .points(driver.getPoints())
                .team(driver.getTeam())
                .overall(driver.getOverall())
                .build();
    }

    public Driver toEntity(DriverDto dto) {
        if (dto == null) {
            return null;
        }
        return Driver.builder()
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .carNumber(dto.getCarNumber())
                .points(dto.getPoints() != null ? dto.getPoints() : 0)
                .team(dto.getTeam())
                .overall(dto.getOverall() != null ? dto.getOverall() : 50)
                .build();
    }
}
