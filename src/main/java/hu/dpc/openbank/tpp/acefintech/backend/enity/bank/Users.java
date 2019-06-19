package hu.dpc.openbank.tpp.acefintech.backend.enity.bank;

import javax.persistence.*;

@Entity
@Table(name = "USERS")
public class Users {
    @Id
    @Column(name = "USERNAME", nullable = false, unique = true)
    private String userName;
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "ENABLED")
    private boolean enabled;

    public Users() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
