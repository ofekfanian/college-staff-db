package project4.ofekFanianAndTalOshri;

import java.io.Serializable;

public class Article implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nameOfArticle;

    public Article(String nameOfArticle) {
        this.nameOfArticle = nameOfArticle;
    }

    public String getNameOfArticle() {
        return nameOfArticle;
    }

    public void setNameOfArticle(String nameOfArticle) {
        this.nameOfArticle = nameOfArticle;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Article)) return false;
        Article other = (Article) obj;
        return this.nameOfArticle.equalsIgnoreCase(other.nameOfArticle);
    }

    @Override
    public String toString() {
        return nameOfArticle;
    }
}
