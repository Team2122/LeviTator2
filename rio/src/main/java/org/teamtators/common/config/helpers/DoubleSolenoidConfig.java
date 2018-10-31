package org.teamtators.common.config.helpers;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.teamtators.common.harness.HarnessContext;

public class DoubleSolenoidConfig implements ConfigHelper<DoubleSolenoid> {
    private int forwardChannel;
    private int reverseChannel;

    public int getForwardChannel() {
        return forwardChannel;
    }

    public void setForwardChannel(int forwardChannel) {
        this.forwardChannel = forwardChannel;
    }

    public int getReverseChannel() {
        return reverseChannel;
    }

    public void setReverseChannel(int reverseChannel) {
        this.reverseChannel = reverseChannel;
    }

    public DoubleSolenoid create(HarnessContext ctx) {
        return new DoubleSolenoid(forwardChannel, reverseChannel);
    }
}
