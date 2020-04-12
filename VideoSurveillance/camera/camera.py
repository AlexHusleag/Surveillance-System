import picamera
from picamera.array import PiRGBArray

import datetime as dt
import subprocess

import cv2

import filename as filename
# import MotionSensor.MotionSensor as ms
from uploadCloud import uploadToFirebase, uploadToDropbox
from faceRecognition import faceRecognition

# from camera.livestream import StreamingOutput

import concurrent.futures


def draw_rectangle(frame, face_locations, face_names):
    for (top, right, bottom, left), name in zip(face_locations, face_names):
        # Scale back up face locations since the frame we detected in was scaled to 1/4 size
        top *= 4
        right *= 4
        bottom *= 4
        left *= 4

        # Draw a box around the face
        cv2.rectangle(frame.array, (left, top), (right, bottom), (0, 0, 255), 2)

        # Draw a label with a name below the face
        cv2.rectangle(frame.array, (left, bottom - 35), (right, bottom), (0, 0, 255), cv2.FILLED)
        font = cv2.FONT_HERSHEY_DUPLEX
        cv2.putText(frame.array, name, (left + 6, bottom - 6), font, 1.0, (255, 255, 255), 1)


def record():
    fname = filename.setFileName(dt.datetime.now().strftime('%Y-%m-%d') + "-" + dt.datetime.now().strftime('%H-%M-%S'))

    # with concurrent.futures.ProcessPoolExecutor() as executor:
    #     f1 = executor.submit(ms.detect_motion())
    with picamera.PiCamera(resolution=(500, 300), framerate=30) as camera:
        with picamera.array.PiRGBArray(camera) as frame:
            camera.rotation = 180
            camera.start_preview()
            camera.annotate_background = picamera.Color('black')

            # camera.start_recording(fname + '.h264', format='h264', motion_output=output)
            camera.start_recording(fname + '.h264', format='mjpeg')

            start = dt.datetime.now()
            while (dt.datetime.now() - start).seconds < 30:
                camera.wait_recording(0.01)
                camera.capture(frame, "bgr", use_video_port=True)

                print('Captured %dx%d image' % (
                    frame.array.shape[1], frame.array.shape[0]))

                small_frame = cv2.resize(frame.array, (0, 0), fx=0.25, fy=0.25)

                face_locations, face_names = faceRecognition.process_frame(small_frame)
                draw_rectangle(frame, face_locations, face_names)

                # show the frame
                cv2.imshow("Live Stream", frame.array)
                key = cv2.waitKey(1) & 0xFF

                # clear the stream in preparation for the next frame
                frame.truncate(0)
                camera.wait_recording(0.01)

                # if f1 == 1:
                #     start = dt.datetime.now()

                # if the `q` key was pressed, break from the loop
                if key == ord("q"):
                    break

            cv2.destroyAllWindows()
            camera.stop_recording()

    print("H264 TO MP3")
    subprocess.run(['MP4Box', '-add', fname + '.h264', fname + '.mp4'])

    print(fname + ".mp4")

    uploadToFirebase.uploadToFirebase(fname + ".mp4")
    uploadToDropbox.uploadToDropbox(fname + ".mp4")

    # os.remove(fname + ".mp4")
    # os.remove(fname + ".h264")
    # print("Files deleted")
