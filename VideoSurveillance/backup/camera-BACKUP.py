import picamera
from picamera.array import PiRGBArray

import datetime as dt
import subprocess
import os
import socket
import io
import zmq
import numpy as np

import cv2

from camera import motionDetection
import filename as filename
from uploadCloud import uploadToFirebase



class MyOutput(object):
    def __init__(self, filename, sock):
        self.output_file = io.open(filename, 'wb')
        self.output_sock = sock.makefile('wb')

    def write(self, buf):
        self.output_file.write(buf)
        self.output_sock.write(buf)

    def flush(self):
        self.output_file.flush()
        self.output_sock.flush()

    def close(self):
        self.output_file.close()
        self.output_sock.close()


# Connect a socket to a remote server on port 8000
# socket = socket.socket()
# socket.connect(('192.168.1.2', 8000))





def record():
    fname = filename.setFileName(dt.datetime.now().strftime('%Y-%m-%d') + "-" + dt.datetime.now().strftime('%H-%M-%S'))
    object_classifier = cv2.CascadeClassifier("models/facial_recognition_model.xml")  # an opencv classifier
    with picamera.PiCamera(resolution=(600, 400), framerate=45) as camera:
        with motionDetection.DetectMotion(camera) as output:
            with picamera.array.PiRGBArray(camera) as detect:
                camera.rotation = 180
                camera.start_preview()
                camera.annotate_background = picamera.Color('black')

                # Construct an instance of our custom output splitter with a filename
                # and a connected socket
                # my_output = MyOutput(fname + '.h264', socket)

                # camera.start_recording(my_output, format='h264', motion_output=output)
                camera.start_recording(fname + '.h264', format='h264', motion_output=output)

                start = dt.datetime.now()
                while (dt.datetime.now() - start).seconds < 50:

                    # write onto the livestream the full date
                    # camera.annotate_text = dt.datetime.now().strftime('%Y-%m-%d %H:%M:%S')
                    camera.wait_recording(0.1)

                    camera.capture(detect, 'bgr', use_video_port=True)
                    print('Captured %dx%d image' % (
                        detect.array.shape[1], detect.array.shape[0]))


                    # FACE DETECTION STUFF
                    gray = cv2.cvtColor(detect.array, cv2.COLOR_BGR2GRAY)

                    faces = object_classifier.detectMultiScale(
                        gray,
                        scaleFactor=1.05,
                        minNeighbors=5,
                        minSize=(30, 30),
                        flags=cv2.CASCADE_SCALE_IMAGE
                    )

                    if len(faces) > 0:
                        print("Object found")
                    else:
                        print("Object not found")

                    # Draw a rectangle around the objects
                    for (x, y, w, h) in faces:
                        print('FACE CEVA BAq')
                        cv2.rectangle(detect.array, (x, y), (x + w, y + h), (255, 0, 0), 2)

                    # show the frame
                    cv2.imshow("Live Stream", detect.array)
                    key = cv2.waitKey(1) & 0xFF

                    # clear the stream in preparation for the next frame
                    detect.truncate(0)
                    camera.wait_recording(0.1)

                    # if the `q` key was pressed, break from the loop
                    if key == ord("q"):
                        break

                cv2.destroyAllWindows()
                camera.stop_recording()

    subprocess.run(['MP4Box', '-add', fname + '.h264', fname + '.mp4'])

    # uploadToFirebase.uploadToFirebase(fname + ".mp4")

    # os.remove(fname + ".mp4")
    # os.remove(fname + ".h264")
    # print("Files deleted")

