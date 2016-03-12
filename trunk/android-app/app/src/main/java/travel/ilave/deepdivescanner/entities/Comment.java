package travel.ilave.deepdivescanner.entities;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lashket on 12.3.16.
 */
public class Comment implements Serializable {
    private String comment;
    private User user;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
