package org.firstinspires.ftc.teamcode.robot.mecanum.auto;

import com.acmerobotics.dashboard.config.Config;
import com.disnodeteam.dogecv.detectors.skystone.SkystoneDetector;

import org.firstinspires.ftc.teamcode.autonomous.PurePursuitPath;
import org.firstinspires.ftc.teamcode.autonomous.waypoints.Waypoint;
import org.firstinspires.ftc.teamcode.common.SimulatableMecanumOpMode;
import org.firstinspires.ftc.teamcode.common.elements.Alliance;
import org.firstinspires.ftc.teamcode.common.elements.SkystoneState;
import org.firstinspires.ftc.teamcode.common.math.MathUtil;
import org.firstinspires.ftc.teamcode.common.math.Pose;
import org.firstinspires.ftc.teamcode.robot.mecanum.MecanumHardware;
import org.firstinspires.ftc.teamcode.robot.mecanum.MecanumUtil;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;
import org.openftc.revextensions2.RevBulkData;

import java.util.List;

import static org.firstinspires.ftc.teamcode.robot.mecanum.MecanumHardware.FIELD_RADIUS;

@Config
public abstract class PurePursuitAuto extends SimulatableMecanumOpMode {
    Pose DEFAULT_START_POSITION = new Pose(-FIELD_RADIUS + 22.75 + 9, FIELD_RADIUS - 9, 3 * Math.PI / 2);

    MecanumHardware robot;
    PurePursuitPath followPath;
    SkystoneDetector detector;
    OpenCvCamera phoneCam;

    // Robot state
    public static SkystoneState SKYSTONE = SkystoneState.UPPER;
    public static Alliance ALLIANCE = Alliance.BLUE;

    public abstract Pose getBlueStartPosition();
    public abstract List<Waypoint> getPurePursuitWaypoints();

    @Override
    public void init() {
        Pose start = getBlueStartPosition();
        if (ALLIANCE == Alliance.RED) {
            start.y *= -1;
            start.heading *= MathUtil.angleWrap(-start.heading);
        }

        this.robot = this.getRobot(getBlueStartPosition());
        robot.initBNO055IMU(hardwareMap);
        robot.initCamera(hardwareMap);

        followPath = new PurePursuitPath(robot, getPurePursuitWaypoints());
        // We design all paths for blue side, and then flip them for red
        if (ALLIANCE == Alliance.RED) {
            followPath.reverse();
        }

        this.detector = new SkystoneDetector();
        int cameraMonitorViewId = hardwareMap.appContext.getResources()
                .getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        phoneCam = new OpenCvWebcam(robot.camera, cameraMonitorViewId);
        phoneCam.openCameraDevice();
        phoneCam.setPipeline(detector);
        phoneCam.startStreaming(320, 240, OpenCvCameraRotation.SIDEWAYS_LEFT);
    }

    @Override
    public void start() {
        robot.initBulkReadTelemetry();
    }

    @Override
    public void loop() {
        RevBulkData data = robot.performBulkRead();
        robot.drawDashboardPath(followPath);
        robot.sendDashboardTelemetryPacket();

        if (!followPath.finished()) {
            followPath.update();
        } else {
            robot.setPowers(MecanumUtil.STOP);
            stop();
        }
    }
}