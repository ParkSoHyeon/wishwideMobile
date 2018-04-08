/*===============================================================================
Copyright (c) 2016 PTC Inc. All Rights Reserved.

Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package com.media.mobile.elin.wishwidemobile.Renderer;

import android.annotation.SuppressLint;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.media.mobile.elin.wishwidemobile.Activity.Game1;
import com.media.mobile.elin.wishwidemobile.Activity.Game2;
import com.media.mobile.elin.wishwidemobile.Activity.Game2;
import com.media.mobile.elin.wishwidemobile.Control.SampleAppRendererControl;
import com.media.mobile.elin.wishwidemobile.Model.GameSettingVO;
import com.media.mobile.elin.wishwidemobile.Model.MarkerVO;
import com.media.mobile.elin.wishwidemobile.Model.MembershipCustomerVO;
import com.media.mobile.elin.wishwidemobile.Session.SampleApplicationSession_Video;
import com.media.mobile.elin.wishwidemobile.utils.SampleMath;
import com.media.mobile.elin.wishwidemobile.utils.SampleUtils;
import com.media.mobile.elin.wishwidemobile.utils.Texture;
import com.vuforia.*;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;
import java.util.Vector;


// The renderer class for the VideoPlayback sample.
public class Game2Renderer implements GLSurfaceView.Renderer, SampleAppRendererControl
{
    private static final String LOGTAG = "Game2Renderer";

    SampleApplicationSession_Video vuforiaAppSession;
    SampleAppRenderer_Video mSampleAppRendererVideo;

    // Video Playback Rendering Specific
    private int videoPlaybackShaderID = 0;
    private int videoPlaybackVertexHandle = 0;
    private int videoPlaybackTexCoordHandle = 0;
    private int videoPlaybackMVPMatrixHandle = 0;
    private int videoPlaybackTexSamplerOESHandle = 0;

    // Video Playback Textures for the two targets
    int videoPlaybackTextureID[] = new int[Game2.NUM_TARGETS];

    // Keyframe and icon rendering specific
    private int keyframeShaderID = 0;
    private int keyframeVertexHandle = 0;
    private int keyframeTexCoordHandle = 0;
    private int keyframeMVPMatrixHandle = 0;
    private int keyframeTexSampler2DHandle = 0;

    // We cannot use the default texture coordinates of the quad since these
    // will change depending on the video itself
    private float videoQuadTextureCoords[] = { 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, };

    // This variable will hold the transformed coordinates (changes every frame)
    private float videoQuadTextureCoordsTransformedStones[] = { 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, };

    private float videoQuadTextureCoordsTransformedChips[] = { 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, };

    // Trackable dimensions
    Vec3F targetPositiveDimensions[] = new Vec3F[Game2.NUM_TARGETS];

    static int NUM_QUAD_VERTEX = 4;
    static int NUM_QUAD_INDEX = 6;

    private GameSettingVO mGameSettingVO;

    //Object Size
    private  final float TARGETAREA = 1f;

    double quadVerticesArray[] = {
            -1.0f, -1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f };

    double quadTexCoordsArray[] = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f };

    double quadNormalsArray[] = { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, };

    short quadIndicesArray[] = { 0, 1, 2, 2, 3, 0 };

    Buffer quadVertices, quadTexCoords, quadIndices, quadNormals;

    private boolean mIsActive = false;
    private Matrix44F tappingProjectionMatrix = null;

    Game2 mActivity;

    public static int mCorrectedCharacterCnt = 0;
    public static int mReadyCharacterSeq = 0;
    private boolean mIsRecognizedMarker = false;

    // Needed to calculate whether a screen tap is inside the target
    Matrix44F modelViewMatrix[] = new Matrix44F[Game2.NUM_TARGETS];
    //The Object ModelViewMatrix corresponding to the target marker
    Matrix44F m_objectsmodelViewMatrix[][];

    //cpyoon
    //objects position setting {x,y,z}
    //좌표, 크기, 회전 설정하는 변수
    private float m_translates[][];
    private float m_scales[][];
    private float m_rotates[][];

    private Vector<Texture> mTextures;
    private Handler mHandler;

    public void setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    // These hold the aspect ratio of both the video and the
    // keyframe
    float videoQuadAspectRatio[] = new float[Game2.NUM_TARGETS];
    float keyframeQuadAspectRatio[] = new float[Game2.NUM_TARGETS];


    public Game2Renderer(Game2 activity, SampleApplicationSession_Video session)
    {
        
        mActivity = activity;
        vuforiaAppSession = session;
        mCorrectedCharacterCnt = 0;
        mReadyCharacterSeq = 0;
        mIsRecognizedMarker = false;

        // SampleAppRenderer_Video used to encapsulate the use of RenderingPrimitives setting
        // the device mode AR/VR and stereo mode
        mSampleAppRendererVideo = new SampleAppRenderer_Video(this, mActivity, Device.MODE.MODE_AR, false, 0.01f, 5f);

        mGameSettingVO = mActivity.mGameSettingVO;

        m_objectsmodelViewMatrix = new Matrix44F[Game2.NUM_TARGETS][mGameSettingVO.getTotalCharacterCnt()];

        for (int i = 0; i < Game2.NUM_TARGETS; i++)
            targetPositiveDimensions[i] = new Vec3F();
        
        for (int i = 0; i < Game2.NUM_TARGETS; i++)
            modelViewMatrix[i] = new Matrix44F();

        for(int i=0;i<Game2.NUM_TARGETS;i++)
            for( int j=0;j<mGameSettingVO.getTotalCharacterCnt();j++)
                m_objectsmodelViewMatrix[i][j] = new Matrix44F();
    }
    

    // Called when the surface is created or recreated.
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        // Call function to initialize rendering:
        // The video texture is also created on this step
        initRendering();
        
        // Call Vuforia function to (re)initialize rendering after first use
        // or after OpenGL ES context was lost (e.g. after onPause/onResume):
        Vuforia.onSurfaceCreated();

        mSampleAppRendererVideo.onSurfaceCreated();

    }

    public void updateConfiguration()
    {
        mSampleAppRendererVideo.onConfigurationChanged(mIsActive);
    }

    // Called when the surface changed size.
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        // Call Vuforia function to handle render surface size changes:
        Vuforia.onSurfaceChanged(width, height);

        // RenderingPrimitives to be updated when some rendering change is done
        mSampleAppRendererVideo.onConfigurationChanged(mIsActive);

        // Upon every on pause the movie had to be unloaded to release resources
        // Thus, upon every surface create or surface change this has to be
        // reloaded
        // See:
        // http://developer.android.com/reference/android/media/MediaPlayer.html#release()

    }
    
    
    // Called to draw the current frame.
    public void onDrawFrame(GL10 gl)
    {
        if (!mIsActive)
            return;

        // Call our function to render content from SampleAppRenderer_Video class
        mSampleAppRendererVideo.render();
    }


    public void setActive(boolean active)
    {
        mIsActive = active;

        if(mIsActive)
            mSampleAppRendererVideo.configureVideoBackground();
    }


    @SuppressLint("InlinedApi")
    void initRendering()
    {
        Log.d(LOGTAG, "VideoPlayback VideoPlaybackRenderer initRendering");
        
        // Define clear color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f
            : 1.0f);
        
        // Now generate the OpenGL texture objects and add settings
        for (Texture t : mTextures) {
            // Here we create the textures for the keyframe
            // and for all the icons
            GLES20.glGenTextures(1, t.mTextureID, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t.mTextureID[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, t.mWidth, t.mHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, t.mData);
        }
        
        // Now we create the texture for the video data from the movie
        // IMPORTANT:
        // Notice that the textures are not typical GL_TEXTURE_2D textures
        // but instead are GL_TEXTURE_EXTERNAL_OES extension textures
        // This is required by the Android SurfaceTexture
        for (int i = 0; i < Game2.NUM_TARGETS; i++)
        {
            GLES20.glGenTextures(Game2.NUM_TARGETS-1, videoPlaybackTextureID, i);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, videoPlaybackTextureID[i]);
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        }
        
        // The first shader is the one that will display the video data of the
        // movie
        // (it is aware of the GL_TEXTURE_EXTERNAL_OES extension)
        videoPlaybackShaderID = SampleUtils.createProgramFromShaderSrc(
            VideoPlaybackShaders.VIDEO_PLAYBACK_VERTEX_SHADER,
            VideoPlaybackShaders.VIDEO_PLAYBACK_FRAGMENT_SHADER);
        videoPlaybackVertexHandle = GLES20.glGetAttribLocation(
            videoPlaybackShaderID, "vertexPosition");
        videoPlaybackTexCoordHandle = GLES20.glGetAttribLocation(
            videoPlaybackShaderID, "vertexTexCoord");
        videoPlaybackMVPMatrixHandle = GLES20.glGetUniformLocation(
            videoPlaybackShaderID, "modelViewProjectionMatrix");
        videoPlaybackTexSamplerOESHandle = GLES20.glGetUniformLocation(
            videoPlaybackShaderID, "texSamplerOES");
        
        // This is a simpler shader with regular 2D textures
        keyframeShaderID = SampleUtils.createProgramFromShaderSrc(KeyFrameShaders.KEY_FRAME_VERTEX_SHADER, KeyFrameShaders.KEY_FRAME_FRAGMENT_SHADER);
        keyframeVertexHandle = GLES20.glGetAttribLocation(keyframeShaderID, "vertexPosition");
        keyframeTexCoordHandle = GLES20.glGetAttribLocation(keyframeShaderID, "vertexTexCoord");
        keyframeMVPMatrixHandle = GLES20.glGetUniformLocation(keyframeShaderID, "modelViewProjectionMatrix");
        keyframeTexSampler2DHandle = GLES20.glGetUniformLocation(keyframeShaderID, "texSampler2D");

        //put marker size
        keyframeQuadAspectRatio[0] = (float) mTextures.get(0).mHeight / (float) mTextures.get(0).mWidth;
        keyframeQuadAspectRatio[Game2.CHIPS] = (float) mTextures.get(1).mHeight / (float) mTextures.get(1).mWidth;

        quadVertices = fillBuffer(quadVerticesArray);
        quadTexCoords = fillBuffer(quadTexCoordsArray);
        quadIndices = fillBuffer(quadIndicesArray);
        quadNormals = fillBuffer(quadNormalsArray);



        int totalCharacterCnt = mGameSettingVO.getTotalCharacterCnt();

        m_translates = new float[totalCharacterCnt][3];
        m_scales = new float[totalCharacterCnt][3];
        m_rotates = new float[totalCharacterCnt][3];

        for (int i = 0; i < totalCharacterCnt; i++) {
            for (int j = 0; j < 3; j++) {
                if (j == 1) {
                    m_translates[i][j] = randFloat(3.0f, 4.5f);
                }
                else {
                    m_translates[i][j] = randFloat(-0.5f, 0.5f);
                }

                m_scales[i][j] = randFloat(0.04f, 0.1f);
                m_rotates[i][j] = randFloat(-60f, 60f);
                Log.d(LOGTAG, "[" + i + "][" + j + "]: " +  m_translates[i][j]);
            }
        }


    }


    private float randFloat(float min, float max) {

        Random rand = new Random();

        float result = rand.nextFloat() * (max - min) + min;

        return result;

    }


    private Buffer fillBuffer(double[] array)
    {
        // Convert to floats because OpenGL doesnt work on doubles, and manually
        // casting each input value would take too much time.
        ByteBuffer bb = ByteBuffer.allocateDirect(4 * array.length); // each float takes 4 bytes
        bb.order(ByteOrder.LITTLE_ENDIAN);
        for (double d : array)
            bb.putFloat((float) d);
        bb.rewind();

        return bb;

    }


    private Buffer fillBuffer(short[] array)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(2 * array.length); // each
        // short
        // takes 2
        // bytes
        bb.order(ByteOrder.LITTLE_ENDIAN);
        for (short s : array)
            bb.putShort(s);
        bb.rewind();

        return bb;

    }


    private Buffer fillBuffer(float[] array)
    {
        // Convert to floats because OpenGL doesnt work on doubles, and manually
        // casting each input value would take too much time.
        ByteBuffer bb = ByteBuffer.allocateDirect(4 * array.length); // each float takes 4 bytes
        bb.order(ByteOrder.LITTLE_ENDIAN);
        for (float d : array)
            bb.putFloat(d);
        bb.rewind();

        return bb;

    }

    @SuppressLint("InlinedApi")
    // The render function called from SampleAppRendering by using RenderingPrimitives views.
    // The state is owned by SampleAppRenderer_Video which is controlling it's lifecycle.
    // State should not be cached outside this method.
    public void renderFrame(State state, float[] projectionMatrix)
    {

        if(mGameSettingVO == null) return;
        // Renders video background replacing Renderer.DrawVideoBackground()
        mSampleAppRendererVideo.renderVideoBackground();
        
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
       
        // We must detect if background reflection is active and adjust the
        // culling direction.
        // If the reflection is active, this means the post matrix has been
        // reflected as well,
        // therefore standard counter clockwise face culling will result in
        // "inside out" models.
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);

        if (!mIsRecognizedMarker) {
            mActivity.showGame2Guide("매장 테이블 위에 있는 마커를 인식해주세요.");
        }


        if(tappingProjectionMatrix == null)
        {
            tappingProjectionMatrix = new Matrix44F();
            tappingProjectionMatrix.setData(projectionMatrix);
        }

        float temp[] = { 0.0f, 0.0f, 0.0f };
        for (int i = 0; i < Game2.NUM_TARGETS; i++)
        {
            targetPositiveDimensions[i].setData(temp);
        }
        
        // Did we find any trackables this frame?
        for (int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++)
        {
            // Get the trackable:
            TrackableResult trackableResult = state.getTrackableResult(tIdx);
            
            ImageTarget imageTarget = (ImageTarget) trackableResult.getTrackable();
            ImageTargetResult imageTargetResult = (ImageTargetResult)trackableResult;

            String imageTargetName = imageTarget.getName();
            Log.d(LOGTAG, "타켓명: " + imageTargetName);
            int characterNum = 0;
            int touchEventCode = -1;

            //hong
            //check whether the marker is included in the target beacon
            //매장과 지금 매장이 같으면
//            for (int i = 0; i < mGameSettingVO.getMarkerCnt(); i++) {
//                if(mGameSettingVO.getMarkerList().get(i).getMarkerId().equals(imageTargetName))
//                {
//                    characterNum = mGameSettingVO.getTotalCharacterCnt();
//                    touchEventCode = 2;
//                }
//            }
            if(imageTargetName.equals("stones"))
            {
                characterNum = mGameSettingVO.getTotalCharacterCnt();
                touchEventCode = 2;
            }

            //is not same
            if(touchEventCode == -1) return;
            //if event type is 1(Virtual button touch)
            if(touchEventCode == 1) {
                for (int virtual = 0; virtual < imageTargetResult.getNumVirtualButtons(); virtual++) {
                    VirtualButtonResult btnResult = imageTargetResult.getVirtualButtonResult(virtual);
                    if (btnResult.isPressed()) {
                        Message msg = mHandler.obtainMessage();
                        msg.obj = new String(imageTarget.getName());
                        msg.arg2 = virtual;
                        msg.sendToTarget();
                    }
                }
            }
            int currentTarget;
            
            // We store the modelview matrix to be used later by the tap
            // calculation
            currentTarget = 0;

            modelViewMatrix[currentTarget] = Tool.convertPose2GLMatrix(trackableResult.getPose());
            

            targetPositiveDimensions[currentTarget] = imageTarget.getSize();
            
            // The pose delivers the center of the target, thus the dimensions
            // go from -width/2 to width/2, same for height
            temp[0] = targetPositiveDimensions[currentTarget].getData()[0] / 2.0f;
            temp[1] = targetPositiveDimensions[currentTarget].getData()[1] / 2.0f;
            targetPositiveDimensions[currentTarget].setData(temp);

            //cpyoon
            //make objects
            //if you want to create an object selectively, must change for syntax
            for(int trans=mCorrectedCharacterCnt; trans < characterNum; trans++) {
                // If the movie is ready to start playing or it has reached the end
                // of playback we render the keyframe

                mActivity.showGame2Guide("순서대로 터치해 \"" + mGameSettingVO.getMarkerGameValue() + "\"단어를 완성해주세요. " + (mGameSettingVO.getBenefitCnt() - mReadyCharacterSeq) + "개 남았습니다.");
                mIsRecognizedMarker = true;

                float[] modelViewMatrixKeyframe = Tool.convertPose2GLMatrix(trackableResult.getPose()).getData();
                float[] modelViewProjectionKeyframe = new float[16];
                // Matrix.translateM(modelViewMatrixKeyframe, 0, 0.0f, 0.0f,
                // targetPositiveDimensions[currentTarget].getData()[0]);

                // Here we use the aspect ratio of the keyframe since it
                // is likely that it is not a perfect square

                float ratio = 1.0f;
                if (mTextures.get(currentTarget).mSuccess)
                    ratio = keyframeQuadAspectRatio[currentTarget];
                else
                    ratio = targetPositiveDimensions[currentTarget].getData()[1] / targetPositiveDimensions[currentTarget].getData()[0];

                //cpyoon
                //Method to translate using m_translates
                Matrix.translateM(
                        modelViewMatrixKeyframe,
                        0,
                        m_translates[trans][0],
                        m_translates[trans][1],
                        m_translates[trans][2]);

                Matrix.rotateM(modelViewMatrixKeyframe,
                        0,
                        m_rotates[trans][0],
                        m_rotates[trans][1],
                        m_rotates[trans][2],
                        m_rotates[trans][0]);

                Matrix.scaleM(
                        modelViewMatrixKeyframe,
                        0,
                        m_scales[trans][0],
                        m_scales[trans][1],
                        m_scales[trans][2]);

                Matrix.multiplyMM(
                        modelViewProjectionKeyframe,
                        0,
                        projectionMatrix,
                        0,
                        modelViewMatrixKeyframe,
                        0);

//                Matrix.rotateM(
//                        modelViewProjectionKeyframe,
//                        0,
//                        random.nextFloat(),
//                        random.nextFloat(),
//                        random.nextFloat(),
//                        random.nextFloat());

                //cpyoon
                //get object modelviewmatrix
                //used for touch event handling
                Matrix44F matrix = new Matrix44F();
                matrix.setData(modelViewMatrixKeyframe);
                m_objectsmodelViewMatrix[currentTarget][trans] =  matrix;

                GLES20.glUseProgram(keyframeShaderID);

                // Prepare for rendering the keyframe
                GLES20.glVertexAttribPointer(keyframeVertexHandle, 3, GLES20.GL_FLOAT, false, 0, quadVertices);
                GLES20.glVertexAttribPointer(keyframeTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, quadTexCoords);

                GLES20.glEnableVertexAttribArray(keyframeVertexHandle);
                GLES20.glEnableVertexAttribArray(keyframeTexCoordHandle);

                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

                // The first loaded texture from the assets folder is the
                // keyframe
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures.get(trans).mTextureID[0]);
                GLES20.glUniformMatrix4fv(keyframeMVPMatrixHandle, 1, false, modelViewProjectionKeyframe, 0);
                GLES20.glUniform1i(keyframeTexSampler2DHandle, 0);

                // Render
                GLES20.glDrawElements(GLES20.GL_TRIANGLES, NUM_QUAD_INDEX, GLES20.GL_UNSIGNED_SHORT, quadIndices);

                GLES20.glDisableVertexAttribArray(keyframeVertexHandle);
                GLES20.glDisableVertexAttribArray(keyframeTexCoordHandle);

                GLES20.glUseProgram(0);

                SampleUtils.checkGLError("VideoPlayback renderFrame");
            }
        }
        
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        
        Renderer.getInstance().end();
        
    }
    //cpyoon
    //touch event handling
    //check touched object corresponding target marker
    public int isTapOnScreenInsideTarget(int target, float x, float y)
    {
        for(int trans = 0;trans<m_translates.length;trans++) {
            //cpyoon
            // Here we calculate that the touch event is inside the target
            Vec3F intersection;
            DisplayMetrics metrics = new DisplayMetrics();
            mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            intersection = SampleMath.getPointToPlaneIntersection(SampleMath.Matrix44FInverse(tappingProjectionMatrix), m_objectsmodelViewMatrix[target][trans], metrics.widthPixels, metrics.heightPixels, new Vec2F(x, y), new Vec3F(0, 0, 0), new Vec3F(0, 0, 1));
            //cpyoon
            // The target returns as pose the center of the trackable. The following
            // if-statement simply checks that the tap is within this range
                if ((intersection.getData()[0] >= -(TARGETAREA) && (intersection.getData()[0] <= (TARGETAREA))) && (intersection.getData()[1] >= -(TARGETAREA)) && (intersection.getData()[1]<= (TARGETAREA)))
                    return trans;
        }
        return -1;
    }
    
    
    void setVideoDimensions(int target, float videoWidth, float videoHeight,
        float[] textureCoordMatrix)
    {
        // The quad originaly comes as a perfect square, however, the video
        // often has a different aspect ration such as 4:3 or 16:9,
        // To mitigate this we have two options:
        // 1) We can either scale the width (typically up)
        // 2) We can scale the height (typically down)
        // Which one to use is just a matter of preference. This example scales
        // the height down.
        // (see the render call in renderFrame)
        videoQuadAspectRatio[target] = videoHeight / videoWidth;
        
        float mtx[] = textureCoordMatrix;
        float tempUVMultRes[] = new float[2];
        
        if (target == 0)
        {
            tempUVMultRes = uvMultMat4f(
                videoQuadTextureCoordsTransformedStones[0],
                videoQuadTextureCoordsTransformedStones[1],
                videoQuadTextureCoords[0], videoQuadTextureCoords[1], mtx);
            videoQuadTextureCoordsTransformedStones[0] = tempUVMultRes[0];
            videoQuadTextureCoordsTransformedStones[1] = tempUVMultRes[1];
            tempUVMultRes = uvMultMat4f(
                videoQuadTextureCoordsTransformedStones[2],
                videoQuadTextureCoordsTransformedStones[3],
                videoQuadTextureCoords[2], videoQuadTextureCoords[3], mtx);
            videoQuadTextureCoordsTransformedStones[2] = tempUVMultRes[0];
            videoQuadTextureCoordsTransformedStones[3] = tempUVMultRes[1];
            tempUVMultRes = uvMultMat4f(
                videoQuadTextureCoordsTransformedStones[4],
                videoQuadTextureCoordsTransformedStones[5],
                videoQuadTextureCoords[4], videoQuadTextureCoords[5], mtx);
            videoQuadTextureCoordsTransformedStones[4] = tempUVMultRes[0];
            videoQuadTextureCoordsTransformedStones[5] = tempUVMultRes[1];
            tempUVMultRes = uvMultMat4f(
                videoQuadTextureCoordsTransformedStones[6],
                videoQuadTextureCoordsTransformedStones[7],
                videoQuadTextureCoords[6], videoQuadTextureCoords[7], mtx);
            videoQuadTextureCoordsTransformedStones[6] = tempUVMultRes[0];
            videoQuadTextureCoordsTransformedStones[7] = tempUVMultRes[1];
        }
        
        // textureCoordMatrix = mtx;
    }
    
    
    // Multiply the UV coordinates by the given transformation matrix
    float[] uvMultMat4f(float transformedU, float transformedV, float u,
        float v, float[] pMat)
    {
        float x = pMat[0] * u + pMat[4] * v /* + pMat[ 8]*0.f */+ pMat[12]
            * 1.f;
        float y = pMat[1] * u + pMat[5] * v /* + pMat[ 9]*0.f */+ pMat[13]
            * 1.f;
        // float z = pMat[2]*u + pMat[6]*v + pMat[10]*0.f + pMat[14]*1.f; // We
        // dont need z and w so we comment them out
        // float w = pMat[3]*u + pMat[7]*v + pMat[11]*0.f + pMat[15]*1.f;
        
        float result[] = new float[2];
        // transformedU = x;
        // transformedV = y;
        result[0] = x;
        result[1] = y;
        return result;
    }

    public void setTextures(Vector<Texture> textures)
    {
        mTextures = textures;
    }
    
}
