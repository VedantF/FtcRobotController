package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
//import org.firstinspires.ftc.teamcode.Sample;

@TeleOp(name = "ClawIntake")
public class ClawIntake extends LinearOpMode {
    enum RotateServoE {
        INTAKE_DOWN,
        INTAKE_MIDDLE,
        INTAKE_UP
    }
    enum GrabServoE {
        GRAB_CLOSE,
        GRAB_OPEN
    }
    enum GimbleServoE {
        GIMBLE_CENTER,
        GIMBLE_NINETY
    }

    //DRIVETRAIN
    public DcMotor leftFrontDrive = null;
    public DcMotor leftBackDrive = null;
    public DcMotor rightFrontDrive = null;
    public DcMotor rightBackDrive = null;

    public double left = 0;
    public double right = 0;
    public double drive = 0;
    public double turn = 0;
    public double max = 0;

    //SERVO CODE
    Servo rotateServo;
    Servo grabServo;
    Servo gimbleServo;

    RotateServoE rotateServoe = RotateServoE.INTAKE_UP;
    GrabServoE grabServoe = GrabServoE.GRAB_CLOSE;
    GimbleServoE gimbleServoe = GimbleServoE.GIMBLE_CENTER;

    boolean rightBumper = false;
    boolean leftBumper = false;
    boolean dpadRight = false;

    @Override
    public void runOpMode() {
        rotateServo = hardwareMap.get(Servo.class, "es3");
        grabServo = hardwareMap.get(Servo.class, "es5");
        gimbleServo = hardwareMap.get(Servo.class, "es1");

        //DT MOTORS

        leftFrontDrive = hardwareMap.get(DcMotor.class, "cm2");
        leftBackDrive = hardwareMap.get(DcMotor.class, "cm3");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "cm0");
        rightBackDrive = hardwareMap.get(DcMotor.class, "cm1");

        // If there are encoders connected, switch to RUN_USING_ENCODER mode for greater accuracy
        leftFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // Pushing the left stick forward MUST make robot go forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();
        while (opModeIsActive()) {

            //drive
            drive = -gamepad1.left_stick_y;
            turn  =  gamepad1.right_stick_x;

            // Combine drive and turn for blended motion.
            //TEMP SOL - DIV by 2 to slow down the DT
            left  = (drive + turn)/1.5;
            right = (drive - turn)/1.5;

            // Normalize the values so neither exceed +/- 1.0
            max = Math.max(Math.abs(left), Math.abs(right));
            if (max > 1.0)
            {
                left /= max;
                right /= max;
            }

            // Output the safe vales to the motor drives.
            leftFrontDrive.setPower(left);
            leftBackDrive.setPower(left);
            rightFrontDrive.setPower(right);
            rightBackDrive.setPower(right);

            telemetry.addData("drive %f", drive);
            telemetry.addData("turn %f", turn );

            if (gamepad1.y == true) {
                grabServo.setPosition(0);
                sleep(250);
                rotateServo.setPosition(0.3);
                sleep(250);
                grabServo.setPosition(1);
                sleep(250);
                rotateServo.setPosition(0.87);
                sleep(500);
            }

            if (rotateServoe == RotateServoE.INTAKE_UP  && gamepad1.right_bumper==true && rightBumper==false) {
                rotateServoe = RotateServoE.INTAKE_MIDDLE;
            }
            else if (rotateServoe == RotateServoE.INTAKE_MIDDLE && gamepad1.right_bumper==true && rightBumper==false){
                rotateServoe = RotateServoE.INTAKE_DOWN;
            }
            else if( rotateServoe == RotateServoE.INTAKE_DOWN && gamepad1.right_bumper==true && rightBumper==false) {
                rotateServoe = RotateServoE.INTAKE_UP;
            }

            rightBumper = gamepad1.right_bumper;
            // hardware calls
            if (rotateServoe == RotateServoE.INTAKE_DOWN)
                rotateServo.setPosition(0.3);//rotating intake fully down
            if (rotateServoe == RotateServoE.INTAKE_UP)
                rotateServo.setPosition(0.87);//rotating intake fully upright
            if (rotateServoe == RotateServoE.INTAKE_MIDDLE)
                rotateServo.setPosition(0.5);//rotate intake middle position
            telemetry.addData("RotateServoPOS", rotateServo.getPosition() );
            telemetry.update();

            if (grabServoe == GrabServoE.GRAB_OPEN && gamepad1.left_bumper==true && leftBumper == false){
                grabServoe = GrabServoE.GRAB_CLOSE;
            }
            else if(grabServoe == GrabServoE.GRAB_CLOSE && gamepad1.left_bumper==true && leftBumper == false){
                grabServoe = GrabServoE.GRAB_OPEN;
            }
            leftBumper = gamepad1.left_bumper;
            //hardware calls
            if (grabServoe == GrabServoE.GRAB_CLOSE)
                grabServo.setPosition(1);//close claw fully
            if (grabServoe == GrabServoE.GRAB_OPEN)
                grabServo.setPosition(0);//open claw fully
            telemetry.addData("GrabServoPOS", grabServo.getPosition() );

            if (gimbleServoe == GimbleServoE.GIMBLE_CENTER  && gamepad1.dpad_right==true && dpadRight==false) {
                gimbleServoe = GimbleServoE.GIMBLE_NINETY;
            }
            // else if (rotateServoe == RotateServoE.INTAKE_MIDDLE && gamepad1.right_bumper==true && rightBumper==false){
            //     rotateServoe = RotateServoE.INTAKE_DOWN;
            // }
            else if(gimbleServoe == GimbleServoE.GIMBLE_NINETY && gamepad1.dpad_right==true && dpadRight==false) {
                gimbleServoe = GimbleServoE.GIMBLE_CENTER;
            }

            dpadRight = gamepad1.dpad_right;
            // hardware calls
            if (gimbleServoe == GimbleServoE.GIMBLE_CENTER){
                gimbleServo.setPosition(0.55);
            }
            if (gimbleServoe == GimbleServoE.GIMBLE_NINETY){
                gimbleServo.setPosition(0.9);
            }
            // if (rotateServoe == RotateServoE.INTAKE_MIDDLE)
            //     rotateServo.setPosition(0.5);//rotate intake middle position



        }

    }
}
