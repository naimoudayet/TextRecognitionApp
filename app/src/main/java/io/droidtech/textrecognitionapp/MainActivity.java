package io.droidtech.textrecognitionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    Button captureImageBtn, detectTextBtn;
    ImageView imageView;
    TextView textView;

    Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        captureImageBtn = findViewById(R.id.capture_image_btn);
        detectTextBtn = findViewById(R.id.detect_text_image_btn);
        imageView = findViewById(R.id.image_view);
        textView = findViewById(R.id.text_display);

        captureImageBtn.setOnClickListener(v -> {
            dispatchTakePictureIntent();
        });

        detectTextBtn.setOnClickListener(v -> {
            detectTextFromImage();
        });

    }

    private void detectTextFromImage() {


        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();

        Task<FirebaseVisionText> result = firebaseVisionTextRecognizer
                .processImage(firebaseVisionImage)
                .addOnSuccessListener(firebaseVisionText -> {
                    String resultText = firebaseVisionText.getText();
                    for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
                        String blockText = block.getText();
                        Log.i("blockText: ", blockText);
                        Float blockConfidence = block.getConfidence();
                        List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
                        Point[] blockCornerPoints = block.getCornerPoints();
                        Rect blockFrame = block.getBoundingBox();
                        for (FirebaseVisionText.Line line : block.getLines()) {
                            String lineText = line.getText();
                            Log.i("lineText: ", lineText);
                            Float lineConfidence = line.getConfidence();
                            List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
                            Point[] lineCornerPoints = line.getCornerPoints();
                            Rect lineFrame = line.getBoundingBox();
                            for (FirebaseVisionText.Element element : line.getElements()) {
                                String elementText = element.getText();
                                Log.i("elementText: ", elementText);
                                Float elementConfidence = element.getConfidence();
                                List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
                                Point[] elementCornerPoints = element.getCornerPoints();
                                Rect elementFrame = element.getBoundingBox();
                            }
                        }
                    }

                    textView.setText("result: " + resultText);
                })
                .addOnFailureListener(e -> {

                });

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }

}
