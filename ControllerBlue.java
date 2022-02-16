package org.firstinspires.ftc.teamcode;
//hello
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.configuration.ServoFlavor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;

@TeleOp(name="ControllerBlue", group="DriveModes") //USE STINKY PRESET
public class ControllerBlue extends OpMode {
    //39a5c0

    private DcMotor frontRight, frontLeft, backRight, backLeft, arm, spinner, carousel;//Instantiating the DcMotors for the wheels
    private Servo cap; //instantiating capping servo
    private boolean still, quarter;//Instantiating a boolean that will be used for the arm lock
    private int current;//Instantiating a count that will be used for the collection system

    private double x, x2, y, y2, power, power2, armPower, spinnerPower, carouselPower;//Instantiating the vars that will be used for the power and direction of the DcMotors
    private double frontRightPower, frontLeftPower, backRightPower, backLeftPower;//Instantiating the different power vars for the different DcMotors
    private double adj = 0.77; //adjustment double to account for unbalanced robots. Mecanum wheels will turn if this isnt here because they are silly
    
    double cpower = 0.5; //carousel power (for expirementing, we wanted the carousel to ramp up in speed automatically so the duck wouldn't fall off)

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
        //Setting all of the DcMotors to their current state by talking to the expansion hub
        frontRight = hardwareMap.dcMotor.get("BL"); //1
        frontLeft = hardwareMap.dcMotor.get("FL"); //1
        backRight = hardwareMap.dcMotor.get("FR"); //1
        backLeft = hardwareMap.dcMotor.get("BR"); //1
        arm = hardwareMap.get(DcMotorEx.class, "ARM"); //2
        spinner = hardwareMap.dcMotor.get("SP"); //2
        carousel = hardwareMap.dcMotor.get("CAR"); //2
        cap = hardwareMap.servo.get("CAP");

        arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); //DIFFERENT SO WE CAN HAVE THE ARM LOCK AT POSITION more details in AutoTemplate
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
            if(cpower<1){ //Sets max cpower to full power, and if it is less than this:
                cpower+= 0.004; //increase the power slightly. This should ramp up the speed slightly on each iteration of the program
            }
            carousel.setPower(cpower); //set power to this
        }else{
            cpower = 0.5; //if dpad up is released, reset cpower to 0.5 for the next time it is pressed. The carousel power is set to 0 later on in the program, so we don't need to do it here
        }
        if(gamepad1.right_trigger > 0 && cap.getPosition() < 0.756){ //if right trigger is pressed, and the cap is not at it's max position
            cap.setPosition(cap.getPosition()+0.004); //move it slightly to that point
        }
        if(gamepad1.left_trigger > 0 && cap.getPosition() > 0.004){ //same as above
            cap.setPosition(cap.getPosition()-0.004);
        }
        power = 1; //sets power variables so we can adjust how fast the robot is moving (like a sens multiplier in a video game)
        power2 = 1;
        //Setting the power and power2 to either normal speed or half speed based on the gamepad1 right bumper
        if(gamepad1.left_bumper){
            quarter = !quarter; //a bool is used here so we can just press it once and it will change, rather than hold
            //This allows us to use more delicate movement without having to have our finger on the left bumper
            //Slightly inconsistent, as you need to quickly press the button so the program doesnt detect it being held the next iteration
            //Ideally, use an int an a modulo of some sort to only check if the left bumper is pressed on every x interations
        }
        if(quarter){
            power /= 2.5;
            power2 /= 2.5;
        }
        //same as above, but this one is a simple hold multiplier with slightly different adjustments
        if(gamepad1.right_bumper){
            power /= 3;
            power2 /= 3;
        }
        
        carouselPower = 0; //sets powers to 0
        spinnerPower = 0;

        if(gamepad1.x) spinnerPower = -0.5*power; //if x is pressed, do da intake
        if(gamepad1.b || gamepad1.right_stick_button) spinnerPower = power; //if b is pressed or right stick is pressed in, outtake
        if(gamepad1.dpad_left) carouselPower = 0.4*power; //if dpad left is pressed, spin carousel slowly
        if(gamepad1.dpad_right) carouselPower = 0.65*power; //if right is pressed, spin carousel quickly
        
    //region Drive Mechanism
        //Setting the different motors to their respective power for lateral movement
        frontRight.setPower(adj*power * ( -y - x ) ); //right motors adjusted for balance
        frontLeft.setPower(power * ( y - x ) );
        backRight.setPower(adj*power * ( y - x ) );
        backLeft.setPower(power * ( y + x ) );
        /*
        HOW TO PROGRAM YOUR OWN POGGERS MECH DRIVE:
        Set all motors to move forward when your stick is forward.
        If a motor is spinning backwards, swap the red and black wires. This will reverse it's output
        Alternatively, set it to negative in the program like a bozo like we did
        Next, make sure the motors all spin the correct way to move right when the stick is right. Directions can be found in AutoTemplate in the movement methods, or on google
        Add the values together. Ezzzzz.
         */
        
        //Setting different motors to their respective power for rotational movement (SPINNA)
        frontLeft.setPower(power2 * -x2);
        frontRight.setPower(adj*power2 * -x2);
        backLeft.setPower(power2 * -x2);
        backRight.setPower(adj*power2 * x2);

    //Set more spinnnnnnaaaaas
    spinner.setPower(spinnerPower);
    carousel.setPower(carouselPower);

    //TODO: GET BOUNDS, CHANGE INCREMENTS
    if(y2<0){ //if stick is down
        still = false; //let the program know that you are boutta start moving
        arm.setPower(Math.abs(y2)*0.75); //set power to magnitude of the stick, so the harder you move it, the faster the arm goes
        arm.setTargetPosition(2100); //set target position to min position (motor was backwards, so high value = low arm)
        //you can find this position by outputting to telemetry the position of your arm, and then move it to your desired top and bottom bound. The phone will tell you what value it is at
    }
    if(y2>0){ //if stick is up
        still = false; //same as above
        arm.setPower(Math.abs(y2)*0.75); //same as above
        arm.setTargetPosition(0); //set target position to max desired
    }
    if(y2==0){ //if stick is not moving (you can add a deadzone if your controller is bad. So set the top two methods to something like if y2 < -0.05, and if y2 > 0.05
        if(!still) current = arm.getCurrentPosition(); //if this is the first time this if statement is accepted, then set the cur arm position to a variable.
        still = true; //let the program know that you are now still and have seen this if statement
        arm.setPower(1); //set arm power to max, so you can keep it still
        arm.setTargetPosition(current); //constantly run to the position that you stopped at
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
