package org.teamtators.levitator2.commands;

import org.teamtators.common.config.Configurable;
import org.teamtators.common.scheduler.Command;
import org.teamtators.levitator2.subsystems.Picker;

public class PickerPick extends Command implements Configurable<PickerPick.Config>{

    private Picker picker;
    private Config config;

    public PickerPick(Picker picker) {
        super("PickerPick");
        this.picker = picker;
    }

    protected void finish(boolean interrupted){
        boolean cubeDetected = picker.getLaserDistance() < config.cubeDistance;
        super.finish(interrupted);
        picker.setRollersPower(0,0);
        picker.setMandibles(Picker.Position.Close);
        if(interrupted && cubeDetected){
            picker.setCubeState(Picker.CubeState.BAD_PICK);
        } else {
            picker.setCubeState(Picker.CubeState.SAFE);
        }
    }

    @Override
    protected void initialize() {
        super.initialize();
        picker.setMandibles(Picker.Position.Pick);
        picker.setRollersPower(config.rollerPower, config.rollerPower);
    }

    @Override
    public boolean step() {
        return picker.isCubeDetected();
    }

    @Override
    public void configure(Config config) {
        this.config = config;
    }

    public static class Config{
        public double rollerPower;
        public double cubeDistance;
    }
}
