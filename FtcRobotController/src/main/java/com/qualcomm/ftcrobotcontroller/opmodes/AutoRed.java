/* Copyright (c) 2014 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;


public class AutoRed extends OpMode {

    final static double MOTOR_POWER = 0.15; // Higher values will cause the robot to move faster
    final static double HOLD_IR_SIGNAL_STRENGTH = 0.20; // Higher values will cause the robot to follow closer
    final static double LIGHT_THRESHOLD = 0.5;

    static double left, right = 0.0;
    double UD = 0.0;
    double LR = 0.0;
    double grabPos = 0.0;

    DcMotor motorRight;
    DcMotor motorLeft;
    public static DcMotor motorArm;
    public static boolean armUp = false;
    DcMotor motorSlide;

    Servo forwardBack;
    Servo rightLeft;
    Servo clawGrab;
    /**
     * Constructor
     */
    public AutoRed() {

    }

    /*
     * Code to run when the op mode is first enabled goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
     */
    @Override
    public void init() {
        //Setup motor and reverse direction of left motor
        motorRight = hardwareMap.dcMotor.get("motor_3");
        motorLeft = hardwareMap.dcMotor.get("motor_4");
        motorLeft.setDirection(DcMotor.Direction.REVERSE);
        motorArm = hardwareMap.dcMotor.get("motor_1");
        motorSlide = hardwareMap.dcMotor.get("motor_2");
        rightLeft = hardwareMap.servo.get("servo_3");
        forwardBack = hardwareMap.servo.get("servo_4");
        clawGrab = hardwareMap.servo.get("servo_5");
        grabPos = clawGrab.getPosition();
        while(grabPos < 0.9) {
            grabPos += 0.1;
            clawGrab.setPosition(grabPos);
        }
        rightLeft.setPosition(0.2);
        forwardBack.setPosition(0.20);
        new ArmStopper().start();
    }

    @Override
    public void loop() {
        if (this.time < 6.5d) {
            goForward(-1.0);
        } else if(this.time < 8.0d){
            turnRight(1.0);
        } else if(this.time < 11d) {
            goForward(-1.0);
        } else if(this.time < 14d) {
            turnRight(1.0);
            armUp = true;
            motorArm.setPower(0.5);
            if(this.time > 11.2d) {
                motorArm.setPower(0.1);
            }
        } else if(this.time < 16) {
            goForward(1.0);
            motorSlide.setPower(1.0);
        } else if(this.time < 18) {
            stopMotors();
            motorSlide.setPower(0.0);
            forwardBack.setPosition(0.75);
        } else if(this.time < 22) {
            motorSlide.setPower(-1.0);
            forwardBack.setPosition(0.5);
        }
        telemetry.addData("Text", "*** Robot Data***");
        telemetry.addData("..", "time: " + String.format("%.2f", this.time));
    }


    public void goForward(double power) {
        motorRight.setPower(-power);
        motorLeft.setPower(-power);
    }

    public void turnRight(double power) {
        motorRight.setPower(-power);
        motorLeft.setPower(power);
    }

    public void curve(double power1, double power2) {
        motorRight.setPower(power1);
        motorLeft.setPower(power2);
    }

    public void turnLeft(double power) {
        motorRight.setPower(power);
        motorLeft.setPower(-power);
    }

    public void stopMotors() {
        motorRight.setPower(0.0);
        motorLeft.setPower(0.0);
    }

    @Override
    public void stop() {
        //Relax motor power
        motorRight.setPower(0.0);
        motorLeft.setPower(0.0);
    }

}
