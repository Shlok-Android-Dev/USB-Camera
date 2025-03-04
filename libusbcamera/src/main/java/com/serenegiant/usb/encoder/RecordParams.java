package com.serenegiant.usb.encoder;

/** Recording Parameters
 * Created by Shlok on 2025/21/02. Jaipur, India.
 */

public class RecordParams {
    private String recordPath;
    private int recordDuration;
    private boolean voiceClose;
    private boolean isAutoSave;
    private boolean isSupportOverlay;

    public RecordParams() {
    }

    public boolean isSupportOverlay() {
        return isSupportOverlay;
    }

    public void setSupportOverlay(boolean supportOverlay) {
        isSupportOverlay = supportOverlay;
    }

    public boolean isVoiceClose() {
        return voiceClose;
    }

    public void setVoiceClose(boolean voiceClose) {
        this.voiceClose = voiceClose;
    }

    public String getRecordPath() {
        return recordPath;
    }

    public void setRecordPath(String recordPath) {
        this.recordPath = recordPath;
    }

    public int getRecordDuration() {
        return recordDuration;
    }

    public void setRecordDuration(int recordDuration) {
        this.recordDuration = recordDuration;
    }

    public boolean isAutoSave() {
        return isAutoSave;
    }

    public void setAutoSave(boolean autoSave) {
        isAutoSave = autoSave;
    }
}
