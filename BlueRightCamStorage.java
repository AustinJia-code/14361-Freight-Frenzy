//blue sides, drops block, parks in storage
//SEE AUTOTEMPLATE FOR COMMENTS AND DETAILS

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

@Autonomous(name="BlueRightCamStorage") //USE STINKY PRESET, SET SO RIGHT WHEELS ARE ON SECOND LINE

public class BlueRightCamStorage extends LinearOpMode{
    
    private double adj = 0.77;
    private DcMotorEx frontRight, frontLeft, backRight, backLeft, arm, spinner, carousel; //Instantiating the DcMotors for the wheels
    private Servo cap;
    private int temp;
    private double x, x2, y, y2, power2, armPower, spinnerPower, carouselPower, location;//Instantiating the vars that will be used for the power and direction of the DcMotors
    private double right = 300, left = -300;//Instantiating the different power vars for the different DcMotors
    private ElapsedTime runtime = new ElapsedTime();
    private static final String TFOD_MODEL_ASSET = "FreightFrenzy_BCDM.tflite";
    private static final String[] LABELS = {"Ball", "Cube", "Duck", "Marker"};
    private static final String VUFORIA_KEY = "AU4yqnP/////AAABmUrsC1ZZo0fZvb9tj1/FmOFK+FTB8Ce9EH5kaPByR9GqcsR99dBl4msRfssM90QFf4deWKUXG0/PQRHCPxpm4q9rbbV7KWNKH0j/UhLdGM2iTMMtKR5GW9EPsgRcA38mA4q0l5Ei2mCgjAehCJgTh0z+/xWOO7C4AhWZAxZJ1JDNBKZ9xG79rqrLIseiNwkhZmnqJDWMTIkGZNwYHyviAOREuFxbSdTnuW34cK38JgmRaAsqe6775geK1F+44HIm+nBSWXJNQChCg0tKjl9sAvI9ek43DVFlr0pKskAeZGL+PK20K5MvSb8zuM87I8Z7cAHsRtGf+UbEZWw1uW1N4I2+tGmik+S2tLgRhihWWYyQ";
    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;

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
            cap = hardwareMap.servo.get("CAP");
        
        //set motors to run based on velocity rather than power (more consistent on charge)
            frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);       
            backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); //DIFFERENT SO WE CAN HAVE THE ARM LOCK AT POSITION
            arm.setTargetPosition(0);
            arm.setPower(0.5);
            arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    
            waitForStart();
            runtime.reset();
            cap.setPosition(0);
            int runTo = 2;
        initVuforia();
        initTfod();
        if (tfod != null) {
            tfod.activate();
            tfod.setZoom(1.25, 20.0/4.0);
        }
        if(opModeIsActive()){
            cap.setPosition(0.1);
            boolean foundDuck = false;
            while (!foundDuck && runtime.time() < 5) {
                if (tfod != null) {
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                      telemetry.addData("# Object Detected", updatedRecognitions.size());
                      for (Recognition recognition : updatedRecognitions) {
                         if(recognition.getLabel().equals("Duck") || recognition.getLabel().equals("Cube")){
                             telemetry.addData("From left: ", recognition.getLeft());
                             location = recognition.getLeft();
                             foundDuck = true;
                             temp = recognition.getImageWidth();
                         }
                      }
                      telemetry.update();
                    }
                }
            }
            double left = temp/3.0;
            double mid = temp/3.0*2.0;
            if(location < left) runTo = 0;
            else if(location < right) runTo = 1;
    
            /***********************************   EDIT HERE   *************************************/
            
            FLeft(3, 3250);
            //left(2, 1000);
            turnLeft(2, 500);
            switch(runTo){
                case 0:
                    arm.setTargetPosition(600);
                    while(arm.isBusy()){
                    }
                    forward(2.5, 600);
                    spinner.setPower(0.8);
                    runFor(800);
                    backward(2.5, 750);
                    break;
                case 1:
                    arm.setTargetPosition(1300);
                    while(arm.isBusy()){
                    }
                    forward(2.5, 700);
                    spinner.setPower(0.8);
                    runFor(800);
                    backward(2.5, 850);
                    break;
                case 2:
                    arm.setTargetPosition(1920);
                    while(arm.isBusy()){
                    }
                    forward(2.5, 800);
                    spinner.setPower(0.8);
                    runFor(800);
                    backward(2.5, 950);
                            break;
                    }
            arm.setPower(0);
            backward(2, 1700);
            turnLeft(2, 2400);
            backward(2.5, 4800);
            carousel.setVelocity(150);
            runFor(2300);
            right(3, 2800);
            backward(3, 1000);
        }
    }

    /***********************************   EDIT HERE   *************************************/
    
    //headers: speed multiplier, time running
    private void forward(double mult, int ms){
        frontRight.setVelocity(mult * right);
        frontLeft.setVelocity(mult * left);
        backRight.setVelocity(mult * left);
        backLeft.setVelocity(mult * left);
        runFor(ms);
    }
    private void backward(double mult, int ms){
        frontRight.setVelocity(adj*mult * -right);
        frontLeft.setVelocity(mult * -left);
        backRight.setVelocity(adj*mult * -left);
        backLeft.setVelocity(mult * -left);
        runFor(ms);
    }
    private void right(double mult, int ms){
        frontRight.setVelocity(adj*mult * -right);
        frontLeft.setVelocity(mult * left);
        backRight.setVelocity(adj*mult * left);
        backLeft.setVelocity(mult * -left);
        runFor(ms);
    }
    private void left(double mult, int ms){
        frontRight.setVelocity(mult * right);
        frontLeft.setVelocity(mult * -left);
        backRight.setVelocity(mult * -left);
        backLeft.setVelocity(mult * left);
        runFor(ms);
    }
    private void turnLeft(double mult, int ms){
        frontRight.setVelocity(mult * right);
        frontLeft.setVelocity(mult * -left);
        backRight.setVelocity(mult * left);
        backLeft.setVelocity(mult * -left);
        runFor(ms);
    }
    private void turnRight(double mult, int ms){
        frontRight.setVelocity(mult * -right);
        frontLeft.setVelocity(mult * left);
        backRight.setVelocity(mult * -left);
        backLeft.setVelocity(mult * left);
        runFor(ms);
    }
    private void FRight(double mult, int ms){
        frontLeft.setVelocity(mult* left);
        backRight.setVelocity(mult* left);
        runFor(ms);
    }
    private void FLeft(double mult, int ms){
        frontRight.setVelocity(mult* right);
        backLeft.setVelocity(mult* left);
        runFor(ms);
    }
    private void runFor(int ms){
        sleep(ms);
        frontLeft.setVelocity(0);
        frontRight.setVelocity(0);
        backLeft.setVelocity(0);
        backRight.setVelocity(0);
        spinner.setVelocity(0);
        carousel.setVelocity(0);
        sleep(250);
    }
    private void initVuforia() {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        vuforia = ClassFactory.getInstance().createVuforia(parameters);
    }
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
            "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
       tfodParameters.minResultConfidence = 0.4f;
       tfodParameters.isModelTensorFlow2 = true;
       tfodParameters.inputSize = 320;
       tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
       tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
    }
}

