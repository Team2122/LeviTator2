package org.teamtators.common.tester.components;

import org.teamtators.common.controllers.LogitechF310;
import org.teamtators.common.hw.SRXEncoder;
import org.teamtators.common.tester.ManualTest;

public class SRXEncoderTest extends ManualTest {
    private SRXEncoder encoder;

    public SRXEncoderTest(String name, SRXEncoder encoder) {
        super(name);
        this.encoder = encoder;
    }

    @Override
    public void start() {
        printTestInstructions("Press 'A' to display the current values, 'B' to reset the encoder values");
    }

    @Override
    public void onButtonDown(LogitechF310.Button button) {
        if (button == LogitechF310.Button.B) {
            encoder.reset();
            printTestInfo("Encoder reset");
        } else if (button == LogitechF310.Button.A) {
            printTestInfo(String.format("Distance: %.3f (ticks: %d), Rate: %.3f",
                    encoder.getDistance(), encoder.get(), encoder.getVelocity()));
        }
    }
}
