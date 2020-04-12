import MotionSensor.MotionSensor as MotionSensor
# import multiprocessing
from Servo import pi_server

import camera.livestream2 as camera


def main():
    # with concurrent.futures.ProcessPoolExecutor() as executor:
    #     executor.submit(pi_server.activate_servoq())
    #     executor.submit(MotionSensor.detect_motion_and_record())

    # p1 = multiprocessing.Process(target=pi_server.activate_servo)
    # p1.start()
    # p2 = multiprocessing.Process(target=MotionSensor.detect_motion_and_record)
    # p2.start()
    camera.record()

if __name__ == '__main__':
    main()

# MotionSensor.detect_motion_and_record()