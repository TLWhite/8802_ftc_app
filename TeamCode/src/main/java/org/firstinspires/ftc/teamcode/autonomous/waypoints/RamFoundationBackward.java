package org.firstinspires.ftc.teamcode.autonomous.waypoints;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.common.elements.Alliance;
import org.firstinspires.ftc.teamcode.robot.mecanum.MecanumPowers;
import org.firstinspires.ftc.teamcode.robot.mecanum.MecanumUtil;
import org.firstinspires.ftc.teamcode.robot.mecanum.SkystoneHardware;

@Config
public class RamFoundationBackward implements Subroutines.ArrivalInterruptSubroutine {

    ElapsedTime timer;
    Alliance alliance;

    public RamFoundationBackward(Alliance alliance) {
        this.alliance = alliance;
        this.timer = null;
    }

    @Override
    public boolean runCycle(SkystoneHardware robot) {
        if (this.timer == null) {
            if (alliance == Alliance.BLUE) {
                robot.setPowers(new MecanumPowers(-1, -0.55, -1, -0.55));
            } else {
                robot.setPowers(new MecanumPowers(-0.55, -1, -0.55, -1));
            }
            this.timer = new ElapsedTime();
        }

        if (timer.milliseconds() > 3000) {
            robot.leftFoundationLatch.retract();
            robot.rightFoundationLatch.retract();
            return true;
        }
        return false;
    }
}