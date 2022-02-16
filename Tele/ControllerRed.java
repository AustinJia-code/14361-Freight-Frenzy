//SEE CONTROLLER BLUE FOR DETAILS, ONLY DIFFERENCE IS WHICH WAY THE CAROUSEL TURNS

package org.firstinspires.ftc.teamcode;
//MOTORS FIXED
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.configuration.ServoFlavor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;

@TeleOp(name="ControllerRed", group="DriveModes") //USE STINKY
public class ControllerRed extends OpMode {
    //39a5c0

    private DcMotor frontRight, frontLeft, backRight, backLeft, arm, spinner, carousel;//Instantiating the DcMotors for the wheels
    private Servo cap;
    private boolean still, quarter;//Instantiating a boolean that will be used for the arm lock
    private int current;//Instantiating a count that will be used for the collection system

    private double x, x2, y, y2, power, power2, armPower, spinnerPower, carouselPower;//Instantiating the vars that will be used for the power and direction of the DcMotors
    private double frontRightPower, frontLeftPower, backRightPower, backLeftPower;//Instantiating the different power vars for the different DcMotors
    private double adj = 0.77;
    
    double cpower = 0.5; 

    /**
     * The init method sets up all of the variables with default values.
     * The method will be run once upon clicking the init button on the Driver Controller app.
     */
    @Override
    public void init() {

        //Setting the position vars to the default value of 0.0
        x = 0.0;
        y = 0.0;
    still = false;
        //Setting all of the power vars to the default value of 0.0
        frontRightPower = frontLeftPower = backRightPower = backLeftPower = armPower = carouselPower = spinnerPower = 0.0;
        //Setting all of the DcMotors the their current state by talking to the expansion hub
        frontRight = hardwareMap.dcMotor.get("BL"); //1
        frontLeft = hardwareMap.dcMotor.get("FL"); //1
        backRight = hardwareMap.dcMotor.get("FR"); //1
        backLeft = hardwareMap.dcMotor.get("BR"); //1
        arm = hardwareMap.get(DcMotorEx.class, "ARM"); //2
        spinner = hardwareMap.dcMotor.get("SP"); //2
        carousel = hardwareMap.dcMotor.get("CAR"); //2
        cap = hardwareMap.servo.get("CAP");

        arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); //DIFFERENT SO WE CAN HAVE THE ARM LOCK AT POSITION
        arm.setTargetPosition(0);
        arm.setPower(0.1);
        arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        cap.setPosition(0);
    }
    /**
     * The loop method runs infinitely once the play button on the Driver Controller has been pressed.
     * The loop only stops once the play button on the Driver Controller has been pressed again.
     */
    @Override
    public void loop() {
        x = gamepad1.left_stick_x;//Setting the x var to the current state of the gamepad1 left stick x value (this is the robots horizontal movement)
        x2 = gamepad1.right_stick_x;//Setting the x2 var to the current state of the gamepad1 right stick x value (this is the robots rotational movement)
        y = gamepad1.left_stick_y;//Setting the y var to the current state of the gamepad1 left stick y value (this is the robots vertical movement)
        y2 = gamepad1.right_stick_y;//arm movement
        
        if(gamepad1.dpad_up){
            if(cpower>-1){
                cpower-= 0.004;
            }
            carousel.setPower(cpower);
        }else{
            cpower = -0.5;
        }
        if(gamepad1.right_trigger > 0 && cap.getPosition() < 0.756){
            cap.setPosition(cap.getPosition()+0.004);
        }
        if(gamepad1.left_trigger > 0 && cap.getPosition() > 0.004){
            cap.setPosition(cap.getPosition()-0.004);
        }
        power = 1;
        power2 = 1;
        //Setting the power and power2 to either normal speed or half speed based on the gamepad1 right bumper
        if(gamepad1.left_bumper){
            quarter = !quarter;
        }
        
        if(quarter){
            power /= 2.5;
            power2 /= 2.5;
        }

        if(gamepad1.right_bumper){
            power /= 3;
            power2 /= 3;
        }
        
        carouselPower = 0;
        spinnerPower = 0;

        if(gamepad1.x) spinnerPower = -0.5*power;
        if(gamepad1.b || gamepad1.right_stick_button) spinnerPower = power;
        if(gamepad1.dpad_left) carouselPower = 0.4*power;
        if(gamepad1.dpad_right) carouselPower = 0.65*power;
        
    //region Drive Mechanism
        //Setting the different motors to their respective power for lateral movement
        frontRight.setPower(adj*power * ( -y - x ) );
        frontLeft.setPower(power * ( y - x ) );
        backRight.setPower(adj*power * ( y - x ) );
        backLeft.setPower(power * ( y + x ) );
        
        //Setting different motors to their respective power for rotational movement
        frontLeft.setPower(power2 * -x2);
        frontRight.setPower(adj*power2 * -x2);
        backLeft.setPower(power2 * -x2);
        backRight.setPower(adj*power2 * x2);

    //Spinny homies
    spinner.setPower(spinnerPower);
    carousel.setPower(-carouselPower);

    //TODO: GET BOUNDS, CHANGE INCREMENTS
    //if(!locked && !arm.isBusy() && y2 > 0 && (arm.getCurrentPosition()+y2*10) > 0) arm.setTargetPosition(arm.getCurrentPosition()+(int)(y2*10));
    //if(!locked && !arm.isBusy() && y2 < 0 && (arm.getCurrentPosition()+y2*10) < 1920) arm.setTargetPosition(arm.getCurrentPosition()+(int)(y2*10));
    if(y2<0){
        still = false;
        arm.setPower(Math.abs(y2)*0.75);
        arm.setTargetPosition(2100);
    }
    if(y2>0){
        still = false;
        arm.setPower(Math.abs(y2)*0.75);
        arm.setTargetPosition(0);
    }
    if(y2==0){
        if(!still) current = arm.getCurrentPosition();
        still = true;
        arm.setPower(1);
        arm.setTargetPosition(current);
    }
    
        //endregion

        //region Telemetry Data

        //Displaying arm position
        telemetry.addData("Arm position", arm.getCurrentPosition());
        telemetry.addData("Target", arm.getTargetPosition());
        telemetry.addData("SERVO POS", cap.getPosition());
        //endregion
    }
}
