package org.notlocalhost.fab;

import android.graphics.drawable.Drawable;

/**
 *
 * Created by mkoenig on 12/12/14.
 */
public class FloatingActionMenuItem {
    private String label;
    private Drawable icon;

    public void setLabel(String label) {
        this.label = label;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public Drawable getIcon() {
        return this.icon;
    }

    public String getLabel() {
        return this.label;
    }

    @Override
    public String toString() {
        return "FloatingActionMenuItem{" +
                "label='" + label + '\'' +
                ", icon=" + icon +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FloatingActionMenuItem that = (FloatingActionMenuItem) o;

        if (icon != null ? !icon.equals(that.icon) : that.icon != null) return false;
        if (label != null ? !label.equals(that.label) : that.label != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = label != null ? label.hashCode() : 0;
        result = 31 * result + (icon != null ? icon.hashCode() : 0);
        return result;
    }
}
