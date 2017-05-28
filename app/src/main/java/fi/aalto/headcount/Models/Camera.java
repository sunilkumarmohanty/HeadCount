package fi.aalto.headcount.Models;

import com.google.firebase.database.Exclude;

/**
 * Created by sunil on 24-05-2017.
 */

public class Camera {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    private String IP;
    private String id;
    private Long faces;

    public Camera(){
    }
    public Camera(String name, String IPAddress){
        this.name = name;
        this.IP = IPAddress;
    }
    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getFaces() {
        if (faces == null)
            return (long)0;
        return faces;
    }

    public void setFaces(Long faces) {
        this.faces = faces;
    }
}
