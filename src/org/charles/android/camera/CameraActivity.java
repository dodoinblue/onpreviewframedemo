package org.charles.android.camera;

import android.app.Activity;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;

public class CameraActivity extends Activity {
    private SurfaceView mLiveView;
    private ImageButton mShutterButton;
    private SurfaceHolder mLiveViewHolder;
    private Camera mCamera;

    private SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            log("surfaceCreated");
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
            log("surfaceChanged");
            initLiveView();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            log("surfaceDestroyed");

        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("onCreate");

        setContentView(R.layout.main);

        mLiveView = (SurfaceView) findViewById(R.id.live_view);
        mLiveViewHolder = mLiveView.getHolder();
        mLiveViewHolder.addCallback(mSurfaceCallback);

        mShutterButton = (ImageButton) findViewById(R.id.shutter);
        mShutterButton.setOnClickListener(new OnShutterClickedListener());

    }

    @Override
    protected void onResume() {
        super.onResume();
        log("onResume");
        mCamera = Camera.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        log("onPause");
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    public void requestPreviewSize(int width, int height) {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPictureSize(1920, 1080);
        for(Camera.Size size : parameters.getSupportedPreviewSizes()){
            if(size.width * 100 /size.height == width * 100 /height){
                log("Aspect Ratio match! Setting Preview size to : " + size.width + "x" + size.height);
                parameters.setPreviewSize(size.width, size.height);
                mCamera.setParameters(parameters);
                return;
            }
        }
        log("Optimal preview size not found, falling back to default");
    }

    public void initLiveView() {
        if(getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            log("orientation is not landscape, skip");
            return;
        }
        if(mCamera == null) {
            log("initPreview called when mCamera is not available. (before onResume)");
            return;
        }
        requestPreviewSize(640, 480);
        try {
            mCamera.setPreviewDisplay(mLiveViewHolder);
        } catch (Exception e) {
            log(e.getMessage());
        }
        mCamera.startPreview();
    }

    private class OnShutterClickedListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            log("Shutter Clicked: " + System.currentTimeMillis());
            if(isGettingPreview == false) {
                mCamera.setPreviewCallback(previewCallback);
                log("PreviewCallback Started");
                isGettingPreview = true;
            } else {
                mCamera.setPreviewCallback(null);
                isGettingPreview = false;
                log("PreviewCallback Stopped");
            }
        }
    }

    boolean isGettingPreview = false;
    Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] bytes, Camera camera) {
            // data length = preview width x preview height * 1.5
            log("onPreviewFrame: " + System.currentTimeMillis() + " data length: " + bytes.length);
        }
    };

    private void log(String s) {
        Log.i("Charles_TAG", s);
    }
}