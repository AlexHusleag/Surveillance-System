import picamera
import datetime as dt
import subprocess
import os
import socket
import io

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
sock = socket.socket()
sock.connect(('192.168.1.6', 8000))




def record():
    fname = filename.setFileName(dt.datetime.now().strftime('%Y-%m-%d') + "-" + dt.datetime.now().strftime('%H-%M-%S'))

    with picamera.PiCamera(resolution=(600, 400), framerate=24) as camera:
        with motionDetection.DetectMotion(camera) as output:
            camera.rotation = 180

            camera.start_preview()
            camera.annotate_background = picamera.Color('black')

            # Construct an instance of our custom output splitter with a filename
            # and a connected socket
            my_output = MyOutput(fname + '.h264', sock)

            camera.start_recording(my_output, format='h264', motion_output=output)

            start = dt.datetime.now()
            while (dt.datetime.now() - start).seconds < 10:
                camera.annotate_text = dt.datetime.now().strftime('%Y-%m-%d %H:%M:%S')
                camera.wait_recording(0.1)
            camera.stop_recording()

    subprocess.run(['MP4Box', '-add', fname + '.h264', fname + '.mp4'])

    uploadToFirebase.uploadToFirebase(fname + ".mp4")

    os.remove(fname + ".mp4")
    os.remove(fname + ".h264")
    print("Files deleted")

