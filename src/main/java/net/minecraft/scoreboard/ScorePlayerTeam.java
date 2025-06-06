package net.minecraft.scoreboard;

import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ScorePlayerTeam extends Team {
    private final Scoreboard theScoreboard;
    private final String registeredName;
    private final Set<String> membershipSet = new HashSet<>();
    private String teamNameSPT;
    private String namePrefixSPT = "";
    private String colorSuffix = "";
    private boolean allowFriendlyFire = true;
    private boolean canSeeFriendlyInvisibles = true;
    private EnumVisible nameTagVisibility = EnumVisible.ALWAYS;
    private EnumVisible deathMessageVisibility = EnumVisible.ALWAYS;
    private Formatting chatFormat = Formatting.RESET;

    public ScorePlayerTeam(Scoreboard theScoreboardIn, String name) {
        this.theScoreboard = theScoreboardIn;
        this.registeredName = name;
        this.teamNameSPT = name;
    }

    public String getRegisteredName() {
        return this.registeredName;
    }

    public String getTeamName() {
        return this.teamNameSPT;
    }

    public void setTeamName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        } else {
            this.teamNameSPT = name;
            this.theScoreboard.sendTeamUpdate(this);
        }
    }

    public Collection<String> getMembershipCollection() {
        return this.membershipSet;
    }

    public String getColorPrefix() {
        return this.namePrefixSPT;
    }

    public void setNamePrefix(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix cannot be null");
        } else {
            this.namePrefixSPT = prefix;
            this.theScoreboard.sendTeamUpdate(this);
        }
    }

    public String getColorSuffix() {
        return this.colorSuffix;
    }

    public void setNameSuffix(String suffix) {
        this.colorSuffix = suffix;
        this.theScoreboard.sendTeamUpdate(this);
    }

    public String formatString(String input) {
        return this.getColorPrefix() + input + this.getColorSuffix();
    }

    public static String formatPlayerName(Team p_96667_0_, String p_96667_1_) {
        return p_96667_0_ == null ? p_96667_1_ : p_96667_0_.formatString(p_96667_1_);
    }

    public boolean getAllowFriendlyFire() {
        return this.allowFriendlyFire;
    }

    public void setAllowFriendlyFire(boolean friendlyFire) {
        this.allowFriendlyFire = friendlyFire;
        this.theScoreboard.sendTeamUpdate(this);
    }

    public boolean getSeeFriendlyInvisiblesEnabled() {
        return this.canSeeFriendlyInvisibles;
    }

    public void setSeeFriendlyInvisiblesEnabled(boolean friendlyInvisibles) {
        this.canSeeFriendlyInvisibles = friendlyInvisibles;
        this.theScoreboard.sendTeamUpdate(this);
    }

    public EnumVisible getNameTagVisibility() {
        return this.nameTagVisibility;
    }

    public EnumVisible getDeathMessageVisibility() {
        return this.deathMessageVisibility;
    }

    public void setNameTagVisibility(EnumVisible p_178772_1_) {
        this.nameTagVisibility = p_178772_1_;
        this.theScoreboard.sendTeamUpdate(this);
    }

    public void setDeathMessageVisibility(EnumVisible p_178773_1_) {
        this.deathMessageVisibility = p_178773_1_;
        this.theScoreboard.sendTeamUpdate(this);
    }

    public int func_98299_i() {
        int i = 0;

        if (this.getAllowFriendlyFire()) {
            i |= 1;
        }

        if (this.getSeeFriendlyInvisiblesEnabled()) {
            i |= 2;
        }

        return i;
    }

    public void func_98298_a(int p_98298_1_) {
        this.setAllowFriendlyFire((p_98298_1_ & 1) > 0);
        this.setSeeFriendlyInvisiblesEnabled((p_98298_1_ & 2) > 0);
    }

    public void setChatFormat(Formatting p_178774_1_) {
        this.chatFormat = p_178774_1_;
    }

    public Formatting getChatFormat() {
        return this.chatFormat;
    }
}
