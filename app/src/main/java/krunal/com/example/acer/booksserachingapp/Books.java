package krunal.com.example.acer.booksserachingapp;

/**
 * Created by acer on 21-02-2018.
 */

/**
 * Model Class from Books to store book related data.
 */
public class Books {

    private String title;
    private String[] author;
    private String description;
    private String infoLink;

    Books(String title, String[] author, String description, String infoLink) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.infoLink = infoLink;
    }

    public String getTitle() {
        return title;
    }

    public String[] getAuthor() {
       return author;
    }

    public String getAuthorNames(){
        String s = "";
        for (int i=0; i<author.length; i++){
            if (i == author.length-1){
                s += author[i];
            }else {
                s += author[1] + ", ";
            }
        }
        return s;
    }

    public String getDescription() {
        return description;
    }

    public String getInfoLink() {
        return infoLink;
    }
}
