package id.kopipintar.pos.model;

public class User {

    private int user_id, role_id, branch_id;
    private String email, name, token, branch_name, branch_address, phone_no;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }

    public int getBranch_id() {
        return branch_id;
    }

    public void setBranch_id(int branch_id) {
        this.branch_id = branch_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getBranch_name() {
        return branch_name;
    }

    public void setBranch_name(String branch_name) {
        this.branch_name = branch_name;
    }

    public String getBranch_address() {
        return branch_address;
    }

    public void setBranch_address(String branch_address) {
        this.branch_address = branch_address;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id=" + user_id +
                ", role_id=" + role_id +
                ", branch_id=" + branch_id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", token='" + token + '\'' +
                ", branch_name='" + branch_name + '\'' +
                ", branch_address='" + branch_address + '\'' +
                ", phone_no='" + phone_no + '\'' +
                '}';
    }
}
