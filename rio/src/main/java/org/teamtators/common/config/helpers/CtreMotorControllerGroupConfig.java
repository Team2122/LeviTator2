package org.teamtators.common.config.helpers;

import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.SpeedController;
import org.teamtators.common.hw.CtreMotorControllerGroup;
import org.teamtators.common.hw.SpeedControllerGroup;

import java.util.ArrayList;

public class CtreMotorControllerGroupConfig extends ArrayList<CtreMotorControllerConfig>
        implements ConfigHelper<CtreMotorControllerGroup> {

    public CtreMotorControllerGroup create() {
        BaseMotorController[] controllers = new BaseMotorController[this.size()];
        for (int i = 0; i < controllers.length; i++) {
            switch (this.get(i).getClass().getTypeName()) {
                case "TalonSRXConfig":
                    controllers[i] = ((TalonSRXConfig)this.get(i)).create();
                    break;
                case "VictorSPXConfig":
                    controllers[i] = ((VictorSPXConfig)this.get(i)).create();
                    break;
            }
        }
        return new CtreMotorControllerGroup(controllers);
    }
}
