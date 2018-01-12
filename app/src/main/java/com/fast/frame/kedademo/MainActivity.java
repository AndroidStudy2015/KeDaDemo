package com.fast.frame.kedademo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "tag";
    // 语音听写对象
    private SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog mIatDialog;

    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    private EditText mResultText;
    private Button mBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SpeechUtility.createUtility(MainActivity.this, "appid=59f7d035");
        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(MainActivity.this, mInitListener);

        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
//        mIatDialog = new RecognizerDialog(MainActivity.this, mInitListener);

//        mSharedPreferences = getSharedPreferences(IatSettings.PREFER_NAME,
//                Activity.MODE_PRIVATE);
//        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
//        mResultText = ((EditText) findViewById(R.id.iat_text));
//    }


        mBt = (Button) findViewById(R.id.bt);
        mResultText = (EditText) findViewById(R.id.et);
        mBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setParam();
                int ret = mIat.startListening(new RecognizerListener() {
                    @Override
                    public void onVolumeChanged(int i, byte[] bytes) {
                        Log.d(TAG, "yinliang  " + i);
                        mBt.setText("开始讲话---音量:" + i);
                    }

                    @Override
                    public void onBeginOfSpeech() {
                        mBt.setBackgroundColor(Color.RED);

                        Log.d(TAG, "kaishi ");

                    }

                    @Override
                    public void onEndOfSpeech() {
                        Log.d(TAG, "jieshu  ");

                    }

                    @Override
                    public void onResult(RecognizerResult recognizerResult, boolean b) {
                        Log.d(TAG, recognizerResult.getResultString());
                        printResult(recognizerResult);
                        mBt.setBackgroundColor(Color.CYAN);
                        mBt.setText("点我开始讲话");

                    }

                    @Override
                    public void onError(SpeechError speechError) {
                        Log.d(TAG, "error " + speechError.getErrorDescription());
                        Toast.makeText(MainActivity.this,speechError.getErrorDescription(),Toast.LENGTH_SHORT).show();
                        mBt.setBackgroundColor(Color.CYAN);

                        mBt.setText("点我开始讲话");
                    }

                    @Override
                    public void onEvent(int i, int i1, int i2, Bundle bundle) {

                    }
                });

                if (ret != ErrorCode.SUCCESS) {
                    Log.e("tag", "cuole");
                }

            }
        });


    }


    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Log.e(TAG, "初始化失败，错误码：" + code);
            }
        }
    };


    /**
     * 参数设置
     *
     * @return
     */
    public void setParam() {
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
//        // 清空参数
//        mIat.setParameter(SpeechConstant.PARAMS, null);
//
//        // 设置听写引擎
//        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
//        // 设置返回结果格式
//        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
//
//        this.mTranslateEnable = mSharedPreferences.getBoolean( this.getString(R.string.pref_key_translate), false );
//        if( mTranslateEnable ){
//            Log.i( TAG, "translate enable" );
//            mIat.setParameter( SpeechConstant.ASR_SCH, "1" );
//            mIat.setParameter( SpeechConstant.ADD_CAP, "translate" );
//            mIat.setParameter( SpeechConstant.TRS_SRC, "its" );
//        }
//
//        String lag = mSharedPreferences.getString("iat_language_preference",
//                "mandarin");
//        if (lag.equals("en_us")) {
//            // 设置语言
//            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
//            mIat.setParameter(SpeechConstant.ACCENT, null);
//
//            if( mTranslateEnable ){
//                mIat.setParameter( SpeechConstant.ORI_LANG, "en" );
//                mIat.setParameter( SpeechConstant.TRANS_LANG, "cn" );
//            }
//        } else {
//            // 设置语言
//            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
//            // 设置语言区域
//            mIat.setParameter(SpeechConstant.ACCENT, lag);
//
//            if( mTranslateEnable ){
//                mIat.setParameter( SpeechConstant.ORI_LANG, "cn" );
//                mIat.setParameter( SpeechConstant.TRANS_LANG, "en" );
//            }
//        }
//
//        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
//        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));
//
//        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
//        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));
//
//        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
//        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));
//
//        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
//        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//        mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
//        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
    }

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        mResultText.setText("");
        mResultText.setText(resultBuffer.toString());
        mResultText.setSelection(mResultText.length());
    }

}
