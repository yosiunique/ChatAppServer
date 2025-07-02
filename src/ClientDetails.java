import java.io.Serializable;

public class ClientDetails  implements Serializable {

    private String userName;
    private String ipAddress;
    private  String message;
    private  String  reciver;
    public ClientDetails() {}
    public ClientDetails(String userName, String ipAddress, String message) {
        this.userName = userName;
        this.ipAddress = ipAddress;
        this.message = message;

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getReciver() {
        return this.reciver;
    }

    public void setReciver(String reciver) {
        this.reciver = reciver;
    }
}
