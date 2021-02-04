package com.easy.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NavUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    final String LOGNAME = "pdf_viewer";
    final String FILENAME = "shannon1948.pdf";
    final int FILERESID = R.raw.shannon1948;

    // manage the pages of the PDF, see below
    PdfRenderer pdfRenderer;
    ImageButton redo;
    ImageButton undo;
    ImageButton pen;
    ImageButton highlighter;
    ImageButton eraser;
    ImageButton viewButton;
    private ParcelFileDescriptor parcelFileDescriptor;
    private PdfRenderer.Page currentPage;
    // custom ImageView class that captures strokes and draws them over the image
    PDFimage pageImage;
    ConstraintLayout pdfLayout;
    TextView label;
    ImageButton next;
    ImageButton prev;


    //vars
    int pageIndex;
    int totalPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.d(LOGNAME, "created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pageIndex = 0;
        pageImage = new PDFimage(this);
        pageImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        pdfLayout = findViewById(R.id.pdfLayout);
        label = findViewById(R.id.pageNumberLabel);
        next = findViewById(R.id.nextPageButton);
        prev = findViewById(R.id.prevPageButton);
        redo = findViewById(R.id.redoButton);
        undo = findViewById(R.id.undoButton);
        pen = findViewById(R.id.penButton);
        highlighter = findViewById(R.id.highlighterButton);
        eraser = findViewById(R.id.eraserButton);
        viewButton = findViewById(R.id.viewButton);
        try{
            openRenderer(this);
            showPage(pageIndex);

        } catch (Exception e) {
            //Log.d(LOGNAME, "Error opening PDF");
        }

        pageImage.init(totalPages, pdfLayout, undo, redo);
        pdfLayout.addView(pageImage);
        pdfLayout.setEnabled(true);

    }


    @Override
    protected void onDestroy() {
        //Log.d(LOGNAME, "destroyed");
        try {
            closeRenderer();
        } catch (IOException ex) {
            //Log.d(LOGNAME, "Unable to close PDF renderer");
        }
        super.onDestroy();
    }






    private void openRenderer(Context context) throws IOException {
        // In this sample, we read a PDF from the assets directory.
        File file = new File(context.getCacheDir(), FILENAME);
        if (!file.exists()) {
            // pdfRenderer cannot handle the resource directly,
            // so extract it into the local cache directory.
            InputStream asset = this.getResources().openRawResource(FILERESID);
            FileOutputStream output = new FileOutputStream(file);
            final byte[] buffer = new byte[1024];
            int size;
            while ((size = asset.read(buffer)) != -1) {
                output.write(buffer, 0, size);
            }
            asset.close();
            output.close();
        }
        parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);

        // capture PDF data
        // all this just to get a handle to the actual PDF representation
        if (parcelFileDescriptor != null) {
            pdfRenderer = new PdfRenderer(parcelFileDescriptor);
        }

        totalPages = pdfRenderer.getPageCount();
    }


    private void showPage(int index){
        if (totalPages <= index) {
            return;
        }
        // Close the current page before opening another one.
        if (null != currentPage) {
            currentPage.close();
        }

        // Use `openPage` to open a specific page in PDF.
        currentPage = pdfRenderer.openPage(index);
        // Important: the destination bitmap must be ARGB (not RGB).
        Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);

        // Here, we render the page onto the Bitmap.
        // To render a portion of the page, use the second and third parameter. Pass nulls to get the default result.
        // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        // Display the page
        label.setText("Page "+ (index+1) + "/" + totalPages);
        if(index == pdfRenderer.getPageCount() - 1){
            next.setColorFilter(Color.parseColor("#BEBEBE"));
        }
        else if(index == 0){
            prev.setColorFilter(Color.parseColor("#BEBEBE"));
        }
        else{
            next.setColorFilter(Color.BLACK);
            prev.setColorFilter(Color.BLACK);
        }
        pageImage.setImage(bitmap);
        pageImage.setPageIndex(index);
    }

    private void closeRenderer() throws IOException {
        if (null != currentPage) {
            currentPage.close();
            currentPage = null;
        }
        pdfRenderer.close();
        parcelFileDescriptor.close();
    }

    public void selectPen(View view){
        pageImage.setPen();
        pen.setColorFilter(null);
        highlighter.setColorFilter(Color.parseColor("#BEBEBE"));
        eraser.setColorFilter(Color.parseColor("#BEBEBE"));
        viewButton.setColorFilter(Color.parseColor("#BEBEBE"));
    }

    public void selectHighlighter(View view){
        pageImage.setHighlighter();
        pen.setColorFilter(Color.parseColor("#BEBEBE"));
        highlighter.setColorFilter(null);
        eraser.setColorFilter(Color.parseColor("#BEBEBE"));
        viewButton.setColorFilter(Color.parseColor("#BEBEBE"));
    }

    public void selectEraser(View view){
        pageImage.setEraser();
        pen.setColorFilter(Color.parseColor("#BEBEBE"));
        highlighter.setColorFilter(Color.parseColor("#BEBEBE"));
        eraser.setColorFilter(null);
        viewButton.setColorFilter(Color.parseColor("#BEBEBE"));
    }

    public void nextPage(View view){
        if(totalPages <= pageIndex) return;
        pageIndex += 1;
        showPage(pageIndex);
        //Log.d("Action", "nextPage");
    }

    public void prevPage(View view){
        if(pageIndex == 0) return;
        pageIndex -= 1;
        showPage(pageIndex);
        //Log.d("Action", "prevPage");
    }

    public void undo(View view){
        pageImage.undo();
    }

    public void redo(View view){
        pageImage.redo();
    }

    public void home(View view){
        this.moveTaskToBack(true);
    }

    public void resetView(View view){
        pageImage.resetView();
    }

    public void setView(View view){
        pageImage.setView();
        viewButton.setColorFilter(null);
        pen.setColorFilter(Color.parseColor("#BEBEBE"));
        highlighter.setColorFilter(Color.parseColor("#BEBEBE"));
        eraser.setColorFilter(Color.parseColor("#BEBEBE"));
    }
}