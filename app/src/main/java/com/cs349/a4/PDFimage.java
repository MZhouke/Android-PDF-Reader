package com.cs349.a4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.gesture.GestureUtils;
import android.graphics.*;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

@SuppressLint("AppCompatCustomView")
public class PDFimage extends ImageView {

    //stores the path and the tool used for this path
    class MyPath{
        Path path;
        int tool;
        int tag;
        public MyPath(Path path, int tool, int tag){
            this.path = path;
            this.tool = tool;
            this.tag = tag;
        }

        public Path getPath(){
            return this.path;
        }

        public int getTool(){
            return tool;
        }

        public int getTag(){
            return tag;
        }

        public void setErased(){
            this.tool += 2;
        }

        public void resetErased(){
            this.tool -= 2;
        }
    }

    final String LOGNAME = "pdf_image";

    //widgets
    ConstraintLayout pdfLayout;
    ImageButton redoButton;
    ImageButton undoButton;

    //zoom & pan
    ScaleGestureDetector mScaleGestureDetector;
    float mScaleFactor = 1.0f;
    GestureDetector mPanDetector;
    float x_translate = 0;
    float y_translate = 0;

    // drawing path
    Path path = null;
    MyPath myPath = null;
    ArrayList<ArrayList<MyPath>> paths;
    ArrayList<ArrayList<MyPath>> undoStack;
    ArrayList<ArrayList<MyPath>> redoStack;
    int pageIndex;
    // 0 = pen, 1 = highlighter, 2 = eraser+pen 3 = eraser+highlighter
    int currTool;

    // image to display
    Bitmap bitmap;
    Paint pen_paint = new Paint(Color.TRANSPARENT);
    Paint highlighter_paint = new Paint(Color.TRANSPARENT);
    Boolean annote;

    // constructor
    public PDFimage(Context context) {
        super(context);
        annote = false;
        pageIndex = 0;
        paths = new ArrayList();
        undoStack = new ArrayList();
        redoStack = new ArrayList();
        mScaleGestureDetector = new ScaleGestureDetector(this.getContext(), new ScaleListener());
        mPanDetector = new GestureDetector(this.getContext(), new PanListener());
    }

    //initialization
    public void init(int pages, ConstraintLayout pdfLayout, ImageButton undo, ImageButton redo){
        for(int i = 0; i < pages; i++){
            ArrayList<MyPath> tempPaths_1 = new ArrayList<>();
            paths.add(tempPaths_1);
            ArrayList<MyPath> tempPaths_2 = new ArrayList<>();
            undoStack.add(tempPaths_2);
            ArrayList<MyPath> tempPaths_3 = new ArrayList<>();
            redoStack.add(tempPaths_3);
        }
        this.pdfLayout = pdfLayout;
        this.undoButton = undo;
        this.redoButton = redo;
        //init paint
        pen_paint.setStrokeWidth(3);
        pen_paint.setColor(Color.BLUE);
        pen_paint.setStyle(Paint.Style.STROKE);
        pen_paint.setStrokeJoin(Paint.Join.ROUND);
        pen_paint.setStrokeCap(Paint.Cap.ROUND);
        pen_paint.setAlpha(250);
        highlighter_paint.setStrokeWidth(30);
        highlighter_paint.setColor(Color.YELLOW);
        highlighter_paint.setStyle(Paint.Style.STROKE);
        highlighter_paint.setStrokeJoin(Paint.Join.ROUND);
        highlighter_paint.setStrokeCap(Paint.Cap.ROUND);
        highlighter_paint.setAlpha(70);
    }

    // capture touch events (down/move/up) to create a path
    // and use that to create a stroke that we can draw
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getPointerCount() == 2){
            mScaleGestureDetector.onTouchEvent(event);
            mPanDetector.onTouchEvent(event);
            this.setScaleX(mScaleFactor);
            this.setScaleY(mScaleFactor);
            //this.scrollBy(x_translate, y_translate);
            this.setX(this.getX() - x_translate*mScaleFactor);
            this.setY(this.getY() - y_translate*mScaleFactor);
            x_translate = 0;
            y_translate = 0;
            return true;
        }

        if(annote != true) return true;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //Log.d(LOGNAME, "Action down");
                path = new Path();
                path.moveTo(event.getX(), event.getY());
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                //Log.d(LOGNAME, "Action move");
                path.lineTo(event.getX(), event.getY());
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                //Log.d(LOGNAME, "Action up");
                if(currTool >= 2){
                    path = null;
                    invalidate();
                    return true;
                };
                myPath = new MyPath(path,currTool, paths.get(pageIndex).size());
                paths.get(pageIndex).add(myPath);
                undoStack.get(pageIndex).add(myPath);
                path = null;
                myPath = null;
                invalidate();
                break;
        }
        return true;
    }

    private void erase(Path erasePath) {
        for(MyPath delPath : paths.get(pageIndex)){
            if(delPath.getTool() >= 2) continue;
            Path pathToDetect = delPath.getPath();
            Region region1 = new Region();
            Region region2 = new Region();
            Region clip = new Region(pdfLayout.getLeft(), pdfLayout.getTop(), pdfLayout.getRight(), pdfLayout.getBottom());
            region1.setPath(erasePath, clip);
            region2.setPath(pathToDetect, clip);
            if (!region1.quickReject(region2)){
                //Log.d("path","collided" + delPath.getTool());
                //change the path to erased
                delPath.setErased();
                //add a temp erased path to undoStack to represent the action, note no extra path will ever be added to paths variable
                MyPath erasedPath = new MyPath(delPath.getPath(), delPath.getTool(), delPath.getTag());
                undoStack.get(pageIndex).add(erasedPath);
                return;
            }
        }
    }


    // set image as background
    public void setImage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    //set pageIndex
    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    //set pen
    public void setPen(){
        annote = true;
        currTool = 0;

    }

    //set highlighter
    public void setHighlighter(){
        annote = true;
        currTool = 1;
    }

    //set Eraser
    public void setEraser(){
        annote = true;
        currTool = 2;
    }

    //undo
    public void undo(){
        int undoStackSize = undoStack.get(pageIndex).size();
        if(undoStackSize == 0) return;
        MyPath undoItem = undoStack.get(pageIndex).get(undoStackSize - 1);
        if(undoItem.getTool() >= 2){
            //find corresponding erased path and change is to unerased
            for(int i = 0; i < paths.get(pageIndex).size(); i++){
                MyPath curPath = paths.get(pageIndex).get(i);
                if(curPath.getTag() == undoItem.getTag()){
                    curPath.resetErased();
                    break;
                }
            }

        }
        else{
            paths.get(pageIndex).remove(undoItem);
        }
        undoStack.get(pageIndex).remove(undoStackSize - 1);
        redoStack.get(pageIndex).add(undoItem);

    }

    //redo
    public void redo(){
        int redoStackSize = redoStack.get(pageIndex).size();
        if(redoStackSize == 0) return;
        MyPath redoItem = redoStack.get(pageIndex).get(redoStackSize - 1);
        if(redoItem.getTool() >= 2){
            //find corresponding unerased path and change it to erased
            for(int i = 0; i < paths.get(pageIndex).size(); i++){
                MyPath curPath = paths.get(pageIndex).get(i);
                if(curPath.getTag() == redoItem.getTag()){
                    curPath.setErased();
                    break;
                }
            }
        }
        else{
            //add the deleted path back
            paths.get(pageIndex).add(redoItem);
        }
        undoStack.get(pageIndex).add(redoItem);
        redoStack.get(pageIndex).remove(redoStackSize - 1);
    }

    //resetview
    public void resetView(){
        mScaleFactor = 1.0f;
        x_translate = 0;
        y_translate = 0;
        this.setScaleX(mScaleFactor);
        this.setScaleY(mScaleFactor);
        this.setX(pdfLayout.getX());
    }

    @Override
    public void onDraw(Canvas canvas) {
        // draw background
        if (bitmap != null) {
            this.setImageBitmap(bitmap);
        }
        // draw stored lines over it
        for (MyPath path : paths.get(pageIndex)) {
            if(path.getTool() == 0){
                canvas.drawPath(path.getPath(), pen_paint);
            }
            else if (path.getTool() == 1){
                canvas.drawPath(path.getPath(), highlighter_paint);
                //Log.d("path","drew" + path.getTool());
            }
        }
        //Log.d(LOGNAME, paths.get(pageIndex).size()+" paths drawn on page" + pageIndex);


        //draw current line if user is still drawing
        if(path != null){
            if(currTool == 0){
                canvas.drawPath(path, pen_paint);
            }
            else if(currTool == 1){
                canvas.drawPath(path, highlighter_paint);
            }
            else{
                erase(path);
            }
        }

        //unable and disable undo redo button
        if(undoStack.get(pageIndex).size() == 0 ){
            undoButton.setColorFilter(Color.parseColor("#BEBEBE"));
        }
        else{
            undoButton.setColorFilter(Color.parseColor("#9C27B0"));
        }

        if(redoStack.get(pageIndex).size() == 0 ){
            redoButton.setColorFilter(Color.parseColor("#BEBEBE"));
        }
        else{
            redoButton.setColorFilter(Color.parseColor("#9C27B0"));
        }



        super.onDraw(canvas);
    }


    //scaleListener
    public class PanListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            //Log.d("pan", "detected");
            x_translate = distanceX;
            y_translate = distanceY;
            return true;
        }

    }

    //scaleListener
    public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            //Log.d("pan", "detected");
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f,
                    Math.min(mScaleFactor, 10.0f));
            return true;
        }

    }




}

