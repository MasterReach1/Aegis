package me.impy.aegis.ui.slides;

import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.paolorotolo.appintro.ISlidePolicy;

import me.impy.aegis.R;
import me.impy.aegis.helpers.FingerprintHelper;

public class CustomAuthenticationSlide extends Fragment implements ISlidePolicy, RadioGroup.OnCheckedChangeListener {
    public static final int CRYPT_TYPE_INVALID = 0;
    public static final int CRYPT_TYPE_NONE = 1;
    public static final int CRYPT_TYPE_PASS = 2;
    public static final int CRYPT_TYPE_FINGER = 3;

    private Spinner _authenticationSpinner;
    private RadioGroup _buttonGroup;
    private int _bgColor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_authentication_slide, container, false);
        _buttonGroup = view.findViewById(R.id.rg_authenticationMethod);
        _buttonGroup.setOnCheckedChangeListener(this);
        onCheckedChanged(_buttonGroup, _buttonGroup.getCheckedRadioButtonId());

        // only show the fingerprint option if the api version is new enough, permission is granted and a scanner is found
        FingerprintManager manager = FingerprintHelper.getManager(getContext());
        if (manager != null) {
            RadioButton button = view.findViewById(R.id.rb_fingerprint);
            TextView text = view.findViewById(R.id.text_rb_fingerprint);
            button.setEnabled(false);
            text.setEnabled(false);
        }

        view.findViewById(R.id.main).setBackgroundColor(_bgColor);
        return view;
    }

    public void setBgColor(int color) {
        _bgColor = color;
    }

    @Override
    public boolean isPolicyRespected() {
        return _buttonGroup.getCheckedRadioButtonId() != -1;
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {
        Snackbar snackbar = Snackbar.make(getView(), "Please select an authentication method", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (i == -1) {
            return;
        }

        int id;
        switch (i) {
            case R.id.rb_none:
                id = CRYPT_TYPE_NONE;
                break;
            case R.id.rb_password:
                id = CRYPT_TYPE_PASS;
                break;
            case R.id.rb_fingerprint:
                id = CRYPT_TYPE_FINGER;
                break;
            default:
                throw new RuntimeException();
        }

        Intent intent = getActivity().getIntent();
        intent.putExtra("cryptType", id);
    }
}
