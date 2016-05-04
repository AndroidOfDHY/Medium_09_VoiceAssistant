package com.zlh.voiceassistant.pocketsphinx;

import android.os.Bundle;

public interface RecognitionListener {

	abstract void onPartialResults(Bundle b);

	abstract void onResults(Bundle b);

	abstract void onError(int err);
}
