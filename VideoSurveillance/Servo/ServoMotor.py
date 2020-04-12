import time

import pigpio

moveServo = 1500
pi = None


def setup():
    global pi
    pi = pigpio.pi()
    pi.set_mode(18, pigpio.OUTPUT)

    print("mode: ", pi.get_mode(18))
    print("setting to: ", pi.set_servo_pulsewidth(18, moveServo))
    print("set to: ", pi.get_servo_pulsewidth(18))
    pi.set_servo_pulsewidth(18, 500)


def servoUp():
    global moveServo
    moveServo += 200

    if moveServo > 2500:
        moveServo = 2500

    pi.set_servo_pulsewidth(18, moveServo)
    time.sleep(1)


def servoDown():
    global moveServo
    moveServo -= 200

    if moveServo < 500:
        moveServo = 500

    pi.set_servo_pulsewidth(18, moveServo)
    time.sleep(1)


def close():
    pi.stop()
