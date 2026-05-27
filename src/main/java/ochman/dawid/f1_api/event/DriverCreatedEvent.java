package ochman.dawid.f1_api.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DriverCreatedEvent extends ApplicationEvent {
    private final String driverName;
    private final String team;

    public DriverCreatedEvent(Object source, String driverName, String team) {
        super(source);
        this.driverName = driverName;
        this.team = team;
    }
}

