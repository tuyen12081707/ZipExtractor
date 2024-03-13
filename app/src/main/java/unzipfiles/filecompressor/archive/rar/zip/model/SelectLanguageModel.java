package unzipfiles.filecompressor.archive.rar.zip.model;

public class SelectLanguageModel {

    private int flag;
    private String name;
    private String langCode;
    private boolean isSelected;

    public SelectLanguageModel(int flag, String name, String langCode, boolean isSelected) {
        this.flag = flag;
        this.name = name;
        this.langCode = langCode;
        this.isSelected = isSelected;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public boolean getSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
