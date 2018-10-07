package org.teamtators.levitator2.subsystems;


import edu.wpi.first.wpilibj.Solenoid;
import org.teamtators.common.config.Configurable;
import org.teamtators.common.config.helpers.SolenoidConfig;
import org.teamtators.common.scheduler.Subsystem;
import org.teamtators.common.tester.ManualTestGroup;
import org.teamtators.common.tester.components.SolenoidTest;


public class Climber extends Subsystem implements Configurable<Climber.Config> {
    private Solenoid forkSolenoid;
    private Solenoid hookSolenoid;

    public Climber() {
        super("Climber");
    }

    public void extendFork() {
        forkSolenoid.set(true);
    }

    public void extendHook() {
        hookSolenoid.set(true);
    }

    @Override
    public ManualTestGroup createManualTests() {
        ManualTestGroup tests = super.createManualTests();
        tests.addTest(new SolenoidTest("forkRelease", forkSolenoid));
        tests.addTest(new SolenoidTest("hookRelease", hookSolenoid));
        return tests;
    }

    public void configure(Config config) {
        this.forkSolenoid = config.forkSolenoid.create();
        this.hookSolenoid = config.hookSolenoid.create();
    }

    public static class Config {
        public SolenoidConfig forkSolenoid;
        public SolenoidConfig hookSolenoid;
    }
}
