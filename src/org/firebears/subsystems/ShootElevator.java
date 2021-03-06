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

import org.firebears.RobotMap;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.can.CANTimeoutException;
import edu.wpi.first.wpilibj.command.PIDSubsystem;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 *
 */
public class ShootElevator extends PIDSubsystem {
    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS

    CANJaguar ElevatorJag = RobotMap.shootElevatorJag;
    AnalogChannel angleVolts = RobotMap.shootElevatorAngle;
    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    final public Preferences m_preferences;
    //Constants
    double TEST_ANGLE;// average SHOOT_ANGLE_MAX - SHOOT_ANGLE_MIN
    private final double SHOOT_ANGLE_MAX = 1.6;//1.52
    private final double SHOOT_ANGLE_MIN = 1.5;
    private final int DIST = 0;
    private final int ANGLE = 1;
    private final int m_ArrayLim = 15;
    private float[][] m_AngleArray = new float[2][m_ArrayLim];//[0 = Dist, 1 = Angle]
    
     DriverStationLCD driverLCD = null;
    DriverStationLCD.Line displayLine = DriverStationLCD.Line.kUser2;
    
    
    // Initialize your subsystem here

    public ShootElevator() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=PID
        super("ShootElevator", 4.0, 0.0, 0.0);
        
        setAbsoluteTolerance(0.2);
        getPIDController().setContinuous(false);
        LiveWindow.addActuator("ShootElevator", "PIDSubsystem Controller", getPIDController());
        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=PID

        //Get preferences and fill Distance to angleVolts array
        m_preferences = Preferences.getInstance();
        prefCalToArray();
        sortCalArray();
        TEST_ANGLE = (SHOOT_ANGLE_MAX + SHOOT_ANGLE_MIN) /2;
         driverLCD = DriverStationLCD.getInstance();

        // Use these to get going:
        // setSetpoint() -  Sets where the PID controller should move the system
        //                  to
        enable();// - Enables the PID controller.
        setInputRange(SHOOT_ANGLE_MIN,SHOOT_ANGLE_MAX);
        System.out.println("Angle SP = " + TEST_ANGLE);
        setSetpoint(TEST_ANGLE);
    }

    public void initDefaultCommand() {
        //setDefaultCommand(new ShootElevatorGoToAngle());
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND
        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND
        //shootElevateJag.changeControlMode(CANJaguar.ControlMode.kPercentVbus);
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }

    protected double returnPIDInput() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=SOURCE
        return angleVolts.getAverageVoltage();
        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=SOURCE
    }

    protected void usePIDOutput(double output) {
        try {
            ElevatorJag.setX(output);
        } catch (CANTimeoutException ex) {
            ex.printStackTrace();
        }
    }

    public void setSetpointShootAngle(double dist) {
        double angleVolts;
        angleVolts = getAngleFromDistance((float) dist);
        if(dist < .001 || dist > 5000){
        angleVolts = TEST_ANGLE;}
        System.out.println("Angle SP = " + angleVolts);
        setSetpoint(angleVolts);
    }


    public void jogAngle(double jogAmount) {
        double angleVolts;
        double testVolts = getSetpoint();
        angleVolts = getSetpoint() + jogAmount;
        
        System.out.println("Setpoint = " + testVolts + "  Jog = " + jogAmount + "New SP = " + angleVolts );
        setSetpoint(angleVolts);
    }

//    public void enablePID(){
//        enable();
//    }
//    
//    public void disablePID(){
//        disable();
//    }
    //************************Calibration Angle retrieval code*********************************
    private float getAngleFromDistance(float Dist) {//Interpolator
        float angle = 0;
        float deltaD;
        float deltaA;
        float ratioD;
        int c;

        for (c = 1; c < (m_ArrayLim); c++) {
            if (m_AngleArray[DIST][c] >= Dist && m_AngleArray[DIST][c - 1] > .001f) {//Assumes ascending distances, found first larger than input dist
                if (m_AngleArray[DIST][c - 1] < Dist) {
                    deltaD = m_AngleArray[DIST][c] - m_AngleArray[DIST][c - 1];
                    deltaA = m_AngleArray[ANGLE][c] - m_AngleArray[ANGLE][c - 1];
                    ratioD = (Dist - m_AngleArray[DIST][c - 1]) / deltaD;
                    angle = m_AngleArray[ANGLE][c - 1] + ratioD * deltaA;
                    System.out.println("interpolate");
                    return angle;
                } else if (c < m_ArrayLim - 1) {//Extrapolate down
                    deltaD = m_AngleArray[DIST][c] - m_AngleArray[DIST][c + 1];
                    deltaA = m_AngleArray[ANGLE][c] - m_AngleArray[ANGLE][c + 1];
                    ratioD = (Dist - m_AngleArray[DIST][c]) / deltaD;
                    angle = m_AngleArray[ANGLE][c] + ratioD * deltaA;
                    System.out.println("Ext Low");
                    return angle;
                }
            }
        }//END for
        //Didn't find distance in array > Dist, extrapolate  for dist > max in array
        c = m_ArrayLim - 1;
        deltaD = m_AngleArray[DIST][c] - m_AngleArray[DIST][c - 1];
        deltaA = m_AngleArray[ANGLE][c] - m_AngleArray[ANGLE][c - 1];
        ratioD = (Dist - m_AngleArray[DIST][c]) / deltaD;
        angle = m_AngleArray[ANGLE][c] + ratioD * deltaA;
        System.out.println("Ext Hi");

        return angle;
    }//END getAngleFromDistance

    //***********************Calibrate command interfaces ********************************
    //For save distance - Angle pair
    public void saveCalDistAngle(float Dist) {
        float Angle = (float) angleVolts.getAverageVoltage();
        driverLCD.println(displayLine, 2, "Distance = " + Dist + " Angle = " + Angle );
        driverLCD.updateLCD();
        sortCalArray();
        insertCal(Dist, Angle);
    }

    public void clearCalPreferences() {
        clearCalArray();
        saveCalArraySet();
    }

    public void saveCalArraySet() {
        m_preferences.putFloat("D1", m_AngleArray[DIST][0]);
        m_preferences.putFloat("A1", m_AngleArray[ANGLE][0]);
        m_preferences.putFloat("D2", m_AngleArray[DIST][1]);
        m_preferences.putFloat("A2", m_AngleArray[ANGLE][1]);
        m_preferences.putFloat("D3", m_AngleArray[DIST][2]);
        m_preferences.putFloat("A3", m_AngleArray[ANGLE][2]);
        m_preferences.putFloat("D4", m_AngleArray[DIST][3]);
        m_preferences.putFloat("A4", m_AngleArray[ANGLE][3]);
        m_preferences.putFloat("D5", m_AngleArray[DIST][4]);
        m_preferences.putFloat("A5", m_AngleArray[ANGLE][4]);
        m_preferences.putFloat("D6", m_AngleArray[DIST][5]);
        m_preferences.putFloat("A6", m_AngleArray[ANGLE][5]);
        m_preferences.putFloat("D7", m_AngleArray[DIST][6]);
        m_preferences.putFloat("A7", m_AngleArray[ANGLE][6]);
        m_preferences.putFloat("D8", m_AngleArray[DIST][7]);
        m_preferences.putFloat("A8", m_AngleArray[ANGLE][7]);
        m_preferences.putFloat("D9", m_AngleArray[DIST][8]);
        m_preferences.putFloat("A9", m_AngleArray[ANGLE][8]);
        m_preferences.putFloat("D10", m_AngleArray[DIST][9]);
        m_preferences.putFloat("A10", m_AngleArray[ANGLE][9]);
        m_preferences.putFloat("D11", m_AngleArray[DIST][10]);
        m_preferences.putFloat("A11", m_AngleArray[ANGLE][10]);
        m_preferences.putFloat("D12", m_AngleArray[DIST][11]);
        m_preferences.putFloat("A12", m_AngleArray[ANGLE][11]);
        m_preferences.putFloat("D13", m_AngleArray[DIST][12]);
        m_preferences.putFloat("A13", m_AngleArray[ANGLE][12]);
        m_preferences.putFloat("D14", m_AngleArray[DIST][13]);
        m_preferences.putFloat("A14", m_AngleArray[ANGLE][13]);
        m_preferences.putFloat("D15", m_AngleArray[DIST][14]);
        m_preferences.putFloat("A15", m_AngleArray[ANGLE][14]);

        m_preferences.save();

    }//END arrayToPref

    private void prefCalToArray() {//***used in startup initialization****
        m_AngleArray[DIST][0] = m_preferences.getFloat("D1", 0);
        m_AngleArray[ANGLE][0] = m_preferences.getFloat("A1", 0);
        m_AngleArray[DIST][1] = m_preferences.getFloat("D2", 0);
        m_AngleArray[ANGLE][1] = m_preferences.getFloat("A2", 0);
        m_AngleArray[DIST][2] = m_preferences.getFloat("D3", 0);
        m_AngleArray[ANGLE][2] = m_preferences.getFloat("A3", 0);
        m_AngleArray[DIST][3] = m_preferences.getFloat("D4", 0);
        m_AngleArray[ANGLE][3] = m_preferences.getFloat("A4", 0);
        m_AngleArray[DIST][4] = m_preferences.getFloat("D5", 0);
        m_AngleArray[ANGLE][4] = m_preferences.getFloat("A5", 0);
        m_AngleArray[DIST][5] = m_preferences.getFloat("D6", 0);
        m_AngleArray[ANGLE][5] = m_preferences.getFloat("A6", 0);
        m_AngleArray[DIST][6] = m_preferences.getFloat("D7", 0);
        m_AngleArray[ANGLE][6] = m_preferences.getFloat("A7", 0);
        m_AngleArray[DIST][7] = m_preferences.getFloat("D8", 0);
        m_AngleArray[ANGLE][7] = m_preferences.getFloat("A8", 0);
        m_AngleArray[DIST][8] = m_preferences.getFloat("D9", 0);
        m_AngleArray[ANGLE][8] = m_preferences.getFloat("A9", 0);
        m_AngleArray[DIST][9] = m_preferences.getFloat("D10", 0);
        m_AngleArray[ANGLE][9] = m_preferences.getFloat("A10", 0);
        m_AngleArray[DIST][10] = m_preferences.getFloat("D11", 0);
        m_AngleArray[ANGLE][10] = m_preferences.getFloat("A11", 0);
        m_AngleArray[DIST][11] = m_preferences.getFloat("D12", 0);
        m_AngleArray[ANGLE][11] = m_preferences.getFloat("A12", 0);
        m_AngleArray[DIST][12] = m_preferences.getFloat("D13", 0);
        m_AngleArray[ANGLE][12] = m_preferences.getFloat("A13", 0);
        m_AngleArray[DIST][13] = m_preferences.getFloat("D14", 0);
        m_AngleArray[ANGLE][13] = m_preferences.getFloat("A14", 0);
        m_AngleArray[DIST][14] = m_preferences.getFloat("D15", 0);
        m_AngleArray[ANGLE][14] = m_preferences.getFloat("A15", 0);

    }//END prefCalToArray

    //*********************Calibrate support methods*******************************
    private void insertCal(float Dist, float Angle) {
        int c;
        //Assumes ascending distances, found first larger than input dist
        for (c = 0; c < (m_ArrayLim); c++) {
            if (m_AngleArray[DIST][c] == 0) {
                m_AngleArray[DIST][c] = Dist;
                m_AngleArray[ANGLE][c] = Angle;
                return;
            }//END if
        }//END for                       
        //no zero distances
        if (Dist >= m_AngleArray[DIST][m_ArrayLim - 1]) {//Distance greater than in array
            m_AngleArray[DIST][m_ArrayLim - 1] = Dist;
            m_AngleArray[ANGLE][m_ArrayLim - 1] = Angle;
            return;
        }

        for (c = 1; c < (m_ArrayLim); c++) {
            if (m_AngleArray[0][c] > Dist) {//Assumes ascending distances, found first larger than input dist
                if (Math.abs(m_AngleArray[DIST][c] - Dist) < Math.abs(m_AngleArray[DIST][c - 1] - Dist)) {
                    m_AngleArray[DIST][c] = Dist;//c distance closest to Dist
                    m_AngleArray[ANGLE][c] = Angle;
                } else {//c -1 distance closest to Dist
                    m_AngleArray[DIST][c - 1] = Dist;
                    m_AngleArray[ANGLE][c - 1] = Angle;
                }
                return;
            }//END if                 
        } //END for       
    }//END insertCal

    private void sortCalArray() {
        float swapD;
        float swapA;

        for (int c = 0; c < (m_ArrayLim - 1); c++) {
            for (int d = 0; d < m_ArrayLim - c - 1; d++) {
                if (m_AngleArray[DIST][d] > m_AngleArray[DIST][d + 1]) /* For decreasing order use < */ {
                    swapD = m_AngleArray[DIST][d];
                    swapA = m_AngleArray[ANGLE][d];
                    m_AngleArray[DIST][d] = m_AngleArray[DIST][d + 1];
                    m_AngleArray[ANGLE][d] = m_AngleArray[ANGLE][d + 1];
                    m_AngleArray[DIST][d + 1] = swapD;
                    m_AngleArray[ANGLE][d + 1] = swapA;
                }
            }
        }
    }//END sortCalArray

    private void clearCalArray() {
        for (int c = 0; c < (m_ArrayLim); c++) {
            m_AngleArray[DIST][c] = 0;
            m_AngleArray[ANGLE][c] = 0;
        }
    }//END clear Array

    protected void PrintArray() {
        for (int c = 0; c < (m_ArrayLim); c++) {
            System.out.println("D" + c + " " + m_AngleArray[DIST][c] + "  A= " + m_AngleArray[ANGLE][c]);

        }
    }
}
