package com.fancyinnovations.fancyholograms.api.data.property;

import org.joml.Quaternionf;

public class HologramRotation {

    private float yaw;
    private float pitch;
    private float roll;
    private Quaternionf quaternion;

    public HologramRotation(float yaw, float pitch, float roll) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
        quaternion = toQuaternion();
    }

    private Quaternionf toQuaternion() {
        double yawRad = Math.toRadians(-yaw);
        double pitchRad = Math.toRadians(pitch);
        double rollRad = Math.toRadians(roll);

        double cy = Math.cos(yawRad * 0.5);
        double sy = Math.sin(yawRad * 0.5);
        double cp = Math.cos(pitchRad * 0.5);
        double sp = Math.sin(pitchRad * 0.5);
        double cr = Math.cos(rollRad * 0.5);
        double sr = Math.sin(rollRad * 0.5);

        double w = cy * cp * cr + sy * sp * sr;
        double x = cy * sp * cr + sy * cp * sr;
        double y = sy * cp * cr - cy * sp * sr;
        double z = cy * cp * sr - sy * sp * cr;

        return new Quaternionf(x, y, z, w);
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
        this.quaternion = toQuaternion();
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
        this.quaternion = toQuaternion();
    }

    public float getRoll() {
        return roll;
    }

    public void setRoll(float roll) {
        this.roll = roll;
        this.quaternion = toQuaternion();
    }

    public Quaternionf getQuaternion() {
        return quaternion;
    }

    public HologramRotation clone() {
        return new HologramRotation(yaw, pitch, roll);
    }
}
