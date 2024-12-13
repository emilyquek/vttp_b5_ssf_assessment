package vttp.batch5.ssf.noticeboard.models;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class Notice {

    @NotEmpty(message = "Title is a mandatory field!")
    @Size(min=3, max=128, message = "Title must be between 3 and 128 characters long!")
    private String title;

    @NotEmpty(message = "Please enter your email!")
    @Email(message = "Must be a well-formed email address!")
    private String poster;

    @NotNull(message = "Please select a date!")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Future(message = "Date must be a future date!")
    private Date postDate;

    @NotEmpty(message = "Please choose at least 1 category!")
    private List<String> categories;

    @NotEmpty(message = "Content cannot be empty!")
    private String text;

    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}

    public String getPoster() {return poster;}
    public void setPoster(String poster) {this.poster = poster;}

    public Date getPostDate() {return postDate;}
    public void setPostDate(Date postDate) {this.postDate = postDate;}

    public List<String> getCategories() {return categories;}
    public void setCategories(List<String> categories) {this.categories = categories;}

    public String getText() {return text;}
    public void setText(String text) {this.text = text;}

    public Notice() {}
    
}
