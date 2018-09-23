package org.teamtators.levitator2.commands;

import org.teamtators.common.config.Configurable;
import org.teamtators.common.control.BooleanSampler;
import org.teamtators.common.control.Timer;
import org.teamtators.common.scheduler.Command;
import org.teamtators.common.scheduler.RobotState;
import org.teamtators.levitator2.subsystems.Picker;

public class PickerPick extends Command implements Configurable<PickerPick.Config> {

    private Picker picker;
    private Config config;
    private BooleanSampler finishSampler = new BooleanSampler(() ->
            picker.isCubeDetected() && picker.getLaserDistance() < config.cubeInDistance
    );

    private Timer timer1 = new Timer();
    private Timer timer2 = new Timer();
    private Timer timer3 = new Timer();

    public PickerPick(Picker picker) {
        super("PickerPick");
        this.picker = picker;
        validIn(RobotState.TELEOP);
    }

    @Override
    protected void initialize() {
        super.initialize();
        picker.setMandibles(Picker.Position.Pick);
        picker.setRollersPower(config.rollerPower, config.rollerPower);
        state = State.Waiting;
        unjamRight = true;
    }

    enum State {
        Waiting,
        Clamp,
        Unjam,
        Finished
    }

    State state;
    boolean unjamRight;

    @Override
    public boolean step() {
        if (picker.getLaserDistance() < config.cubeCloseDistance) {
            if (state == State.Waiting) {
                state = State.Clamp;
                logger.info("clamp");
                timer1.start();
                timer2.stop();
            }
        } else if (state != State.Waiting) {
            state = State.Waiting;
            logger.info("back to waiting");
            timer1.stop();
        }
        if (timer1.hasPeriodElapsed(config.t1) && timer2.hasPeriodElapsed(config.t3) && state == State.Clamp) {
            state = State.Unjam;
            logger.info("unjam");
            timer2.start();
        }
        if (timer2.hasPeriodElapsed(config.t2) && state == State.Unjam) {
            state = State.Clamp;
            unjamRight = !unjamRight;
            logger.info("back to clamp");
        }
        picker.setMandibles((state == State.Clamp || state == State.Unjam || state == State.Finished) ?
                Picker.Position.Close : Picker.Position.Pick);
        if (state == State.Unjam) {
            if (unjamRight) {
                picker.setRollersPower(config.rollerPower, -config.rollerPower);
            } else {
                picker.setRollersPower(-config.rollerPower, config.rollerPower);
            }
        } else {
            picker.setRollersPower(config.rollerPower, config.rollerPower);
        }
//            if (!timer1.isRunning() && !(timer2.isRunning() || timer3.isRunning())) {
//                logger.info("Switching to T1");
//                timer1.start();
//            }
//            if (timer1.hasPeriodElapsed(config.t1) && !timer2.isRunning()) {
//                logger.info("Switching to T2");
//                timer1.stop();
//                timer2.start();
//            } else {
//                if (timer2.isRunning() && !timer2.hasPeriodElapsed(config.t2)) {
//                    logger.info("In T2");
//                    picker.setMandibles(Picker.Position.Pick);
//                } else if (timer2.isRunning() && timer2.hasPeriodElapsed(config.t2)) {
//                    logger.info("Switching to T3, {}", timer2.get());
//                    timer2.stop();
//                    timer3.start();
//                } else if (timer3.isRunning() && !timer3.hasPeriodElapsed(config.t3)) {
//                    logger.info("In T3");
//                    picker.setMandibles(Picker.Position.Close);
//                } else if (timer3.isRunning() && timer3.hasPeriodElapsed(config.t3)) {
//                    logger.info("Switching to T2, {}", timer3.get());
//                    timer3.stop();
//                    timer2.start();
//                } else {
//                    picker.setMandibles(Picker.Position.Close);
//                }
//            }
//        }
        return finishSampler.get();
    }

    protected void finish(boolean interrupted) {
        boolean cubeDetected = picker.getLaserDistance() < config.cubeDetectDistance;
        super.finish(interrupted);
        picker.setRollersPower(0, 0);
        picker.setMandibles(Picker.Position.Close);
        if (interrupted && cubeDetected) {
            picker.setCubeState(Picker.CubeState.BAD_PICK);
        } else {
            picker.setCubeState(interrupted ? Picker.CubeState.SAFE_NOCUBE : Picker.CubeState.SAFE_CUBE);
        }
    }

    @Override
    public void configure(Config config) {
        this.config = config;
        finishSampler.configure(config.sampler);
    }

    public static class Config {
        public double rollerPower;
        public double cubeDetectDistance;
        public double cubeInDistance;
        public BooleanSampler.Config sampler;
        public double cubeCloseDistance;
        public double t1;
        public double t2;
        public double t3;
    }
}
