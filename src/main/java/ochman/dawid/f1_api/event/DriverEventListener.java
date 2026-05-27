package ochman.dawid.f1_api.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DriverEventListener {

    @EventListener
    public void handleDriverCreatedEvent(DriverCreatedEvent event) {
        log.info("Event Received: A new driver was added! Welcome {} to the {} team!",
                 event.getDriverName(), event.getTeam());
    }
}

