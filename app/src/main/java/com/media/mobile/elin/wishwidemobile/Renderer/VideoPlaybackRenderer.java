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
import com.media.mobile.elin.wishwidemobile.Activity.VideoPlayback;
import com.media.mobile.elin.wishwidemobile.Control.SampleAppRendererControl;
import com.media.mobile.elin.wishwidemobile.Model.GameSettingVO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;


// The renderer class for the VideoPlayback sample.
public class VideoPlaybackRenderer implements GLSurfaceView.Renderer, SampleAppRendererControl
{
    private static final String LOGTAG = "VideoPlaybackRenderer";
    
    SampleApplicationSession_Video vuforiaAppSession;
    SampleAppRenderer_Video mSampleAppRendererVideo;


    // Video Playback Rendering Specific
    private int videoPlaybackShaderID = 0;
    private int videoPlaybackVertexHandle = 0;
    private int videoPlaybackTexCoordHandle = 0;
    private int videoPlaybackMVPMatrixHandle = 0;
    private int videoPlaybackTexSamplerOESHandle = 0;

    // Video Playback Textures for the two targets
    int videoPlaybackTextureID;
    
    // Keyframe and icon rendering specific
    private int keyframeShaderID = 0;
    private int keyframeVertexHandle = 0;
    private int keyframeTexCoordHandle = 0;
    private int keyframeMVPMatrixHandle = 0;
    private int keyframeTexSampler2DHandle = 0;

    // Trackable dimensions
    Vec3F targetPositiveDimensions;
    
    static int NUM_QUAD_INDEX = 6;

    //캐릭터 랜덤 위치에 띄우게 하기 위한 변수
    public static int randNum = new Random().nextInt(3);

    //Object Size
    private  final float TARGETAREA = 1f;

    double quadVerticesArray[] = { -1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, 1.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f };
    
    double quadTexCoordsArray[] = { 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f };
    
    double quadNormalsArray[] = { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, };
    
    short quadIndicesArray[] = { 0, 1, 2, 2, 3, 0 };
    
    Buffer quadVertices, quadTexCoords, quadIndices, quadNormals;
    
    private boolean mIsActive = false;
    private Matrix44F tappingProjectionMatrix = null;

    VideoPlayback mActivity;

    private boolean mIsRecognizedMarker = false;

    GameSettingVO mGameSettingVO;
    
    // Needed to calculate whether a screen tap is inside the target
    Matrix44F modelViewMatrix;
    //The Object ModelViewMatrix corresponding to the target marker
    Matrix44F m_objectsmodelViewMatrix[];

    //cpyoon
    //objects position setting {x,y,z}
    private float m_translates[][]= new float[10][6];

    private Vector<Texture> mTextures;
    private Handler mHandler;

    public void setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    // These hold the aspect ratio of both the video and the
    // keyframe
    float keyframeQuadAspectRatio;
    
    
    public VideoPlaybackRenderer(VideoPlayback activity, SampleApplicationSession_Video session)
    {
        
        mActivity = activity;
        vuforiaAppSession = session;
        mIsRecognizedMarker = false;


        mGameSettingVO = mActivity.mGameSettingVO;


        // SampleAppRenderer_Video used to encapsulate the use of RenderingPrimitives setting
        // the device mode AR/VR and stereo mode
        mSampleAppRendererVideo = new SampleAppRenderer_Video(this, mActivity, Device.MODE.MODE_AR, false, 0.01f, 5f);


        targetPositiveDimensions = new Vec3F();
        
        modelViewMatrix = new Matrix44F();

        m_objectsmodelViewMatrix = new Matrix44F[mGameSettingVO.getTotalCharacterCnt()];

        for(int i = 0;i < mGameSettingVO.getTotalCharacterCnt(); i++)
            m_objectsmodelViewMatrix[i] = new Matrix44F();
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
            : 1.0f);    //투명도 안돼면 0.0f 바꿀 것

        // Now generate the OpenGL texture objects and add settings
        for (Texture t : mTextures)
        {
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
        GLES20.glGenTextures(1, new int[]{videoPlaybackTextureID}, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, videoPlaybackTextureID);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);


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
        keyframeQuadAspectRatio = (float) mTextures.get(0).mHeight / (float) mTextures.get(0).mWidth;

        quadVertices = fillBuffer(quadVerticesArray);
        quadTexCoords = fillBuffer(quadTexCoordsArray);
        quadIndices = fillBuffer(quadIndicesArray);
        quadNormals = fillBuffer(quadNormalsArray);

//        int totalCharacterCnt = mGameSettingVO.getTotalCharacterCnt();

//        m_scales = new float[totalCharacterCnt][3];
//        m_rotates = new float[totalCharacterCnt][3];

//        for (int i = 0; i < totalCharacterCnt; i++) {
//            for (int j = 0; j < 3; j++) {
////                if (j == 0) {   //x축
////                    m_translates[i][j] = randFloat(-1.0f, 1.0f);
////                }
////                else if (j == 1) {  //y축
////                    m_translates[i][j] = randFloat(-1.7f, 1.7f);
////                }
////                else {  //z축
////                    m_translates[i][j] = randFloat(0.2f, 0.2f);
////                }
////
////                m_scales[i][j] = 0.1f;//randFloat(0.04f, 0.1f);
//                m_rotates[i][j] = randFloat(-60f, 60f);
////                Log.d(LOGTAG, "[" + i + "][" + j + "]: " +  m_translates[i][j]);
//            }
//        }

        m_translates[0][0] = 0f;  //1 - x
        m_translates[0][1] = 0.3f;  //1 - y
        m_translates[0][2] = 2.0f;  //2 - x
        m_translates[0][3] = 2.0f;  //2 - y
        m_translates[0][4] = 0.19f;  //3 - x
        m_translates[0][5] = 0.0f;  //3 - y

        m_translates[1][0] = 1.0f;
        m_translates[1][1] = 0.4f;
        m_translates[1][2] = -2.240f;
        m_translates[1][3] = 0.0f;
        m_translates[1][4] = -1.17f;
        m_translates[1][5] = 1.109f;

        m_translates[2][0] = -1.0f;
        m_translates[2][1] = 1.0f;
        m_translates[2][2] = 0.0f;
        m_translates[2][3] = 0.0f;
        m_translates[2][4] = 2.0f;
        m_translates[2][5] = -0.55f;

        m_translates[3][0] = -.5f;
        m_translates[3][1] = 0.9f;
        m_translates[3][2] = 1.2f;
        m_translates[3][3] = -0.8f;
        m_translates[3][4] = 0.43f;
        m_translates[3][5] = 1.71f;

        m_translates[4][0] = 0.5f;
        m_translates[4][1] = 0.7f;
        m_translates[4][2] = -1.456f;
        m_translates[4][3] = 1.35f;
        m_translates[4][4] = 0.101f;
        m_translates[4][5] = 0.89f;

        m_translates[5][0] = 2.0f;
        m_translates[5][1] = 2.0f;
        m_translates[5][2] = 0.0f;
        m_translates[5][3] = 0.0f;
        m_translates[5][4] = -1.71f;
        m_translates[5][5] = -0.07f;

        m_translates[6][0] = -0.67f;
        m_translates[6][1] = 1.3f;
        m_translates[6][2] = 0.67f;
        m_translates[6][3] = 1.46f;
        m_translates[6][4] = 1.999f;
        m_translates[6][5] = 0.501f;

        m_translates[7][0] = 2.112f;
        m_translates[7][1] = 0.0f;
        m_translates[7][2] = -0.136f;
        m_translates[7][3] = 2.342f;
        m_translates[7][4] = -1.82f;
        m_translates[7][5] = 1.605f;

        m_translates[8][0] = 1.0f;
        m_translates[8][1] = -0.912f;
        m_translates[8][2] = -0.559f;
        m_translates[8][3] = 1.0f;
        m_translates[8][4] = 2.15f;
        m_translates[8][5] = 1.61f;

        m_translates[9][0] = -1.54f;
        m_translates[9][1] = -0.5f;
        m_translates[9][2] = 1.651f;
        m_translates[9][3] = 1.0f;
        m_translates[9][4] = 1.0f;
        m_translates[9][5] = -0.39f;
    }


    //float 랜던 값 생성
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

        GLES20.glEnable(GLES20.GL_BLEND);


        //마커 인식 전
        if (!mIsRecognizedMarker) {
            mActivity.showGame1Guide("매장 테이블 위에 있는 마커를 인식해주세요.");
        }


        if(tappingProjectionMatrix == null)
        {
            tappingProjectionMatrix = new Matrix44F();
            tappingProjectionMatrix.setData(projectionMatrix);
        }

        float temp[] = { 0.0f, 0.0f, 0.0f };
        targetPositiveDimensions.setData(temp);

        // Did we find any trackables this frame?
        if (state.getNumTrackableResults() > 0)
        {
            // Get the trackable:
            TrackableResult trackableResult = state.getTrackableResult(0);

            ImageTarget imageTarget = (ImageTarget) trackableResult.getTrackable();
            ImageTargetResult imageTargetResult = (ImageTargetResult)trackableResult;

            String imageTargetName = imageTarget.getName();
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
//            for (MarkerVO markerVO : mGameSettingVO.getMarkerList()) {
//                if (imageTargetName.equals(markerVO.getMarkerId())) {
//                    characterNum = mGameSettingVO.getTotalCharacterCnt();
//                    touchEventCode = 2;
//                }
//            }

            if (imageTargetName.equals("stones")) {
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


            // We store the modelview matrix to be used later by the tap
            // calculation
            float matrix1[] = Tool.convertPose2GLMatrix(trackableResult.getPose()).getData();
//            matrix1[0]=0.042890187f;
//            matrix1[1]=-0.998534f;
//            matrix1[2]=-0.033018753f;
//            matrix1[4]=-0.7565703f;
//            matrix1[5]=-0.05404617f;
//            matrix1[6]=0.65167516f;
//            matrix1[8]=-0.65250427f;
//            matrix1[9]=-0.002969464f;
//            matrix1[10]=-0.75777924f;
            modelViewMatrix.setData(matrix1);

            targetPositiveDimensions = imageTarget.getSize();

            //camera position and orientation 추출
            Matrix44F modelviewmatrix = Tool.convertPose2GLMatrix(trackableResult.getPose());
            Matrix44F inverseMV = SampleMath.Matrix44FInverse(modelviewmatrix);
            Matrix44F invTranspMV = SampleMath.Matrix44FTranspose(inverseMV);


//            float[] tempMatrix = invTranspMV.getData();
//            tempMatrix[12] = 0.8465671f;
//            tempMatrix[13] = 0.017374111f;
//            tempMatrix[14] = 1.1985523f;
//            invTranspMV.setData(tempMatrix);

//            System.out.println("modelviewmatrix(" + modelviewmatrix.getData()[0] + " " + modelviewmatrix.getData()[1] + " " + modelviewmatrix.getData()[2] + " " + modelviewmatrix.getData()[3]);
//            System.out.println(modelviewmatrix.getData()[4] + " " + modelviewmatrix.getData()[5] + " " + modelviewmatrix.getData()[6] + " " + modelviewmatrix.getData()[7]);
//            System.out.println(modelviewmatrix.getData()[8] + " " + modelviewmatrix.getData()[9] + " " + modelviewmatrix.getData()[10] + " " + modelviewmatrix.getData()[11]);
//            System.out.println(modelviewmatrix.getData()[12] + " " + modelviewmatrix.getData()[13] + " " + modelviewmatrix.getData()[14] + " " + modelviewmatrix.getData()[15] + ")");
//            System.out.println("바뀌었다");


            //추출한 데이터 다시 되돌리기(index 12~15는 약간 오차 있음)
            Matrix44F textMatrix = SampleMath.Matrix44FTranspose(invTranspMV);
            Matrix44F testInverseMV = SampleMath.Matrix44FInverse(textMatrix);


            // The pose delivers the center of the target, thus the dimensions
            // go from -width/2 to width/2, same for height
            temp[0] = targetPositiveDimensions.getData()[0] / 2.0f;
            temp[1] = targetPositiveDimensions.getData()[1] / 2.0f;
            targetPositiveDimensions.setData(temp);


            //make objects
            //if you want to create an object selectively, must change for syntax
            for(int trans=0; trans < characterNum; trans++) {
                // If the movie is ready to start playing or it has reached the end
                // of playback we render the keyframe
                mActivity.showGame1Guide("캐릭터를 하나 잡으세요.");
                mIsRecognizedMarker = true;

                float[] modelViewMatrixKeyframe = Tool.convertPose2GLMatrix(trackableResult.getPose()).getData();
                float[] translateMatrix = Tool.convertPose2GLMatrix(trackableResult.getPose()).getData();
                //Matrix.setRotateM(modelViewMatrixKeyframe,0,90,0,0,-1f);
//                translateMatrix[0]= 0.03351045f;
//                translateMatrix[1]=-0.9990541f;
//                translateMatrix[2]=-0.027713306f;
//                translateMatrix[4]=-0.33674696f;
//                translateMatrix[5]=-0.037394043f;
//                translateMatrix[6]= 0.9408524f;
//                translateMatrix[8]=-0.65250427f;
//                translateMatrix[9]=-0.002969464f;
//                translateMatrix[10]=-0.75777924f;
                float[] modelViewProjectionKeyframe = new float[16];
                // Matrix.translateM(modelViewMatrixKeyframe, 0, 0.0f, 0.0f,
                // targetPositiveDimensions[currentTarget].getData()[0]);

                // Here we use the aspect ratio of the keyframe since it
                // is likely that it is not a perfect square


                float ratio = 1.0f;
                if (mTextures.get(0).mSuccess)
                    ratio = keyframeQuadAspectRatio;
                else
                    ratio = targetPositiveDimensions.getData()[1] / targetPositiveDimensions.getData()[0];


                //Method to translate using m_translates
//                Log.e("Test1", translateMatrix[0]+ ", "+translateMatrix[1]+ ", "+translateMatrix[2]+ ", "+translateMatrix[3]+ ", ");
//                Log.e("Test2", translateMatrix[4]+ ", "+translateMatrix[5]+ ", "+translateMatrix[6]+ ", "+translateMatrix[7]+ ", ");
//                Log.e("Test3", translateMatrix[8]+ ", "+translateMatrix[9]+ ", "+translateMatrix[10]+ ", "+translateMatrix[11]+ ", ");
//                Log.e("Test4", translateMatrix[12]+ ", "+translateMatrix[13]+ ", "+translateMatrix[14]+ ", "+translateMatrix[15]+ ", ");

//                Log.e("Test5", translateMatrix[0]+ ", "+translateMatrix[1]+ ", "+translateMatrix[2]+ ", "+translateMatrix[3]+ ", ");
//                Log.e("Test6", translateMatrix[4]+ ", "+translateMatrix[5]+ ", "+translateMatrix[6]+ ", "+translateMatrix[7]+ ", ");
//                Log.e("Test7", translateMatrix[8]+ ", "+translateMatrix[9]+ ", "+translateMatrix[10]+ ", "+translateMatrix[11]+ ", ");
//                Log.e("Test8", translateMatrix[12]+ ", "+translateMatrix[13]+ ", "+translateMatrix[14]+ ", "+translateMatrix[15]+ ", ");
                //위치 설정
                translateMatrix[4]= -0.33674696f;
                translateMatrix[5]= -0.037394043f;
                translateMatrix[6]= 0.9408524f;


                int indexRL, indexTB = 0;    //좌우 위치와 상하 위치가 저장되어 있는 index
                if (randNum == 0) {
                    indexRL = 0;
                    indexTB = 1;
                }
                else if (randNum == 1) {
                    indexRL = 2;
                    indexTB = 3;
                }
                else {
                    indexRL = 4;
                    indexTB = 5;
                }


                Matrix.translateM(translateMatrix,
                        0, m_translates[trans][0],
                        1.0f,
                        m_translates[trans][1]);

                //회전 설정
                modelViewMatrixKeyframe[0]= 0.005300097f;
                modelViewMatrixKeyframe[1]=-0.99907345f;
                modelViewMatrixKeyframe[2]=-0.042711787f;
                modelViewMatrixKeyframe[4]= -0.025353879f;
                modelViewMatrixKeyframe[5]=-0.04283292f;
                modelViewMatrixKeyframe[6]=0.99876046f;
                modelViewMatrixKeyframe[8]=-0.99966455f;
                modelViewMatrixKeyframe[9]=-0.004210618f;
                modelViewMatrixKeyframe[10]=-0.0255574f;
                modelViewMatrixKeyframe[12]=translateMatrix[12];
                modelViewMatrixKeyframe[13]=translateMatrix[13];
                modelViewMatrixKeyframe[14]=translateMatrix[14];

                Matrix.rotateM(modelViewMatrixKeyframe,  0,  90.0f,  90.0f,  0.0f,  0.0f);

                Matrix.scaleM(modelViewMatrixKeyframe, 0,
                        targetPositiveDimensions.getData()[0],
                        targetPositiveDimensions.getData()[0],
                        targetPositiveDimensions.getData()[0]);


//                if (!isFixedState) {
                    Matrix.multiplyMM(modelViewProjectionKeyframe, 0, projectionMatrix, 0, modelViewMatrixKeyframe, 0);
//                    fixedModelViewMatrixKeyframe.add(trans, modelViewProjectionKeyframe);
//
//                }
//                else {
//                    Matrix.multiplyMM(fixedModelViewMatrixKeyframe.get(trans), 0, projectionMatrix, 0, modelViewMatrixKeyframe, 0);
//                }

                //cpyoon
                //get object modelviewmatrix
                //used for touch event handling
                Matrix44F matrix = new Matrix44F();
                matrix.setData(modelViewMatrixKeyframe);
                m_objectsmodelViewMatrix[trans] =  matrix ;

                GLES20.glUseProgram(keyframeShaderID);

                // Prepare for rendering the keyframe
                GLES20.glVertexAttribPointer(keyframeVertexHandle, 3, GLES20.GL_FLOAT, false, 0, quadVertices);
                GLES20.glVertexAttribPointer(keyframeTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, quadTexCoords);

                GLES20.glEnableVertexAttribArray(keyframeVertexHandle);
                GLES20.glEnableVertexAttribArray(keyframeTexCoordHandle);

                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

                //png 바탕 투명화
                GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

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

        GLES20.glDisable(GLES20.GL_BLEND);
        
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        Renderer.getInstance().end();
//        GLSurfaceView.Renderer.getInstance().end();
        
    }


    //cpyoon
    //touch event handling
    //check touched object corresponding target marker
    public int isTapOnScreenInsideTarget(float x, float y)
    {
        for(int trans = 0; trans < m_objectsmodelViewMatrix.length; trans++) {
            // Here we calculate that the touch event is inside the target
            Vec3F intersection;
            DisplayMetrics metrics = new DisplayMetrics();
            mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            intersection = SampleMath.getPointToPlaneIntersection(SampleMath.Matrix44FInverse(tappingProjectionMatrix), m_objectsmodelViewMatrix[trans], metrics.widthPixels, metrics.heightPixels, new Vec2F(x, y), new Vec3F(0, 0, 0), new Vec3F(0, 0, 1));


            // The target returns as pose the center of the trackable. The following
            // if-statement simply checks that the tap is within this range
            if (intersection != null) {
                if ((intersection.getData()[0] >= -(TARGETAREA) && (intersection.getData()[0] <= (TARGETAREA))) && (intersection.getData()[1] >= -(TARGETAREA)) && (intersection.getData()[1]<= (TARGETAREA)))
                    return trans;
            }
        }
        return -1;
    }


    public void setTextures(Vector<Texture> textures)
    {
        mTextures = textures;
    }
    
}
