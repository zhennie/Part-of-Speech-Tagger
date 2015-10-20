package me.ash.part_of_speech.data_structure;

/**
 * Created by ash on 10/19/15.
 */
public class Tag {
    private String tagValue;

    public Tag(String tagValue) {
        this.tagValue = tagValue;
    }

    public String getTagValue() {
        return tagValue;
    }

    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;

        Tag tag = (Tag) o;

        if (tagValue != null ? !tagValue.equals(tag.tagValue) : tag.tagValue != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return tagValue != null ? tagValue.hashCode() : 0;
    }
}
