from google.cloud import storage
from firebase import firebase
import os

import filename as filename

os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = "credentials/video-surveillance-580ac-d5c1433210dd.json"

firebase = firebase.FirebaseApplication("https://video-surveillance-580ac.firebaseio.com/")

def uploadToFirebase(file):

    client = storage.Client()

    bucket = client.get_bucket("video-surveillance-580ac.appspot.com")

    # posting to firebase storage

    videoPath = file
    videoBlob = bucket.blob("videos/" + file)

    videoBlob.upload_from_filename(videoPath)

    print("File uploaded")
