package tess.mutindamike.com.facebookdemo.models;

public class Posts {
    private String post_title;
    private String post_text;
    private String post_image;
    private String created_by;
    private int likes;

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public String getPost_text() {
        return post_text;
    }

    public void setPost_text(String post_text) {
        this.post_text = post_text;
    }

    public String getPost_image() {
        return post_image;
    }

    public void setPost_image(String post_image) {
        this.post_image = post_image;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public Posts(String post_title, String post_text, String post_image, String created_by, int likes) {

        this.post_title = post_title;
        this.post_text = post_text;
        this.post_image = post_image;
        this.created_by = created_by;
        this.likes = likes;
    }

    public Posts() {

    }
}
