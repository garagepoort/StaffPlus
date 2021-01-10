package net.shortninja.staffplus.staff.mode.config.modeitems.counter;

import net.shortninja.staffplus.staff.mode.config.ModeItemConfiguration;

public class CounterModeConfiguration extends ModeItemConfiguration {

    private boolean modeCounterShowStaffMode;
    private String title;

    public CounterModeConfiguration(boolean modeCounterShowStaffMode, String title) {
        this.modeCounterShowStaffMode = modeCounterShowStaffMode;
        this.title = title;
    }

    public boolean isModeCounterShowStaffMode() {
        return modeCounterShowStaffMode;
    }

    public String getTitle() {
        return title;
    }
}
