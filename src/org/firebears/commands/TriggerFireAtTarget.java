package org.firebears.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.firebears.Robot;

/**
 * Fire all Frisbes at a visible target. Normal behavior is to only fire if a
 * target has been detected by the camera. If the <tt>manualShoot</tt> variable
 * is set to true, then push even if no target has been seen.
 */
public class TriggerFireAtTarget extends Command {

    private boolean manualShoot = true;

    public TriggerFireAtTarget() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
        requires(Robot.shooter);
        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
    }

    protected void initialize() {
    }

    // TODO If we get a sensor for the frisbee chamber, reimplement TriggerFireAtTarget and GroupAutonomousShooting
    protected void execute() {
        if (isOkToFire() && frisbeeIsInChamber()) {//TODO  TrigerFireAtTarget is not used and is inactive
//            Robot.trigger.push();
//            Robot.trigger.retract();
        }
    }

    protected boolean isFinished() {
        if (!frisbeeIsInChamber()) {
            return true;
        }
        boolean okToFire = (Robot.camera.getTargetType() != 0);
        if (manualShoot) {
            okToFire = true;
        }
        return !okToFire;
    }

    protected void end() {
    }

    protected void interrupted() {
    }

    /**
     * @return whether there is a target in view
     */
    protected boolean isOkToFire() {
        boolean okToFire = (Robot.camera.getTargetType() != 0);
        if (manualShoot) {
            okToFire = true;
        }
        return okToFire;
    }

    protected boolean frisbeeIsInChamber() {
        // TODO - get frisbee status from subsystems.
        return true;
    }
}
