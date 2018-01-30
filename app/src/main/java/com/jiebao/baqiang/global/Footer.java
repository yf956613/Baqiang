package com.jiebao.baqiang.global;

import android.widget.TextView;

public interface Footer {

	 void setFooterBtnVisible(int tvId, int visableState);


	 TextView getFooterTextView(int index);


	 void setFooterTVText(int tvId, String text);
}
