package org.teamtators.levitator2.commands;

import org.teamtators.common.config.Configurable;
import org.teamtators.common.control.Ramper;
import org.teamtators.common.control.Timer;
import org.teamtators.common.scheduler.Command;
import org.teamtators.common.scheduler.RobotState;
import org.teamtators.common.util.JoystickModifiers;
import org.teamtators.levitator2.TatorRobot;
import org.teamtators.levitator2.subsystems.Drive;
import org.teamtators.levitator2.subsystems.Lift;
import org.teamtators.levitator2.subsystems.OperatorInterface;

public class DriveTank extends Command implements Configurable<DriveTank.Config> {
    private final Drive drive;
    private final OperatorInterface oi;
    private final Lift lift;

    private JoystickModifiers modifiers;
    private Config config;

    private Ramper rightRamper = new Ramper();
    private Ramper leftRamper = new Ramper();
    private Timer timer = new Timer();


    public DriveTank(TatorRobot robot) {
        super("DriveTank");
        drive = robot.getSubsystems().getDrive();
        oi = robot.getSubsystems().getOperatorInterface();
        lift = robot.getSubsystems().getLift();
        requires(drive);
        validIn(RobotState.TELEOP);
    }

    @Override
    protected void initialize() {
        super.initialize();
        timer.start();
    }

    @Override
    public boolean step() {
        double left = oi.getDriveLeft();
        double right = oi.getDriveRight();

        double liftHeight = lift.getCurrentHeight();
        double scale = 1;
        double maxAcceleration;

        maxAcceleration = config.maxAcceleration;

        leftRamper.setMaxAcceleration(maxAcceleration);
        rightRamper.setMaxAcceleration(maxAcceleration);
        modifiers.scale = scale;

        left = modifiers.apply(left);
        right = modifiers.apply(right);

        double delta = timer.restart();

        leftRamper.setValue(left);
        rightRamper.setValue(right);
        leftRamper.update(delta);
        rightRamper.update(delta);

        drive.drivePowers(leftRamper.getOutput(), rightRamper.getOutput());
        return false;
    }

    @Override
    public void configure(Config config) {
        this.config = config;
        leftRamper.configure(config.leftRamper);
        rightRamper.configure(config.rightRamper);
        leftRamper.setOnlyUp(false);
        rightRamper.setOnlyUp(false);
        this.modifiers = config.modifiers;
    }

    public static class Config {
        public JoystickModifiers modifiers;
        public double maxAcceleration;
        public Ramper.Config leftRamper;
        public Ramper.Config rightRamper;
    }
}
