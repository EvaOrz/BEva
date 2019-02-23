package cn.com.modernmedia.anim;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * 下沉动画
 * 
 * @author ZhuQiao
 * 
 */
public class SinkAnimation extends Animation {
	private Camera mCamera;
	private final float mCenterX;
	private final float mCenterY;
	private final float mDepthZ;
	private final boolean zoomIn;

	public SinkAnimation(float centerX, float centerY, float depthZ,
			boolean zoomIn) {
		mCenterX = centerX;
		mCenterY = centerY;
		mDepthZ = depthZ;
		this.zoomIn = zoomIn;
	}

	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		mCamera = new Camera();
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		final float centerX = mCenterX;
		final float centerY = mCenterY;
		final Camera camera = mCamera;

		final Matrix matrix = t.getMatrix();

		camera.save();
		if (zoomIn) {
			camera.translate(0, 0, mDepthZ * interpolatedTime);
		} else {
			camera.translate(0, 0, mDepthZ * (1.0f - interpolatedTime));
		}
		camera.getMatrix(matrix);
		camera.restore();

		matrix.preTranslate(-centerX, -centerY);
		matrix.postTranslate(centerX, centerY);
	}

}
