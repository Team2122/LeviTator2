package org.teamtators.common.config.helpers;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.Timer;
import org.teamtators.common.harness.HarnessContext;

public class VictorSPXConfig extends CtreMotorControllerConfig implements ConfigHelper<WPI_VictorSPX> {
    public static int REQUIRED_FIRMWARE = 0x0308; // VictorSPX firmware 3.8

    public WPI_VictorSPX create(HarnessContext ctx) {
        super.validate();
        WPI_VictorSPX motor;
        if (super.logTiming)
            motor = new WPI_VictorSPX(id) {
                @Override
                public void set(ControlMode mode, double demand0, DemandType demand1Type, double demand1) {
                    double startTime = Timer.getFPGATimestamp();
                    super.set(mode, demand0, demand1Type, demand1);
                    double elapsedTime = Timer.getFPGATimestamp() - startTime;
                    if (elapsedTime > 0.008) {
                        System.out.println("VictorSPX.set call time: " + elapsedTime);
                    }
                }
            };
        else
            motor = new WPI_VictorSPX(id);
        super.configure(motor);
        super.checkVersion(motor, REQUIRED_FIRMWARE);

        return motor;
    }
}
