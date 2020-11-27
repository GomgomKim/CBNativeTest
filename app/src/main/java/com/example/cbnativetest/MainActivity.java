package com.example.cbnativetest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.StringTokenizer;

import kr.go.mobile.common.v3.CommonBasedAPI;
import kr.go.mobile.common.v3.CommonBasedConstants;
import kr.go.mobile.common.v3.broker.Response;
import kr.go.mobile.common.v3.broker.SSO;

public class MainActivity extends AppCompatActivity {

    private static final int GOV_INIT_REQUEST = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CommonBasedAPI.startInitActivityForResult(this, GOV_INIT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (intent != null) {
            if (requestCode == GOV_INIT_REQUEST) {
                if (resultCode == Activity.RESULT_OK) {
                    // 공통기반 서비스 초기화 성공
                    // dn 추출 및 파싱
                    String dn = intent.getStringExtra(CommonBasedConstants.EXTRA_KEY_DN);
                    String cn =null;
                    String ou =null;
                    StringTokenizer st =new StringTokenizer(dn, ",");
                    while (st.hasMoreTokens()) {
                        String token = st.nextToken();
                        if (token.startsWith("cn")) {
                            cn = token.substring(3);
                        } else if (token.startsWith("ou")) {
                            ou = token.substring(3);
                        }
                    }

                    StringBuilder sb = new StringBuilder();
                    sb.append("이름: ").append(cn);
                    sb.append("\n소속기관: ").append(ou);
                    sb.append("\n(").append(dn).append(")");

                    TextView txtView = findViewById(R.id.txtUesrInfo);
                    txtView.setText(sb.toString());

                    // 다음 화면으로 전환.
                    Intent mainIntent =new Intent(this, NativeActivity.class);
                    mainIntent.putExtra("dn", dn);
                    mainIntent.putExtra("cn", cn);
                    mainIntent.putExtra("ou", ou);
                    startActivity(mainIntent);

                } else {
                    // 공통기반 API 초기화 실패
                }
                finish();
            }
        }
    }




}