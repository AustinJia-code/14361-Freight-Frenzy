//Template outline for learning/setting up a quick auton

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

@Autonomous(name="AutoTemplate") //USE STINKY PRESET, SET SO RIGHT WHEELS ARE ON SECOND LINE

public class AutoTemplate extends LinearOpMode{
    private double adj = 0.77; //used when the robot is not balanced. Forces one side of motors to spin faster so the robot doesn't rotate (unfortunate downside of mecanum wheels)
    private DcMotorEx frontRight, frontLeft, backRight, backLeft, arm, spinner, carousel; //Instantiating all DC motorex, DO NOT USE DCMotor, as you won't be able to use encoders
    private Servo cap; //capping mechanism, is instantiated so the arm can move freely
    private int temp; //temp variable for whatever we may need it for
    private double location;//Instantiating the location variable to detect where the TSE is placed
    private double right = 300, left = -300;//Instantiating the different power vars for the different DcMotors - this is pretty useless. Use one variable, and make sure the motors are spinning the right way.
    private ElapsedTime runtime = new ElapsedTime(); //variable to view runtime
    private static final String TFOD_MODEL_ASSET = "FreightFrenzy_BCDM.tflite"; //fetch the TFOD models provided by FTC
    private static final String[] LABELS = {"Ball", "Cube", "Duck", "Marker"}; //set labes so we can determine what TFOD is detected
    private static final String VUFORIA_KEY = "INSERTKEY"; //insert your key here. Examples in other files
    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;

    @Override

    public void runOpMode() throws InterruptedException {
        //Setting all of the DcMotors the their current state by talking to the expansion hub
        frontRight = hardwareMap.get(DcMotorEx.class, "BL"); //1 (hub number, frontRight is set to backLeft. This should not be the case, but it works so we didn't fix it
        frontLeft = hardwareMap.get(DcMotorEx.class, "FL"); //1
        backRight = hardwareMap.get(DcMotorEx.class, "FR"); //1
        backLeft = hardwareMap.get(DcMotorEx.class, "BR"); //1 (switched with frontRight. Make sure you do this right unlike us)
        arm = hardwareMap.get(DcMotorEx.class, "ARM"); //2 (motors on expansion hub, labelled correctly this time)
        spinner = hardwareMap.get(DcMotorEx.class, "SP"); //2
        carousel = hardwareMap.get(DcMotorEx.class, "CAR"); //2
        cap = hardwareMap.servo.get("CAP"); //1 (found in servo port of control hub)

        //set motors to run based on velocity rather than power (more consistent if the battery is differently charged)
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); //Lets the encoder know the current position is 0
        arm.setTargetPosition(0); //Needs a target position, so is set to 0, or initialization position
        arm.setPower(0.5); //Sets arm to move at half power. Too high and the gears will break, too low and it will be slow
        arm.setMode(DcMotor.RunMode.RUN_TO_POSITION); //Use mode run to position

        waitForStart();
        runtime.reset(); //resets runtime variable, for accurate runtime
        cap.setPosition(0);
        int runTo = 2; //sets a default runTo, so if the camera does not detect something, the robot will go somewhere
        initVuforia();
        initTfod();
        if (tfod != null) {
            tfod.activate(); //activates cam
            tfod.setZoom(1.25, 20.0/4.0); //sets zoom so we don't pick up junk
        }

        if(opModeIsActive()){
            cap.setPosition(0.1); //moves cap out of the way of the robot;
            boolean foundDuck = false;
            while (!foundDuck && runtime.time() < 5) { //while we havent seen our TSE and the cam hasn't been searching for very long
                if (tfod != null) { //if the camera exists (caught so we don't get errors)
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions(); //get list of what the cam sees
                    if (updatedRecognitions != null) { //if the list is populated by something (aka if the cam sees something)
                        telemetry.addData("# Object Detected", updatedRecognitions.size()); //print what we see to the phone
                        for (Recognition recognition : updatedRecognitions) { //for each thing the cam sees
                            if(recognition.getLabel().equals("Duck") || recognition.getLabel().equals("Cube")){ //if it matches the TSE (our TSE is yellow, so we trick the camera into detecting it as either a cube or a duck. Cedar Poor stuff ong. Use OpenCV instead of vuforia if you are good)
                                telemetry.addData("From left: ", recognition.getLeft()); //find where in the picture the match was
                                location = recognition.getLeft(); //set location to this value
                                foundDuck = true; //set found TSE to true
                                temp = recognition.getImageWidth(); //get the image size, used to find which third of the screen the TSE was in
                            }
                        }
                        telemetry.update(); //updates phone
                    }
                }
            }
            double left = temp/3.0; //left third of the screen
            double mid = temp/3.0*2.0; //middle third of the screen
            if(location < left) runTo = 0; //if the match was in less than this bound, we know the TSE was on left barcode. Run to floor 0
            else if(location < right) runTo = 1; //else if the location was less than middle bound, we know TSE is in middle.
            //if not either, then the robot defaults to think that the TSE was in the right third of the screen

            /***********************************   EDIT HERE   *************************************/
            //movement methods to get to the alliance hub. Examples in other autons
            switch(runTo){ //switch case of which third the TSE was in
                case 0:
                    arm.setTargetPosition(600); //if the TSE was in the left third, set height to bottom hub
                    while(arm.isBusy()){ //let the arm do its thang before you move
                    }
                    //approach the hub.
                    spinner.setPower(0.7); //drop the preloaded block
                    runFor(700); //keep droppin it bruh
                    //leave the hub
                    break;
                case 1:
                    arm.setTargetPosition(1300);
                    while(arm.isBusy()){ //let the arm do its thang before you move
                    }
                    //approach the hub.
                    spinner.setPower(0.7); //drop the preloaded block
                    runFor(700); //keep droppin it bruh
                    //leave the hub
                    break;
                case 2:
                    arm.setTargetPosition(1920);
                    while(arm.isBusy()){ //let the arm do its thang before you move
                    }
                    //approach the hub.
                    spinner.setPower(0.7); //drop the preloaded block
                    runFor(700); //keep droppin it bruh
                    //leave the hub
                    break;
            }
            arm.setPower(0); //drop le arm
            //park wherever (examples in other autons
        }
    }

    /***********************************   EDIT HERE   *************************************/

    //headers: speed multiplier, time running
    private void forward(double mult, int ms){
        frontRight.setVelocity(adj*mult * right); //adj comes in handy here. The right was moving faster than the left on our robot, so we set the right speed to move only adj% of the speed set. This way the robot moves how you want it
        frontLeft.setVelocity(mult * left); //mult changes the speed the motors go. Slower is more consistent, as there is less discrepancy in acceleration and slippage of wheels
        backRight.setVelocity(adj*mult * left);
        backLeft.setVelocity(mult * left);
        runFor(ms); //runs for this amount of time
        /* Mecanum forward (+ means forward, - means backwards)
        + +
        + +
         */
    }
    private void backward(double mult, int ms){
        frontRight.setVelocity(adj*mult * -right);
        frontLeft.setVelocity(mult * -left);
        backRight.setVelocity(adj*mult * -left);
        backLeft.setVelocity(mult * -left);
        runFor(ms);
        /*
        - -
        - -
         */
    }
    private void right(double mult, int ms){
        frontRight.setVelocity(adj*mult * -right);
        frontLeft.setVelocity(mult * left);
        backRight.setVelocity(adj*mult * left);
        backLeft.setVelocity(mult * -left);
        runFor(ms);
        /*
        + -
        - +
         */
    }
    private void left(double mult, int ms){
        frontRight.setVelocity(mult * right);
        frontLeft.setVelocity(mult * -left);
        backRight.setVelocity(mult * -left);
        backLeft.setVelocity(mult * left);
        runFor(ms);
        /*
        - +
        + -
         */
    }
    private void turnLeft(double mult, int ms){
        frontRight.setVelocity(mult * right);
        frontLeft.setVelocity(mult * -left);
        backRight.setVelocity(mult * left);
        backLeft.setVelocity(mult * -left);
        runFor(ms);
        /*
        - -
        - -
         */
    }
    private void turnRight(double mult, int ms){
        frontRight.setVelocity(mult * -right);
        frontLeft.setVelocity(mult * left);
        backRight.setVelocity(mult * -left);
        backLeft.setVelocity(mult * left);
        runFor(ms);
        /*
        + -
        + -
         */
    }
    private void FRight(double mult, int ms){
        frontLeft.setVelocity(mult* left);
        backRight.setVelocity(mult* left);
        runFor(ms);
        /*
        -
          -
         */
    }
    //Google mecanum wheel guides for more fun movement, like cornering and axis turns!! is very poggers and based
    private void runFor(int ms){
        //sleeps for given time, so the program can run. FTC sleep means keep doing what you are doing, not stop everything
        sleep(ms);
        //sets all relevant motors to stop
        frontLeft.setVelocity(0);
        frontRight.setVelocity(0);
        backLeft.setVelocity(0);
        backRight.setVelocity(0);
        spinner.setVelocity(0);
        sleep(250);
    }
    private void initVuforia() {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1"); //set name to whatever it is in the phone

        vuforia = ClassFactory.getInstance().createVuforia(parameters);
    }
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.4f; //sets the confidence the program needs to report a match. We used pretty low, as if an object is yellow, we want to report it
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 320;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
    }
}

