# CVUtils

Utility library for working with **OpenCV** via **Bytedeco** in Java.  
This project demonstrates how to integrate video playback, frame conversion, and **face landmark detection** into a Swing application.

---

## ✨ Features
- Convert OpenCV `Mat` frames to Java `BufferedImage` for Swing display.
- Play video streams with frame‑by‑frame rendering.
- Detect faces using Haar cascades.
- Run **68‑point facial landmark detection** with OpenCV’s `FacemarkLBF`.
- Overlay landmarks and bounding boxes directly on video frames.
- Utility methods for resource loading and frame conversion.

---

## 📦 Requirements
- Java 11+
- Maven
- Dependencies:
  ```xml
  <dependency>
    <groupId>org.bytedeco</groupId>
    <artifactId>opencv-platform</artifactId>
    <version>4.12.0-1.5.10</version>
  </dependency>
---

## 📂 Resources
Place the following files in `src/main/resources/models/`:
- `haarcascade_frontalface_alt.xml` (face detection)
- `lbfmodel.yaml` (landmark detection model, ~54 MB)

These are official OpenCV models, licensed under BSD.

---

## 🚀 Usage
Example: detecting and drawing landmarks on video frames
```java
// Load face detector
CascadeClassifier faceDetector = new CascadeClassifier(
    getClass().getResource("/models/haarcascade_frontalface_alt.xml").getPath()
);

// Load landmark model
FacemarkLBF facemark = FacemarkLBF.create();
facemark.loadModel(getClass().getResource("/models/lbfmodel.yaml").getPath());

// Detect faces
RectVector faces = new RectVector();
cvtColor(frame, grayFrame, COLOR_BGR2GRAY);
faceDetector.detectMultiScale(grayFrame, faces);

// Fit landmarks
Point2fVectorVector landmarks = new Point2fVectorVector();
if (facemark.fit(grayFrame, faces, landmarks)) {
    for (long i = 0; i < landmarks.size(); i++) {
        Point2fVector points = landmarks.get(i);
        for (long j = 0; j < points.size(); j++) {
            Point2f p = points.get(j);
            circle(frame, new Point((int)p.x(), (int)p.y()), 2, Scalar.RED, FILLED, LINE_8, 0);
        }
    }
}

// Display frame in Swing
BufferedImage img = CVUtils.matToBufferedImage(frame);
videoLabel.setIcon(new ImageIcon(img));
frameWindow.pack();
```

---

## 📜 License
- **CVUtils code** → MIT License
- **OpenCV models** → BSD License

---

## 🙌 Credits
- **OpenCV** for computer vision algorithms and pretrained models.
- **Bytedeco** for JavaCPP and JavaCV bindings that make OpenCV usable in Java.
- This repo simply wires them together for demonstration and learning purposes.
---