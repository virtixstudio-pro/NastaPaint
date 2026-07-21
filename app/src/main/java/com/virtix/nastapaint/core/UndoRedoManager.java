package com.virtix.nastapaint.core;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import java.util.LinkedList;

public class UndoRedoManager {

    private final LinkedList<Bitmap> undoStack = new LinkedList<>();
    private final LinkedList<Bitmap> redoStack = new LinkedList<>();
    private static final int MAX_UNDO = 10;

    public void saveState(Bitmap current) {
        if (current == null || current.isRecycled()) return;

        Bitmap copy = current.copy(Bitmap.Config.ARGB_8888, true);
        undoStack.push(copy);

        clearStack(redoStack);

        while (undoStack.size() > MAX_UNDO) {
            Bitmap removed = undoStack.removeLast();
            if (removed != null && !removed.isRecycled()) {
                removed.recycle();
            }
        }
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public void undo(Bitmap target, Canvas targetCanvas) {
        if (undoStack.isEmpty() || target == null || targetCanvas == null) return;

        Bitmap currentCopy = target.copy(Bitmap.Config.ARGB_8888, true);
        redoStack.push(currentCopy);

        Bitmap previous = undoStack.pop();
        
        Paint clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        targetCanvas.drawPaint(clearPaint);

        targetCanvas.drawBitmap(previous, 0, 0, null);
        previous.recycle();
    }

    public void redo(Bitmap target, Canvas targetCanvas) {
        if (redoStack.isEmpty() || target == null || targetCanvas == null) return;

        Bitmap currentCopy = target.copy(Bitmap.Config.ARGB_8888, true);
        undoStack.push(currentCopy);

        Bitmap next = redoStack.pop();

        Paint clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        targetCanvas.drawPaint(clearPaint);

        targetCanvas.drawBitmap(next, 0, 0, null);
        next.recycle();
    }

    private void clearStack(LinkedList<Bitmap> stack) {
        for (Bitmap b : stack) {
            if (b != null && !b.isRecycled()) {
                b.recycle();
            }
        }
        stack.clear();
    }

    public void release() {
        clearStack(undoStack);
        clearStack(redoStack);
    }
}
