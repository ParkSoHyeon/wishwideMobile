///*===============================================================================
//Copyright (c) 2016 PTC Inc. All Rights Reserved.
//
//Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.
//
//Vuforia is a trademark of PTC Inc., registered in the United States and other
//countries.
//===============================================================================*/
//
//package com.media.mobile.elin.wishwidemobile.Renderer;
//
//import android.annotation.SuppressLint;
//import android.opengl.GLES11Ext;
//import android.opengl.GLES20;
//import android.opengl.GLSurfaceView;
//import android.opengl.Matrix;
//import android.os.Handler;
//import android.os.Message;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import com.media.mobile.elin.wishwidemobile.Control.SampleAppRendererControl;
//import com.media.mobile.elin.wishwidemobile.Session.SampleApplicationSession_Video;
//import com.media.mobile.elin.wishwidemobile.utils.SampleMath;
//import com.media.mobile.elin.wishwidemobile.utils.SampleUtils;
//import com.media.mobile.elin.wishwidemobile.utils.Texture;
//
//import javax.microedition.khronos.egl.EGLConfig;
//import javax.microedition.khronos.opengles.GL10;
//import java.nio.Buffer;
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Vector;
//
//
//// The renderer class for the VideoPlayback sample.
//public class VideoPlaybackRenderer2 implements GLSurfaceView.Renderer, SampleAppRendererControl
//{
//    private static final String LOGTAG = "VideoPlaybackRenderer";
//
//    SampleApplicationSession_Video vuforiaAppSession;
//    SampleAppRenderer_Video mSampleAppRendererVideo;
//
//    //객체 위치 고정에 필요한 변수 선언
//    private List<float[]> fixedModelViewMatrixKeyframe;
//    private boolean isFixedState = false;
//
//    // Video Playback Rendering Specific
//    private int videoPlaybackShaderID = 0;
//    private int videoPlaybackVertexHandle = 0;
//    private int videoPlaybackTexCoordHandle = 0;
//    private int videoPlaybackMVPMatrixHandle = 0;
//    private int videoPlaybackTexSamplerOESHandle = 0;
//
//    // Video Playback Textures for the two targets
//    int videoPlaybackTextureID[] = new int[VideoPlayback.NUM_TARGETS];
//
//    // Keyframe and icon rendering specific
//    private int keyframeShaderID = 0;
//    private int keyframeVertexHandle = 0;
//    private int keyframeTexCoordHandle = 0;
//    private int keyframeMVPMatrixHandle = 0;
//    private int keyframeTexSampler2DHandle = 0;
//
//    // We cannot use the default texture coordinates of the quad since these
//    // will change depending on the video itself
//    private float videoQuadTextureCoords[] = { 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, };
//
//    // This variable will hold the transformed coordinates (changes every frame)
//    private float videoQuadTextureCoordsTransformedStones[] = { 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, };
//
//    private float videoQuadTextureCoordsTransformedChips[] = { 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, };
//
//    // Trackable dimensions
//    Vec3F targetPositiveDimensions[] = new Vec3F[VideoPlayback.NUM_TARGETS];
//
//    static int NUM_QUAD_VERTEX = 4;
//    static int NUM_QUAD_INDEX = 6;
//
//    //Object Size
//    private  final float TARGETAREA = 1f;
//
//    double quadVerticesArray[] = { -1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, 1.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f };
//
//    double quadTexCoordsArray[] = { 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f };
//
//    double quadNormalsArray[] = { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, };
//
//    short quadIndicesArray[] = { 0, 1, 2, 2, 3, 0 };
//
//    Buffer quadVertices, quadTexCoords, quadIndices, quadNormals;
//
//    private boolean mIsActive = false;
//    private Matrix44F tappingProjectionMatrix = null;
//
//    VideoPlayback mActivity;
//
//    // Needed to calculate whether a screen tap is inside the target
//    Matrix44F modelViewMatrix[] = new Matrix44F[VideoPlayback.NUM_TARGETS];
//    //The Object ModelViewMatrix corresponding to the target marker
//    Matrix44F m_objectsmodelViewMatrix[][]= new Matrix44F[VideoPlayback.NUM_TARGETS][5];
//
//    //cpyoon
//    //objects position setting {x,y,z}
//    private float m_translates[][]= new float[10][6];
//
//    private Vector<Texture> mTextures;
//    private Handler mHandler;
//
//    public void setmHandler(Handler mHandler) {
//        this.mHandler = mHandler;
//    }
//
//    // These hold the aspect ratio of both the video and the
//    // keyframe
//    float videoQuadAspectRatio[] = new float[VideoPlayback.NUM_TARGETS];
//    float keyframeQuadAspectRatio[] = new float[VideoPlayback.NUM_TARGETS];
//
//
//    public VideoPlaybackRenderer2(VideoPlayback activity, SampleApplicationSession_Video session)
//    {
//
//        mActivity = activity;
//        vuforiaAppSession = session;
//
//        // SampleAppRenderer_Video used to encapsulate the use of RenderingPrimitives setting
//        // the device mode AR/VR and stereo mode
//        mSampleAppRendererVideo = new SampleAppRenderer_Video(this, mActivity, Device.MODE.MODE_AR, false, 0.01f, 5f);
//
//
//        for (int i = 0; i < VideoPlayback.NUM_TARGETS; i++)
//            targetPositiveDimensions[i] = new Vec3F();
//
//        for (int i = 0; i < VideoPlayback.NUM_TARGETS; i++)
//            modelViewMatrix[i] = new Matrix44F();
//
//        for(int i=0;i<VideoPlayback.NUM_TARGETS;i++)
//            for( int j=0;j<5;j++)
//                m_objectsmodelViewMatrix[i][j] = new Matrix44F();
//
//
//        m_translates[0][0] = 0f;  //1 - x
//        m_translates[0][1] = 0.3f;  //1 - y
//        m_translates[0][2] = 2.0f;  //2 - x
//        m_translates[0][3] = 2.0f;  //2 - y
//        m_translates[0][4] = 0.19f;  //3 - x
//        m_translates[0][5] = 0.0f;  //3 - y
//
//        m_translates[1][0] = 1.0f;
//        m_translates[1][1] = 0.4f;
//        m_translates[1][2] = -2.240f;
//        m_translates[1][3] = 0.0f;
//        m_translates[1][4] = -1.17f;
//        m_translates[1][5] = 1.109f;
//
//        m_translates[2][0] = -1.0f;
//        m_translates[2][1] = 1.0f;
//        m_translates[2][2] = 0.0f;
//        m_translates[2][3] = 0.0f;
//        m_translates[2][4] = 2.0f;
//        m_translates[2][5] = -0.55f;
//
//        m_translates[3][0] = -.5f;
//        m_translates[3][1] = 0.9f;
//        m_translates[3][2] = 1.2f;
//        m_translates[3][3] = -0.8f;
//        m_translates[3][4] = 0.43f;
//        m_translates[3][5] = 1.71f;
//
//        m_translates[4][0] = 0.5f;
//        m_translates[4][1] = 0.7f;
//        m_translates[4][2] = -1.456f;
//        m_translates[4][3] = 1.35f;
//        m_translates[4][4] = 0.101f;
//        m_translates[4][5] = 0.89f;
//
//        m_translates[5][0] = 2.0f;
//        m_translates[5][1] = 2.0f;
//        m_translates[5][2] = 0.0f;
//        m_translates[5][3] = 0.0f;
//        m_translates[5][4] = -1.71f;
//        m_translates[5][5] = -0.07f;
//
//        m_translates[6][0] = -0.67f;
//        m_translates[6][1] = 1.3f;
//        m_translates[6][2] = 0.67f;
//        m_translates[6][3] = 1.46f;
//        m_translates[6][4] = 1.999f;
//        m_translates[6][5] = 0.501f;
//
//        m_translates[7][0] = 2.112f;
//        m_translates[7][1] = 0.0f;
//        m_translates[7][2] = -0.136f;
//        m_translates[7][3] = 2.342f;
//        m_translates[7][4] = -1.82f;
//        m_translates[7][5] = 1.605f;
//
//        m_translates[8][0] = 1.0f;
//        m_translates[8][1] = -0.912f;
//        m_translates[8][2] = -0.559f;
//        m_translates[8][3] = 1.0f;
//        m_translates[8][4] = 2.15f;
//        m_translates[8][5] = 1.61f;
//
//        m_translates[9][0] = -1.54f;
//        m_translates[9][1] = -0.5f;
//        m_translates[9][2] = 1.651f;
//        m_translates[9][3] = 1.0f;
//        m_translates[9][4] = 1.0f;
//        m_translates[9][5] = -0.39f;
//    }
//
//
//    // Called when the surface is created or recreated.
//    public void onSurfaceCreated(GL10 gl, EGLConfig config)
//    {
//        // Call function to initialize rendering:
//        // The video texture is also created on this step
//        initRendering();
//
//        // Call Vuforia function to (re)initialize rendering after first use
//        // or after OpenGL ES context was lost (e.g. after onPause/onResume):
//        Vuforia.onSurfaceCreated();
//
//        mSampleAppRendererVideo.onSurfaceCreated();
//
//    }
//
//    public void updateConfiguration()
//    {
//        mSampleAppRendererVideo.onConfigurationChanged(mIsActive);
//    }
//
//    // Called when the surface changed size.
//    public void onSurfaceChanged(GL10 gl, int width, int height)
//    {
//        // Call Vuforia function to handle render surface size changes:
//        Vuforia.onSurfaceChanged(width, height);
//
//        // RenderingPrimitives to be updated when some rendering change is done
//        mSampleAppRendererVideo.onConfigurationChanged(mIsActive);
//
//        // Upon every on pause the movie had to be unloaded to release resources
//        // Thus, upon every surface create or surface change this has to be
//        // reloaded
//        // See:
//        // http://developer.android.com/reference/android/media/MediaPlayer.html#release()
//
//    }
//
//
//    // Called to draw the current frame.
//    public void onDrawFrame(GL10 gl)
//    {
//        if (!mIsActive)
//            return;
//
//        // Call our function to render content from SampleAppRenderer_Video class
//        mSampleAppRendererVideo.render();
//    }
//
//
//    public void setActive(boolean active)
//    {
//        mIsActive = active;
//
//        if(mIsActive)
//            mSampleAppRendererVideo.configureVideoBackground();
//    }
//
//
//    @SuppressLint("InlinedApi")
//    void initRendering()
//    {
//        Log.d(LOGTAG, "VideoPlayback VideoPlaybackRenderer initRendering");
//
//        // Define clear color
//        GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f
//            : 1.0f);
//
//        // Now generate the OpenGL texture objects and add settings
//        for (Texture t : mTextures)
//        {
//            // Here we create the textures for the keyframe
//            // and for all the icons
//            GLES20.glGenTextures(1, t.mTextureID, 0);
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t.mTextureID[0]);
//            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
//            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
//            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, t.mWidth, t.mHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, t.mData);
//        }
//
//        // Now we create the texture for the video data from the movie
//        // IMPORTANT:
//        // Notice that the textures are not typical GL_TEXTURE_2D textures
//        // but instead are GL_TEXTURE_EXTERNAL_OES extension textures
//        // This is required by the Android SurfaceTexture
//        for (int i = 0; i < VideoPlayback.NUM_TARGETS; i++)
//        {
//            GLES20.glGenTextures(VideoPlayback.NUM_TARGETS-1, videoPlaybackTextureID, i);
//            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, videoPlaybackTextureID[i]);
//            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
//            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
//        }
//
//        // The first shader is the one that will display the video data of the
//        // movie
//        // (it is aware of the GL_TEXTURE_EXTERNAL_OES extension)
//        videoPlaybackShaderID = SampleUtils.createProgramFromShaderSrc(
//            VideoPlaybackShaders.VIDEO_PLAYBACK_VERTEX_SHADER,
//            VideoPlaybackShaders.VIDEO_PLAYBACK_FRAGMENT_SHADER);
//        videoPlaybackVertexHandle = GLES20.glGetAttribLocation(
//            videoPlaybackShaderID, "vertexPosition");
//        videoPlaybackTexCoordHandle = GLES20.glGetAttribLocation(
//            videoPlaybackShaderID, "vertexTexCoord");
//        videoPlaybackMVPMatrixHandle = GLES20.glGetUniformLocation(
//            videoPlaybackShaderID, "modelViewProjectionMatrix");
//        videoPlaybackTexSamplerOESHandle = GLES20.glGetUniformLocation(
//            videoPlaybackShaderID, "texSamplerOES");
//
//        // This is a simpler shader with regular 2D textures
//        keyframeShaderID = SampleUtils.createProgramFromShaderSrc(KeyFrameShaders.KEY_FRAME_VERTEX_SHADER, KeyFrameShaders.KEY_FRAME_FRAGMENT_SHADER);
//        keyframeVertexHandle = GLES20.glGetAttribLocation(keyframeShaderID, "vertexPosition");
//        keyframeTexCoordHandle = GLES20.glGetAttribLocation(keyframeShaderID, "vertexTexCoord");
//        keyframeMVPMatrixHandle = GLES20.glGetUniformLocation(keyframeShaderID, "modelViewProjectionMatrix");
//        keyframeTexSampler2DHandle = GLES20.glGetUniformLocation(keyframeShaderID, "texSampler2D");
//
//        //put marker size
//        keyframeQuadAspectRatio[VideoPlayback.STONES] = (float) mTextures.get(0).mHeight / (float) mTextures.get(0).mWidth;
//        keyframeQuadAspectRatio[VideoPlayback.CHIPS] = (float) mTextures.get(1).mHeight / (float) mTextures.get(1).mWidth;
//
//        quadVertices = fillBuffer(quadVerticesArray);
//        quadTexCoords = fillBuffer(quadTexCoordsArray);
//        quadIndices = fillBuffer(quadIndicesArray);
//        quadNormals = fillBuffer(quadNormalsArray);
//
//    }
//
//
//    private Buffer fillBuffer(double[] array)
//    {
//        // Convert to floats because OpenGL doesnt work on doubles, and manually
//        // casting each input value would take too much time.
//        ByteBuffer bb = ByteBuffer.allocateDirect(4 * array.length); // each float takes 4 bytes
//        bb.order(ByteOrder.LITTLE_ENDIAN);
//        for (double d : array)
//            bb.putFloat((float) d);
//        bb.rewind();
//
//        return bb;
//
//    }
//
//
//    private Buffer fillBuffer(short[] array)
//    {
//        ByteBuffer bb = ByteBuffer.allocateDirect(2 * array.length); // each
//        // short
//        // takes 2
//        // bytes
//        bb.order(ByteOrder.LITTLE_ENDIAN);
//        for (short s : array)
//            bb.putShort(s);
//        bb.rewind();
//
//        return bb;
//
//    }
//
//
//    private Buffer fillBuffer(float[] array)
//    {
//        // Convert to floats because OpenGL doesnt work on doubles, and manually
//        // casting each input value would take too much time.
//        ByteBuffer bb = ByteBuffer.allocateDirect(4 * array.length); // each float takes 4 bytes
//        bb.order(ByteOrder.LITTLE_ENDIAN);
//        for (float d : array)
//            bb.putFloat(d);
//        bb.rewind();
//
//        return bb;
//
//    }
//
//
//    @SuppressLint("InlinedApi")
//    // The render function called from SampleAppRendering by using RenderingPrimitives views.
//    // The state is owned by SampleAppRenderer_Video which is controlling it's lifecycle.
//    // State should not be cached outside this method.
//    public void renderFrame(State state, float[] projectionMatrix)
//    {
//        Beacon_Marker beacon_marker =mActivity.beacon_marker;
//        if(beacon_marker == null) return;
//        // Renders video background replacing Renderer.DrawVideoBackground()
//        mSampleAppRendererVideo.renderVideoBackground();
//
//        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
//
//        // We must detect if background reflection is active and adjust the
//        // culling direction.
//        // If the reflection is active, this means the post matrix has been
//        // reflected as well,
//        // therefore standard counter clockwise face culling will result in
//        // "inside out" models.
//        GLES20.glEnable(GLES20.GL_CULL_FACE);
//        GLES20.glCullFace(GLES20.GL_BACK);
//
//        if(tappingProjectionMatrix == null)
//        {
//            tappingProjectionMatrix = new Matrix44F();
//            tappingProjectionMatrix.setData(projectionMatrix);
//        }
//
//        float temp[] = { 0.0f, 0.0f, 0.0f };
//        for (int i = 0; i < VideoPlayback.NUM_TARGETS; i++)
//        {
//            targetPositiveDimensions[i].setData(temp);
//        }
//
//        // Did we find any trackables this frame?
//        if (state.getNumTrackableResults() > 0)
//        {
//            // Get the trackable:
//            TrackableResult trackableResult = state.getTrackableResult(0);
//
//            ImageTarget imageTarget = (ImageTarget) trackableResult.getTrackable();
//            ImageTargetResult imageTargetResult = (ImageTargetResult)trackableResult;
//
//            String name = imageTarget.getName();
//            int num = 0;
//            int type = -1;
//
//            //hong
//            //check whether the marker is included in the target beacon
//            for(Marker_data md : beacon_marker.marker_datas)
//            {
//                if(md.str_name.equals(name))
//                {
//                    num = md.object_num;
//                    type = md.type;
//                    break;
//                }
//            }
//            //is not same
//            if(type == -1) return;
//            //if event type is 1(Virtual button touch)
//            if(type == 1) {
//                for (int virtual = 0; virtual < imageTargetResult.getNumVirtualButtons(); virtual++) {
//                    VirtualButtonResult btnResult = imageTargetResult.getVirtualButtonResult(virtual);
//                    if (btnResult.isPressed()) {
//                        Message msg = mHandler.obtainMessage();
//                        msg.obj = new String(imageTarget.getName());
//                        msg.arg2 = virtual;
//                        msg.sendToTarget();
//                    }
//                }
//            }
//            int currentTarget;
//
//            // We store the modelview matrix to be used later by the tap
//            // calculation
//            if (imageTarget.getName().compareTo("stones") == 0)
//                currentTarget = VideoPlayback.STONES;
//            else
//                currentTarget = VideoPlayback.CHIPS;
//            float matrix1[] = Tool.convertPose2GLMatrix(trackableResult.getPose()).getData();
////            matrix1[0]=0.042890187f;
////            matrix1[1]=-0.998534f;
////            matrix1[2]=-0.033018753f;
////            matrix1[4]=-0.7565703f;
////            matrix1[5]=-0.05404617f;
////            matrix1[6]=0.65167516f;
////            matrix1[8]=-0.65250427f;
////            matrix1[9]=-0.002969464f;
////            matrix1[10]=-0.75777924f;
//            modelViewMatrix[currentTarget].setData(matrix1);
//
//            targetPositiveDimensions[currentTarget] = imageTarget.getSize();
//
//            //camera position and orientation 추출
//            Matrix44F modelviewmatrix = Tool.convertPose2GLMatrix(trackableResult.getPose());
//            Matrix44F inverseMV = SampleMath.Matrix44FInverse(modelviewmatrix);
//            Matrix44F invTranspMV = SampleMath.Matrix44FTranspose(inverseMV);
//
//
////            float[] tempMatrix = invTranspMV.getData();
////            tempMatrix[12] = 0.8465671f;
////            tempMatrix[13] = 0.017374111f;
////            tempMatrix[14] = 1.1985523f;
////            invTranspMV.setData(tempMatrix);
//
////            System.out.println("modelviewmatrix(" + modelviewmatrix.getData()[0] + " " + modelviewmatrix.getData()[1] + " " + modelviewmatrix.getData()[2] + " " + modelviewmatrix.getData()[3]);
////            System.out.println(modelviewmatrix.getData()[4] + " " + modelviewmatrix.getData()[5] + " " + modelviewmatrix.getData()[6] + " " + modelviewmatrix.getData()[7]);
////            System.out.println(modelviewmatrix.getData()[8] + " " + modelviewmatrix.getData()[9] + " " + modelviewmatrix.getData()[10] + " " + modelviewmatrix.getData()[11]);
////            System.out.println(modelviewmatrix.getData()[12] + " " + modelviewmatrix.getData()[13] + " " + modelviewmatrix.getData()[14] + " " + modelviewmatrix.getData()[15] + ")");
////            System.out.println("바뀌었다");
//
//
//            //추출한 데이터 다시 되돌리기(index 12~15는 약간 오차 있음)
//            Matrix44F textMatrix = SampleMath.Matrix44FTranspose(invTranspMV);
//            Matrix44F testInverseMV = SampleMath.Matrix44FInverse(textMatrix);
//
//
//            // The pose delivers the center of the target, thus the dimensions
//            // go from -width/2 to width/2, same for height
//            temp[0] = targetPositiveDimensions[currentTarget].getData()[0] / 2.0f;
//            temp[1] = targetPositiveDimensions[currentTarget].getData()[1] / 2.0f;
//            targetPositiveDimensions[currentTarget].setData(temp);
//
//            if (fixedModelViewMatrixKeyframe == null) {
//                fixedModelViewMatrixKeyframe = new ArrayList<>();
//            }
//
//            //cpyoon
//            //make objects
//            //if you want to create an object selectively, must change for syntax
//            for(int trans=0;trans < num;trans++) {
//                // If the movie is ready to start playing or it has reached the end
//                // of playback we render the keyframe
//
//                float[] modelViewMatrixKeyframe = Tool.convertPose2GLMatrix(trackableResult.getPose()).getData();
//                float[] translateMatrix = Tool.convertPose2GLMatrix(trackableResult.getPose()).getData();
//                //Matrix.setRotateM(modelViewMatrixKeyframe,0,90,0,0,-1f);
////                translateMatrix[0]= 0.03351045f;
////                translateMatrix[1]=-0.9990541f;
////                translateMatrix[2]=-0.027713306f;
////                translateMatrix[4]=-0.33674696f;
////                translateMatrix[5]=-0.037394043f;
////                translateMatrix[6]= 0.9408524f;
////                translateMatrix[8]=-0.65250427f;
////                translateMatrix[9]=-0.002969464f;
////                translateMatrix[10]=-0.75777924f;
//                float[] modelViewProjectionKeyframe = new float[16];
//                // Matrix.translateM(modelViewMatrixKeyframe, 0, 0.0f, 0.0f,
//                // targetPositiveDimensions[currentTarget].getData()[0]);
//
//                // Here we use the aspect ratio of the keyframe since it
//                // is likely that it is not a perfect square
//
//
//                float ratio = 1.0f;
//                if (mTextures.get(currentTarget).mSuccess)
//                    ratio = keyframeQuadAspectRatio[currentTarget];
//                else
//                    ratio = targetPositiveDimensions[currentTarget].getData()[1] / targetPositiveDimensions[currentTarget].getData()[0];
//
//                //cpyoon
//                //Method to translate using m_translates
////                Log.e("Test1", translateMatrix[0]+ ", "+translateMatrix[1]+ ", "+translateMatrix[2]+ ", "+translateMatrix[3]+ ", ");
////                Log.e("Test2", translateMatrix[4]+ ", "+translateMatrix[5]+ ", "+translateMatrix[6]+ ", "+translateMatrix[7]+ ", ");
////                Log.e("Test3", translateMatrix[8]+ ", "+translateMatrix[9]+ ", "+translateMatrix[10]+ ", "+translateMatrix[11]+ ", ");
////                Log.e("Test4", translateMatrix[12]+ ", "+translateMatrix[13]+ ", "+translateMatrix[14]+ ", "+translateMatrix[15]+ ", ");
//
////                Log.e("Test5", translateMatrix[0]+ ", "+translateMatrix[1]+ ", "+translateMatrix[2]+ ", "+translateMatrix[3]+ ", ");
////                Log.e("Test6", translateMatrix[4]+ ", "+translateMatrix[5]+ ", "+translateMatrix[6]+ ", "+translateMatrix[7]+ ", ");
////                Log.e("Test7", translateMatrix[8]+ ", "+translateMatrix[9]+ ", "+translateMatrix[10]+ ", "+translateMatrix[11]+ ", ");
////                Log.e("Test8", translateMatrix[12]+ ", "+translateMatrix[13]+ ", "+translateMatrix[14]+ ", "+translateMatrix[15]+ ", ");
//                //위치 설정
//                translateMatrix[4]= -0.33674696f;
//                translateMatrix[5]= -0.037394043f;
//                translateMatrix[6]= 0.9408524f;
//
//                Matrix.translateM(translateMatrix,
//                        0, m_translates[trans][0],
//                        1.0f,
//                        m_translates[trans][1]);
//
//                //회전 설정
//                modelViewMatrixKeyframe[0]= 0.005300097f;
//                modelViewMatrixKeyframe[1]=-0.99907345f;
//                modelViewMatrixKeyframe[2]=-0.042711787f;
//                modelViewMatrixKeyframe[4]= -0.025353879f;
//                modelViewMatrixKeyframe[5]=-0.04283292f;
//                modelViewMatrixKeyframe[6]=0.99876046f;
//                modelViewMatrixKeyframe[8]=-0.99966455f;
//                modelViewMatrixKeyframe[9]=-0.004210618f;
//                modelViewMatrixKeyframe[10]=-0.0255574f;
//                modelViewMatrixKeyframe[12]=translateMatrix[12];
//                modelViewMatrixKeyframe[13]=translateMatrix[13];
//                modelViewMatrixKeyframe[14]=translateMatrix[14];
//
//                Matrix.rotateM(modelViewMatrixKeyframe,  0,  90.0f,  90.0f,  0.0f,  0.0f);
//
//                Matrix.scaleM(modelViewMatrixKeyframe, 0,
//                        targetPositiveDimensions[currentTarget].getData()[0],
//                        targetPositiveDimensions[currentTarget].getData()[0],
//                        targetPositiveDimensions[currentTarget].getData()[0]);
//
//
////                if (!isFixedState) {
//                    Matrix.multiplyMM(modelViewProjectionKeyframe, 0, projectionMatrix, 0, modelViewMatrixKeyframe, 0);
////                    fixedModelViewMatrixKeyframe.add(trans, modelViewProjectionKeyframe);
////
////                }
////                else {
////                    Matrix.multiplyMM(fixedModelViewMatrixKeyframe.get(trans), 0, projectionMatrix, 0, modelViewMatrixKeyframe, 0);
////                }
//
//                //cpyoon
//                //get object modelviewmatrix
//                //used for touch event handling
//                Matrix44F matrix = new Matrix44F();
//                matrix.setData(modelViewMatrixKeyframe);
//                m_objectsmodelViewMatrix[currentTarget][trans] =  matrix ;
//
//                GLES20.glUseProgram(keyframeShaderID);
//
//                // Prepare for rendering the keyframe
//                GLES20.glVertexAttribPointer(keyframeVertexHandle, 3, GLES20.GL_FLOAT, false, 0, quadVertices);
//                GLES20.glVertexAttribPointer(keyframeTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, quadTexCoords);
//
//                GLES20.glEnableVertexAttribArray(keyframeVertexHandle);
//                GLES20.glEnableVertexAttribArray(keyframeTexCoordHandle);
//
//                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//
//                // The first loaded texture from the assets folder is the
//                // keyframe
//                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures.get(trans).mTextureID[0]);
//                GLES20.glUniformMatrix4fv(keyframeMVPMatrixHandle, 1, false, modelViewProjectionKeyframe, 0);
//                GLES20.glUniform1i(keyframeTexSampler2DHandle, 0);
//
//                // Render
//                GLES20.glDrawElements(GLES20.GL_TRIANGLES, NUM_QUAD_INDEX, GLES20.GL_UNSIGNED_SHORT, quadIndices);
//
//                GLES20.glDisableVertexAttribArray(keyframeVertexHandle);
//                GLES20.glDisableVertexAttribArray(keyframeTexCoordHandle);
//
//                GLES20.glUseProgram(0);
//
//                SampleUtils.checkGLError("VideoPlayback renderFrame");
//            }
//
//            if (fixedModelViewMatrixKeyframe != null) {
//                isFixedState = true;
//            }
//        }
//
//        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
//
//        GLSurfaceView.Renderer.getInstance().end();
//
//    }
//
//
//    //cpyoon
//    //touch event handling
//    //check touched object corresponding target marker
//    public int isTapOnScreenInsideTarget(int target, float x, float y)
//    {
//        for(int trans = 0;trans<mActivity.beacon_marker.marker_datas.get(0).object_num;trans++) {
//            //cpyoon
//            // Here we calculate that the touch event is inside the target
//            Vec3F intersection;
//            DisplayMetrics metrics = new DisplayMetrics();
//            mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
//            intersection = SampleMath.getPointToPlaneIntersection(SampleMath.Matrix44FInverse(tappingProjectionMatrix), m_objectsmodelViewMatrix[target][trans], metrics.widthPixels, metrics.heightPixels, new Vec2F(x, y), new Vec3F(0, 0, 0), new Vec3F(0, 0, 1));
//            //cpyoon
//            // The target returns as pose the center of the trackable. The following
//            // if-statement simply checks that the tap is within this range
//            if (intersection != null) {
//                if ((intersection.getData()[0] >= -(TARGETAREA) && (intersection.getData()[0] <= (TARGETAREA))) && (intersection.getData()[1] >= -(TARGETAREA)) && (intersection.getData()[1]<= (TARGETAREA)))
//                    return trans;
//            }
//        }
//        return -1;
//    }
//
//
////    void setVideoDimensions(int target, float videoWidth, float videoHeight,
////        float[] textureCoordMatrix)
////    {
////        // The quad originaly comes as a perfect square, however, the video
////        // often has a different aspect ration such as 4:3 or 16:9,
////        // To mitigate this we have two options:
////        // 1) We can either scale the width (typically up)
////        // 2) We can scale the height (typically down)
////        // Which one to use is just a matter of preference. This example scales
////        // the height down.
////        // (see the render call in renderFrame)
////        videoQuadAspectRatio[target] = videoHeight / videoWidth;
////
////        float mtx[] = textureCoordMatrix;
////        float tempUVMultRes[] = new float[2];
////
////        if (target == VideoPlayback.STONES)
////        {
////            tempUVMultRes = uvMultMat4f(
////                videoQuadTextureCoordsTransformedStones[0],
////                videoQuadTextureCoordsTransformedStones[1],
////                videoQuadTextureCoords[0], videoQuadTextureCoords[1], mtx);
////            videoQuadTextureCoordsTransformedStones[0] = tempUVMultRes[0];
////            videoQuadTextureCoordsTransformedStones[1] = tempUVMultRes[1];
////            tempUVMultRes = uvMultMat4f(
////                videoQuadTextureCoordsTransformedStones[2],
////                videoQuadTextureCoordsTransformedStones[3],
////                videoQuadTextureCoords[2], videoQuadTextureCoords[3], mtx);
////            videoQuadTextureCoordsTransformedStones[2] = tempUVMultRes[0];
////            videoQuadTextureCoordsTransformedStones[3] = tempUVMultRes[1];
////            tempUVMultRes = uvMultMat4f(
////                videoQuadTextureCoordsTransformedStones[4],
////                videoQuadTextureCoordsTransformedStones[5],
////                videoQuadTextureCoords[4], videoQuadTextureCoords[5], mtx);
////            videoQuadTextureCoordsTransformedStones[4] = tempUVMultRes[0];
////            videoQuadTextureCoordsTransformedStones[5] = tempUVMultRes[1];
////            tempUVMultRes = uvMultMat4f(
////                videoQuadTextureCoordsTransformedStones[6],
////                videoQuadTextureCoordsTransformedStones[7],
////                videoQuadTextureCoords[6], videoQuadTextureCoords[7], mtx);
////            videoQuadTextureCoordsTransformedStones[6] = tempUVMultRes[0];
////            videoQuadTextureCoordsTransformedStones[7] = tempUVMultRes[1];
////        }
////
////        // textureCoordMatrix = mtx;
////    }
//
//
//    // Multiply the UV coordinates by the given transformation matrix
////    float[] uvMultMat4f(float transformedU, float transformedV, float u,
////        float v, float[] pMat)
////    {
////        float x = pMat[0] * u + pMat[4] * v /* + pMat[ 8]*0.f */+ pMat[12]
////            * 1.f;
////        float y = pMat[1] * u + pMat[5] * v /* + pMat[ 9]*0.f */+ pMat[13]
////            * 1.f;
////        // float z = pMat[2]*u + pMat[6]*v + pMat[10]*0.f + pMat[14]*1.f; // We
////        // dont need z and w so we comment them out
////        // float w = pMat[3]*u + pMat[7]*v + pMat[11]*0.f + pMat[15]*1.f;
////
////        float result[] = new float[2];
////        // transformedU = x;
////        // transformedV = y;
////        result[0] = x;
////        result[1] = y;
////        return result;
////    }
//
//    public void setTextures(Vector<Texture> textures)
//    {
//        mTextures = textures;
//    }
//
//}
