package com.example.hri.proximity_test;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;


import com.estimote.internal_plugins_api.cloud.CloudCredentials;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory;
import com.estimote.proximity_sdk.proximity.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.proximity.ProximityAttachment;
import com.estimote.proximity_sdk.proximity.ProximityObserver;
import com.estimote.proximity_sdk.proximity.ProximityObserverBuilder;
import com.estimote.proximity_sdk.proximity.ProximityZone;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;


public class MainActivity extends AppCompatActivity {

    private ProximityObserver proximityObserver;
    private LinearLayout layout12;
    private Fileio fileio;
    private List<String> fread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout12 = (LinearLayout) findViewById(R.id.LO1);

/*
        onAddField("HCL", R.drawable.hcl);
        onAddField("FIBO",R.drawable.logo300x80);
*/
//        // add this:
//        CloudCredentials cloudCredentials =
//                new EstimoteCloudCredentials("fibo-hri-proximity-301", "d5b463dadc257cc256285c4a75322a64");
//
//        this.proximityObserver =
//                new ProximityObserverBuilder(getApplicationContext(), cloudCredentials)
//                        .withOnErrorAction(new Function1<Throwable, Unit>() {
//                            @Override
//                            public Unit invoke(Throwable throwable) {
//                                Log.e("app", "proximity observer error: " + throwable);
//                                return null;
//                            }
//                        })
//                        .withBalancedPowerMode()
//                        .build();


        //read all from file
        fileio = new Fileio(this);
        fread = fileio.readLine("list.txt");

        EstimoteCloudCredentials cloudCredentials = new EstimoteCloudCredentials(fread.get(0), fread.get(1));
        proximityObserver = new ProximityObserverBuilder(getApplicationContext(), cloudCredentials)
                .withBalancedPowerMode()
                .withOnErrorAction(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        Log.d("holy","shit,error");
                        return null;
                    }
                })
                .build();
/*
        ProximityZone zone1 = this.proximityObserver.zoneBuilder()
                .forAttachmentKeyAndValue("room", "research1")
                .inCustomRange(0.5)
                .withOnEnterAction(new Function1<ProximityAttachment, Unit>() {
                    @Override
                    public Unit invoke(ProximityAttachment attachment) {
                        Log.d("app", attachment.getDeviceId());

                        return null;
                    }
                })
                .withOnExitAction(new Function1<ProximityAttachment, Unit>() {
                    @Override
                    public Unit invoke(ProximityAttachment attachment) {
                        Log.d("app", "Bye bye, 1st lab");
                        return null;
                    }
                })
                .create();
        this.proximityObserver.addProximityZone(zone1);

        ProximityZone zone2 = this.proximityObserver.zoneBuilder()
                .forAttachmentKeyAndValue("room", "research2")
                .inCustomRange(0.5)
                .withOnEnterAction(new Function1<ProximityAttachment, Unit>() {
                    @Override
                    public Unit invoke(ProximityAttachment attachment) {
                        Log.d("app", "Welcome to the 2nd lab");

                        return null;
                    }
                })
                .withOnExitAction(new Function1<ProximityAttachment, Unit>() {
                    @Override
                    public Unit invoke(ProximityAttachment attachment) {
                        Log.d("app", "Bye bye, 2nd lab");
                        return null;
                    }
                })
                .create();
        this.proximityObserver.addProximityZone(zone2);
*/

        List<ProximityZone> zone = new ArrayList<>();

        int zonecount = Integer.parseInt(fread.get(2));
        for (int i =0;i<zonecount;i++)
        {
            final List<String> subdata = fileio.readLine(fread.get(3+4*i+3));
            ProximityZone zonetemp = this.proximityObserver.zoneBuilder()
                    .forAttachmentKeyAndValue(fread.get(3+4*i), fread.get(3+4*i+1))
                    .inCustomRange(Float.parseFloat(fread.get(3+4*i+2)))
                    .withOnEnterAction(new Function1<ProximityAttachment, Unit>() {
                        @Override
                        public Unit invoke(ProximityAttachment attachment) {
                            Log.d("appDETECT", attachment.getDeviceId());
                            layout12.removeAllViews();
                            int labcount = Integer.parseInt(subdata.get(0));
                            for(int i =0;i<labcount;i++) {
                                onAddField(subdata.get(1+4*i), subdata.get(1+4*i+1), subdata.get(1+4*i+2), subdata.get(1+4*i+3));
                            }
                            return null;
                        }
                    })
                    .withOnExitAction(new Function1<ProximityAttachment, Unit>() {
                        @Override
                        public Unit invoke(ProximityAttachment attachment) {
                            Log.d("appLOSS", attachment.getDeviceId());
                            return null;
                        }
                    })
                    .create();
            zone.add(zonetemp);
            this.proximityObserver.addProximityZone(zone.get(zone.size()-1));
        }
        RequirementsWizardFactory
                .createEstimoteRequirementsWizard()
                .fulfillRequirements(this,
                        // onRequirementsFulfilled
                        new Function0<Unit>() {
                            @Override public Unit invoke() {
                                Log.d("app", "requirements fulfilled");
                                proximityObserver.start();
                                return null;
                            }
                        },
                        // onRequirementsMissing
                        new Function1<List<? extends Requirement>, Unit>() {
                            @Override public Unit invoke(List<? extends Requirement> requirements) {
                                Log.e("app", "requirements missing: " + requirements);
                                return null;
                            }
                        },
                        // onError
                        new Function1<Throwable, Unit>() {
                            @Override public Unit invoke(Throwable throwable) {
                                Log.e("app", "requirements error: " + throwable);
                                return null;
                            }
                        });
    }
    public void onAddField(String titl,String txt,String image_res,String video_res) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.filed, null);
        rowView.setVisibility(View.VISIBLE);
        rowView.setY(layout12.getHeight());
        rowView.setAlpha(0.0f);



// Start the animation
        rowView.animate()
                .alpha(1.0f)
                .translationY(0)
                .setListener(null);
        layout12.addView(rowView, 0);

        try {
            TextView text = (TextView) ((ViewGroup) layout12.getChildAt(0)).getChildAt(0);
            text.setText(titl);
            ImageView image = (ImageView)((ViewGroup)layout12.getChildAt(0)).getChildAt(1);
            int resId = getResources().getIdentifier(image_res, "drawable", getPackageName());
            image.setImageResource(resId);
            TextView text2 = (TextView) ((ViewGroup) layout12.getChildAt(0)).getChildAt(2);
            text2.setText(txt);
            VideoView video = (VideoView)((ViewGroup)layout12.getChildAt(0)).getChildAt(3);
            resId = getResources().getIdentifier(video_res, "raw", getPackageName());
            video.setVideoURI(Uri.parse("android.resource://" + getPackageName() +"/"+resId));
            video.setMediaController(new MediaController(this));
        } catch (Exception ex) {
                Log.d("1",ex.toString());
        }

    }



}

