package io.github.lmikoto;

public class RenameBean {

    private String textToFind;

    private String replaceWith;

    private Boolean searchInCommentsAndStrings;
    private Boolean searchForTextOccurrences;

    public String getTextToFind() {
        return textToFind;
    }

    public void setTextToFind(String textToFind) {
        this.textToFind = textToFind;
    }

    public String getReplaceWith() {
        return replaceWith;
    }

    public void setReplaceWith(String replaceWith) {
        this.replaceWith = replaceWith;
    }

    public Boolean getSearchInCommentsAndStrings() {
        return searchInCommentsAndStrings;
    }

    public void setSearchInCommentsAndStrings(Boolean searchInCommentsAndStrings) {
        this.searchInCommentsAndStrings = searchInCommentsAndStrings;
    }

    public Boolean getSearchForTextOccurrences() {
        return searchForTextOccurrences;
    }

    public void setSearchForTextOccurrences(Boolean searchForTextOccurrences) {
        this.searchForTextOccurrences = searchForTextOccurrences;
    }
}
