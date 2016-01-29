package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

public class TeleOpRedBlue extends OpMode {

    DcMotor motorRight;
    DcMotor motorLeft;

    public static DcMotor motorArm;
    public static boolean armUp;
    //public Runnable armStopper;
    //public double armPos

    DcMotor motorSlide;

    double scaleFactor = 1.0;

    Servo front1;
    Servo front2;

    Servo forwardBack;
    Servo rightLeft;
    Servo clawGrab;

    //Motor values
    float left = 0.0f;
    float right = 0.0f;
    float slide = 0.0f;
    float arm = 0.0f;

    //Servo values
    double UD = 0.0;
    double LR = 0.0;
    double grabPos = 0.0;
    double frontOne = 0.0;
    double frontTwo = 0.0;

    boolean outL = false;
    boolean outR = false;

    String gamepadOne;
    String gamepadTwo;

    public TeleOpRedBlue() {

    }

    @Override
    public void init() {
        motorRight = hardwareMap.dcMotor.get("motor_3");
        motorLeft = hardwareMap.dcMotor.get("motor_4");
        motorArm = hardwareMap.dcMotor.get("motor_1");
        motorSlide = hardwareMap.dcMotor.get("motor_2");
        front1 = hardwareMap.servo.get("servo_1");
        front2 = hardwareMap.servo.get("servo_2");
        rightLeft = hardwareMap.servo.get("servo_3");
        forwardBack = hardwareMap.servo.get("servo_4");
        clawGrab = hardwareMap.servo.get("servo_5");
        motorLeft.setDirection(DcMotor.Direction.REVERSE);
        grabPos = clawGrab.getPosition();
        rightLeft.setPosition(0.00);
        forwardBack.setPosition(0.50);
        front1.setPosition(1.0);
        front2.setPosition(0.0);
        new ArmStopper().start();
        //armStopper = new ArmStopper();
    }

    /*
     * This method will be called repeatedly in a loop
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
     */
    @Override
    public void loop() {

        UD = forwardBack.getPosition();
        LR = rightLeft.getPosition();
        frontOne = front1.getPosition();
        frontTwo = front2.getPosition();
        grabPos = clawGrab.getPosition();

        if (arm < 0.1 && arm > -0.1)
            armUp = true;
        else
            armUp = false;

        if(updatedGamepads()) {
            left = gamepad1.left_stick_y;
            right = gamepad1.right_stick_y;
            right = Range.clip(right, -1, 1);
            left = Range.clip(left, -1, 1);
            right = (float) scaleInput(right) * (float) scaleFactor;
            left = (float) scaleInput(left) * (float) scaleFactor;
            motorRight.setPower(right);
            motorLeft.setPower(left);

            if(gamepad1.left_trigger != 0.0) {
                scaleFactor = 0.75;
            }

            if(gamepad1.y)
                stop();

            if(gamepad1.right_trigger != 0.0) {
                scaleFactor = 1.0;
            }


            if(gamepad2.dpad_up) {
                slide = 1.0f;
                motorSlide.setPower(slide);
            }else if(gamepad2.dpad_down) {
                slide = -1.0f;
                motorSlide.setPower(slide);
            } else {
                slide = 0.0f;
                motorSlide.setPower(slide);
            }

            arm = -gamepad2.left_stick_y;
            arm = Range.clip(arm, -1, 1);
            arm = (float) scaleInput(arm);
            arm = arm * 0.75f * (float) scaleFactor;
            if(arm < 0)
                arm *= 0.5;
            motorArm.setPower(arm);
            if(gamepad2.left_bumper) {
                frontOne -= 0.01;
                front1.setPosition(Range.clip(frontOne, 0.0, 1.0));
            }
            if(gamepad2.left_trigger != 0.0) {
                frontOne += 0.01;
                front1.setPosition(Range.clip(frontOne, 0.0, 1.0));
            }

            if(gamepad2.right_bumper) {
                frontTwo += 0.01;
                front2.setPosition(Range.clip(frontTwo, 0.0, 1.0));
            }
            if(gamepad2.right_trigger != 0.0) {
                frontTwo -= 0.01;
                front2.setPosition(Range.clip(frontTwo, 0.0, 1.0));
            }

            if(gamepad2.right_stick_x > 0.5f) {
                UD -= 0.01;
                forwardBack.setPosition(Range.clip(UD, 0.0, 1.0));
            } else if(gamepad2.right_stick_x < -0.5f){
                UD += 0.01;
                forwardBack.setPosition(Range.clip(UD, 0.0, 1.0));
            }

            if(gamepad2.right_stick_y > 0.5f) {
                LR -= 0.01;
                rightLeft.setPosition(Range.clip(LR, 0.0, 1.0));
            } else if(gamepad2.right_stick_y < -0.5f){
                LR += 0.01;
                rightLeft.setPosition(Range.clip(LR, 0.0, 1.0));
            }

            if(gamepad2.b) {
                grabPos -= 0.01;
                clawGrab.setPosition(Range.clip(grabPos, 0.0, 1.0));
            }
            if(gamepad2.a) {
                grabPos += 0.01;
                clawGrab.setPosition(Range.clip(grabPos, 0.0, 1.0));
            }
        }

        telemetry.addData("Text", "*** Robot Data***");
        telemetry.addData("LR", "LR  pwr: " + String.format("%.2f", LR));
        telemetry.addData("UD", "UD pwr: " + String.format("%.2f", UD));
        telemetry.addData("F1", "F1 pwr: " + String.format("%.2f", frontOne));
        telemetry.addData("F2", "F2 pwr: " + String.format("%.2f", frontTwo));
        telemetry.addData("Power", "ScaleFactor: " + String.format("%.2f", scaleFactor));
    }

    /*
     * Code to run when the op mode is first disabled goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
     */
    @Override
    public void stop() {
        motorArm.setPower(0.0);
        motorSlide.setPower(0.0);
        motorLeft.setPower(0.0);
        motorRight.setPower(0.0);
        clawGrab.close();
        rightLeft.close();
        forwardBack.close();
        front1.close();
        front2.close();
        motorArm.close();
        motorSlide.close();
        motorLeft.close();
        motorRight.close();
    }


    /*
     * This method scales the joystick input so for low joystick values, the
     * scaled value is less than linear.  This is to make it easier to drive
     * the robot more precisely at slower speeds.
     */
    double scaleInput(double dVal) {
        double[] scaleArray = {0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
                0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00};

        // get the corresponding index for the scaleInput array.
        int index = (int) (dVal * 16.0);

        // index should be positive.
        if (index < 0) {
            index = -index;
        }

        // index cannot exceed size of array minus 1.
        if (index > 16) {
            index = 16;
        }

        // get value from the array.
        double dScale = 0.0;
        if (dVal < 0) {
            dScale = -scaleArray[index];
        } else {
            dScale = scaleArray[index];
        }

        // return scaled value.
        return dScale;
    }
    boolean updatedGamepads() {
        return (gamepadOne != gamepad1.toString() || gamepadTwo != gamepad2.toString());
    }
}

class ArmStopper extends Thread {
    public void run() {
        while(true) {
            if(TeleOpRedBlue.armUp) {
                TeleOpRedBlue.motorArm.setPower(0.1);
            }
        }
    }
}
