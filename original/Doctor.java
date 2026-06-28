package project4.ofekFanianAndTalOshri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Doctor extends Lecturer implements Comparable<Lecturer>, Serializable {

    private static final long serialVersionUID = 1L;

    protected List<Article> articles;

    public Doctor(String name, String id, Degree degree, String degreeName, double salary) {
        super(name, id, degree, degreeName, salary);
        this.articles = new ArrayList<>();
    }

    public List<Article> getArticles() {
        return articles;
    }

    public int getNumArticles() {
        return articles.size();
    }

    public void addArticle(Article article) {
        if (article == null || articles.contains(article))
            return;
        articles.add(article);
    }

    @Override
    public int compareTo(Lecturer other) {
        int otherArticles = 0;
        if (other instanceof Doctor otherDoctor) {
            otherArticles = otherDoctor.getNumArticles();
        }
        return Integer.compare(this.getNumArticles(), otherArticles);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Doctor)) return false;
        Doctor other = (Doctor) obj;
        return super.equals(other);
    }

    @Override
    public String toString() {
        return super.toString() + ", Articles: " + getNumArticles();
    }
}
