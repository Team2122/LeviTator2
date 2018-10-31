package org.teamtators.common.config.helpers;

import edu.wpi.first.wpilibj.Relay;
import org.teamtators.common.harness.HarnessContext;

public class RelayConfig implements ConfigHelper<Relay> {
    public Integer channel = null;
    public Relay.Direction direction = null;

    public Relay create(HarnessContext ctx) {
        if (this.channel == null)
            throw new NullPointerException("Relay channel not specified");
        Relay relay = new Relay(channel);
        if (direction != null)
            relay.setDirection(direction);
        return relay;
    }

}
