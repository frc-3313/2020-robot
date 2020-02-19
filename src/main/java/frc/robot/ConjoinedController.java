package frc.robot;

import edu.wpi.first.wpilibj.Joystick;

/**
 * ConjoinedController
 */
public class ConjoinedController {
    private Joystick joy1;
    private Joystick joy2;

    ConjoinedController(int joy1Port, int joy2Port) {
        joy1 = new Joystick(joy1Port);
        joy2 = new Joystick(joy2Port);
    }

    public boolean getRawButton(int buttonId) {
        return joy1.getRawButton(buttonId) || joy2.getRawButton(buttonId);
    }

    public double getRawAxis(int axisId) {
        return joy1.getRawAxis(axisId);
    }
}