package com.laboo.londontrafficcams;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

/**
 * Created by VaiosK on 15/12/2014.
 */
public class example extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_view1);
        FlurryAgent.logEvent("About");
        TextView t=(TextView)findViewById(R.id.textView);
        t.setTextColor(Color.WHITE);

        t.setText(Html.fromHtml("<html>\n" +
                                "<head>\n" +
                                "\t<title></title>\n" +
                                "</head>\n" +
                                "<body>\n" +
                                "<p style=\"text-align: center;\">&nbsp;</p>\n" +
                                "\n" +
                                "<h1 style=\"text-align: center;\"><span style=\"font-size:36px;\">London Traffic Cams</span></h1>\n" +
                                "\n" +
                                "<p style=\"text-align: center;\">&nbsp;</p>\n" +
                                "\n" +
                                "<p style=\"text-align: center;\">&nbsp;</p>\n" +
                                "\n" +
                                "<h2 style=\"text-align: center;\"><span style=\"font-size:28px;\">Version 1.0</span></h2>\n" +
                                "\n" +
                                "<h2 style=\"text-align: center;\"><span style=\"font-size:28px;\">Laboo Ltd. 2014-2015</span></h2>\n" +
                                "</body>\n" +
                                "</html>\n"
                )
        );





    }
}