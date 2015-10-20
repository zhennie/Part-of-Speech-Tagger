package me.ash.part_of_speech.data_structure;

/**
 * Created by ash on 10/19/15.
 */
public class Word {
    private String wordValue;

    public Word(String wordValue) {
        this.wordValue = wordValue;
    }

    public String getWordValue() {
        return wordValue;
    }

    public void setWordValue(String wordValue) {
        this.wordValue = wordValue;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof Word)) return false;

        Word word = (Word) o;

        if (wordValue != null ? !wordValue.equals(word.wordValue) : word.wordValue != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return wordValue != null ? wordValue.hashCode() : 0;
    }
}
