package org.teamtators.common.tester.components;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import org.teamtators.common.controllers.LogitechF310;
import org.teamtators.common.hw.CtreMotorControllerGroup;
import org.teamtators.common.tester.ManualTest;

public class CtreMotorControllerGroupTest extends ManualTest {

    private final CtreMotorControllerGroup group;
    private BaseMotorController controller;
    private int selected = 0;
    private double axisValue;

    public CtreMotorControllerGroupTest(String name, CtreMotorControllerGroup group) {
        super(name);
        this.group = group;
    }

    @Override
    public void start() {
        printTestInstructions("Press 'A' to cycle motor. Press 'B' to select all motors. Push joystick in direction to move (forward +, backward -)");
        selected = 0;
        controller = group.getSpeedControllers()[selected];
        axisValue = 0;
    }

    @Override
    public void onButtonDown(LogitechF310.Button button) {
        switch (button) {
            case A:
                int max = group.getSpeedControllers().length - 1;
                if(selected + 1 > max) {
                    selected = 0;
                } else {
                    selected++;
                }
                printTestInfo("Selected motor {}", selected);
                group.disableFollowerMode(); //to reset all of them
                controller = group.getSpeedControllers()[selected];
                controller.set(ControlMode.PercentOutput, 0);
                break;
            case B:
                group.enableFollowerMode();
                controller = group.getMaster();
                printTestInfo("Selected master");
                break;
        }
    }

    @Override
    public void stop() {
        group.enableFollowerMode();
    }

    @Override
    public void update(double delta) {
        controller.set(ControlMode.PercentOutput, axisValue);
    }

    @Override
    public void updateAxis(double value) {
        axisValue = value;
    }
}
