from google.cloud import storage
from firebase import firebase
import os

os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = "credentials/video-surveillance-580ac-d5c1433210dd.json"

firebase = firebase.FirebaseApplication("https://video-surveillance-580ac.firebaseio.com/")

def uploadToFirebase(file):

    client = storage.Client()

    bucket = client.get_bucket("video-surveillance-580ac.appspot.com")

    # posting to firebase storage

    # /home/pi/Desktop/Stream Camera Feed/2020-01-08-18-55-31.h264
    videoPath = file
    videoBlob = bucket.blob("videos/" + file)

    videoBlob.upload_from_filename(videoPath)

    print("File uploaded to firebase")
