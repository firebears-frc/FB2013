package org.firebears.commands;

import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.firebears.Robot;
import org.firebears.RobotMap;
import org.firebears.commands.camera.ProcessingPlan;
import org.firebears.commands.camera.RectangleTargetPlan;
import org.firebears.subsystems.Lights;

/**
 * Use the Axis Camera to take a picture.
 */
public class CameraTakePicture extends Command {

    Gyro gyro = RobotMap.chassisGyro;
    static final int START = 3;
    static final int CAMERA_IS_RESET = 2;
    static final int PICTURE_TAKEN = 1;
    static final int FINISHED = 0;
    private int state = FINISHED;
    private boolean saveFiles = true;
    private ProcessingPlan plan = null;

    /**
     * @param save Whether intermediate files should be saved to the cRIO in the
     * <tt>/tmp</tt> directory.
     */
    public CameraTakePicture(boolean save) {
        this();
        this.saveFiles = save;
    }

    public CameraTakePicture() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
        requires(Robot.camera);
        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
//        plan = new RectangleTargetPlan(52, 24);
        plan = new RectangleTargetPlan(58, 16);
    }

    protected void initialize() {
        state = START;
        System.out.println("picture initialized");
    }

    protected void execute() {
        switch (state) {
            case START:
                Robot.camera.reset();
                state = CAMERA_IS_RESET;
                break;
            case CAMERA_IS_RESET:
                Robot.camera.takePicture();
                state = PICTURE_TAKEN;
                break;
            case PICTURE_TAKEN:
                Robot.camera.processImage(plan, saveFiles);
                state = FINISHED;
                log("::: CameraTakePicture: " + Robot.camera.getAllParticles().length);
                if (Robot.camera.hasPicture()) {
                    Robot.lights.lightProgram(Lights.TARGET_IN_SIGHT);
                } else {
                    Robot.lights.lightProgram(Lights.TARGET_NOT_IN_SIGHT);
                }
                break;
            case FINISHED:
                break;
            default:
                System.err.println("::: Unknown state: " + state);
                state = FINISHED;
        }
        if (gyro != null) {
            SmartDashboard.putNumber("Gyro Angle:", gyro.getAngle());
        }
        System.out.println("picture execute");
    }

    protected boolean isFinished() {
        System.out.println("picture isFinished");
        return (state == FINISHED);
    }

    protected void end() {
    }

    protected void interrupted() {
    }

    private void log(String msg) {
        if (RobotMap.DEBUG) {
            System.out.println(msg);
        }
    }
}
