package org.nondoc.android.a2vnc;

import android.app.Activity;
import android.os.Bundle;
import android.view.View.OnKeyListener;
import android.view.*;
import android.widget.*;

public class A2VNC extends Activity implements OnKeyListener
{

    private RemoteControlView rcv;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        RemoteControlView rcv = new RemoteControlView(this);
        setContentView(rcv);
        rcv.requestFocus();
        rcv.setOnKeyListener(this);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        
        rcv.factor *= .8;
        System.out.println("key: " + keyCode);
        return true;
    }

}
