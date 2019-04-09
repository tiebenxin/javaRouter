package com.example.common.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import com.example.common.R;
import com.example.common.base.BaseDialog;


/**
 * Created by LY309313 on 2016/8/30.
 */

public class CommenProgressDialog extends BaseDialog {

    private String message;
    private TextView mDialogWranning;

    public CommenProgressDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public CommenProgressDialog(Context context, int theme, String message) {
        super(context, theme);
        this.message = message;
    }

    public CommenProgressDialog(Context context) {
        super(context);
    }

    @Override
    public void initView() {
        setContentView(R.layout.dialog_progress);
        mDialogWranning = (TextView) findViewById(R.id.mDialogWranning);
        mDialogWranning.setText(message);
    }

    @Override
    public void processClick(View view) {

    }

    public void setMessage(String message){
        if(mDialogWranning != null){
            mDialogWranning.setText(message);
        }
    }
}
