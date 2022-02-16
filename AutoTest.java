//basic auto to test if all methods are working as intended
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="Auto Test") //USE STINKY PRESET, SET SO RIGHT WHEELS ARE ON SECOND LINE

public class AutoTest extends LinearOpMode{
    
    private DcMotorEx frontRight, frontLeft, backRight, backLeft, arm, spinner, carousel; //Instantiating the DcMotors for the wheels

    private double x, x2, y, y2, power2, armPower, spinnerPower, carouselPower;//Instantiating the vars that will be used for the power and direction of the DcMotors
    private double right = 300, left = -300;//Instantiating the different power vars for the different DcMotors
    private ElapsedTime runtime = new ElapsedTime();
    @Override
    
    public void runOpMode() throws InterruptedException {
         //Setting all of the DcMotors the their current state by talking to the expansion hub
        frontRight = hardwareMap.get(DcMotorEx.class, "BL"); //1
        frontLeft = hardwareMap.get(DcMotorEx.class, "FL"); //1
        backRight = hardwareMap.get(DcMotorEx.class, "FR"); //1
        backLeft = hardwareMap.get(DcMotorEx.class, "BR"); //1
        arm = hardwareMap.get(DcMotorEx.class, "ARM"); //2
        spinner = hardwareMap.get(DcMotorEx.class, "SP"); //2
        carousel = hardwareMap.get(DcMotorEx.class, "CAR"); //2
    
    //set motors to run based on velocity rather than power (more consistent on charge)
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);       
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); //DIFFERENT SO WE CAN HAVE THE ARM LOCK AT POSITION
        arm.setTargetPosition(0);
        arm.setPower(0.1);
        arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        waitForStart();
        runtime.reset();
        arm.setTargetPosition(1820);
        while(arm.isBusy()){
        }
    }

    //headers: speed multiplier, time running
    private void forward(double mult, int ms){
        frontRight.setVelocity(right);
        frontLeft.setVelocity(left);
        backRight.setVelocity(left);
        backLeft.setVelocity(left);
        runFor(ms);
    }
    private void backward(double mult, int ms){
        frontRight.setVelocity(-right);
        frontLeft.setVelocity(-left);
        backRight.setVelocity(-left);
        backLeft.setVelocity(-left);
        runFor(ms);
    }
    private void right(double mult, int ms){
        frontRight.setVelocity(-right);
        frontLeft.setVelocity(left);
        backRight.setVelocity(left);
        backLeft.setVelocity(-left);
        runFor(ms);
    }
    private void left(double mult, int ms){
        frontRight.setVelocity(right);
        frontLeft.setVelocity(-left);
        backRight.setVelocity(-left);
        backLeft.setVelocity(left);
        runFor(ms);
    }
    private void turnLeft(double mult, int ms){
        frontRight.setVelocity(right);
        frontLeft.setVelocity(-left);
        backRight.setVelocity(left);
        backLeft.setVelocity(-left);
        runFor(ms);
    }
    private void turnRight(double mult, int ms){
        frontRight.setVelocity(-right);
        frontLeft.setVelocity(left);
        backRight.setVelocity(-left);
        backLeft.setVelocity(left);
        runFor(ms);
    }
    private void DBRight(double mult, int ms){
        frontRight.setVelocity(right);
        backLeft.setVelocity(left);
        runFor(ms);
    }
    private void runFor(int ms){
        sleep(ms);
        frontLeft.setVelocity(0);
        frontRight.setVelocity(0);
        backLeft.setVelocity(0);
        backRight.setVelocity(0);
        sleep(1000);
    }
}

