package com.example.cbnativetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import kr.go.mobile.common.v3.CommonBasedAPI;
import kr.go.mobile.common.v3.CommonBasedConstants;
import kr.go.mobile.common.v3.RestrictedAPI;
import kr.go.mobile.common.v3.broker.Response;
import kr.go.mobile.common.v3.broker.SSO;

public class NativeActivity extends AppCompatActivity {

    static Context context;
    String absolutePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);
        absolutePath = new File(getExternalFilesDir("images"), "sample.jpg").getAbsolutePath();
        context = this;
        String dn = getIntent().getStringExtra("dn");
        String cn = getIntent().getStringExtra("cn");
        String ou = getIntent().getStringExtra("ou");

        StringBuilder sb = new StringBuilder();
        sb.append("이름: ").append(cn);
        sb.append("\n소속기관: ").append(ou);
        sb.append("\n(").append(dn).append(")");

        TextView txtView = findViewById(R.id.txtUesrInfo);
        txtView.setText(sb.toString());

//        RestrictedAPI.setKey("aHR0cDovLzEwLjE4MC4yMi43Nzo2NTUzNQ==");
    }

    public void callSBAPI(View view) throws CommonBasedAPI.CommonBaseAPIException {
        CommonBasedAPI.call("IF_MSERVICE", "", new Response.Listener() {
            @Override
            public void onSuccess(Response resp) {
                int code = resp.getErrorCode();
                StringBuilder sb =new StringBuilder();
                if(resp.OK()) {
                    String message = resp.getResponseString();
                    sb.append("Result :: ")
                            .append(", result = ").append(message);
                } else {
                    String title;
                    String message = resp.getErrorMessage();
                    switch (resp.getErrorCode()) {
                        case CommonBasedConstants.BROKER_ERROR_RELAY_SYSTEM: // 공통기반 시스템에서 확인된 에러 (중계 서버에서 처리 중 발생한 에러)
                            title ="서비스 연계 에러";
                            break;
                        case CommonBasedConstants.BROKER_ERROR_SERVICE_SERVER: // 서비스 제공 서버에서 발생한 HTTP 에러 (행정 서비스 서버 접속시 발생함)
                            title ="서비스 제공 서버 HTTP 응답 에러";
                            break;
                        case CommonBasedConstants.BROKER_ERROR_FAILED_REQUEST: // 서비스 요청 실패 (네트워크 유실로 발생할 수 있음)
                            title ="서비스 요청 실패";
                            break;
                        case CommonBasedConstants.BROKER_ERROR_INVALID_RESPONSE: // 서비스 응답 메시지 처리 에러 (네트워크 유실로 발생할 수 있음)
                            title ="서비스 응답 처리 실패";
                            break;
                        default:
                            title ="정의되지 않음.";
                            message ="알수없음.";
                    }
                    sb.append("ERROR :: ").append("[").append(title).append("]\n").append(message);
                    Log.e("ERROR : ",  sb.toString());
                }
                Toast.makeText(NativeActivity.this, sb.toString(), Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(int errorCode, String errMessage, Throwable t) {
                Log.e("gomgomTest", errMessage +"(code : "+ errorCode +")", t);
                Toast.makeText(NativeActivity.this, errMessage +"(code : "+ errorCode +")", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getSSO(View view) {
        final StringBuilder sb =new StringBuilder();
        try {
            SSO sso = CommonBasedAPI.getSSO();
            sb.append("====== 사용자 정보 획득 ======\n");
            sb.append("nickname : ").append(sso.getUserDN()).append("\n");
            sb.append("cn : ").append(sso.getUserID()).append("\n");
            sb.append("ou : ").append(sso.getOuName()).append("\n");
            sb.append("ou code : ").append(sso.getOuCode()).append("\n");
            sb.append("department : ").append(sso.getDepartmentName()).append("\n");
            sb.append("department number : ").append(sso.getDepartmentCode()).append("\n");
            sb.append("========================");
        } catch (CommonBasedAPI.CommonBaseAPIException e) {
            sb.append("** 사용자 정보 획득에 실패하였습니다. **");
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(NativeActivity.this, sb.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void DocView(View view) {
        // 문서뷰어 요청
        CommonBasedAPI.startDefaultDocViewActivity(this,
                // 문서뷰어로 요청할 파일명
                // 실제 파일 형식과 파일명의 확장자는 서로 같아야 함
                "http://10.180.22.77:65535/MOI_API/file/sample.pdf",
                // 문서뷰어로 요청할 파일의 URL
                // URL의 IP/PORT는 공통기반 사용 신청 시 발급되는 문서변환 IP/PORT를 사용해야 함
                "sample.pdf",
                // 문서뷰어로 요청할 파일의 생성날짜
                "");
    }
}