// RobotBuilder Version: 0.0.2
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in th future.
package org.firebears.subsystems;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.can.CANTimeoutException;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.firebears.RobotMap;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 *
 */
public class Shooter extends Subsystem {

    CANJaguar spinnerFrontJag = RobotMap.shooterWheelFrontJag;
    CANJaguar spinnerRearJag = RobotMap.shooterWheelRearJag;

    public Shooter() {
        super("Shooter");
    }

    public void initDefaultCommand() {
    }

    public void setSpeed(double speed) {
        try {
            if (spinnerFrontJag!=null) { spinnerFrontJag.setX(speed); } else { System.out.println("Front jag missing"); }
            if (spinnerRearJag!=null) { spinnerRearJag.setX(speed); } else { System.out.println("Rear jag missing"); }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
