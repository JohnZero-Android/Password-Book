package com.johnzero.passwordbook.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.johnzero.passwordbook.R;
import com.johnzero.passwordbook.codec.binary.Base64;
import com.johnzero.passwordbook.util.CipherUtil;
import com.johnzero.passwordbook.util.Utils;

import java.security.KeyPair;

import static com.johnzero.passwordbook.util.CipherUtil.getKeyPair;

/**
 * @author: JohnZero
 * @date: 2021-01-15
 **/
public class VerifyActivity extends BaseActivity implements View.OnClickListener {
    private EditText et_public;
    private EditText et_private;
    private Button btn_in;
    private Button btn_out;
    private ImageView iv_update;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_verify;
    }

    @Override
    protected void initData() { mContext = this; }

    @Override
    protected void initView() {
        et_public = findViewById(R.id.et_public);
        et_private = findViewById(R.id.et_private);
        btn_in = findViewById(R.id.btn_in);
        btn_out = findViewById(R.id.btn_out);
        iv_update = findViewById(R.id.iv_update);

        btn_in.setOnClickListener(this);
        btn_out.setOnClickListener(this);
        iv_update.setOnClickListener(this);

        et_public.setText(CipherUtil.publicKey);
        et_private.setText(CipherUtil.privateKey);
    }

    @Override
    protected void onPause() {
        super.onPause();
        save();
        Utils.log("VerifyActivity onPause");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_in:
                Utils.passwordIn(mContext);
                finish();
                break;
            case R.id.btn_out:
                try {
                    Utils.passwordOut(mContext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
                break;
            case R.id.iv_update:
                try {
                    KeyPair keyPair = getKeyPair();
                    et_public.setText(new String(Base64.encodeBase64(keyPair.getPublic().getEncoded())));
                    et_private.setText(new String(Base64.encodeBase64(keyPair.getPrivate().getEncoded())));
                    save();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    void save(){
        CipherUtil.publicKey=String.valueOf(et_public.getText());
        CipherUtil.privateKey=String.valueOf(et_private.getText());
        SharedPreferences sharedPreferences = getSharedPreferences("PasswordBook", Utils.mode);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("publicKey", CipherUtil.publicKey);
        editor.putString("privateKey", CipherUtil.privateKey);
        editor.commit();
    }
}