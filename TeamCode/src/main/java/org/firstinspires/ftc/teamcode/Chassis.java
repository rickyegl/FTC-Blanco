package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import com.qualcomm.robotcore.hardware.Blinker;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.subsystems.ArmSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.ChassisSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.DroneLauncherSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.ElevatorSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.GyroscopeSubsystem;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.teamcode.subsystems.Parameters;
import org.firstinspires.ftc.teamcode.subsystems.PivotSubsystem;


@TeleOp

public class Chassis extends LinearOpMode {
    
    public static Gamepad controller1;
    public static Gamepad controller2;
    
    private Blinker control_Hub;
    private double speed=1;
    private IMU imu;
    public double baseSpeed=1;
    private Orientation orientation;
    private DcMotor intake;
    boolean hasTarget=false;
    public double gyr;

    private boolean mix = true;
    void ElevatorMethods(ElevatorSubsystem elevator, ChassisSubsystem chassis,ArmSubsystem arm){
        /*
        if(controller1.right_trigger>0.1){
            elevator.goUp(controller1.right_trigger);
        }else if(controller1.left_trigger<0.1){
            elevator.goDown(controller1.left_trigger);
        }
        */

        elevator.getPosition(telemetry);
        if(!controller2.start) {
            /*
            if (controller1.dpad_up && controller1.b) {
                elevator.elevator1.setPower(-1);
            } else if (controller1.dpad_down && controller1.b) {
                elevator.elevator1.setPower(1);
            } else if (controller1.dpad_up && controller1.a) {
                elevator.elevator1.setPower(-1);
            } else if (controller1.dpad_down && controller1.a) {
                elevator.elevator1.setPower(1);
            } else if (controller1.dpad_up) {
                elevator.elevator2.setPower(-1);
                elevator.elevator1.setPower(-1);
            } else if (controller1.dpad_down) {
                elevator.elevator2.setPower(1);
                elevator.elevator1.setPower(1);
            } else {
                elevator.elevator1.setPower(0);
                elevator.elevator2.setPower(0);
            }

             */
            if(Math.abs(controller2.right_stick_y)>0.2) {
                elevator.DebugSpeed = Math.abs(controller2.right_stick_y);
                telemetry.addData("lsthikkk",controller2.right_stick_y);
                if (controller2.right_stick_y > 0.2) {
                    elevator.goingup = false;
                    elevator.goingdown = true;
                    elevator.goDown();
                    //elevator.holding = false;
                    if(controller2.y){
                        elevator.holding = true;
                    }

                } else if (controller2.right_stick_y < -0.2) {
                    elevator.goingdown=false;
                    elevator.goingup=true;
                    elevator.goUp();
                    elevator.holding = false;
                }else{
                    elevator.goingup = false;
                    elevator.goingdown = false;
                    elevator.stop();
                }
            }else if(elevator.holding){
                elevator.DebugSpeed=0.4;
                elevator.goDown();
                chassis.REnabled=false;
                arm.piding = false;
            }else {
                elevator.stop();
            }
        }
    }
    void ChassisMethods(ChassisSubsystem chassis, GyroscopeSubsystem gyroscope){
        //IMUMethods();
        gyr = gyroscope.getRotation();
        //gyr=0;
        telemetry.addData("gyr",gyr);
        if(controller1.back){
            if(controller1.a){
                gyroscope.reset();
                chassis.updateTargetAngle();
            }

        }
        if(controller1.back&&controller1.x){
            chassis.doOverride=true;
            chassis.REnabled=false;
            //control_Hub.setConstant();
        }
        if(controller1.y&&false){
            chassis.FrontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            chassis.FrontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            chassis.BackLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            chassis.BackRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        }else{
            chassis.FrontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            chassis.FrontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            chassis.BackLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            chassis.BackRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
        if(controller1.start){
            if (controller1.dpad_up){
                gyroscope.setOffset(180);
                chassis.updateTargetAngle();
            }
            if (controller1.dpad_right){
                gyroscope.setOffset(270);
                chassis.updateTargetAngle();
            }
            if (controller1.dpad_down){
                gyroscope.setOffset(0);
                chassis.updateTargetAngle();
            }
            if (controller1.dpad_left){
                gyroscope.setOffset(90);
                chassis.updateTargetAngle();
            }
        }
        chassis.arcadeDrive(controller1.left_stick_x,-controller1.left_stick_y,controller1.right_stick_x,speed,gyr);

        if(controller1.right_trigger>0.1){
            speed=baseSpeed*(1.4-controller1.right_trigger);
        }else{
            speed=baseSpeed;
        }

        if(controller2.x){
            if(controller1.back){
                intake.setPower(-1);
            }else {
                intake.setPower(1);
            }
        }else if(controller2.b){
            if(!controller1.back){
                intake.setPower(-1);
            }else {
                intake.setPower(1);
            }
        }else {
            intake.setPower(0);
        }

        if((controller1.a||controller2.a)&&!controller1.back){
            chassis.snap = true;
        }else{
            chassis.snap = false;
        }

    }
    void IMUMethods(){
        orientation=imu.getRobotOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        //telemetry.addData("GyroX", AGyroscope.getX());
        //telemetry.addData("GyroY", AGyroscope.getY());
        telemetry.addData("Gyro", orientation.firstAngle);


        if(controller1.right_stick_button){
            //imu.resetYaw();

        }
    }
    void ArmMethods(ArmSubsystem arm, PivotSubsystem pivot, ElevatorSubsystem elevator){

        if(info.name=="gobilda"){
            arm.going_down =Chassis.controller2.left_stick_y < -0.2;
            arm.going_down = false;
        }

        if(!controller2.start){
            if (controller2.dpad_right) {
                if(info.name=="gobilda") {
                    arm.piding=true;
                    arm.setPosition(2);

                }else {
                    pivot.up();
                }

            } else if (controller2.dpad_down) {
                if(info.name=="gobilda") {
                    arm.piding=true;
                    arm.setPosition(1);
                }else {
                    pivot.up();
                }

            }
            else if(controller2.dpad_left) {
                if(info.name=="gobilda") {
                    if(!arm.open){
                        arm.piding=true;
                        arm.setZero();

                    }
                }else {
                    pivot.down();
                }

            }else if(controller2.dpad_up){
                if(info.name=="gobilda") {
                    arm.piding=true;
                    arm.setPosition(3);
                }else {

                }

            }
            if(controller2.right_stick_button){
                arm.armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                arm.armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
            if(arm.extraAmount>0&&!arm.open){
                arm.piding = false;
            }
            telemetry.addData("arm.piding",arm.piding);
            telemetry.addData("arm.extraAmount",arm.extraAmount);
        }

        if (controller2.right_bumper) {
            if(info.name=="rev"){
                pivot.open();
            }else{
                arm.open();
            }

        } else if(controller2.left_bumper) {
            if(info.name=="rev"){
                pivot.close();
            }else{
                arm.close();
            }
        }
        if(info.name=="gobilda") {
            arm.showPositions();
            if (arm.servoDelta.seconds() > 0.5) {
                arm.servoDelta.reset();


            }
            telemetry.addData("angle", arm.NotAngle);
        }

        if(controller2.right_trigger>0.1){
            arm.extraAmount = controller2.right_trigger*.3;
            arm.updateArm();
        }else if(controller2.left_trigger>0.1) {
            arm.extraAmount = -controller2.left_trigger*.3;
            arm.updateArm();
        }else if(arm.extraAmount!=0){
           arm.extraAmount=0;
        arm.updateArm();
        }


    }

    @Override
    public void runOpMode() {
        //this.telemetry = telemetry;
        control_Hub = hardwareMap.get(Blinker.class, "Control Hub");
        //imu = hardwareMap.get(IMU.class, "imu");
        intake = hardwareMap.get(DcMotor.class, "intake");


        //control_Hub.setConstant(3);

        telemetry.addData("Status", "Initialized");
        /////////////////////////////
        ChassisSubsystem chassis=ChassisSubsystem.getInstance(hardwareMap,telemetry);
        ElevatorSubsystem elevator = ElevatorSubsystem.getInstance(hardwareMap,telemetry);
        //Odometry odometry = Odometry.getInstance(hardwareMap,chassis);
        GyroscopeSubsystem gyroscope = GyroscopeSubsystem.getInstance(hardwareMap);
        DroneLauncherSubsystem launcher = DroneLauncherSubsystem.getInstance(hardwareMap,telemetry);
        PivotSubsystem pivot = null;
        ArmSubsystem arm = null;
        if(info.name=="rev") {
            pivot = PivotSubsystem.getInstance(hardwareMap, telemetry);
        }else{
            arm = ArmSubsystem.getInstance(hardwareMap, telemetry);
        }
        /////////////////////////////
        //orientation=imu.getRobotOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        //GyroscopeSubsystem gyroscope=GyroscopeSubsystem.getInstance();

        //telemetry.addData("GyroX", orientation.secondAngle);
        //telemetry.addData("GyroY", AndroidOrientation().getAngle().secondPosition());
        ////telemetry.update();


        //launcher.reset();
        telemetry.update();

        waitForStart();

        chassis.updateTargetAngle();

        if(info.name=="gobilda"){
            arm.setZero();
            arm.updateArm();
        }


        //arm.setZero();

        controller1 = this.gamepad1;
        controller2 = this.gamepad2;

        if(this.gamepad2.start&this.gamepad2.back){
            controller1=this.gamepad2;
        }

        while(opModeIsActive()) {

            ChassisMethods(chassis,gyroscope);

            ElevatorMethods(elevator,chassis,arm);

            if(info.name=="gobilda") {
                ArmMethods(arm, pivot,elevator);
            }else{
                pivot.pivotControls(this.gamepad2.a,this.gamepad2.b,this.gamepad2.left_bumper, this.gamepad2.right_bumper);
            }


            telemetry.update();

            if (controller2.y&&!(controller2.start||controller2.back)) {
                launcher.launch();
                chassis.doOverride=true;
            } else {
                //launcher.reset();
            }


        }
        /*
        odometry.update();
        telemetry.addData("X", odometry.position().x);

        if(hasTarget){
            odometry.moveToCoords(2000,3000);

        }
        */
        ChassisSubsystem.unaliveInstance();
        ElevatorSubsystem.unaliveInstance();
        GyroscopeSubsystem.unaliveInstance();
        DroneLauncherSubsystem.unaliveInstance();


    }

}