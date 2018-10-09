package org.teamtators.levitator2.subsystems;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.UsbCameraInfo;
import edu.wpi.first.wpilibj.CameraServer;
import org.teamtators.common.scheduler.Subsystem;

public class Vision extends Subsystem {

    private CameraServer cs;
    private UsbCamera usbCamera;

    public Vision() {
        super("Vision");
        cs = CameraServer.getInstance();
        if (usbCamera == null) {
            UsbCameraInfo[] usbCameras = getCameras();
            if (usbCameras.length == 0) {
                logger.warn("No USB webcam!");
                return;
            }
            logger.debug("Starting vision thread with source: " + usbCameras[0].name);
            usbCamera = cs.startAutomaticCapture("USBWebCam_Pick", usbCameras[0].dev);
            //cvSink = cs.getVideo();
            cs.putVideo("Vision_Pick", 176, 144);
        } else {
            logger.debug("Starting vision thread with existing source");
        }

        boolean success = usbCamera.setResolution(176, 144);
        if (!success) {
            logger.warn("Could not set camera resolution to {}x{}", 176, 144);
        }
        success = usbCamera.setFPS(20);
        if (!success) {
            logger.warn("Could not set camera fps to {}", 20);
        }
//        usbCamera.setExposureManual(config.exposure);
        usbCamera.setExposureAuto();
    }

    private UsbCameraInfo[] getCameras() {
        return UsbCamera.enumerateUsbCameras();
    }


}
