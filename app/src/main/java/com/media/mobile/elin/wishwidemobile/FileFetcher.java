package com.media.mobile.elin.wishwidemobile;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import com.media.mobile.elin.wishwidemobile.Activity.MainActivity;
import com.media.mobile.elin.wishwidemobile.Activity.VideoPlayback;
import com.media.mobile.elin.wishwidemobile.Model.GameCharacterFileVO;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FileFetcher {
    private static final String TAG = "FileFetcher";
    private final static String strAppPath = Environment.getExternalStorageDirectory().getPath() + "/Wishwide/game/";



    //Cloud에서 다운로드한 파일을 콘텐츠 폴더에 파일명을 "콘텐츠명.확장자"로 저장
    public void downloadFile(GameCharacterFileVO gameCharacterFileVO) {
//        Log.i(TAG, "콘텐츠 아이템 확인 : " + gameCharacterFileVO);
//        File contentsDir = new File(strAppPath);
//        int readByte = 0;
//
//        //콘텐츠 관리 폴더 존재 확인, 없으면 생성
//        boolean isContentExist = checkDirectory(contentsDir);
//
//        Log.d(TAG, "폴더 존재 확인: " + isContentExist);
//        if (!isContentExist) {
//            makeDirectory(contentsDir);
//        } else {
//            removeFileAll();
//        }
//
//        HttpURLConnection connection = null;
//        InputStream inputStream = null;
//        FileOutputStream outputStream = null;
//        try {
//            //인터넷 연결
//            URL url = new URL(gameCharacterFileVO.getCharacterFileUrl());
//            connection = (HttpURLConnection) url.openConnection();
//            connection.connect();
//
//            inputStream = new BufferedInputStream(connection.getInputStream());
//
//            outputStream = new FileOutputStream(strAppPath + gameCharacterFileVO.getCharacterFileName());
//            byte[] buffer = new byte[connection.getContentLength()];
//            Log.d(TAG, "크기 확인: "+ connection.getContentLength());
//
//            while ((readByte = inputStream.read(buffer)) != -1) {
//                outputStream.write(buffer, 0, readByte);
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//
//            try {
//                if (outputStream != null) {
//                    outputStream.close();
//                }
//
//                if (inputStream != null) {
//                    inputStream.close();
//                }
//
//                if (connection != null) {
//                    connection.disconnect();
//                }
//
//            } catch (IOException e) {
//            }
//        }
        new FileDownloadTask(gameCharacterFileVO).execute();
    }

    public class FileDownloadTask extends AsyncTask<String, Void, String> {
        private final GameCharacterFileVO mGameCharacterFileVO;

        public FileDownloadTask(GameCharacterFileVO gameCharacterFileVO) {
            mGameCharacterFileVO = gameCharacterFileVO;
        }

        @Override
        protected String doInBackground(String... params) {

            Log.i(TAG, "콘텐츠 아이템 확인 : " + mGameCharacterFileVO);
            File contentsDir = new File(strAppPath);
            int readByte = 0;

            //콘텐츠 관리 폴더 존재 확인, 없으면 생성
            boolean isContentExist = checkDirectory(contentsDir);

            Log.d(TAG, "폴더 존재 확인: " + isContentExist);
            if (!isContentExist) {
                makeDirectory(contentsDir);
            } else {
//                removeFileAll();
            }

            HttpURLConnection connection = null;
            InputStream inputStream = null;
            FileOutputStream outputStream = null;
            try {
                //인터넷 연결
                URL url = new URL(mGameCharacterFileVO.getCharacterFileUrl());
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                inputStream = new BufferedInputStream(connection.getInputStream());

                outputStream = new FileOutputStream(strAppPath + mGameCharacterFileVO.getCharacterFileName());
                byte[] buffer = new byte[connection.getContentLength()];

                while ((readByte = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, readByte);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }

                    if (inputStream != null) {
                        inputStream.close();
                    }

                    if (connection != null) {
                        connection.disconnect();
                    }

                } catch (IOException e) {
                }
            }
            return strAppPath + mGameCharacterFileVO.getCharacterFileName();
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d(TAG, "캐릭터 파일 저장 경로 확인: " + s);

            while (!checkCompleteFile(s, mGameCharacterFileVO.getCharacterFileSize())) {
            }

            VideoPlayback.mCompletedFileCnt++;
        }
    }

    public List<String> getFilePaths() {
        File[] files = new File(strAppPath).listFiles();

        List<String> filePaths = new ArrayList();

        for (int i = 0; i < files.length; i++) {
            filePaths.add(files[i].getPath());
        }

        return filePaths;
    }

    //스케줄 파일 가져오기
    public InputStream readFile(String path) {
        Log.d(TAG, "최신 스케줄 경로: " + path);
        FileInputStream inputStream = null;

        try {
            inputStream = new FileInputStream(path);

            return inputStream;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //파일 디렉토리 존재 여부 체크
    public boolean checkDirectory(File dir) {
        return dir.exists();
    }

    //파일 디렉토리 생성
    public boolean makeDirectory(File dir) {
        return dir.mkdirs();
    }

    //이전버전의 스케줄 파일 및 관련 없는 파일들 삭제
    public void removeFileAll() {
        File[] files = new File(strAppPath).listFiles();

        for (File file : files) {
            //폴더일 경우 폴더 내 파일들 먼저 제거 후 폴더 제거
            if (file.isDirectory()) {
                File[] scheduleFiles = file.listFiles();

                if (scheduleFiles.length > 0) {
                    for (int i = 0; i < scheduleFiles.length; i++) {
                        scheduleFiles[i].delete();
                    }
                }

                file.delete();
            }
            //파일일 경우 파일 제거
            else if (file.isFile()) {
                file.delete();
            }
        }
    }

    public boolean checkCompleteFile(String filePath, int fileSize) {
        File file = new File(filePath);

        if (file.exists()) {
            //콘텐츠 파일 있음
            //기기에 저장된 콘텐츠 파일과 클라우드에 저장된 콘텐츠 사이즈가 같은지 확인
            boolean isSameFile = fileSize == file.length();

            Log.i(TAG, fileSize + " 파일 사이즈 확인 " + file.length());

            if(isSameFile) {
                return true;
            }
        }

        return false;
    }
}
