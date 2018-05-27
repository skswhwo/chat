package kr.ac.koreatech.chat.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import kr.ac.koreatech.chat.model.User;
import kr.ac.koreatech.chat.view.sign_in.SignInActivity_;

public class BaseActivity extends AppCompatActivity {

    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("loading...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    protected void sign_out() {
        User.currentUser.clearData();
        User.currentUser = null;

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();

        startActivity(new Intent(this, SignInActivity_.class));
    }
}